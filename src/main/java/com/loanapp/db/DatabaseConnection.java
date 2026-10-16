package com.loanapp.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * JDBC Database Connection manager and schema bootstrapper.
 * Connects to MySQL/MariaDB server using environment variables with local defaults.
 */
public class DatabaseConnection {

    private static final String DEFAULT_HOST = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";
    private static final String DEFAULT_PORT = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "3306";
    private static final String DEFAULT_DB = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : "loan_system_db";

    private static final String BASE_URL = "jdbc:mysql://" + DEFAULT_HOST + ":" + DEFAULT_PORT + "/";
    private static final String URL_PARAMS = "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true";

    private static final String FULL_URL = System.getenv("DB_URL") != null
            ? System.getenv("DB_URL")
            : (BASE_URL + DEFAULT_DB + URL_PARAMS);

    private static final String USER = System.getenv("DB_USER") != null
            ? System.getenv("DB_USER")
            : "root";

    private static final String PASSWORD = System.getenv("DB_PASSWORD") != null
            ? System.getenv("DB_PASSWORD")
            : "root";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // Driver auto-registers in modern JDBC 4.0+
        }
    }

    private DatabaseConnection() {
    }

    /**
     * Gets a connection to the loan system database.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(FULL_URL, USER, PASSWORD);
    }

    /**
     * Initializes database schemas and initial seed data if not already present.
     */
    public static void initializeDatabase() {
        // Step 1: Ensure database exists
        try (Connection rootConn = DriverManager.getConnection(BASE_URL + URL_PARAMS, USER, PASSWORD);
             Statement stmt = rootConn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DEFAULT_DB);
        } catch (SQLException e) {
            // Root connect may fail if restricted, continue to direct db connection
        }

        // Step 2: Initialize tables within the database
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. Users Table (Stages 1-4)
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                    + "user_id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "first_name VARCHAR(50) NOT NULL, "
                    + "last_name VARCHAR(50) NOT NULL, "
                    + "username VARCHAR(50) NOT NULL UNIQUE, "
                    + "password VARCHAR(255) NOT NULL, "
                    + "email VARCHAR(100) NOT NULL UNIQUE, "
                    + "mobile VARCHAR(15) NOT NULL, "
                    + "location VARCHAR(100) DEFAULT NULL, "
                    + "pan VARCHAR(10) DEFAULT NULL, "
                    + "cibil_score INT DEFAULT 750, "
                    + "user_role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER', "
                    + "status VARCHAR(20) NOT NULL DEFAULT 'Active', "
                    + "failed_login_attempts INT NOT NULL DEFAULT 0, "
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
                    + ")";
            stmt.executeUpdate(createUsersTable);

            // 2. Loan Applications Table (Stages 3 & 5)
            String createLoansTable = "CREATE TABLE IF NOT EXISTS loan_applications ("
                    + "loan_id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "user_id INT NOT NULL, "
                    + "loan_type VARCHAR(50) NOT NULL, "
                    + "loan_amount DECIMAL(12, 2) NOT NULL, "
                    + "interest_rate DECIMAL(5, 2) NOT NULL, "
                    + "tenure_months INT NOT NULL, "
                    + "loan_duration VARCHAR(50) NOT NULL, "
                    + "annual_income DECIMAL(12, 2) NOT NULL, "
                    + "purpose TEXT NOT NULL, "
                    + "loan_status VARCHAR(30) NOT NULL DEFAULT 'Pending', "
                    + "monthly_emi DECIMAL(12, 2) DEFAULT NULL, "
                    + "total_interest DECIMAL(12, 2) DEFAULT NULL, "
                    + "total_payable DECIMAL(12, 2) DEFAULT NULL, "
                    + "rejection_reason TEXT DEFAULT NULL, "
                    + "admin_remarks TEXT DEFAULT NULL, "
                    + "applied_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + "decision_date TIMESTAMP NULL DEFAULT NULL, "
                    + "CONSTRAINT fk_loan_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE"
                    + ")";
            stmt.executeUpdate(createLoansTable);

            // 3. Audit Log Table (Stage 2)
            String createAuditTable = "CREATE TABLE IF NOT EXISTS audit_log ("
                    + "audit_id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "admin_username VARCHAR(50) NOT NULL, "
                    + "deleted_user_id INT NOT NULL, "
                    + "deleted_username VARCHAR(50) NOT NULL, "
                    + "deleted_full_name VARCHAR(100) NOT NULL, "
                    + "deleted_email VARCHAR(100) NOT NULL, "
                    + "action_type VARCHAR(30) NOT NULL DEFAULT 'DELETE_USER', "
                    + "remarks VARCHAR(255) DEFAULT NULL, "
                    + "action_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")";
            stmt.executeUpdate(createAuditTable);

            // Seed default Admin if missing
            seedAdminUser(conn);

            // Seed demo regular users if missing
            seedDemoUsers(conn);

        } catch (SQLException e) {
            System.err.println("[DB WARNING] Could not auto-initialize tables: " + e.getMessage());
        }
    }

    private static void seedAdminUser(Connection conn) throws SQLException {
        String checkAdmin = "SELECT COUNT(*) FROM users WHERE username = 'admin'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkAdmin)) {
            if (rs.next() && rs.getInt(1) == 0) {
                String insertAdmin = "INSERT INTO users (first_name, last_name, username, password, email, mobile, location, pan, cibil_score, user_role, status) "
                        + "VALUES ('System', 'Administrator', 'admin', 'Admin@123', 'admin@loansystem.com', '9876543210', 'Mumbai', 'ABCDE1234F', 850, 'ROLE_ADMIN', 'Active')";
                stmt.executeUpdate(insertAdmin);
            }
        }
    }

    private static void seedDemoUsers(Connection conn) throws SQLException {
        String checkUser = "SELECT COUNT(*) FROM users WHERE username = 'aarav_s'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkUser)) {
            if (rs.next() && rs.getInt(1) == 0) {
                String insertUser = "INSERT INTO users (first_name, last_name, username, password, email, mobile, location, pan, cibil_score, user_role, status) "
                        + "VALUES ('Aarav', 'Sharma', 'aarav_s', 'User@123', 'aarav.sharma@example.com', '9820112233', 'Pune', 'BKZPS1234K', 780, 'ROLE_USER', 'Active')";
                stmt.executeUpdate(insertUser);

                // Seed sample pending loan for Aarav
                String getUserId = "SELECT user_id FROM users WHERE username = 'aarav_s'";
                try (ResultSet uRs = stmt.executeQuery(getUserId)) {
                    if (uRs.next()) {
                        int userId = uRs.getInt(1);
                        String insertLoan = "INSERT INTO loan_applications (user_id, loan_type, loan_amount, interest_rate, tenure_months, loan_duration, annual_income, purpose, loan_status) "
                                + "VALUES (?, 'Home Loan', 500000.00, 7.00, 60, '60 Months', 1200000.00, 'Home extension and interior renovation', 'Pending')";
                        try (PreparedStatement ps = conn.prepareStatement(insertLoan)) {
                            ps.setInt(1, userId);
                            ps.executeUpdate();
                        }
                    }
                }
            }
        }
    }
}
