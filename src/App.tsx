import React, { useState, useRef, useEffect } from 'react';
import {
  Terminal,
  Code2,
  Database,
  Check,
  Copy,
  RotateCcw,
  Send,
  BookOpen,
  FolderTree,
  Mail,
  Download,
} from 'lucide-react';

interface JavaFileItem {
  filename: string;
  path: string;
  role: string;
  vivaSummary: string;
  content: string;
}

interface DbUser {
  userId: number;
  firstName: string;
  lastName: string;
  username: string;
  password: string;
  email: string;
  mobile: string;
  userRole: 'ROLE_ADMIN' | 'ROLE_USER';
  status: 'Active' | 'Inactive';
  failedLoginAttempts: number;
}

interface DbLoan {
  loanId: number;
  userId: number;
  loanType: string;
  loanAmount: number;
  interestRate: number;
  tenureMonths: number;
  loanStatus: 'Pending' | 'Approved' | 'Rejected';
  appliedDate: string;
}

interface DbAuditLog {
  auditId: number;
  adminUsername: string;
  deletedUserId: number;
  deletedUsername: string;
  deletedFullName: string;
  deletedEmail: string;
  actionType: string;
  remarks: string;
  actionTimestamp: string;
}

interface EmailRecord {
  id: number;
  toEmail: string;
  subject: string;
  body: string;
  actionType: 'ACTIVATION' | 'DEACTIVATION' | 'DELETION' | 'EDIT';
  timestamp: string;
}

const INITIAL_USERS: DbUser[] = [
  {
    userId: 1,
    firstName: 'System',
    lastName: 'Administrator',
    username: 'admin',
    password: 'Admin@123',
    email: 'admin@loansystem.com',
    mobile: '9876543210',
    userRole: 'ROLE_ADMIN',
    status: 'Active',
    failedLoginAttempts: 0,
  },
  {
    userId: 2,
    firstName: 'Riya',
    lastName: 'Pillai',
    username: 'riya_admin',
    password: 'Riya@2026',
    email: 'riya.admin@loansystem.com',
    mobile: '9811122233',
    userRole: 'ROLE_ADMIN',
    status: 'Active',
    failedLoginAttempts: 0,
  },
  {
    userId: 3,
    firstName: 'Aarav',
    lastName: 'Sharma',
    username: 'aarav_s',
    password: 'User@123',
    email: 'aarav.sharma@example.com',
    mobile: '9820112233',
    userRole: 'ROLE_USER',
    status: 'Active',
    failedLoginAttempts: 0,
  },
  {
    userId: 4,
    firstName: 'Priya',
    lastName: 'Patel',
    username: 'priya_p',
    password: 'User@123',
    email: 'priya.patel@example.com',
    mobile: '9820445566',
    userRole: 'ROLE_USER',
    status: 'Active',
    failedLoginAttempts: 1,
  },
  {
    userId: 5,
    firstName: 'Rohan',
    lastName: 'Deshmukh',
    username: 'rohan_d',
    password: 'User@123',
    email: 'rohan.deshmukh@example.com',
    mobile: '9820778899',
    userRole: 'ROLE_USER',
    status: 'Inactive',
    failedLoginAttempts: 3,
  },
  {
    userId: 6,
    firstName: 'Sneha',
    lastName: 'Kulkarni',
    username: 'sneha_k',
    password: 'User@123',
    email: 'sneha.kulkarni@example.com',
    mobile: '9820990011',
    userRole: 'ROLE_USER',
    status: 'Active',
    failedLoginAttempts: 0,
  },
  {
    userId: 7,
    firstName: 'Vikram',
    lastName: 'Verma',
    username: 'vikram_v',
    password: 'User@123',
    email: 'vikram.verma@example.com',
    mobile: '9820334455',
    userRole: 'ROLE_USER',
    status: 'Inactive',
    failedLoginAttempts: 0,
  },
];

const INITIAL_LOANS: DbLoan[] = [
  {
    loanId: 101,
    userId: 3,
    loanType: 'Home Loan',
    loanAmount: 4500000.0,
    interestRate: 8.5,
    tenureMonths: 240,
    loanStatus: 'Approved',
    appliedDate: '2026-01-15',
  },
  {
    loanId: 102,
    userId: 3,
    loanType: 'Car Loan',
    loanAmount: 850000.0,
    interestRate: 9.25,
    tenureMonths: 60,
    loanStatus: 'Pending',
    appliedDate: '2026-02-20',
  },
  {
    loanId: 103,
    userId: 4,
    loanType: 'Education Loan',
    loanAmount: 1200000.0,
    interestRate: 7.8,
    tenureMonths: 84,
    loanStatus: 'Approved',
    appliedDate: '2026-02-10',
  },
  {
    loanId: 104,
    userId: 5,
    loanType: 'Personal Loan',
    loanAmount: 300000.0,
    interestRate: 11.5,
    tenureMonths: 36,
    loanStatus: 'Rejected',
    appliedDate: '2026-01-28',
  },
];

