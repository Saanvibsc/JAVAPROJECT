-- ============================================================================
-- LOAN APPLICATION MANAGEMENT SYSTEM - UNIFIED COMPLETE SCHEMA & SEED DATA
-- Covers Stages 1 to 5:
--   Stage 1: User Registration, Lockout, and Password Reset
--   Stage 2: Admin Portal, User Search, Status Control, and Audit Logging
--   Stage 3: Loan Origination and Review
--   Stage 4: PAN and Simulated CIBIL Score Evaluation
--   Stage 5: EMI Amortization Calculation and Post-Approval Servicing
-- ============================================================================

DROP DATABASE IF EXISTS loan_system_db;
CREATE DATABASE loan_system_db;
USE loan_system_db;

-- ----------------------------------------------------------------------------
-- 1. USERS TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    mobile VARCHAR(15) NOT NULL,
    location VARCHAR(100) DEFAULT NULL,
    pan VARCHAR(10) DEFAULT NULL,
    cibil_score INT NOT NULL DEFAULT 750,
    user_role ENUM('ROLE_USER', 'ROLE_ADMIN') NOT NULL DEFAULT 'ROLE_USER',
    status ENUM('Active', 'Inactive') NOT NULL DEFAULT 'Active',
    failed_login_attempts INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- 2. LOAN APPLICATIONS TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE loan_applications (
    loan_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    loan_type VARCHAR(50) NOT NULL,
    loan_amount DECIMAL(12, 2) NOT NULL,
    interest_rate DECIMAL(5, 2) NOT NULL,
    tenure_months INT NOT NULL,
    loan_duration VARCHAR(50) NOT NULL,
    annual_income DECIMAL(12, 2) NOT NULL,
    purpose TEXT NOT NULL,
    loan_status ENUM('Pending', 'Approved', 'Rejected', 'Needs More Info') NOT NULL DEFAULT 'Pending',
    monthly_emi DECIMAL(12, 2) DEFAULT NULL,
    total_interest DECIMAL(12, 2) DEFAULT NULL,
    total_payable DECIMAL(12, 2) DEFAULT NULL,
    rejection_reason TEXT DEFAULT NULL,
    admin_remarks TEXT DEFAULT NULL,
    applied_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    decision_date TIMESTAMP NULL DEFAULT NULL,
    CONSTRAINT fk_loan_user FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
);

-- ----------------------------------------------------------------------------
-- 3. AUDIT LOG TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE audit_log (
    audit_id INT AUTO_INCREMENT PRIMARY KEY,
    admin_username VARCHAR(50) NOT NULL,
    deleted_user_id INT NOT NULL,
    deleted_username VARCHAR(50) NOT NULL,
    deleted_full_name VARCHAR(100) NOT NULL,
    deleted_email VARCHAR(100) NOT NULL,
    action_type VARCHAR(30) NOT NULL DEFAULT 'DELETE_USER',
    remarks VARCHAR(255) DEFAULT NULL,
    action_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- SEED DATA FOR TESTING & VIVA EVALUATION
-- ============================================================================

-- Predefined Administrator Accounts (Direct Login, Exempt from Lockout)
INSERT INTO users (user_id, first_name, last_name, username, password, email, mobile, location, pan, cibil_score, user_role, status, failed_login_attempts)
VALUES
(1, 'System', 'Administrator', 'admin', 'Admin@123', 'admin@loansystem.com', '9876543210', 'Mumbai', 'ABCDE1234F', 850, 'ROLE_ADMIN', 'Active', 0),
(2, 'Riya', 'Pillai', 'riya_admin', 'Riya@2026', 'riya.admin@loansystem.com', '9811122233', 'Bangalore', 'PQRSE5678K', 820, 'ROLE_ADMIN', 'Active', 0);

-- Predefined Regular Borrowers (ROLE_USER)
INSERT INTO users (user_id, first_name, last_name, username, password, email, mobile, location, pan, cibil_score, user_role, status, failed_login_attempts)
VALUES
(3, 'Aarav', 'Sharma', 'aarav_s', 'User@123', 'aarav.sharma@example.com', '9820112233', 'Pune', 'BKZPS1234K', 780, 'ROLE_USER', 'Active', 0),
(4, 'Priya', 'Patel', 'priya_p', 'User@123', 'priya.patel@example.com', '9820445566', 'Ahmedabad', 'CPTPM4567N', 760, 'ROLE_USER', 'Active', 1),
(5, 'Rohan', 'Deshmukh', 'rohan_d', 'User@123', 'rohan.deshmukh@example.com', '9820778899', 'Nagpur', 'ADFPD8901L', 680, 'ROLE_USER', 'Inactive', 3),
(6, 'Sneha', 'Kulkarni', 'sneha_k', 'User@123', 'sneha.kulkarni@example.com', '9820990011', 'Nashik', 'BGTPS3456Q', 720, 'ROLE_USER', 'Active', 0),
(7, 'Vikram', 'Verma', 'vikram_v', 'User@123', 'vikram.verma@example.com', '9820334455', 'Delhi', 'XYZPK7890R', 640, 'ROLE_USER', 'Inactive', 0);

-- Predefined Loan Applications
INSERT INTO loan_applications (loan_id, user_id, loan_type, loan_amount, interest_rate, tenure_months, loan_duration, annual_income, purpose, loan_status, monthly_emi, total_interest, total_payable, applied_date)
VALUES
(1, 3, 'Home Loan', 4500000.00, 7.00, 240, '20 Years (240 Months)', 2400000.00, 'Apartment purchase in Pune', 'Approved', 34888.66, 3873278.40, 8373278.40, '2026-01-15 10:30:00'),
(2, 3, 'Car Loan', 850000.00, 7.50, 60, '5 Years (60 Months)', 2400000.00, 'Electric Vehicle purchase', 'Pending', NULL, NULL, NULL, '2026-02-20 14:15:00'),
(3, 4, 'Education Loan', 1200000.00, 4.50, 84, '7 Years (84 Months)', 950000.00, 'Postgraduate studies in Data Science', 'Approved', 16674.34, 200644.56, 1400644.56, '2026-02-10 11:00:00'),
(4, 5, 'Personal Loan', 300000.00, 11.00, 36, '3 Years (36 Months)', 600000.00, 'Medical emergency expense', 'Rejected', NULL, NULL, NULL, '2026-01-28 09:45:00');

UPDATE loan_applications SET rejection_reason = 'High existing debt-to-income ratio and subprime credit score' WHERE loan_id = 4;
