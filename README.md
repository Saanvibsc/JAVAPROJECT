# Loan Application System — Stage 2: Admin Management System (Core Java + JDBC + MySQL)

Built strictly for **Stage 2** of the assignment in **Core Java** for **VS Code** using `Scanner`, **MySQL + JDBC (`PreparedStatement`)**, Role-Based Access Control (`ROLE_ADMIN` / `ROLE_USER`), transactional deletion logging (`audit_log`), and automated email notifications (`Subject: Account Update Notification`).

## 1. Minimal Folder Structure (5 Java Files)

```text
JAVAPROJECT/
├── .vscode/
│   └── settings.json               # VS Code classpath configuration (points to lib/*.jar)
├── lib/
│   └── mysql-connector-j-8.3.0.jar # MySQL JDBC Driver JAR
├── bin/                            # Compiled .class files output folder
├── sql/
│   └── stage2_schema_and_seed.sql  # MySQL database, tables & sample data
└── src/
    ├── DBConnection.java           # 1. JDBC Connection utility
    ├── User.java                   # 2. User POJO model (ROLE_ADMIN / ROLE_USER)
    ├── EmailNotificationService.java # 3. Email notification service (Subject: Account Update Notification)
    ├── AdminDAO.java               # 4. All JDBC PreparedStatement queries & audit_log transaction
    └── AdminManagementApp.java     # 5. Main Console App using Scanner, RBAC & Regex validation
```

## 2. Windows Compile & Run Commands

```bat
mkdir bin
javac -cp ".;lib/*" -d bin src/*.java
java -cp "bin;lib/*" AdminManagementApp
```