const JAVA_FILES: JavaFileItem[] = [
  {
    filename: 'DBConnection.java',
    path: 'src/DBConnection.java',
    role: '1. JDBC Connection Utility',
    vivaSummary: 'Loads com.mysql.cj.jdbc.Driver and provides DriverManager.getConnection() to loan_system_db.',
    content: `import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection.java
 * Provides a centralized JDBC connection to the MySQL database.
 */
public class DBConnection {

    // Update DB_USER and DB_PASSWORD according to your local MySQL setup
    private static final String DB_URL = "jdbc:mysql://localhost:3306/loan_system_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[ERROR] MySQL JDBC Driver not found. Add mysql-connector-j JAR to lib/ folder.");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}`,
  },
  {
    filename: 'User.java',
    path: 'src/User.java',
    role: '2. User Model POJO',
    vivaSummary: 'Encapsulates user attributes including userRole (ROLE_ADMIN / ROLE_USER) and status (Active / Inactive).',
    content: `/**
 * User.java
 * Model class representing a user record from the MySQL 'users' table.
 */
public class User {

    private int userId;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private String mobile;
    private String userRole; // ROLE_ADMIN or ROLE_USER
    private String status;   // Active or Inactive
    private int failedLoginAttempts;

    public User() {}

    public User(int userId, String firstName, String lastName, String username,
                String password, String email, String mobile, String userRole,
                String status, int failedLoginAttempts) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.mobile = mobile;
        this.userRole = userRole;
        this.status = status;
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return firstName + " " + lastName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(int failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }
}`,
  },
  {
    filename: 'EmailNotificationService.java',
    path: 'src/EmailNotificationService.java',
    role: '3. Email Notification Service',
    vivaSummary: 'Sends account-update emails with Subject "Account Update Notification" and exact assignment wording.',
    content: `/**
 * EmailNotificationService.java
 * Sends user email notifications after Admin operations with exact PDF wording.
 * Subject: Account Update Notification
 */
public class EmailNotificationService {

    public static final String SUBJECT = "Account Update Notification";

    public static void sendActivationEmail(String toEmail, String firstName) {
        String body = "Dear " + firstName + ", your account has been successfully activated by the admin. You can now log in to the system.";
        sendEmail(toEmail, SUBJECT, body);
    }

    public static void sendDeactivationEmail(String toEmail, String firstName) {
        String body = "Dear " + firstName + ", your account has been deactivated. Please contact support for more information.";
        sendEmail(toEmail, SUBJECT, body);
    }

    public static void sendDeletionEmail(String toEmail, String firstName) {
        String body = "Dear " + firstName + ", your account has been deleted from the system. If this was a mistake, please contact support immediately.";
        sendEmail(toEmail, SUBJECT, body);
    }

    public static void sendEditEmail(String toEmail, String firstName) {
        String body = "Dear " + firstName + ", your account details have been updated. If you did not request this change, please contact support immediately.";
        sendEmail(toEmail, SUBJECT, body);
    }

    private static void sendEmail(String toEmail, String subject, String body) {
        System.out.println("\\n+--------------------------------------------------------------------------+");
        System.out.println("|                        EMAIL NOTIFICATION SENT                           |");
        System.out.println("+--------------------------------------------------------------------------+");
        System.out.println("  To      : " + toEmail);
        System.out.println("  Subject : " + subject);
        System.out.println("  Message : " + body);
        System.out.println("+--------------------------------------------------------------------------+\\n");
    }
}`,
  },
  {
    filename: 'AdminDAO.java',
    path: 'src/AdminDAO.java',
    role: '4. JDBC PreparedStatement DAO',
    vivaSummary: 'Performs all PreparedStatement queries and transactional audit_log recording on user deletion.',
    content: `import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * AdminDAO.java
 * Handles all JDBC PreparedStatement operations, transactional audit_log recording,
 * and user email notifications for Stage 2 Admin Management System.
 */
public class AdminDAO {

    private static final String EMAIL_SUBJECT = "Account Update Notification";

    public User authenticateAdmin(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("[AUTH ERROR] Invalid username or password.");
                    return null;
                }

                String role = rs.getString("user_role");
                String dbPassword = rs.getString("password");
                String status = rs.getString("status");

                if (!"ROLE_ADMIN".equalsIgnoreCase(role)) {
                    System.out.println("[ACCESS DENIED] Regular users (ROLE_USER) are not allowed to perform admin actions.");
                    return null;
                }

                if (!"Active".equalsIgnoreCase(status)) {
                    System.out.println("[AUTH ERROR] This admin account is currently inactive.");
                    return null;
                }

                if (!dbPassword.equals(password)) {
                    System.out.println("[AUTH ERROR] Invalid admin credentials. (Admin accounts are exempt from failed login lockouts).");
                    return null;
                }

                return mapRowToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Login failed: " + e.getMessage());
            return null;
        }
    }

    public List<User> searchUsers(int searchOption, String searchValue) {
        List<User> users = new ArrayList<>();
        String column;
        boolean exactMatch = false;

        switch (searchOption) {
            case 1: column = "first_name"; break;
            case 2: column = "last_name"; break;
            case 3: column = "email"; break;
            case 4: column = "mobile"; break;
            case 5: column = "username"; break;
            case 6:
                column = "status";
                exactMatch = true;
                break;
            default:
                return users;
        }

        String sql = exactMatch
                ? "SELECT * FROM users WHERE user_role = 'ROLE_USER' AND " + column + " = ? ORDER BY user_id ASC"
                : "SELECT * FROM users WHERE user_role = 'ROLE_USER' AND " + column + " LIKE ? ORDER BY user_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (exactMatch) {
                String normalizedStatus = searchValue.trim().equalsIgnoreCase("Active") ? "Active" : "Inactive";
                ps.setString(1, normalizedStatus);
            } else {
                ps.setString(1, "%" + searchValue.trim() + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Search failed: " + e.getMessage());
        }
        return users;
    }

    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ? AND user_role = 'ROLE_USER'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Could not fetch user: " + e.getMessage());
        }
        return null;
    }

    public void displayLoanApplications(int userId) {
        String sql = "SELECT loan_id, loan_type, loan_amount, interest_rate, tenure_months, loan_status, applied_date "
                + "FROM loan_applications WHERE user_id = ? ORDER BY applied_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                boolean hasLoans = false;
                System.out.println("\\n  --- Loan Applications ---");
                while (rs.next()) {
                    if (!hasLoans) {
                        System.out.printf("  %-8s %-18s %-14s %-10s %-10s %-12s %-12s%n",
                                "Loan ID", "Loan Type", "Amount (INR)", "Rate (%)", "Tenure(M)", "Status", "Applied On");
                        System.out.println("  ----------------------------------------------------------------------------------------");
                        hasLoans = true;
                    }
                    System.out.printf("  %-8d %-18s %-14.2f %-10.2f %-10d %-12s %-12s%n",
                            rs.getInt("loan_id"),
                            rs.getString("loan_type"),
                            rs.getDouble("loan_amount"),
                            rs.getDouble("interest_rate"),
                            rs.getInt("tenure_months"),
                            rs.getString("loan_status"),
                            rs.getDate("applied_date").toString());
                }

                if (!hasLoans) {
                    System.out.println("  No loan applications found for this user.");
                }
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Failed to retrieve loan applications: " + e.getMessage());
        }
    }

    public boolean isEmailTakenByOther(String email, int currentUserId) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ? AND user_id <> ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, currentUserId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Duplicate check failed: " + e.getMessage());
        }
        return false;
    }

    public boolean updateUserDetails(User user) {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ?, mobile = ? "
                + "WHERE user_id = ? AND user_role = 'ROLE_USER'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getMobile());
            ps.setInt(5, user.getUserId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Could not update user details: " + e.getMessage());
            return false;
        }
    }

    public boolean updateUserStatus(int userId, String newStatus) {
        String sql = "UPDATE users SET status = ?, failed_login_attempts = CASE WHEN ? = 'Active' THEN 0 ELSE failed_login_attempts END "
                + "WHERE user_id = ? AND user_role = 'ROLE_USER'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setString(2, newStatus);
            ps.setInt(3, userId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Status update failed: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUserWithAudit(User userToDelete, String adminUsername, String remarks) {
        String insertAuditSql = "INSERT INTO audit_log (admin_username, deleted_user_id, deleted_username, "
                + "deleted_full_name, deleted_email, action_type, remarks) VALUES (?, ?, ?, ?, ?, 'DELETE_USER', ?)";
        String deleteUserSql = "DELETE FROM users WHERE user_id = ? AND user_role = 'ROLE_USER'";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement auditPs = conn.prepareStatement(insertAuditSql);
                 PreparedStatement deletePs = conn.prepareStatement(deleteUserSql)) {

                auditPs.setString(1, adminUsername);
                auditPs.setInt(2, userToDelete.getUserId());
                auditPs.setString(3, userToDelete.getUsername());
                auditPs.setString(4, userToDelete.getFullName());
                auditPs.setString(5, userToDelete.getEmail());
                auditPs.setString(6, remarks);
                auditPs.executeUpdate();

                deletePs.setInt(1, userToDelete.getUserId());
                int rowsDeleted = deletePs.executeUpdate();

                if (rowsDeleted > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { /* ignore */ }
            }
            System.err.println("[DB ERROR] Delete user failed: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) { /* ignore */ }
            }
        }
    }

    public void sendEmailNotification(String toEmail, String firstName, String actionType) {
        String body;
        switch (actionType) {
            case "ACTIVATION":
                body = "Dear " + firstName + ", your account has been successfully activated by the admin. You can now log in to the system.";
                break;
            case "DEACTIVATION":
                body = "Dear " + firstName + ", your account has been deactivated. Please contact support for more information.";
                break;
            case "DELETION":
                body = "Dear " + firstName + ", your account has been deleted from the system. If this was a mistake, please contact support immediately.";
                break;
            default:
                body = "Dear " + firstName + ", your account details have been updated. If you did not request this change, please contact support immediately.";
                break;
        }

        System.out.println("\\n+--------------------------------------------------------------------------+");
        System.out.println("|                        EMAIL NOTIFICATION SENT                           |");
        System.out.println("+--------------------------------------------------------------------------+");
        System.out.println("  To      : " + toEmail);
        System.out.println("  Subject : " + EMAIL_SUBJECT);
        System.out.println("  Message : " + body);
        System.out.println("+--------------------------------------------------------------------------+\\n");
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getString("mobile"),
                rs.getString("user_role"),
                rs.getString("status"),
                rs.getInt("failed_login_attempts")
        );
    }
}`,
  },
  {
    filename: 'AdminManagementApp.java',
    path: 'src/AdminManagementApp.java',
    role: '4. Main Console Class (Scanner)',
    vivaSummary: 'Entry point handling Direct Admin Login, 7-option Dashboard Menu, and inline Regex validation.',
    content: `import java.util.List;
import java.util.Scanner;

/**
 * AdminManagementApp.java
 * Stage 2: Console-Based Admin Management System using Scanner, Validation, and JDBC.
 */
public class AdminManagementApp {

    private static final AdminDAO adminDAO = new AdminDAO();
    private static final Scanner scanner = new Scanner(System.in);
    private static User loggedInAdmin = null;

    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println("             LOAN APPLICATION SYSTEM - STAGE 2: ADMIN PORTAL              ");
        System.out.println("==========================================================================");

        while (true) {
            if (loggedInAdmin == null) {
                if (!performAdminLogin()) {
                    System.out.print("\\nWould you like to try logging in again? (yes/no): ");
                    String retry = scanner.nextLine().trim();
                    if (!retry.equalsIgnoreCase("yes") && !retry.equalsIgnoreCase("y")) {
                        System.out.println("Exiting system. Goodbye!");
                        break;
                    }
                }
            } else {
                showAdminDashboard();
            }
        }
        scanner.close();
    }

    private static boolean performAdminLogin() {
        System.out.println("\\n--- ADMIN DIRECT LOGIN ---");
        System.out.print("Enter Admin Username : ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter Admin Password : ");
        String password = scanner.nextLine().trim();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("[VALIDATION ERROR] Username and password cannot be empty.");
            return false;
        }

        User admin = adminDAO.authenticateAdmin(username, password);
        if (admin != null) {
            loggedInAdmin = admin;
            System.out.println("\\n[SUCCESS] Welcome, " + loggedInAdmin.getFullName()
                    + " (" + loggedInAdmin.getUserRole() + ")!");
            return true;
        }
        return false;
    }

    private static void showAdminDashboard() {
        if (!isAuthorizedAdmin()) return;

        System.out.println("\\n==========================================================================");
        System.out.println("                             ADMIN DASHBOARD                              ");
        System.out.println("==========================================================================");
        System.out.println("  1. Search Users");
        System.out.println("  2. View User Details");
        System.out.println("  3. Edit User Information");
        System.out.println("  4. Activate User");
        System.out.println("  5. Deactivate User");
        System.out.println("  6. Delete User");
        System.out.println("  7. Logout");
        System.out.println("==========================================================================");
        System.out.print("Select an option (1-7): ");

        int choice = readIntInput();
        switch (choice) {
            case 1: handleSearchUsers(); break;
            case 2: handleViewUserDetails(); break;
            case 3: handleEditUser(); break;
            case 4: handleChangeStatus("Active"); break;
            case 5: handleChangeStatus("Inactive"); break;
            case 6: handleDeleteUser(); break;
            case 7:
                System.out.println("[LOGOUT] Admin " + loggedInAdmin.getUsername() + " logged out successfully.");
                loggedInAdmin = null;
                break;
            default:
                System.out.println("[VALIDATION ERROR] Invalid choice. Please enter a number between 1 and 7.");
        }
    }

    private static void handleSearchUsers() {
        if (!isAuthorizedAdmin()) return;

        System.out.println("\\n--- SEARCH USERS ---");
        System.out.println("Search By: 1.First Name  2.Last Name  3.Email  4.Mobile  5.Username  6.Status");
        System.out.print("Enter search criterion (1-6): ");

        int criterion = readIntInput();
        if (criterion < 1 || criterion > 6) {
            System.out.println("[VALIDATION ERROR] Please choose a valid search option (1-6).");
            return;
        }

        System.out.print("Enter search value: ");
        String value = scanner.nextLine().trim();
        if (value.isEmpty()) {
            System.out.println("[VALIDATION ERROR] Search value cannot be empty.");
            return;
        }

        if (criterion == 6 && !value.equalsIgnoreCase("Active") && !value.equalsIgnoreCase("Inactive")) {
            System.out.println("[VALIDATION ERROR] Status must be 'Active' or 'Inactive'.");
            return;
        }

        List<User> results = adminDAO.searchUsers(criterion, value);
        if (results.isEmpty()) {
            System.out.println("[INFO] No matching users found.");
            return;
        }

        System.out.println("\\n------------------------------------------------------------------------------------------");
        System.out.printf("%-6s %-18s %-14s %-28s %-13s %-10s%n",
                "ID", "Full Name", "Username", "Email", "Mobile", "Status");
        System.out.println("------------------------------------------------------------------------------------------");
        for (User u : results) {
            System.out.printf("%-6d %-18s %-14s %-28s %-13s %-10s%n",
                    u.getUserId(), u.getFullName(), u.getUsername(),
                    u.getEmail(), u.getMobile(), u.getStatus());
        }
        System.out.println("------------------------------------------------------------------------------------------");
    }

    private static void handleViewUserDetails() {
        if (!isAuthorizedAdmin()) return;

        System.out.println("\\n--- VIEW USER DETAILS ---");
        System.out.print("Enter User ID to view: ");
        int userId = readIntInput();
        if (userId <= 0) {
            System.out.println("[VALIDATION ERROR] User ID must be a positive integer.");
            return;
        }

        User user = adminDAO.getUserById(userId);
        if (user == null) {
            System.out.println("[INFO] No regular user (ROLE_USER) found with ID: " + userId);
            return;
        }

        System.out.println("\\n+--------------------------------------------------------------------------+");
        System.out.println("|                              USER DETAILS                                |");
        System.out.println("+--------------------------------------------------------------------------+");
        System.out.println("  User ID   : " + user.getUserId());
        System.out.println("  Full Name : " + user.getFullName());
        System.out.println("  Username  : " + user.getUsername());
        System.out.println("  Email     : " + user.getEmail());
        System.out.println("  Mobile    : " + user.getMobile());
        System.out.println("  Role      : " + user.getUserRole());
        System.out.println("  Status    : " + user.getStatus());
        adminDAO.displayLoanApplications(user.getUserId());
        System.out.println("+--------------------------------------------------------------------------+");
    }

    private static void handleEditUser() {
        if (!isAuthorizedAdmin()) return;

        System.out.println("\\n--- EDIT USER INFORMATION ---");
        System.out.print("Enter User ID to edit: ");
        int userId = readIntInput();
        if (userId <= 0) {
            System.out.println("[VALIDATION ERROR] Invalid User ID.");
            return;
        }

        User user = adminDAO.getUserById(userId);
        if (user == null) {
            System.out.println("[INFO] No regular user found with ID: " + userId);
            return;
        }

        System.out.println("Editing User: " + user.getFullName() + " (Press Enter to keep current value)");

        System.out.print("First Name [" + user.getFirstName() + "]: ");
        String firstName = scanner.nextLine().trim();
        if (!firstName.isEmpty()) {
            if (!firstName.matches("^[A-Za-z]{2,50}$")) {
                System.out.println("[VALIDATION ERROR] First Name must contain only letters (2-50 chars).");
                return;
            }
            user.setFirstName(firstName);
        }

        System.out.print("Last Name [" + user.getLastName() + "]: ");
        String lastName = scanner.nextLine().trim();
        if (!lastName.isEmpty()) {
            if (!lastName.matches("^[A-Za-z]{2,50}$")) {
                System.out.println("[VALIDATION ERROR] Last Name must contain only letters (2-50 chars).");
                return;
            }
            user.setLastName(lastName);
        }

        System.out.print("Email [" + user.getEmail() + "]: ");
        String email = scanner.nextLine().trim();
        if (!email.isEmpty()) {
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\\\.[A-Za-z]{2,6}$")) {
                System.out.println("[VALIDATION ERROR] Invalid email format.");
                return;
            }
            if (adminDAO.isEmailTakenByOther(email, user.getUserId())) {
                System.out.println("[VALIDATION ERROR] Email is already registered to another user.");
                return;
            }
            user.setEmail(email);
        }

        System.out.print("Mobile [" + user.getMobile() + "]: ");
        String mobile = scanner.nextLine().trim();
        if (!mobile.isEmpty()) {
            if (!mobile.matches("^[6-9][0-9]{9}$")) {
                System.out.println("[VALIDATION ERROR] Mobile must be a valid 10-digit number starting with 6-9.");
                return;
            }
            user.setMobile(mobile);
        }

        if (adminDAO.updateUserDetails(user)) {
            System.out.println("[SUCCESS] User details updated successfully.");
            adminDAO.sendEmailNotification(user.getEmail(), user.getFirstName(), "EDIT");
        } else {
            System.out.println("[ERROR] Failed to update user information.");
        }
    }

    private static void handleChangeStatus(String targetStatus) {
        if (!isAuthorizedAdmin()) return;

        System.out.println("\\n--- " + targetStatus.toUpperCase() + " USER ---");
        System.out.print("Enter User ID to set as " + targetStatus + ": ");
        int userId = readIntInput();
        if (userId <= 0) {
            System.out.println("[VALIDATION ERROR] Invalid User ID.");
            return;
        }

        User user = adminDAO.getUserById(userId);
        if (user == null) {
            System.out.println("[INFO] No regular user found with ID: " + userId);
            return;
        }

        if (user.getStatus().equalsIgnoreCase(targetStatus)) {
            System.out.println("[INFO] User '" + user.getUsername() + "' is already " + targetStatus + ".");
            return;
        }

        if (adminDAO.updateUserStatus(userId, targetStatus)) {
            System.out.println("[SUCCESS] User '" + user.getUsername() + "' status changed to " + targetStatus + ".");
            adminDAO.sendEmailNotification(
                    user.getEmail(),
                    user.getFirstName(),
                    "Active".equalsIgnoreCase(targetStatus) ? "ACTIVATION" : "DEACTIVATION"
            );
        } else {
            System.out.println("[ERROR] Could not change user status.");
        }
    }

    private static void handleDeleteUser() {
        if (!isAuthorizedAdmin()) return;

        System.out.println("\\n--- DELETE USER ---");
        System.out.print("Enter User ID to delete: ");
        int userId = readIntInput();
        if (userId <= 0) {
            System.out.println("[VALIDATION ERROR] Invalid User ID.");
            return;
        }

        User user = adminDAO.getUserById(userId);
        if (user == null) {
            System.out.println("[INFO] No regular user found with ID: " + userId);
            return;
        }

        System.out.println("Selected User: " + user.getFullName() + " | Username: "
                + user.getUsername() + " | Email: " + user.getEmail());
        System.out.print("Enter reason for deletion (for audit_log): ");
        String reason = scanner.nextLine().trim();
        if (reason.isEmpty()) {
            reason = "Deleted by Admin via Console Dashboard";
        }

        System.out.print("Are you sure you want to permanently delete this user? (yes/no): ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("[CANCELLED] User deletion aborted.");
            return;
        }

        if (adminDAO.deleteUserWithAudit(user, loggedInAdmin.getUsername(), reason)) {
            System.out.println("[SUCCESS] User deleted and action recorded in 'audit_log' table.");
            adminDAO.sendEmailNotification(user.getEmail(), user.getFirstName(), "DELETION");
        } else {
            System.out.println("[ERROR] Failed to delete user.");
        }
    }

    private static boolean isAuthorizedAdmin() {
        if (loggedInAdmin == null || !"ROLE_ADMIN".equalsIgnoreCase(loggedInAdmin.getUserRole())) {
            System.out.println("[SECURITY ALERT] Unauthorized access! Only ROLE_ADMIN can perform this action.");
            return false;
        }
        return true;
    }

    private static int readIntInput() {
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}`,
  },
];

