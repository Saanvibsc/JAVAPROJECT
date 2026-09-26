# Loan Application Management System (Stage 3)

A Java-based console application for managing loan applications, adapted to the **Indian Banking System (INR / ₹)** and built strictly using **Scanner**, **JDBC**, **MySQL**, and **SMTP/JavaMail**.

---

## 📋 Summary of What Has Been Done

### 1. Minimal 5-File Architecture (`LoanApplicationSystem/`)
In strict compliance with Stage 3 specifications, the Java codebase was consolidated into the minimal recommended structure with no unnecessary enterprise boilerplate:

```text
LoanApplicationSystem/
├── Main.java               # Console Scanner entry point & menus (Main, User, Admin)
├── User.java               # User model (userId, fullName, email, mobileNumber, password, role)
├── LoanApplication.java     # Stage 3 entity model with all 11 required fields
├── Database.java           # Pure JDBC data layer using PreparedStatement
├── LoanManager.java        # Loan application workflows, validation, & SMTP email dispatch
│
├── database.sql            # Clean MySQL schema with users & loan_applications tables
└── pom.xml                 # Maven POM configuration with MySQL Connector & Jakarta Mail
```

---

### 2. Indian Banking System Adaptations
* **Currency**: Formatted in Indian Rupees (`Rs.`, `₹`) with Indian numbering format (e.g., `Rs. 5,00,000.00`).
* **Automated Interest Rates**:
  * **Home Loan**: 7.5% p.a.
  * **Education Loan**: 8.0% p.a.
  * **Car Loan**: 8.5% p.a.
  * **Personal Loan**: 10.5% p.a.
  * *Other*: 9.0% p.a.

---

### 3. User-Side Loan Application Flow
* **Automatic User Binding**: Automatically uses `loggedInUser.getUserId()` (the applicant is never asked to type their own user ID).
* **Automatic Status Assignment**: Sets `status = "Pending"` upon submission.
* **Form Validation**:
  * `loanAmount > 0`
  * `loanDuration > 0` (months)
  * `annualIncome > 0`
  * Required `purpose` field cannot be empty.
  * Safe `Scanner` token and newline buffer management.
* **Database Persistence**: Saved directly into the `loan_applications` table in MySQL using JDBC `PreparedStatement`.

---

### 4. Admin-Side Loan Review & Decisions
* **Menu Options**:
  1. `View Loan Applications` (lists Loan ID, Full Name, Loan Amount in INR, Loan Type, Application Date, and Status)
  2. `View Loan Details` (displays user contact info, annual income, tenure, and purpose)
  3. `Approve Loan`
  4. `Reject Loan`
  5. `Logout`
* **Loan Approval**:
  * Updates status to `Approved` in MySQL via JDBC.
  * Sends an approval email to the applicant's registered email address:
    * **Subject**: `Your Loan Application has been Approved!`
    * **Body**: `Dear [User], your loan application for [loan amount] has been approved. Please check your dashboard for further details.`
* **Loan Rejection**:
  * Prompts the admin for a required rejection reason.
  * Updates status to `Rejected` and records `rejection_reason` in MySQL.
  * Sends a rejection email to the applicant's registered email address:
    * **Subject**: `Your Loan Application has been Rejected`
    * **Body**: `Dear [User], we regret to inform you that your loan application has been rejected. Reason: [reason]. Please contact support for more details.`

---

### 5. Email Dispatching (Jakarta Mail / SMTP)
* Real SMTP email sending via JavaMail (`jakarta.mail`).
* Configurable via standard environment variables:
  * `SMTP_HOST` (defaults to `smtp.gmail.com`)
  * `SMTP_PORT` (defaults to `587`)
  * `SMTP_USERNAME`
  * `SMTP_PASSWORD`
  * `SMTP_FROM`
* If SMTP credentials are not configured, details are gracefully logged to the console without crashing.

---

### 6. Interactive Web Console & Source Code Inspector
To support browser-based testing and viva evaluation in AI Studio:
* **Interactive Terminal Emulator**: Allows full testing of the console workflow (login, loan application, admin approval/rejection) right in the web UI.
* **Loan Applications Table**: Real-time view of records stored in the database with status badges and quick-action modals.
* **Source Code Viewer**: One-click tabbed viewing and copying of all 5 Java files, `database.sql`, and `pom.xml`.
* **SMTP Notification Stream**: Live feed showing simulated and live email transmissions.

---

## 🚀 How to Run the Java Console Application

### Prerequisites
* Java JDK 11 or higher
* MySQL Server (optional; in-memory fallback enabled if offline)
* Apache Maven

### 1. Database Setup (MySQL)
Run the script in MySQL:
```bash
mysql -u root -p < LoanApplicationSystem/database.sql
```

### 2. Build and Execute with Maven
```bash
cd LoanApplicationSystem
mvn clean compile
mvn exec:java -Dexec.mainClass="Main"
```

### 3. Or Compile Directly with javac
```bash
cd LoanApplicationSystem
javac -cp "lib/*:." *.java
java -cp "lib/*:." Main
```

---

## 👥 Default Demo Credentials

| Role | Name | Email | Password |
|---|---|---|---|
| **Admin** | System Administrator | `admin@loanapp.com` | `admin123` |
| **User** | Rahul Sharma | `rahul.sharma@example.com` | `user123` |
| **User** | Priya Patel | `priya.patel@example.com` | `user123` |
