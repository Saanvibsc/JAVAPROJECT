import express from 'express';
import cors from 'cors';
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();
const PORT = process.env.PORT ? parseInt(process.env.PORT) : 3000;

app.use(cors());
app.use(express.json());

// Stage 3 Data Models matching User.java and LoanApplication.java
interface UserRecord {
  userId: number;
  fullName: string;
  email: string;
  mobileNumber: string;
  role: 'USER' | 'ADMIN';
}

interface LoanRecord {
  loanId: number;
  userId: number;
  userName: string;
  userEmail: string;
  userMobile: string;
  loanAmount: number;
  loanType: string;
  loanDuration: string;
  interestRate: number;
  annualIncome: number;
  purpose: string;
  status: 'Pending' | 'Approved' | 'Rejected' | 'Needs More Info';
  applicationDate: string;
  rejectionReason: string | null;
}

interface EmailLog {
  id: string;
  to: string;
  subject: string;
  body: string;
  timestamp: string;
  status: 'Sent (Live SMTP)' | 'Simulated (Console)';
}

// Initial Data matching database.sql
const users: UserRecord[] = [
  {
    userId: 1,
    fullName: 'System Administrator',
    email: 'admin@loanapp.com',
    mobileNumber: '9876543210',
    role: 'ADMIN',
  },
  {
    userId: 2,
    fullName: 'Rahul Sharma',
    email: 'rahul.sharma@example.com',
    mobileNumber: '9876543211',
    role: 'USER',
  },
  {
    userId: 3,
    fullName: 'Priya Patel',
    email: 'priya.patel@example.com',
    mobileNumber: '9876543212',
    role: 'USER',
  }
];

let nextLoanId = 3;

const loans: LoanRecord[] = [
  {
    loanId: 1,
    userId: 2,
    userName: 'Rahul Sharma',
    userEmail: 'rahul.sharma@example.com',
    userMobile: '9876543211',
    loanAmount: 500000.0,
    loanType: 'Home',
    loanDuration: '60 Months',
    interestRate: 7.5,
    annualIncome: 1200000.0,
    purpose: 'Home extension and interior renovation',
    status: 'Pending',
    applicationDate: new Date(Date.now() - 86400000 * 2).toISOString(),
    rejectionReason: null,
  },
  {
    loanId: 2,
    userId: 3,
    userName: 'Priya Patel',
    userEmail: 'priya.patel@example.com',
    userMobile: '9876543212',
    loanAmount: 250000.0,
    loanType: 'Personal',
    loanDuration: '24 Months',
    interestRate: 10.5,
    annualIncome: 850000.0,
    purpose: 'Higher education course and certifications',
    status: 'Approved',
    applicationDate: new Date(Date.now() - 86400000 * 4).toISOString(),
    rejectionReason: null,
  }
];

const emailLogs: EmailLog[] = [
  {
    id: 'eml-1',
    to: 'priya.patel@example.com',
    subject: 'Your Loan Application has been Approved!',
    body: 'Dear Priya Patel, your loan application for Rs. 2,50,000.00 has been approved. Please check your dashboard for further details.',
    timestamp: new Date(Date.now() - 86400000 * 3).toISOString(),
    status: 'Simulated (Console)',
  }
];

// Helper to determine benchmark rate according to Indian banking norms
function determineInterestRate(loanType: string): number {
  switch (loanType.trim().toLowerCase()) {
    case 'personal':
      return 10.5;
    case 'home':
      return 7.5;
    case 'education':
      return 8.0;
    case 'car':
      return 8.5;
    default:
      return 9.0;
  }
}

