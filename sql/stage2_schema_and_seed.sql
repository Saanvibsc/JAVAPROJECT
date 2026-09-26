-- ============================================================================
-- STAGE 2: ADMIN MANAGEMENT SYSTEM - MYSQL DATABASE SCRIPT
-- Database: loan_system_db
-- ============================================================================

DROP DATABASE IF EXISTS loan_system_db;
CREATE DATABASE loan_system_db;
USE loan_system_db;

-- ----------------------------------------------------------------------------
-- 1. USERS TABLE
-- Includes user_role ('ROLE_ADMIN', 'ROLE_USER') and status ('Active', 'Inactive')
-- Admins are excluded from user-level failed_login_attempts locking in Java logic
-- ----------------------------------------------------------------------------
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

-- ----------------------------------------------------------------------------
-- 2. LOAN APPLICATIONS TABLE
-- Stores loan applications associated with users (viewed in Section 3.2)
-- ON DELETE CASCADE ensures child loan records are cleaned up when user is deleted
-- ----------------------------------------------------------------------------
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

-- ----------------------------------------------------------------------------
-- 3. AUDIT LOG TABLE
-- Logs user deletion actions performed by ROLE_ADMIN (Section 3.4)
-- ----------------------------------------------------------------------------
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

-- ============================================================================
-- PREDEFINED SEED DATA FOR TESTING & VIVA
-- ============================================================================

-- 1. Predefined Admin Credentials (No Registration for Admin - Direct Login)
INSERT INTO users (first_name, last_name, username, password, email, mobile, user_role, status, failed_login_attempts)
VALUES
('System', 'Administrator', 'admin', 'Admin@123', 'admin@loansystem.com', '9876543210', 'ROLE_ADMIN', 'Active', 0),
('Riya', 'Pillai', 'riya_admin', 'Riya@2026', 'riya.admin@loansystem.com', '9811122233', 'ROLE_ADMIN', 'Active', 0);

-- 2. Sample Regular Users (ROLE_USER)
INSERT INTO users (first_name, last_name, username, password, email, mobile, user_role, status, failed_login_attempts)
VALUES
('Aarav', 'Sharma', 'aarav_s', 'User@123', 'aarav.sharma@example.com', '9820112233', 'ROLE_USER', 'Active', 0),
('Priya', 'Patel', 'priya_p', 'User@123', 'priya.patel@example.com', '9820445566', 'ROLE_USER', 'Active', 1),
('Rohan', 'Deshmukh', 'rohan_d', 'User@123', 'rohan.deshmukh@example.com', '9820778899', 'ROLE_USER', 'Inactive', 3),
('Sneha', 'Kulkarni', 'sneha_k', 'User@123', 'sneha.kulkarni@example.com', '9820990011', 'ROLE_USER', 'Active', 0),
('Vikram', 'Verma', 'vikram_v', 'User@123', 'vikram.verma@example.com', '9820334455', 'ROLE_USER', 'Inactive', 0);

-- 3. Sample Loan Applications linked to Regular Users
INSERT INTO loan_applications (user_id, loan_type, loan_amount, interest_rate, tenure_months, loan_status, applied_date)
VALUES
(3, 'Home Loan', 4500000.00, 8.50, 240, 'Approved', '2026-01-15'),
(3, 'Car Loan', 850000.00, 9.25, 60, 'Pending', '2026-02-20'),
(4, 'Education Loan', 1200000.00, 7.80, 84, 'Approved', '2026-02-10'),
(5, 'Personal Loan', 300000.00, 11.50, 36, 'Rejected', '2026-01-28');
-- Note: sneha_k (user_id = 6) and vikram_v (user_id = 7) have no loan applications to test both cases.
