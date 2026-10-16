# Loan Application Management System (Stages 1–5)

A complete, production-grade **Core Java Console Application** built strictly using **`Scanner`**, **MySQL with JDBC (`PreparedStatement`)**, **Apache Maven**, and **Jakarta Mail (Java SMTP integration)**.

All five stages specified in the assignment have been unified into a single coherent Java codebase with **zero reliance on TypeScript, JavaScript, React, Node.js, HTML, CSS, or Spring Boot**.

---

## 📋 System Architecture & Stages Overview

```text
com.loanapp/
├── Main.java                       # Unified Scanner console entry point & interactive menus
├── db/
│   └── DatabaseConnection.java     # JDBC connection pool, schema bootstrapper & seed initializer
├── model/
│   ├── User.java                   # User entity (ROLE_USER / ROLE_ADMIN, status, lockout counters)
│   ├── LoanApplication.java        # Loan application model with tenure, EMI, and decision tracking
│   ├── LoanType.java               # Enum for Home, Personal, Education, and Vehicle loans
│   ├── LoanStatus.java             # Enum for Pending, Approved, Rejected, Needs More Info
│   └── AuditLog.java               # Audit entity recording administrative user deletions
├── dao/
│   ├── UserDAO.java                # PreparedStatement CRUD, lockout tracking & transactional delete
│   ├── LoanDAO.java                # PreparedStatement loan submission, queries & approval/rejection
│   └── AuditDAO.java               # PreparedStatement audit log queries
├── service/
│   ├── UserManagementService.java  # Stage 1: Registration, Login (3-attempt lockout), Password Reset
│   ├── AdminManagementService.java # Stage 2: RBAC, 6-criteria Search, View/Edit/Activate/Delete
│   ├── LoanManager.java            # Stage 3: Loan origination, duration handling, review workflows
│   ├── CibilService.java           # Stage 4: PAN validation & simulated CIBIL score (600–900)
│   ├── InterestRateCalculator.java # Stage 4: Tiered rate calculator across loan types
│   ├── EMICalculator.java          # Stage 5: Mathematical EMI, total interest, and total payable
│   ├── LoanApprovalService.java    # Stage 5: Post-approval EMI persistence & decision dispatches
│   └── EmailService.java           # Live JavaMail SMTP dispatching via jakarta.mail
└── util/
    ├── ValidationUtil.java         # Regex validations for PAN, Mobile, Email, Passwords
    ├── CredentialGenerator.java    # Username pattern & cryptographically strong password generator
    └── PasswordUtil.java           # Salted SHA-256 password hashing & verification
```

---

## 🚀 Key Implemented Features

### 1. User Management (Stage 1)
* **Registration**: Validates First Name, Last Name, 10-digit Mobile, valid Email, Location, and strong Password (min 8 chars, 1 uppercase, 1 lowercase, 1 digit, 1 special character). Checks username and email uniqueness.
* **Dual Login**: Supports login using either `Username + Password` or `Email + Password`.
* **Account Lockout Policy**:
  * Tracks failed login attempts in MySQL.
  * Displays remaining attempts after each failure (`Attempts remaining: 2`, `Attempts remaining: 1`).
  * **Locks account after 3 consecutive failed attempts**: Changes status from `Active` to `Inactive`.
  * Locked accounts receive: `Your account has been locked after 3 failed attempts. Please contact support.`
  * Successfully logging in resets `failed_login_attempts` back to `0`.
* **Forgot Password**: Verifies identity by username or email, validates new password against the strong policy, updates MySQL, and dispatches confirmation email.
* **Security**: Passwords are saved using salted cryptographic hashes with backward-compatible verification.

