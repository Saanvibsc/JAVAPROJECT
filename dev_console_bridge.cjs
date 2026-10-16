const http = require('http');
const { spawn, execSync } = require('child_process');
const path = require('path');
const fs = require('fs');

const PORT = 3000;
const HOST = '0.0.0.0';

// Ensure MariaDB is running in the background container
try {
  execSync('mariadb -u root -proot -e "SELECT 1;" >/dev/null 2>&1');
} catch (e) {
  try {
    execSync('mkdir -p /run/mysqld && chown -R mysql:mysql /run/mysqld /var/lib/mysql && (/usr/sbin/mariadbd --user=root --bind-address=127.0.0.1 --port=3306 &) && sleep 2');
  } catch (err) {
    console.error('Could not auto-start MariaDB:', err.message);
  }
}

// Set classpath for Java runtime
const classpath = 'target/classes:lib/*';

// Global active Java console process
let javaProc = null;
let consoleLogs = [];
const MAX_LOGS = 1000;

function appendLog(text) {
  consoleLogs.push(text);
  if (consoleLogs.length > MAX_LOGS) consoleLogs.shift();
}

function startJavaProcess() {
  if (javaProc) {
    try { javaProc.kill(); } catch (e) {}
  }
  appendLog('[SYSTEM] Starting Core Java Console Application (com.loanapp.Main)...\n');
  
  javaProc = spawn('java', ['-cp', classpath, 'com.loanapp.Main'], {
    env: { ...process.env, DB_URL: 'jdbc:mysql://localhost:3306/loan_system_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC', DB_USER: 'root', DB_PASSWORD: 'root' }
  });

  javaProc.stdout.on('data', (data) => {
    appendLog(data.toString());
  });

  javaProc.stderr.on('data', (data) => {
    appendLog('[ERR] ' + data.toString());
  });

  javaProc.on('close', (code) => {
    appendLog(`\n[SYSTEM] Java process exited with code ${code}. Click "RESET CONSOLE" or enter input to restart.\n`);
    javaProc = null;
  });
}

// Start initial process
startJavaProcess();