const MYSQL_SCRIPT = `-- ============================================================================
-- STAGE 2: ADMIN MANAGEMENT SYSTEM - MYSQL DATABASE SCRIPT
-- Database: loan_system_db
-- ============================================================================

DROP DATABASE IF EXISTS loan_system_db;
CREATE DATABASE loan_system_db;
USE loan_system_db;

-- 1. USERS TABLE
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    mobile VARCHAR(15) NOT NULL,
    user_role ENUM('ROLE_USER', 'ROLE_ADMIN') NOT NULL DEFAULT 'ROLE_USER',
    status ENUM('Active', 'Inactive') NOT NULL DEFAULT 'Active',
    failed_login_attempts INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. LOAN APPLICATIONS TABLE
CREATE TABLE loan_applications (
    loan_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    loan_type VARCHAR(50) NOT NULL,
    loan_amount DECIMAL(12, 2) NOT NULL,
    interest_rate DECIMAL(5, 2) NOT NULL,
    tenure_months INT NOT NULL,
    loan_status ENUM('Pending', 'Approved', 'Rejected') NOT NULL DEFAULT 'Pending',
    applied_date DATE NOT NULL,
    CONSTRAINT fk_loan_user FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
);

-- 3. AUDIT LOG TABLE
CREATE TABLE audit_log (
    audit_id INT AUTO_INCREMENT PRIMARY KEY,
    admin_username VARCHAR(50) NOT NULL,
    deleted_user_id INT NOT NULL,
    deleted_username VARCHAR(50) NOT NULL,
    deleted_full_name VARCHAR(100) NOT NULL,
    deleted_email VARCHAR(100) NOT NULL,
    action_type VARCHAR(30) NOT NULL DEFAULT 'DELETE_USER',
    remarks VARCHAR(255),
    action_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- PREDEFINED ADMIN CREDENTIALS
INSERT INTO users (first_name, last_name, username, password, email, mobile, user_role, status, failed_login_attempts)
VALUES
('System', 'Administrator', 'admin', 'Admin@123', 'admin@loansystem.com', '9876543210', 'ROLE_ADMIN', 'Active', 0),
('Riya', 'Pillai', 'riya_admin', 'Riya@2026', 'riya.admin@loansystem.com', '9811122233', 'ROLE_ADMIN', 'Active', 0);

-- SAMPLE REGULAR USERS (ROLE_USER)
INSERT INTO users (first_name, last_name, username, password, email, mobile, user_role, status, failed_login_attempts)
VALUES
('Aarav', 'Sharma', 'aarav_s', 'User@123', 'aarav.sharma@example.com', '9820112233', 'ROLE_USER', 'Active', 0),
('Priya', 'Patel', 'priya_p', 'User@123', 'priya.patel@example.com', '9820445566', 'ROLE_USER', 'Active', 1),
('Rohan', 'Deshmukh', 'rohan_d', 'User@123', 'rohan.deshmukh@example.com', '9820778899', 'ROLE_USER', 'Inactive', 3),
('Sneha', 'Kulkarni', 'sneha_k', 'User@123', 'sneha.kulkarni@example.com', '9820990011', 'ROLE_USER', 'Active', 0),
('Vikram', 'Verma', 'vikram_v', 'User@123', 'vikram.verma@example.com', '9820334455', 'ROLE_USER', 'Inactive', 0);

-- SAMPLE LOAN APPLICATIONS
INSERT INTO loan_applications (user_id, loan_type, loan_amount, interest_rate, tenure_months, loan_status, applied_date)
VALUES
(3, 'Home Loan', 4500000.00, 8.50, 240, 'Approved', '2026-01-15'),
(3, 'Car Loan', 850000.00, 9.25, 60, 'Pending', '2026-02-20'),
(4, 'Education Loan', 1200000.00, 7.80, 84, 'Approved', '2026-02-10'),
(5, 'Personal Loan', 300000.00, 11.50, 36, 'Rejected', '2026-01-28');`;

