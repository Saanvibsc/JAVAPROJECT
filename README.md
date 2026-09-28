# User Management System (Stage 1)

A Java-based console application for managing user accounts using
**Core Java, Scanner, JDBC, and MySQL**.

---

## 📋 Summary of What Has Been Done

### 1. User Management System Architecture

The application is implemented as a console-based Core Java project with
separate classes for user management, database connectivity, and database
operations.

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