const HTML_CONTENT = `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>LoanFlow Console UI</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Oswald:wght@500;700&family=Space+Mono:wght@400;700&family=Inter:wght@400;700&display=swap" rel="stylesheet">
  <style>
    :root {
      --bg: #ffc800;
      --ink: #000000;
      --accent: #000000;
      --ink-faint: rgba(0, 0, 0, 0.1);
      --ink-muted: rgba(0, 0, 0, 0.6);
    }

    * { box-sizing: border-box; margin: 0; padding: 0; }
    
    body { 
      background-color: var(--bg); 
      color: var(--ink); 
      font-family: 'Inter', sans-serif; 
      height: 100vh; 
      display: flex;
      flex-direction: column;
      padding: 1.5rem;
      -webkit-font-smoothing: antialiased;
      overflow: hidden;
    }

    ::selection { background: var(--ink); color: var(--bg); }

    .main-container {
      flex: 1;
      border: 3px solid var(--ink);
      display: grid;
      grid-template-columns: 280px 1fr;
      grid-template-rows: auto 1fr;
      background: #ffffff;
      overflow: hidden;
    }

    aside {
      background: var(--bg);
      border-right: 3px solid var(--ink);
      padding: 2rem 1.5rem;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      overflow-y: auto;
    }

    .brand h1 {
      font-family: 'Oswald', sans-serif;
      font-size: 2.5rem;
      line-height: 1;
      text-transform: uppercase;
      margin-bottom: 0.5rem;
      letter-spacing: -0.5px;
    }

    .brand p {
      font-family: 'Space Mono', monospace;
      font-size: 0.7rem;
      font-weight: 700;
      text-transform: uppercase;
    }

    .nav-buttons {
      display: flex;
      flex-direction: column;
      gap: 0.75rem;
      margin: 1.5rem 0;
    }

    .nav-btn {
      background: #ffffff;
      border: 2px solid var(--ink);
      padding: 0.8rem;
      font-family: 'Space Mono', monospace;
      font-size: 0.85rem;
      font-weight: 700;
      text-align: left;
      cursor: pointer;
      box-shadow: 4px 4px 0 var(--ink);
      transition: all 0.1s;
    }

    .nav-btn:hover {
      background: #fffbe6;
    }

    .nav-btn:active {
      box-shadow: 0 0 0 var(--ink);
      transform: translate(4px, 4px);
    }

    .nav-btn.primary {
      background: var(--ink);
      color: var(--bg);
    }

    .nav-btn.primary:hover {
      background: #222222;
      color: var(--bg);
    }

    main {
      display: grid;
      grid-template-rows: 1fr auto;
      overflow: hidden;
      background: #fafafa;
    }

    .terminal-view {
      padding: 1.5rem 2rem;
      background: #fafafa;
      overflow-y: auto;
      border-bottom: 3px solid var(--ink);
      position: relative;
    }

    #terminal {
      font-family: 'Space Mono', monospace;
      font-size: 0.85rem;
      line-height: 1.5;
      color: #111;
      white-space: pre-wrap;
      word-break: break-word;
    }

    .console-input-area {
      padding: 1.25rem 2rem;
      background: #ffffff;
    }

    .input-bar {
      display: flex;
      gap: 1rem;
      align-items: center;
    }

    .input-prefix {
      font-family: 'Space Mono', monospace;
      font-weight: 700;
      font-size: 1.1rem;
    }

    input[type="text"] {
      flex: 1;
      border: none;
      border-bottom: 3px solid var(--ink);
      padding: 0.5rem 0;
      font-family: 'Space Mono', monospace;
      font-size: 1rem;
      outline: none;
      background: transparent;
      color: #000;
    }

    .chip-container {
      margin-top: 1.25rem;
      display: flex;
      gap: 0.5rem;
      flex-wrap: wrap;
      align-items: center;
    }

    .chip-label {
      font-family: 'Space Mono', monospace;
      font-size: 0.65rem;
      font-weight: 700;
      color: var(--ink-muted);
      text-transform: uppercase;
      margin-right: 0.25rem;
    }

    .chip {
      font-family: 'Space Mono', monospace;
      font-size: 0.7rem;
      padding: 0.35rem 0.65rem;
      background: var(--bg);
      border: 1.5px solid var(--ink);
      cursor: pointer;
      font-weight: 700;
      transition: background 0.1s, color 0.1s;
    }

    .chip:hover {
      background: var(--ink);
      color: var(--bg);
    }

    .header-bar {
      grid-column: span 2;
      border-bottom: 3px solid var(--ink);
      background: var(--ink);
      color: var(--bg);
      padding: 0.6rem 1.5rem;
      display: flex;
      justify-content: space-between;
      font-family: 'Space Mono', monospace;
      font-size: 0.75rem;
      font-weight: 700;
      letter-spacing: 0.5px;
    }

    .status-indicator {
      display: flex;
      align-items: center;
    }

    .status-dot {
      display: inline-block;
      width: 8px;
      height: 8px;
      background: #00ff00;
      border-radius: 50%;
      margin-right: 0.5rem;
      box-shadow: 0 0 6px #00ff00;
    }

    @media (max-width: 768px) {
      body { padding: 0.5rem; }
      .main-container { grid-template-columns: 1fr; }
      aside { display: none; }
      .header-bar { grid-column: span 1; }
    }
  </style>
</head>
<body>
  <div class="main-container">
    <div class="header-bar">
      <span>COM.LOANAPP.MAIN // JVM 17</span>
      <span class="status-indicator"><span class="status-dot"></span>SYS_ACTIVE</span>
    </div>

    <aside>
      <div class="brand">
        <h1>LoanFlow</h1>
        <p>Pure Core Java / JDBC</p>
      </div>

      <div class="nav-buttons">
        <button class="nav-btn" onclick="sendQuickCommand('1')">[1] LOGIN</button>
        <button class="nav-btn" onclick="sendQuickCommand('2')">[2] REGISTER</button>
        <button class="nav-btn" onclick="sendQuickCommand('4')">[4] EMI CALC</button>
        <button class="nav-btn primary" onclick="restartSession()">RESET CONSOLE</button>
      </div>

      <div class="brand">
        <p style="font-size: 0.65rem; opacity: 0.75;">INDIAN BANKING SYSTEM</p>
        <p style="font-size: 0.65rem; opacity: 0.75;">V 2.0.4.STABLE</p>
      </div>
    </aside>

    <main>
      <div class="terminal-view">
        <div id="terminal">Loading Java console output...</div>
      </div>

      <div class="console-input-area">
        <form class="input-bar" id="inputForm" onsubmit="handleSend(event)">
          <span class="input-prefix">&gt;</span>
          <input type="text" id="userInput" placeholder="EXECUTE COMMAND..." autocomplete="off" autofocus />
          <button type="submit" style="display:none">Submit</button>
        </form>

        <div class="chip-container">
          <span class="chip-label">Quick Commands:</span>
          <span class="chip" onclick="fillInput('1')">1.LOGIN</span>
          <span class="chip" onclick="fillInput('admin')">ADMIN</span>
          <span class="chip" onclick="fillInput('Admin@123')">PASS_01</span>
          <span class="chip" onclick="fillInput('aarav_s')">USER_01</span>
          <span class="chip" onclick="fillInput('User@123')">PASS_USER</span>
          <span class="chip" onclick="fillInput('2')">2.REGISTER</span>
          <span class="chip" onclick="fillInput('5')">5.EXIT</span>
        </div>
      </div>
    </main>
  </div>

  <script>
    const term = document.getElementById('terminal');
    const input = document.getElementById('userInput');

    function fillInput(val) {
      input.value = val;
      input.focus();
    }

    function sendQuickCommand(val) {
      fetch('/api/input', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ input: val })
      }).then(() => pollLogs());
    }

    function handleSend(e) {
      e.preventDefault();
      const val = input.value;
      if (val === undefined || val === null) return;
      input.value = '';
      fetch('/api/input', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ input: val })
      }).then(() => pollLogs());
    }

    function restartSession() {
      fetch('/api/restart', { method: 'POST' }).then(() => pollLogs());
    }

    function pollLogs() {
      fetch('/api/logs')
        .then(r => r.json())
        .then(data => {
          if (data && data.logs) {
            term.textContent = data.logs.join('');
            const view = term.parentElement;
            view.scrollTop = view.scrollHeight;
          }
        })
        .catch(e => console.error(e));
    }

    setInterval(pollLogs, 1000);
    pollLogs();
  </script>
</body>
</html>`;