type MainTab = 'simulator' | 'java-code' | 'sql-script' | 'vscode-setup' | 'viva-guide';

type ConsoleStep =
  | 'LOGIN_USERNAME'
  | 'LOGIN_PASSWORD'
  | 'LOGIN_RETRY'
  | 'DASHBOARD_MENU'
  | 'SEARCH_CRITERION'
  | 'SEARCH_VALUE'
  | 'VIEW_USER_ID'
  | 'EDIT_USER_ID'
  | 'EDIT_FIRST_NAME'
  | 'EDIT_LAST_NAME'
  | 'EDIT_EMAIL'
  | 'EDIT_MOBILE'
  | 'ACTIVATE_USER_ID'
  | 'DEACTIVATE_USER_ID'
  | 'DELETE_USER_ID'
  | 'DELETE_REASON'
  | 'DELETE_CONFIRM';

export function App() {
  const [activeTab, setActiveTab] = useState<MainTab>('simulator');
  const [selectedFileIndex, setSelectedFileIndex] = useState<number>(3); // AdminManagementApp.java default
  const [copiedKey, setCopiedKey] = useState<string | null>(null);

  const [users, setUsers] = useState<DbUser[]>(INITIAL_USERS);
  const [loans] = useState<DbLoan[]>(INITIAL_LOANS);
  const [auditLogs, setAuditLogs] = useState<DbAuditLog[]>([]);
  const [emails, setEmails] = useState<EmailRecord[]>([]);
  const [dbInspectorTab, setDbInspectorTab] = useState<'users' | 'loans' | 'audit' | 'emails'>('users');

  const [consoleLines, setConsoleLines] = useState<string[]>([
    '==========================================================================',
    '             LOAN APPLICATION SYSTEM - STAGE 2: ADMIN PORTAL              ',
    '==========================================================================',
    '',
    '--- ADMIN DIRECT LOGIN ---',
    'Enter Admin Username : ',
  ]);
  const [step, setStep] = useState<ConsoleStep>('LOGIN_USERNAME');
  const [inputVal, setInputVal] = useState<string>('');
  const [loggedInAdmin, setLoggedInAdmin] = useState<DbUser | null>(null);

  const [tempUsername, setTempUsername] = useState<string>('');
  const [tempSearchCriterion, setTempSearchCriterion] = useState<number>(1);
  const [tempTargetUser, setTempTargetUser] = useState<DbUser | null>(null);
  const [tempDeleteReason, setTempDeleteReason] = useState<string>('');

  const terminalEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    terminalEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [consoleLines]);

  const copyToClipboard = (text: string, key: string) => {
    navigator.clipboard.writeText(text);
    setCopiedKey(key);
    setTimeout(() => setCopiedKey(null), 1800);
  };

  const downloadTextFile = (filename: string, content: string) => {
    const blob = new Blob([content], { type: 'text/plain;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  };

  const downloadAllJavaAndSql = () => {
    JAVA_FILES.forEach((f, idx) => {
      setTimeout(() => {
        downloadTextFile(f.filename, f.content);
      }, idx * 200);
    });
    setTimeout(() => {
      downloadTextFile('stage2_schema_and_seed.sql', MYSQL_SCRIPT);
    }, JAVA_FILES.length * 200);
  };

  const resetSimulation = () => {
    setUsers(INITIAL_USERS);
    setAuditLogs([]);
    setEmails([]);
    setLoggedInAdmin(null);
    setStep('LOGIN_USERNAME');
    setInputVal('');
    setConsoleLines([
      '==========================================================================',
      '             LOAN APPLICATION SYSTEM - STAGE 2: ADMIN PORTAL              ',
      '==========================================================================',
      '',
      '--- ADMIN DIRECT LOGIN ---',
      'Enter Admin Username : ',
    ]);
  };

  const appendOutput = (newLines: string[], nextPrompt?: string) => {
    setConsoleLines((prev) => {
      const updated = [...prev];
      if (updated.length > 0 && nextPrompt !== undefined) {
        return [...updated, ...newLines, nextPrompt];
      }
      return [...updated, ...newLines];
    });
  };

  const getDashboardMenuLines = (): string[] => [
    '',
    '==========================================================================',
    '                             ADMIN DASHBOARD                              ',
    '==========================================================================',
    '  1. Search Users',
    '  2. View User Details',
    '  3. Edit User Information',
    '  4. Activate User',
    '  5. Deactivate User',
    '  6. Delete User',
    '  7. Logout',
    '==========================================================================',
    'Select an option (1-7): ',
  ];

  const pushEmailNotification = (
    toEmail: string,
    firstName: string,
    actionType: 'ACTIVATION' | 'DEACTIVATION' | 'DELETION' | 'EDIT'
  ): string[] => {
    const subject = 'Account Update Notification';
    let body = '';
    if (actionType === 'ACTIVATION') {
      body = `Dear ${firstName}, your account has been successfully activated by the admin. You can now log in to the system.`;
    } else if (actionType === 'DEACTIVATION') {
      body = `Dear ${firstName}, your account has been deactivated. Please contact support for more information.`;
    } else if (actionType === 'DELETION') {
      body = `Dear ${firstName}, your account has been deleted from the system. If this was a mistake, please contact support immediately.`;
    } else {
      body = `Dear ${firstName}, your account details have been updated. If you did not request this change, please contact support immediately.`;
    }

    setEmails((prev) => [
      {
        id: prev.length + 1,
        toEmail,
        subject,
        body,
        actionType,
        timestamp: new Date().toLocaleTimeString(),
      },
      ...prev,
    ]);

    return [
      '',
      '+--------------------------------------------------------------------------+',
      '|                        EMAIL NOTIFICATION SENT                           |',
      '+--------------------------------------------------------------------------+',
      `  To      : ${toEmail}`,
      `  Subject : ${subject}`,
      `  Message : ${body}`,
      '+--------------------------------------------------------------------------+',
    ];
  };

  const handleScannerSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const raw = inputVal;
    const trimmed = raw.trim();
    setInputVal('');

    setConsoleLines((prev) => {
      const copy = [...prev];
      const lastIdx = copy.length - 1;
      const displayVal = step === 'LOGIN_PASSWORD' ? '*'.repeat(trimmed.length || 1) : raw;
      copy[lastIdx] = copy[lastIdx] + displayVal;
      return copy;
    });

    if (step === 'LOGIN_USERNAME') {
      setTempUsername(trimmed);
      appendOutput([], 'Enter Admin Password : ');
      setStep('LOGIN_PASSWORD');
      return;
    }

    if (step === 'LOGIN_PASSWORD') {
      if (!tempUsername || !trimmed) {
        appendOutput(
          ['[VALIDATION ERROR] Username and password cannot be empty.'],
          'Would you like to try logging in again? (yes/no): '
        );
        setStep('LOGIN_RETRY');
        return;
      }

      const found = users.find((u) => u.username.toLowerCase() === tempUsername.toLowerCase());
      if (!found) {
        appendOutput(
          ['[AUTH ERROR] Invalid username or password.'],
          'Would you like to try logging in again? (yes/no): '
        );
        setStep('LOGIN_RETRY');
        return;
      }

      if (found.userRole !== 'ROLE_ADMIN') {
        appendOutput(
          ['[ACCESS DENIED] Regular users (ROLE_USER) are not allowed to perform admin actions.'],
          'Would you like to try logging in again? (yes/no): '
        );
        setStep('LOGIN_RETRY');
        return;
      }

      if (found.password !== trimmed) {
        appendOutput(
          ['[AUTH ERROR] Invalid admin credentials. (Admin accounts are exempt from failed login lockouts).'],
          'Would you like to try logging in again? (yes/no): '
        );
        setStep('LOGIN_RETRY');
        return;
      }

      setLoggedInAdmin(found);
      appendOutput([
        '',
        `[SUCCESS] Welcome, ${found.firstName} ${found.lastName} (${found.userRole})!`,
        ...getDashboardMenuLines(),
      ]);
      setStep('DASHBOARD_MENU');
      return;
    }

    if (step === 'LOGIN_RETRY') {
      if (trimmed.toLowerCase() === 'yes' || trimmed.toLowerCase() === 'y') {
        appendOutput(['', '--- ADMIN DIRECT LOGIN ---'], 'Enter Admin Username : ');
        setStep('LOGIN_USERNAME');
      } else {
        appendOutput(['Exiting system. Goodbye! (Click Reset Console to restart)']);
      }
      return;
    }

    if (step === 'DASHBOARD_MENU') {
      const choice = parseInt(trimmed, 10);
      switch (choice) {
        case 1:
          appendOutput(
            [
              '',
              '--- SEARCH USERS ---',
              'Search By: 1.First Name  2.Last Name  3.Email  4.Mobile  5.Username  6.Status',
            ],
            'Enter search criterion (1-6): '
          );
          setStep('SEARCH_CRITERION');
          break;
        case 2:
          appendOutput(['', '--- VIEW USER DETAILS ---'], 'Enter User ID to view: ');
          setStep('VIEW_USER_ID');
          break;
        case 3:
          appendOutput(['', '--- EDIT USER INFORMATION ---'], 'Enter User ID to edit: ');
          setStep('EDIT_USER_ID');
          break;
        case 4:
          appendOutput(['', '--- ACTIVATE USER ---'], 'Enter User ID to set as Active: ');
          setStep('ACTIVATE_USER_ID');
          break;
        case 5:
          appendOutput(['', '--- DEACTIVATE USER ---'], 'Enter User ID to set as Inactive: ');
          setStep('DEACTIVATE_USER_ID');
          break;
        case 6:
          appendOutput(['', '--- DELETE USER ---'], 'Enter User ID to delete: ');
          setStep('DELETE_USER_ID');
          break;
        case 7:
          appendOutput(
            [
              `[LOGOUT] Admin ${loggedInAdmin?.username} logged out successfully.`,
              '',
              '--- ADMIN DIRECT LOGIN ---',
            ],
            'Enter Admin Username : '
          );
          setLoggedInAdmin(null);
          setStep('LOGIN_USERNAME');
          break;
        default:
          appendOutput([
            '[VALIDATION ERROR] Invalid choice. Please enter a number between 1 and 7.',
            ...getDashboardMenuLines(),
          ]);
      }
      return;
    }

    if (step === 'SEARCH_CRITERION') {
      const crit = parseInt(trimmed, 10);
      if (isNaN(crit) || crit < 1 || crit > 6) {
        appendOutput([
          '[VALIDATION ERROR] Please choose a valid search option (1-6).',
          ...getDashboardMenuLines(),
        ]);
        setStep('DASHBOARD_MENU');
        return;
      }
      setTempSearchCriterion(crit);
      appendOutput([], 'Enter search value: ');
      setStep('SEARCH_VALUE');
      return;
    }

    if (step === 'SEARCH_VALUE') {
      if (!trimmed) {
        appendOutput([
          '[VALIDATION ERROR] Search value cannot be empty.',
          ...getDashboardMenuLines(),
        ]);
        setStep('DASHBOARD_MENU');
        return;
      }

      if (
        tempSearchCriterion === 6 &&
        trimmed.toLowerCase() !== 'active' &&
        trimmed.toLowerCase() !== 'inactive'
      ) {
        appendOutput([
          "[VALIDATION ERROR] Status must be 'Active' or 'Inactive'.",
          ...getDashboardMenuLines(),
        ]);
        setStep('DASHBOARD_MENU');
        return;
      }

      const regularUsers = users.filter((u) => u.userRole === 'ROLE_USER');
      const q = trimmed.toLowerCase();
      const matched = regularUsers.filter((u) => {
        switch (tempSearchCriterion) {
          case 1: return u.firstName.toLowerCase().includes(q);
          case 2: return u.lastName.toLowerCase().includes(q);
          case 3: return u.email.toLowerCase().includes(q);
          case 4: return u.mobile.includes(q);
          case 5: return u.username.toLowerCase().includes(q);
          case 6: return u.status.toLowerCase() === q;
          default: return false;
        }
      });

      if (matched.length === 0) {
        appendOutput(['[INFO] No matching users found.', ...getDashboardMenuLines()]);
      } else {
        const tableLines = [
          '',
          '------------------------------------------------------------------------------------------',
          'ID     Full Name          Username       Email                        Mobile        Status',
          '------------------------------------------------------------------------------------------',
          ...matched.map(
            (u) =>
              `${String(u.userId).padEnd(6)} ${(u.firstName + ' ' + u.lastName).padEnd(18)} ${u.username.padEnd(14)} ${u.email.padEnd(28)} ${u.mobile.padEnd(13)} ${u.status}`
          ),
          '------------------------------------------------------------------------------------------',
        ];
        appendOutput([...tableLines, ...getDashboardMenuLines()]);
      }
      setStep('DASHBOARD_MENU');
      return;
    }

    if (step === 'VIEW_USER_ID') {
      const id = parseInt(trimmed, 10);
      if (isNaN(id) || id <= 0) {
        appendOutput([
          '[VALIDATION ERROR] User ID must be a positive integer.',
          ...getDashboardMenuLines(),
        ]);
        setStep('DASHBOARD_MENU');
        return;
      }
      const user = users.find((u) => u.userId === id && u.userRole === 'ROLE_USER');
      if (!user) {
        appendOutput([
          `[INFO] No regular user (ROLE_USER) found with ID: ${id}`,
          ...getDashboardMenuLines(),
        ]);
        setStep('DASHBOARD_MENU');
        return;
      }

      const userLoans = loans.filter((l) => l.userId === user.userId);
      const loanLines =
        userLoans.length > 0
          ? [
              '  Loan ID   Loan Type          Amount (INR)   Rate (%)   Tenure(M)  Status       Applied On',
              '  ----------------------------------------------------------------------------------------',
              ...userLoans.map(
                (l) =>
                  `  ${String(l.loanId).padEnd(9)} ${l.loanType.padEnd(18)} ${l.loanAmount.toFixed(2).padEnd(14)} ${l.interestRate.toFixed(2).padEnd(10)} ${String(l.tenureMonths).padEnd(10)} ${l.loanStatus.padEnd(12)} ${l.appliedDate}`
              ),
            ]
          : ['  No loan applications found for this user.'];

      appendOutput([
        '',
        '+--------------------------------------------------------------------------+',
        '|                              USER DETAILS                                |',
        '+--------------------------------------------------------------------------+',
        `  User ID   : ${user.userId}`,
        `  Full Name : ${user.firstName} ${user.lastName}`,
        `  Username  : ${user.username}`,
        `  Email     : ${user.email}`,
        `  Mobile    : ${user.mobile}`,
        `  Role      : ${user.userRole}`,
        `  Status    : ${user.status}`,
        '',
        '  --- Loan Applications ---',
        ...loanLines,
        '+--------------------------------------------------------------------------+',
        ...getDashboardMenuLines(),
      ]);
      setStep('DASHBOARD_MENU');
      return;
    }

    if (step === 'EDIT_USER_ID') {
      const id = parseInt(trimmed, 10);
      const user = users.find((u) => u.userId === id && u.userRole === 'ROLE_USER');
      if (!user) {
        appendOutput([
          `[INFO] No regular user found with ID: ${trimmed}`,
          ...getDashboardMenuLines(),
        ]);
        setStep('DASHBOARD_MENU');
        return;
      }
      setTempTargetUser({ ...user });
      appendOutput(
        [`Editing User: ${user.firstName} ${user.lastName} (Press Enter to keep current value)`],
        `First Name [${user.firstName}]: `
      );
      setStep('EDIT_FIRST_NAME');
      return;
    }

    if (step === 'EDIT_FIRST_NAME' && tempTargetUser) {
      const updated = { ...tempTargetUser };
      if (trimmed) {
        if (!/^[A-Za-z]{2,50}$/.test(trimmed)) {
          appendOutput([
            '[VALIDATION ERROR] First Name must contain only letters (2-50 chars).',
            ...getDashboardMenuLines(),
          ]);
          setStep('DASHBOARD_MENU');
          return;
        }
        updated.firstName = trimmed;
      }
      setTempTargetUser(updated);
      appendOutput([], `Last Name [${updated.lastName}]: `);
      setStep('EDIT_LAST_NAME');
      return;
    }

    if (step === 'EDIT_LAST_NAME' && tempTargetUser) {
      const updated = { ...tempTargetUser };
      if (trimmed) {
        if (!/^[A-Za-z]{2,50}$/.test(trimmed)) {
          appendOutput([
            '[VALIDATION ERROR] Last Name must contain only letters (2-50 chars).',
            ...getDashboardMenuLines(),
          ]);
          setStep('DASHBOARD_MENU');
          return;
        }
        updated.lastName = trimmed;
      }
      setTempTargetUser(updated);
      appendOutput([], `Email [${updated.email}]: `);
      setStep('EDIT_EMAIL');
      return;
    }

    if (step === 'EDIT_EMAIL' && tempTargetUser) {
      const updated = { ...tempTargetUser };
      if (trimmed) {
        if (!/^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,6}$/.test(trimmed)) {
          appendOutput(['[VALIDATION ERROR] Invalid email format.', ...getDashboardMenuLines()]);
          setStep('DASHBOARD_MENU');
          return;
        }
        const duplicate = users.some(
          (u) => u.email.toLowerCase() === trimmed.toLowerCase() && u.userId !== updated.userId
        );
        if (duplicate) {
          appendOutput([
            '[VALIDATION ERROR] Email is already registered to another user.',
            ...getDashboardMenuLines(),
          ]);
          setStep('DASHBOARD_MENU');
          return;
        }
        updated.email = trimmed;
      }
      setTempTargetUser(updated);
      appendOutput([], `Mobile [${updated.mobile}]: `);
      setStep('EDIT_MOBILE');
      return;
    }

    if (step === 'EDIT_MOBILE' && tempTargetUser) {
      const updated = { ...tempTargetUser };
      if (trimmed) {
        if (!/^[6-9][0-9]{9}$/.test(trimmed)) {
          appendOutput([
            '[VALIDATION ERROR] Mobile must be a valid 10-digit number starting with 6-9.',
            ...getDashboardMenuLines(),
          ]);
          setStep('DASHBOARD_MENU');
          return;
        }
        updated.mobile = trimmed;
      }

      setUsers((prev) => prev.map((u) => (u.userId === updated.userId ? updated : u)));
      const emailBox = pushEmailNotification(updated.email, updated.firstName, 'EDIT');
      appendOutput([
        '[SUCCESS] User details updated successfully.',
        ...emailBox,
        ...getDashboardMenuLines(),
      ]);
      setTempTargetUser(null);
      setStep('DASHBOARD_MENU');
      return;
    }

    if (step === 'ACTIVATE_USER_ID' || step === 'DEACTIVATE_USER_ID') {
      const targetStatus: 'Active' | 'Inactive' =
        step === 'ACTIVATE_USER_ID' ? 'Active' : 'Inactive';
      const id = parseInt(trimmed, 10);
      const user = users.find((u) => u.userId === id && u.userRole === 'ROLE_USER');
      if (!user) {
        appendOutput([
          `[INFO] No regular user found with ID: ${trimmed}`,
          ...getDashboardMenuLines(),
        ]);
        setStep('DASHBOARD_MENU');
        return;
      }
      if (user.status === targetStatus) {
        appendOutput([
          `[INFO] User '${user.username}' is already ${targetStatus}.`,
          ...getDashboardMenuLines(),
        ]);
        setStep('DASHBOARD_MENU');
        return;
      }

      setUsers((prev) =>
        prev.map((u) =>
          u.userId === id
            ? {
                ...u,
                status: targetStatus,
                failedLoginAttempts: targetStatus === 'Active' ? 0 : u.failedLoginAttempts,
              }
            : u
        )
      );

      const emailBox = pushEmailNotification(
        user.email,
        user.firstName,
        targetStatus === 'Active' ? 'ACTIVATION' : 'DEACTIVATION'
      );
      appendOutput([
        `[SUCCESS] User '${user.username}' status changed to ${targetStatus}.`,
        ...emailBox,
        ...getDashboardMenuLines(),
      ]);
      setStep('DASHBOARD_MENU');
      return;
    }

    if (step === 'DELETE_USER_ID') {
      const id = parseInt(trimmed, 10);
      const user = users.find((u) => u.userId === id && u.userRole === 'ROLE_USER');
      if (!user) {
        appendOutput([
          `[INFO] No regular user found with ID: ${trimmed}`,
          ...getDashboardMenuLines(),
        ]);
        setStep('DASHBOARD_MENU');
        return;
      }
      setTempTargetUser(user);
      appendOutput(
        [
          `Selected User: ${user.firstName} ${user.lastName} | Username: ${user.username} | Email: ${user.email}`,
        ],
        'Enter reason for deletion (for audit_log): '
      );
      setStep('DELETE_REASON');
      return;
    }

    if (step === 'DELETE_REASON' && tempTargetUser) {
      setTempDeleteReason(trimmed || 'Deleted by Admin via Console Dashboard');
      appendOutput([], 'Are you sure you want to permanently delete this user? (yes/no): ');
      setStep('DELETE_CONFIRM');
      return;
    }

    if (step === 'DELETE_CONFIRM' && tempTargetUser) {
      if (trimmed.toLowerCase() !== 'yes') {
        appendOutput(['[CANCELLED] User deletion aborted.', ...getDashboardMenuLines()]);
        setTempTargetUser(null);
        setStep('DASHBOARD_MENU');
        return;
      }

      const deleted = tempTargetUser;
      setUsers((prev) => prev.filter((u) => u.userId !== deleted.userId));
      setAuditLogs((prev) => [
        {
          auditId: prev.length + 1,
          adminUsername: loggedInAdmin?.username || 'admin',
          deletedUserId: deleted.userId,
          deletedUsername: deleted.username,
          deletedFullName: `${deleted.firstName} ${deleted.lastName}`,
          deletedEmail: deleted.email,
          actionType: 'DELETE_USER',
          remarks: tempDeleteReason,
          actionTimestamp: new Date().toISOString().replace('T', ' ').slice(0, 19),
        },
        ...prev,
      ]);

      const emailBox = pushEmailNotification(deleted.email, deleted.firstName, 'DELETION');
      appendOutput([
        "[SUCCESS] User deleted and action recorded in 'audit_log' table.",
        ...emailBox,
        ...getDashboardMenuLines(),
      ]);
      setTempTargetUser(null);
      setStep('DASHBOARD_MENU');
      return;
    }
  };

  const currentJavaFile = JAVA_FILES[selectedFileIndex] || JAVA_FILES[0];

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col">
      <header className="flex items-center justify-between px-6 py-4 border-b border-slate-800 bg-slate-950/90 sticky top-0 z-20">
        <a
          href="#top"
          onClick={(e) => {
            e.preventDefault();
            setActiveTab('simulator');
          }}
          className="text-lg font-bold tracking-tight text-white whitespace-nowrap"
        >
          LoanSystem Stage 2
        </a>

        <nav className="hidden md:flex items-center gap-6 text-sm font-medium text-slate-400">
          <button
            onClick={() => setActiveTab('simulator')}
            className={`hover:text-white transition-colors whitespace-nowrap pb-0.5 ${
              activeTab === 'simulator' ? 'text-white border-b-2 border-emerald-500' : ''
            }`}
          >
            Console Simulator
          </button>
          <button
            onClick={() => setActiveTab('java-code')}
            className={`hover:text-white transition-colors whitespace-nowrap pb-0.5 ${
              activeTab === 'java-code' ? 'text-white border-b-2 border-emerald-500' : ''
            }`}
          >
            Java Source (4 Files)
          </button>
          <button
            onClick={() => setActiveTab('sql-script')}
            className={`hover:text-white transition-colors whitespace-nowrap pb-0.5 ${
              activeTab === 'sql-script' ? 'text-white border-b-2 border-emerald-500' : ''
            }`}
          >
            MySQL Script
          </button>
          <button
            onClick={() => setActiveTab('vscode-setup')}
            className={`hover:text-white transition-colors whitespace-nowrap pb-0.5 ${
              activeTab === 'vscode-setup' ? 'text-white border-b-2 border-emerald-500' : ''
            }`}
          >
            VS Code Setup
          </button>
          <button
            onClick={() => setActiveTab('viva-guide')}
            className={`hover:text-white transition-colors whitespace-nowrap pb-0.5 ${
              activeTab === 'viva-guide' ? 'text-white border-b-2 border-emerald-500' : ''
            }`}
          >
            Test Cases &amp; Viva
          </button>
        </nav>

        <div className="flex items-center gap-3">
          <button
            onClick={downloadAllJavaAndSql}
            className="flex items-center gap-1.5 px-3.5 py-2 text-xs font-semibold text-white bg-slate-800 border border-slate-700 rounded-lg hover:bg-slate-700 transition-colors whitespace-nowrap"
          >
            <Download className="w-3.5 h-3.5 text-emerald-400" />
            Download Java + SQL Files
          </button>
          <button
            onClick={() => setActiveTab('java-code')}
            className="px-4 py-2 text-xs font-semibold text-slate-950 bg-emerald-400 rounded-lg hover:bg-emerald-300 transition-colors whitespace-nowrap"
          >
            View All 5 Java Files
          </button>
        </div>
      </header>

      <main className="flex-1 max-w-[1400px] w-full mx-auto px-6 py-6">
        <div className="flex flex-wrap items-center justify-between gap-4 pb-6 mb-6 border-b border-slate-800/80">
          <div>
            <h1 className="text-2xl font-bold tracking-tight text-white">
              Admin Management System — Stage 2 (4 Minimal Java Files)
            </h1>
            <div className="flex flex-wrap items-center gap-2 text-xs text-slate-400 mt-1.5">
              <span>Core Java (Scanner + JDBC)</span>
              <span aria-hidden="true">·</span>
              <span>4 Java Files Only</span>
              <span aria-hidden="true">·</span>
              <span>PreparedStatement Queries</span>
              <span aria-hidden="true">·</span>
              <span>ROLE_ADMIN &amp; ROLE_USER</span>
            </div>
          </div>

          <div className="text-xs text-slate-400 font-mono tabular-nums">
            Predefined Login: <strong className="text-emerald-400">admin</strong> /{' '}
            <strong className="text-emerald-400">Admin@123</strong>
          </div>
        </div>

        {activeTab === 'simulator' && (
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
            <section className="lg:col-span-7 flex flex-col border border-slate-800 rounded-xl bg-slate-900/60 overflow-hidden">
              <div className="flex items-center justify-between px-4 py-3 border-b border-slate-800 bg-slate-900">
                <div className="flex items-center gap-2 text-xs font-mono text-slate-300">
                  <Terminal className="w-4 h-4 text-emerald-400" />
                  <span>java -cp &quot;bin;lib/*&quot; AdminManagementApp</span>
                </div>
                <div className="flex items-center gap-2">
                  {!loggedInAdmin && step === 'LOGIN_USERNAME' && (
                    <button
                      type="button"
                      onClick={() => {
                        setLoggedInAdmin(INITIAL_USERS[0]);
                        setConsoleLines((prev) => [
                          ...prev.slice(0, -1),
                          'Enter Admin Username : admin',
                          'Enter Admin Password : *********',
                          '',
                          '[SUCCESS] Welcome, System Administrator (ROLE_ADMIN)!',
                          ...getDashboardMenuLines(),
                        ]);
                        setStep('DASHBOARD_MENU');
                      }}
                      className="px-2.5 py-1 text-xs font-medium text-emerald-300 bg-emerald-950/60 hover:bg-emerald-900/70 rounded transition-colors whitespace-nowrap"
                    >
                      Quick Fill Admin Login
                    </button>
                  )}
                  <button
                    type="button"
                    onClick={resetSimulation}
                    className="flex items-center gap-1.5 px-2.5 py-1 text-xs font-medium text-slate-300 hover:text-white bg-slate-800 hover:bg-slate-700 rounded transition-colors whitespace-nowrap"
                  >
                    <RotateCcw className="w-3.5 h-3.5" />
                    Reset Console
                  </button>
                </div>
              </div>

              <div className="p-4 font-mono text-xs leading-relaxed text-slate-200 h-[440px] overflow-y-auto whitespace-pre-wrap select-text bg-slate-950">
                {consoleLines.map((line, i) => (
                  <div
                    key={i}
                    className={
                      line.includes('[SUCCESS]')
                        ? 'text-emerald-400 font-medium'
                        : line.includes('[ERROR]') ||
                            line.includes('[AUTH ERROR]') ||
                            line.includes('[ACCESS DENIED]') ||
                            line.includes('[VALIDATION ERROR]')
                          ? 'text-rose-400 font-medium'
                          : line.includes('EMAIL NOTIFICATION SENT')
                            ? 'text-amber-300 font-semibold'
                            : 'text-slate-200'
                    }
                  >
                    {line || '\u00A0'}
                  </div>
                ))}
                <div ref={terminalEndRef} />
              </div>

              <form
                onSubmit={handleScannerSubmit}
                className="flex items-center gap-2 p-3 border-t border-slate-800 bg-slate-900"
              >
                <span className="text-xs font-mono text-emerald-400 pl-1 whitespace-nowrap">
                  Scanner.nextLine() &gt;
                </span>
                <input
                  type={step === 'LOGIN_PASSWORD' ? 'password' : 'text'}
                  value={inputVal}
                  onChange={(e) => setInputVal(e.target.value)}
                  placeholder={
                    step === 'LOGIN_USERNAME'
                      ? 'Type admin (or aarav_s to test RBAC rejection)...'
                      : step === 'LOGIN_PASSWORD'
                        ? 'Type Admin@123...'
                        : 'Type input and press Enter...'
                  }
                  className="flex-1 bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-xs font-mono text-white placeholder:text-slate-500 focus:outline-none focus:border-emerald-500"
                  autoFocus
                />
                <button
                  type="submit"
                  className="flex items-center gap-1.5 px-4 py-2 text-xs font-semibold bg-emerald-500 text-slate-950 rounded-lg hover:bg-emerald-400 transition-colors whitespace-nowrap"
                >
                  <Send className="w-3.5 h-3.5" />
                  Enter
                </button>
              </form>
            </section>

            <section className="lg:col-span-5 flex flex-col border border-slate-800 rounded-xl bg-slate-900/60 overflow-hidden">
              <div className="flex items-center justify-between px-4 py-3 border-b border-slate-800 bg-slate-900">
                <div className="flex items-center gap-2 text-xs font-semibold text-slate-200">
                  <Database className="w-4 h-4 text-emerald-400" />
                  <span>Live MySQL State (loan_system_db)</span>
                </div>
                <div className="flex items-center gap-1 p-0.5 bg-slate-950 rounded-lg">
                  <button
                    onClick={() => setDbInspectorTab('users')}
                    className={`px-2.5 py-1 text-xs font-medium rounded-md transition-colors whitespace-nowrap ${
                      dbInspectorTab === 'users'
                        ? 'bg-slate-800 text-white'
                        : 'text-slate-400 hover:text-slate-200'
                    }`}
                  >
                    users ({users.length})
                  </button>
                  <button
                    onClick={() => setDbInspectorTab('loans')}
                    className={`px-2.5 py-1 text-xs font-medium rounded-md transition-colors whitespace-nowrap ${
                      dbInspectorTab === 'loans'
                        ? 'bg-slate-800 text-white'
                        : 'text-slate-400 hover:text-slate-200'
                    }`}
                  >
                    loans ({loans.length})
                  </button>
                  <button
                    onClick={() => setDbInspectorTab('audit')}
                    className={`px-2.5 py-1 text-xs font-medium rounded-md transition-colors whitespace-nowrap ${
                      dbInspectorTab === 'audit'
                        ? 'bg-slate-800 text-white'
                        : 'text-slate-400 hover:text-slate-200'
                    }`}
                  >
                    audit_log ({auditLogs.length})
                  </button>
                  <button
                    onClick={() => setDbInspectorTab('emails')}
                    className={`px-2.5 py-1 text-xs font-medium rounded-md transition-colors whitespace-nowrap ${
                      dbInspectorTab === 'emails'
                        ? 'bg-slate-800 text-white'
                        : 'text-slate-400 hover:text-slate-200'
                    }`}
                  >
                    Emails ({emails.length})
                  </button>
                </div>
              </div>

              <div className="p-4 flex-1 overflow-x-auto overflow-y-auto max-h-[495px]">
                {dbInspectorTab === 'users' && (
                  <table className="w-full text-left border-collapse text-xs font-mono tabular-nums">
                    <thead>
                      <tr className="border-b border-slate-800 text-slate-400">
                        <th className="py-2 pr-2">ID</th>
                        <th className="py-2 px-2">Username</th>
                        <th className="py-2 px-2">Name</th>
                        <th className="py-2 px-2">Role</th>
                        <th className="py-2 pl-2">Status</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-800/60">
                      {users.map((u) => (
                        <tr key={u.userId} className="hover:bg-slate-800/30">
                          <td className="py-2.5 pr-2 text-slate-400">{u.userId}</td>
                          <td className="py-2.5 px-2 font-medium text-white">{u.username}</td>
                          <td className="py-2.5 px-2 text-slate-300">
                            {u.firstName} {u.lastName}
                          </td>
                          <td className="py-2.5 px-2 text-slate-400">{u.userRole}</td>
                          <td
                            className={`py-2.5 pl-2 font-medium ${
                              u.status === 'Active' ? 'text-emerald-400' : 'text-amber-400'
                            }`}
                          >
                            {u.status}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                )}

                {dbInspectorTab === 'loans' && (
                  <table className="w-full text-left border-collapse text-xs font-mono tabular-nums">
                    <thead>
                      <tr className="border-b border-slate-800 text-slate-400">
                        <th className="py-2 pr-2">Loan ID</th>
                        <th className="py-2 px-2">User ID</th>
                        <th className="py-2 px-2">Type</th>
                        <th className="py-2 px-2 text-right">Amount</th>
                        <th className="py-2 pl-2">Status</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-800/60">
                      {loans.map((l) => (
                        <tr key={l.loanId} className="hover:bg-slate-800/30">
                          <td className="py-2.5 pr-2 text-slate-400">{l.loanId}</td>
                          <td className="py-2.5 px-2 text-white">{l.userId}</td>
                          <td className="py-2.5 px-2 text-slate-300">{l.loanType}</td>
                          <td className="py-2.5 px-2 text-right text-slate-200">
                            ₹{l.loanAmount.toLocaleString('en-IN')}
                          </td>
                          <td className="py-2.5 pl-2 text-slate-300">{l.loanStatus}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                )}

                {dbInspectorTab === 'audit' && (
                  <div>
                    {auditLogs.length === 0 ? (
                      <div className="py-12 text-center text-xs text-slate-400">
                        No deletion rows in <code className="text-slate-200">audit_log</code> yet.
                      </div>
                    ) : (
                      <div className="space-y-3">
                        {auditLogs.map((a) => (
                          <div
                            key={a.auditId}
                            className="p-3 border border-slate-800 rounded-lg bg-slate-950 text-xs font-mono space-y-1"
                          >
                            <div className="flex items-center justify-between text-slate-400">
                              <span>
                                audit_id: #{a.auditId} · {a.actionType}
                              </span>
                              <span>{a.actionTimestamp}</span>
                            </div>
                            <div className="text-white font-medium">
                              Deleted User: {a.deletedFullName} (ID: {a.deletedUserId}, Username:{' '}
                              {a.deletedUsername})
                            </div>
                            <div className="text-slate-400">
                              Admin: {a.adminUsername} · Reason: {a.remarks}
                            </div>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                )}

                {dbInspectorTab === 'emails' && (
                  <div>
                    {emails.length === 0 ? (
                      <div className="py-12 text-center text-xs text-slate-400">
                        No emails dispatched yet.
                      </div>
                    ) : (
                      <div className="space-y-3">
                        {emails.map((m) => (
                          <div
                            key={m.id}
                            className="p-3 border border-slate-800 rounded-lg bg-slate-950 text-xs space-y-1.5"
                          >
                            <div className="flex items-center justify-between text-slate-400 font-mono">
                              <span className="flex items-center gap-1.5 text-emerald-400">
                                <Mail className="w-3.5 h-3.5" />
                                {m.actionType}
                              </span>
                              <span>{m.timestamp}</span>
                            </div>
                            <div className="text-slate-300">
                              <span className="text-slate-500">To:</span> {m.toEmail} ·{' '}
                              <span className="text-slate-500">Subject:</span> {m.subject}
                            </div>
                            <p className="text-white font-mono bg-slate-900/90 p-2.5 rounded border border-slate-800/80">
                              {m.body}
                            </p>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                )}
              </div>
            </section>
          </div>
        )}

        {activeTab === 'java-code' && (
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
            <aside className="lg:col-span-4 space-y-3">
              <div className="p-4 border border-slate-800 rounded-xl bg-slate-900/60">
                <div className="flex items-center gap-2 text-sm font-semibold text-white mb-3">
                  <FolderTree className="w-4 h-4 text-emerald-400" />
                  <span>Reduced Structure (Only 4 Java Files)</span>
                </div>
                <div className="space-y-1.5">
                  {JAVA_FILES.map((file, idx) => (
                    <button
                      key={file.filename}
                      onClick={() => setSelectedFileIndex(idx)}
                      className={`w-full text-left px-3 py-2.5 rounded-lg border transition-colors ${
                        selectedFileIndex === idx
                          ? 'bg-slate-800/90 border-emerald-500/60 text-white'
                          : 'bg-slate-950/60 border-slate-800/80 text-slate-300 hover:bg-slate-900'
                      }`}
                    >
                      <div className="flex items-center justify-between">
                        <span className="font-mono text-xs font-semibold">{file.path}</span>
                      </div>
                      <p className="text-xs text-emerald-400/90 mt-0.5">{file.role}</p>
                      <p className="text-xs text-slate-400 mt-1 line-clamp-2">{file.vivaSummary}</p>
                    </button>
                  ))}
                </div>
              </div>
            </aside>

            <section className="lg:col-span-8 flex flex-col border border-slate-800 rounded-xl bg-slate-900/60 overflow-hidden">
              <div className="flex items-center justify-between px-4 py-3 border-b border-slate-800 bg-slate-900">
                <div>
                  <span className="font-mono text-sm font-semibold text-white">
                    {currentJavaFile.path}
                  </span>
                  <span className="text-xs text-slate-400 ml-3">{currentJavaFile.role}</span>
                </div>
                <div className="flex items-center gap-2">
                  <button
                    onClick={() =>
                      downloadTextFile(currentJavaFile.filename, currentJavaFile.content)
                    }
                    className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium bg-emerald-500/15 hover:bg-emerald-500/25 text-emerald-300 border border-emerald-500/30 rounded-lg transition-colors whitespace-nowrap"
                  >
                    <Download className="w-3.5 h-3.5" />
                    Download {currentJavaFile.filename}
                  </button>
                  <button
                    onClick={() => copyToClipboard(currentJavaFile.content, currentJavaFile.filename)}
                    className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium bg-slate-800 hover:bg-slate-700 text-slate-100 rounded-lg transition-colors whitespace-nowrap"
                  >
                    {copiedKey === currentJavaFile.filename ? (
                      <>
                        <Check className="w-3.5 h-3.5 text-emerald-400" />
                        Copied
                      </>
                    ) : (
                      <>
                        <Copy className="w-3.5 h-3.5" />
                        Copy {currentJavaFile.filename}
                      </>
                    )}
                  </button>
                </div>
              </div>
              <pre className="p-5 font-mono text-xs leading-relaxed text-slate-200 overflow-x-auto bg-slate-950 max-h-[640px]">
                <code>{currentJavaFile.content}</code>
              </pre>
            </section>
          </div>
        )}

        {activeTab === 'sql-script' && (
          <div className="border border-slate-800 rounded-xl bg-slate-900/60 overflow-hidden">
            <div className="flex items-center justify-between px-5 py-3.5 border-b border-slate-800 bg-slate-900">
              <div className="flex items-center gap-2">
                <Database className="w-4 h-4 text-emerald-400" />
                <span className="font-mono text-sm font-semibold text-white">
                  sql/stage2_schema_and_seed.sql
                </span>
              </div>
              <div className="flex items-center gap-2">
                <button
                  onClick={() => downloadTextFile('stage2_schema_and_seed.sql', MYSQL_SCRIPT)}
                  className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium bg-emerald-500/15 hover:bg-emerald-500/25 text-emerald-300 border border-emerald-500/30 rounded-lg transition-colors whitespace-nowrap"
                >
                  <Download className="w-3.5 h-3.5" />
                  Download SQL File
                </button>
                <button
                  onClick={() => copyToClipboard(MYSQL_SCRIPT, 'sql-script')}
                  className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium bg-slate-800 hover:bg-slate-700 text-slate-100 rounded-lg transition-colors whitespace-nowrap"
                >
                  {copiedKey === 'sql-script' ? (
                    <>
                      <Check className="w-3.5 h-3.5 text-emerald-400" />
                      Copied SQL Script
                    </>
                  ) : (
                    <>
                      <Copy className="w-3.5 h-3.5" />
                      Copy SQL Script
                    </>
                  )}
                </button>
              </div>
            </div>
            <pre className="p-5 font-mono text-xs leading-relaxed text-slate-200 overflow-x-auto bg-slate-950">
              <code>{MYSQL_SCRIPT}</code>
            </pre>
          </div>
        )}

        {activeTab === 'vscode-setup' && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="p-6 border border-slate-800 rounded-xl bg-slate-900/60 space-y-4">
              <div className="flex items-center gap-2 text-base font-semibold text-white">
                <Code2 className="w-5 h-5 text-emerald-400" />
                <h2>01. Minimal Folder Structure (4 Java Files)</h2>
              </div>
              <pre className="p-4 rounded-lg bg-slate-950 border border-slate-800 text-xs font-mono text-slate-200 leading-relaxed">
                {`JAVAPROJECT/
├── lib/
│   └── mysql-connector-j-8.3.0.jar
├── sql/
│   └── stage2_schema_and_seed.sql
└── src/
    ├── DBConnection.java        # 1. JDBC Connection
    ├── User.java                # 2. User POJO Model
    ├── AdminDAO.java            # 3. PreparedStatement DAO + Email + Audit
    └── AdminManagementApp.java  # 4. Scanner Console Main App`}
              </pre>
            </div>

            <div className="p-6 border border-slate-800 rounded-xl bg-slate-900/60 space-y-4">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2 text-base font-semibold text-white">
                  <Terminal className="w-5 h-5 text-emerald-400" />
                  <h2>02. Windows Compile &amp; Run Commands</h2>
                </div>
                <button
                  onClick={() =>
                    copyToClipboard(
                      'mkdir bin\njavac -cp ".;lib/*" -d bin src/*.java\njava -cp "bin;lib/*" AdminManagementApp',
                      'win-cmds'
                    )
                  }
                  className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium bg-slate-800 hover:bg-slate-700 text-slate-100 rounded-lg transition-colors whitespace-nowrap"
                >
                  {copiedKey === 'win-cmds' ? (
                    <>
                      <Check className="w-3.5 h-3.5 text-emerald-400" />
                      Copied
                    </>
                  ) : (
                    <>
                      <Copy className="w-3.5 h-3.5" />
                      Copy Commands
                    </>
                  )}
                </button>
              </div>
              <pre className="p-4 rounded-lg bg-slate-950 border border-slate-800 text-xs font-mono text-emerald-300 leading-relaxed">
                {`mkdir bin
javac -cp ".;lib/*" -d bin src/*.java
java -cp "bin;lib/*" AdminManagementApp`}
              </pre>
            </div>
          </div>
        )}

        {activeTab === 'viva-guide' && (
          <div className="p-6 border border-slate-800 rounded-xl bg-slate-900/60">
            <div className="flex items-center gap-2 text-base font-semibold text-white mb-4">
              <BookOpen className="w-5 h-5 text-emerald-400" />
              <h2>Predefined MySQL Credentials &amp; Viva Summary</h2>
            </div>
            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse text-xs font-mono tabular-nums">
                <thead>
                  <tr className="border-b border-slate-800 text-slate-400">
                    <th className="py-2.5 pr-4">User ID</th>
                    <th className="py-2.5 px-4">Username</th>
                    <th className="py-2.5 px-4">Password</th>
                    <th className="py-2.5 px-4">Role</th>
                    <th className="py-2.5 pl-4">Status</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800/60">
                  {INITIAL_USERS.map((u) => (
                    <tr key={u.userId}>
                      <td className="py-2.5 pr-4 text-slate-400">{u.userId}</td>
                      <td className="py-2.5 px-4 text-emerald-400 font-semibold">{u.username}</td>
                      <td className="py-2.5 px-4 text-white">{u.password}</td>
                      <td className="py-2.5 px-4 text-slate-300">{u.userRole}</td>
                      <td className="py-2.5 pl-4 text-slate-300">{u.status}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}

export default App;