### 2. Admin Management (Stage 2)
* **Direct Admin Authentication**: Only accounts with `ROLE_ADMIN` can access the portal. Admins are strictly exempted from user-level failed attempt lockout.
* **User Search (6 Criteria)**: Search by 1. First Name, 2. Last Name, 3. Email, 4. Mobile, 5. Username, 6. Status (`Active`/`Inactive`).
* **View User Details**: Shows full demographic data, PAN, CIBIL score, account status, and all associated loan applications.
* **Edit User Information**: Prompts admin to update user fields with validation and sends an email notification (`Subject: Account Update Notification`).
* **Activate / Deactivate Accounts**: Toggles user status between `Active` and `Inactive` with instant email notification.
* **ACID Transactional User Deletion with Audit Logging**:
  * Prompts for confirmation and mandatory remarks.
  * Executes inside a **JDBC Transaction** (`conn.setAutoCommit(false)`): deletes user record (with cascading delete of loans) and simultaneously records the action in `audit_log`.
  * Rolls back completely if any step fails.
  * Dispatches an account deletion email notification.

### 3. Loan Management (Stage 3)
* **User-Side Loan Application**:
  * Automatically binds `loggedInUser.getUserId()` (the applicant is never asked to type their user ID).
  * Enforces `loanAmount > 0`, `annualIncome > 0`, and mandatory non-empty `purpose`.
  * Supports loan tenure specified in either **Months** or **Years** (e.g. `60 Months` or `5 Years (60 Months)`).
  * Automatically initializes loan status to `Pending`.
* **Borrower Loan Tracking**: View all submitted applications with real-time status badges, tenure, approved EMI, and admin feedback.
* **Admin Loan Decisions**:
  * List all loans in the system.
  * View comprehensive loan details with applicant profile.
  * Approve loan, Reject loan with mandatory reason, or Request more information.

### 4. PAN and CIBIL Integration (Stage 4)
* **PAN Validation**: Strict regular expression validation (`^[A-Z]{5}[0-9]{4}[A-Z]$`).
* **Simulated CIBIL Score Generation**: Generates scores strictly between **600 and 900** (`600 + random.nextInt(301)`).
* **Tiered Interest Rate Table**:
  * **Home Loan**: 800+ → 6.5%, 750–799 → 7.0%, 700–749 → 7.5%, 650–699 → 8.0%, <650 → 8.5%
  * **Personal Loan**: 800+ → 9.0%, 750–799 → 9.5%, 700–749 → 10.0%, 650–699 → 11.0%, <650 → 12.0%
  * **Education Loan**: 800+ → 4.0%, 750–799 → 4.5%, 700–749 → 5.0%, 650–699 → 5.5%, <650 → 6.0%
  * **Vehicle Loan**: 800+ → 7.0%, 750–799 → 7.5%, 700–749 → 8.0%, 650–699 → 8.5%, <650 → 9.0%
* **Automatic Credential Generation**:
  * **Username Pattern**: First 3 chars of First Name + First 3 chars of Last Name + Last 4 digits of Mobile.
  * **Password Generator**: Compliant 8–10 character password with uppercase, lowercase, digit, and special symbol.
  * **Delivery**: Generated credentials and CIBIL score delivered directly to the user's email via SMTP.

### 5. EMI Calculation and Post-Approval Servicing (Stage 5)
* **Mathematical Amortization Formula**:
  $$\text{EMI} = \frac{P \times r \times (1 + r)^n}{(1 + r)^n - 1}$$
  where $P$ = Loan Amount, $r$ = Monthly interest rate ($R / 12 / 100$), $n$ = Tenure in months.
* **Totals Calculated**:
  * $\text{Total Payable} = \text{EMI} \times n$
  * $\text{Total Interest} = \text{Total Payable} - P$
* **Database Storage**: Monthly EMI, Total Interest, Total Payable, and Decision Date are persisted to MySQL upon admin approval.
* **Email Notification**: Automated approval email dispatched via Java SMTP containing full amortization breakdown.
* **Rejection Notification**: Automated rejection email dispatched with mandatory reason recorded.

---

## 🗄️ MySQL Database Setup

The complete SQL schema and initial seed data are provided in `sql/loan_system_complete.sql`.

```bash
# Import schema and seed data into MySQL
mysql -u root -p < sql/loan_system_complete.sql
```