const server = http.createServer((req, res) => {
  if (req.method === 'GET' && req.url === '/') {
    res.writeHead(200, { 'Content-Type': 'text/html' });
    res.end(HTML_CONTENT);
  } else if (req.method === 'GET' && req.url === '/api/logs') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ logs: consoleLogs }));
  } else if (req.method === 'POST' && req.url === '/api/input') {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      try {
        const data = JSON.parse(body);
        if (javaProc && javaProc.stdin.writable) {
          appendLog('> ' + data.input + '\n');
          javaProc.stdin.write(data.input + '\n');
        } else {
          startJavaProcess();
          setTimeout(() => {
            if (javaProc && javaProc.stdin.writable) {
              appendLog('> ' + data.input + '\n');
              javaProc.stdin.write(data.input + '\n');
            }
          }, 500);
        }
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ success: true }));
      } catch (e) {
        res.writeHead(400);
        res.end(JSON.stringify({ error: e.message }));
      }
    });
  } else if (req.method === 'POST' && req.url === '/api/restart') {
    startJavaProcess();
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ success: true }));
  } else {
    res.writeHead(404);
    res.end('Not Found');
  }
});

server.listen(PORT, HOST, () => {
  console.log(`Core Java Console Bridge running at http://${HOST}:${PORT}`);
});
