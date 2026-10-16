stage3-saanvi
# Loan Application Management System (Stage 3)

A Java-based console application for managing loan applications, adapted to the **Indian Banking System (INR / ₹)** and built strictly using **Scanner**, **JDBC**, **MySQL**, and **SMTP/JavaMail**.

 stage2-riya
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

# User Management System (Stage 1)

A Java-based console application for managing user accounts using
**Core Java, Scanner, JDBC, and MySQL**.
 main

---

## 📋 Summary of What Has Been Done
 stage3-saanvi
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


```text
UserManagementSystem/
├── UserManagementSystem.java   # Console Scanner entry point & menus
├── User.java                   # User model
├── UserDAO.java                # JDBC database operations
├── DBConnection.java           # MySQL database connection
└── database.sql                # MySQL database and table structure
```
### 2. User Registration
The system allows users to register with the following details:
- First Name
- Last Name
- Mobile Number
- Email Address
- Location
- Username
- Password
- Status
- Last Modified Date
During registration:
- Required fields are validated.
- Mobile number must contain exactly 10 digits.
- Email format is validated.
- Password must be at least 8 characters long.
- Password must contain:
  - One uppercase letter
  - One lowercase letter
  - One digit
  - One special character
- Username uniqueness is checked.
- User status is automatically set to Active.
- Registration date/time is stored automatically.
- User details are stored in MySQL using JDBC.
### 3. User Login
The system allows users to log in using:
- Username + Password
- OR
- Email + Password
Login functionality includes:
- Empty field validation.
- Checking whether the user exists.
- Checking account status.
- Password verification.
- Welcome message after successful login.
Example:
Welcome Amruta Patil
### 4. Post-Login Menu
After successful login, the system displays a welcome message using the
user's first and last name.
Welcome, FirstName LastName

1. Logout

The user can select Logout to end the current session and return to
the main menu.
### 5. Forgot Password
The system provides a password reset option using:
- Username
- OR
- Email
The system:
1. Accepts the username or email.
2. Checks whether the user exists.
3. Accepts a new password.
4. Validates the new password using the same password policy.
5. Updates the password in the MySQL database.
6. Updates the last modified date/time.
### 6. Account Lockout
The system protects user accounts by tracking failed login attempts.
The functionality includes:
- Failed login attempts are stored in MySQL.
- The counter increases after an unsuccessful login.
- Users are allowed a maximum of 3 consecutive failed attempts.
- After the third failed attempt, the account is locked.
- The user's status changes from Active to Inactive.
- A locked user cannot log in.
- The account must be manually unlocked by an administrator.
### 7. Failed Login Feedback
The application informs users about their remaining login attempts.
Example:
Invalid password.
Attempts remaining: 2

After three failed attempts:
Your account has been locked after 3 failed attempts.
Please contact support.

When a user successfully logs in, the failed_attempts value is reset to:
0
### Database Structure
The application uses MySQL with a users table.
CREATE DATABASE user_management;

USE user_management;

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    fname VARCHAR(50) NOT NULL,
    lname VARCHAR(50) NOT NULL,
    mobile VARCHAR(10) NOT NULL,
    email VARCHAR(100) NOT NULL,
    location VARCHAR(100),
    lastmodifieddate DATETIME,
    status VARCHAR(20) DEFAULT 'Active',
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    failed_attempts INT DEFAULT 0
);

The failed_attempts and status fields are used to implement account
lockout and account state management.     Stage 1
🔌 Technologies Used
- Java - Core application development
- Scanner - Console-based user input
- JDBC - Java Database Connectivity
- MySQL - Database management
- MySQL Connector/J - JDBC driver for MySQL
- PreparedStatement - Database queries and updates
🚀 How to Run the Application
Prerequisites
- Java JDK
- MySQL Server
- MySQL Workbench
- Spring Tools / Eclipse
- MySQL Connector/J JDBC Driver
1. Database Setup
Open MySQL Workbench and create the database:
CREATE DATABASE user_management;

USE user_management;

Then create the users table using the SQL schema provided above.
2. Configure MySQL Connection
Update DBConnection.java with your MySQL credentials:
private static final String URL =
        "jdbc:mysql://localhost:3306/user_management";

private static final String USER = "root";

private static final String PASSWORD = "YOUR_PASSWORD";

3. Add MySQL JDBC Driver
Add the MySQL Connector/J .jar file to the Java project's build path.
Example:
mysql-connector-j-26.7.0.jar

4. Run the Application
Run:
UserManagementSystem.java

as:
Run As → Java Application

🖥️ Application Menu
When the application starts, the console displays:
===== USER MANAGEMENT SYSTEM =====

1. Register
2. Login
3. Forgot Password
4. Exit

Enter your choice:

Registration Example
===== USER REGISTRATION =====

First Name: Amruta
Last Name: Patil
Mobile Number: 9876543210
Email: amruta@gmail.com
Location: Pune
Username: amruta123
Password: Amruta@123

Registration successful!
Your account is Active.

Login Example
===== LOGIN =====

Enter Username or Email: amruta123
Enter Password: Amruta@123

Welcome Amruta Patil

Account Lock Example
Invalid password.
Attempts remaining: 2

Invalid password.
Attempts remaining: 1

Your account has been locked after 3 failed attempts.
Please contact support.

📁 Project Structure
UserManagementSystem/
│
├── src/
│   └── usermanagement/
│       ├── UserManagementSystem.java
│       ├── User.java
│       ├── UserDAO.java
│       └── DBConnection.java
│
└── database.sql

🔐 Security & Validation Features
- Mandatory field validation
- Email validation
- 10-digit mobile number validation
- Strong password validation
- Unique username validation
- Account status checking
- Failed login attempt tracking
- Automatic account lock after 3 failed attempts
- Failed attempt reset after successful login
- JDBC PreparedStatement for database operations
main
 main