### Database Tables:
1. `users`: Stores user identity, hashed passwords, roles (`ROLE_USER`/`ROLE_ADMIN`), status (`Active`/`Inactive`), PAN, CIBIL score, and failed login attempt counters.
2. `loan_applications`: Stores loan applications, amounts in INR (`Rs.`), tenure, interest rate, annual income, purpose, status (`Pending`/`Approved`/`Rejected`/`Needs More Info`), calculated monthly EMI, total interest, and total payable.
3. `audit_log`: Records admin user deletions with timestamps, admin username, deleted user details, and remarks.

---

## 🔑 Pre-Configured Test Accounts (For Viva / Demo)

| Account Type | Username | Password | Email | Role | Status / Notes |
|---|---|---|---|---|---|
| **System Administrator** | `admin` | `Admin@123` | `admin@loansystem.com` | `ROLE_ADMIN` | Active, exempt from lockout |
| **Secondary Administrator** | `riya_admin` | `Riya@2026` | `riya.admin@loansystem.com` | `ROLE_ADMIN` | Active |
| **Regular Borrower** | `aarav_s` | `User@123` | `aarav.sharma@example.com` | `ROLE_USER` | Active (CIBIL: 780, 1 Approved, 1 Pending Loan) |
| **Regular Borrower** | `priya_p` | `User@123` | `priya.patel@example.com` | `ROLE_USER` | Active (1 failed attempt recorded) |
| **Locked Borrower** | `rohan_d` | `User@123` | `rohan.deshmukh@example.com` | `ROLE_USER` | **Inactive (Locked after 3 failed attempts)** |
| **Borrower (No Loans)** | `sneha_k` | `User@123` | `sneha.kulkarni@example.com` | `ROLE_USER` | Active |

---

## 🛠️ How to Compile & Run

### Method 1: Using Apache Maven (Recommended)

```bash
# 1. Compile the project
mvn clean compile

# 2. Run all unit and integration tests (19 tests)
mvn test

# 3. Run the console application directly
mvn exec:java -Dexec.mainClass="com.loanapp.Main"
```

### Method 2: Direct Java Compilation (`javac` / `java`)

All required dependency JARs (`mysql-connector-j-8.3.0.jar`, `jakarta.mail-2.0.1.jar`, etc.) are included in `lib/`:

```bash
# Compile
javac -cp "lib/*:src/main/java" -d target/classes src/main/java/com/loanapp/**/*.java src/main/java/com/loanapp/*.java

# Run
java -cp "target/classes:lib/*" com.loanapp.Main
```

---

## ✉️ SMTP Email Configuration

The system uses standard environment variables for live SMTP dispatching:

```bash
export SMTP_HOST="smtp.gmail.com"
export SMTP_PORT="587"
export SMTP_USERNAME="your_email@gmail.com"
export SMTP_PASSWORD="your_16_digit_app_password"
export SMTP_FROM="your_email@gmail.com"
```

* If environment variables are not set, the application continues to run smoothly, formatting and logging the exact dispatch payload and headers so evaluations can be conducted without mandatory internet SMTP credentials.

---

## 🧪 Automated Test Suite Verification

Run the comprehensive JUnit 5 test suite:

```bash
mvn test
```

### Test Results Summary:
* `ValidationUtilTest`: **5/5 Passed** (PAN format, 10-digit mobile, email format, password complexity)
* `CredentialGeneratorTest`: **2/2 Passed** (Username generation rules, password randomness)
* `CibilAndInterestTest`: **5/5 Passed** (Score range 600–900, interest matrices across 4 loan types)
* `EMICalculatorTest`: **3/3 Passed** (Amortization math, zero-interest edge cases, boundary validation)
* `DatabaseIntegrationTest`: **4/4 Passed** (JDBC connection, 3-attempt lockout, loan approval with EMI persistence, transactional user deletion with audit log)

**Total: 19 Tests Run, 19 Passed, 0 Failures, 0 Errors.**