// Indian currency formatter
function formatRupees(amount: number): string {
  return 'Rs. ' + amount.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

// REST Endpoints
app.get('/api/users', (_req, res) => {
  res.json({ success: true, users });
});

app.get('/api/loans', (req, res) => {
  const { userId, status } = req.query;
  let result = [...loans];

  if (userId) {
    result = result.filter(l => l.userId === Number(userId));
  }
  if (status) {
    result = result.filter(l => l.status.toLowerCase() === String(status).toLowerCase());
  }

  res.json({ success: true, count: result.length, loans: result });
});

app.get('/api/loans/:id', (req, res) => {
  const loanId = parseInt(req.params.id);
  const loan = loans.find(l => l.loanId === loanId);
  if (!loan) {
    return res.status(404).json({ success: false, message: `Loan with ID ${loanId} not found.` });
  }
  res.json({ success: true, loan });
});

// Stage 3: User Side - Apply for Loan
app.post('/api/loans', (req, res) => {
  const { userId, loanAmount, loanType, loanDuration, annualIncome, purpose } = req.body;

  // Validation according to Requirement 6, 8, 10, 11, 14
  if (!userId || typeof userId !== 'number') {
    return res.status(400).json({ success: false, message: 'Valid userId is required.' });
  }
  const user = users.find(u => u.userId === userId);
  if (!user) {
    return res.status(404).json({ success: false, message: 'User not found in system.' });
  }

  const amount = Number(loanAmount);
  if (isNaN(amount) || amount <= 0) {
    return res.status(400).json({ success: false, message: 'Loan amount must be a positive number greater than 0.' });
  }

  const durationStr = typeof loanDuration === 'number' ? `${loanDuration} Months` : String(loanDuration || '').trim();
  if (!durationStr || durationStr.length === 0) {
    return res.status(400).json({ success: false, message: 'Loan duration is required.' });
  }

  const income = Number(annualIncome);
  if (isNaN(income) || income <= 0) {
    return res.status(400).json({ success: false, message: 'Annual income must be greater than 0.' });
  }

  if (!purpose || String(purpose).trim().length === 0) {
    return res.status(400).json({ success: false, message: 'Purpose of loan is required and cannot be empty.' });
  }

  if (!loanType || String(loanType).trim().length === 0) {
    return res.status(400).json({ success: false, message: 'Loan type is required and cannot be empty.' });
  }
  const type = String(loanType).trim();
  const interestRate = determineInterestRate(type);

  // Requirement 12: status must be automatically set to "Pending"
  const newLoan: LoanRecord = {
    loanId: nextLoanId++,
    userId: user.userId,
    userName: user.fullName,
    userEmail: user.email,
    userMobile: user.mobileNumber,
    loanAmount: amount,
    loanType: type,
    loanDuration: durationStr,
    interestRate,
    annualIncome: income,
    purpose: String(purpose).trim(),
    status: 'Pending',
    applicationDate: new Date().toISOString(),
    rejectionReason: null,
  };

  loans.unshift(newLoan);

  res.status(201).json({
    success: true,
    message: 'Your loan application has been submitted successfully!',
    loan: newLoan
  });
});

// Stage 3: Admin Side - Approve Loan (Requirement 20 & 21)
app.post('/api/loans/:id/approve', (req, res) => {
  const loanId = parseInt(req.params.id);
  const loan = loans.find(l => l.loanId === loanId);
  if (!loan) {
    return res.status(404).json({ success: false, message: `Loan ID ${loanId} not found.` });
  }

  loan.status = 'Approved';
  loan.rejectionReason = null;

  // Requirement 21 Approval Email Format
  const subject = 'Your Loan Application has been Approved!';
  const body = `Dear ${loan.userName}, your loan application for ${formatRupees(loan.loanAmount)} has been approved. Please check your dashboard for further details.`;

  const emailRecord: EmailLog = {
    id: 'eml-' + Date.now(),
    to: loan.userEmail,
    subject,
    body,
    timestamp: new Date().toISOString(),
    status: process.env.SMTP_USERNAME ? 'Sent (Live SMTP)' : 'Simulated (Console)'
  };
  emailLogs.unshift(emailRecord);

  res.json({
    success: true,
    message: `Loan ID ${loanId} has been approved. Notification email dispatched to ${loan.userEmail}.`,
    loan,
    email: emailRecord
  });
});

// Stage 3: Admin Side - Reject Loan (Requirement 22 & 23)
app.post('/api/loans/:id/reject', (req, res) => {
  const loanId = parseInt(req.params.id);
  const { reason } = req.body;

  if (!reason || String(reason).trim().length === 0) {
    return res.status(400).json({ success: false, message: 'Rejection reason is required.' });
  }

  const loan = loans.find(l => l.loanId === loanId);
  if (!loan) {
    return res.status(404).json({ success: false, message: `Loan ID ${loanId} not found.` });
  }

  loan.status = 'Rejected';
  loan.rejectionReason = String(reason).trim();

  // Requirement 23 Rejection Email Format
  const subject = 'Your Loan Application has been Rejected';
  const body = `Dear ${loan.userName}, we regret to inform you that your loan application has been rejected. Reason: ${loan.rejectionReason}. Please contact support for more details.`;

  const emailRecord: EmailLog = {
    id: 'eml-' + Date.now(),
    to: loan.userEmail,
    subject,
    body,
    timestamp: new Date().toISOString(),
    status: process.env.SMTP_USERNAME ? 'Sent (Live SMTP)' : 'Simulated (Console)'
  };
  emailLogs.unshift(emailRecord);

  res.json({
    success: true,
    message: `Loan ID ${loanId} has been rejected. Notification email dispatched to ${loan.userEmail}.`,
    loan,
    email: emailRecord
  });
});

// Stage 3: Admin Side - Needs More Info (Requirement 6)
app.post('/api/loans/:id/needs-more-info', (req, res) => {
  const loanId = parseInt(req.params.id);
  const { reason } = req.body;

  if (!reason || String(reason).trim().length === 0) {
    return res.status(400).json({ success: false, message: 'Information request note is required.' });
  }

  const loan = loans.find(l => l.loanId === loanId);
  if (!loan) {
    return res.status(404).json({ success: false, message: `Loan ID ${loanId} not found.` });
  }

  loan.status = 'Needs More Info';
  loan.rejectionReason = String(reason).trim();

  res.json({
    success: true,
    message: `Loan ID ${loanId} status updated to 'Needs More Info'. Information request recorded.`,
    loan
  });
});

// Email logs
app.get('/api/emails', (_req, res) => {
  res.json({ success: true, count: emailLogs.length, emails: emailLogs });
});

// Fetch Java source files for display in the UI
app.get('/api/java-files', (_req, res) => {
  const basePath = path.join(__dirname, 'LoanApplicationSystem');
  const files = [
    { name: 'Main.java', role: 'Console Menu & Scanner Entry Point' },
    { name: 'User.java', role: 'User Model (userId, fullName, email, mobile, role)' },
    { name: 'LoanApplication.java', role: 'Stage 3 Loan Entity Model' },
    { name: 'Database.java', role: 'JDBC Connection & PreparedStatement Methods' },
    { name: 'LoanManager.java', role: 'Console Loan Flows & SMTP Email Sending' },
    { name: 'database.sql', role: 'MySQL Schema & Table Definitions' },
    { name: 'pom.xml', role: 'Maven Configuration & Dependencies' },
  ];

  const result = files.map(f => {
    const fullPath = path.join(basePath, f.name);
    let content = '';
    try {
      content = fs.readFileSync(fullPath, 'utf8');
    } catch {
      content = '// File content unavailable';
    }
    return { ...f, content };
  });

  res.json({ success: true, files: result });
});

// Mount Vite in development
async function startServer() {
  if (process.env.NODE_ENV !== 'production') {
    const { createServer: createViteServer } = await import('vite');
    const vite = await createViteServer({
      server: { middlewareMode: true },
      appType: 'spa',
    });
    app.use(vite.middlewares);
  } else {
    app.use(express.static(path.join(__dirname, 'dist')));
    app.get('*', (_req, res) => {
      res.sendFile(path.join(__dirname, 'dist', 'index.html'));
    });
  }

  app.listen(PORT, '0.0.0.0', () => {
    console.log(`Loan Application System running on http://localhost:${PORT}`);
  });
}

startServer();
