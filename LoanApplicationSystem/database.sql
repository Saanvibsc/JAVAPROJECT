-- Database creation script for Loan Application System (Stage 3)

CREATE DATABASE IF NOT EXISTS loan_db;
USE loan_db;

-- 1. Users Table (from Stage 1 & 2)
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    mobile_number VARCHAR(20) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER'
);

-- 2. Loan Applications Table (Stage 3 Requirement)
CREATE TABLE IF NOT EXISTS loan_applications (
    loan_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    loan_amount DOUBLE NOT NULL,
    loan_type VARCHAR(50) NOT NULL,
    loan_duration VARCHAR(50) NOT NULL,
    interest_rate DOUBLE NOT NULL,
    annual_income DOUBLE NOT NULL,
    purpose TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'Pending',
    application_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    rejection_reason TEXT DEFAULT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Seed Initial Users (Password in plain text for student demonstration or hashing)
INSERT INTO users (user_id, full_name, email, mobile_number, password, role)
VALUES 
    (1, 'System Administrator', 'admin@loanapp.com', '9876543210', 'admin123', 'ADMIN'),
    (2, 'Rahul Sharma', 'rahul.sharma@example.com', '9876543211', 'user123', 'USER')
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

-- Seed Sample Loan Application for Testing
INSERT INTO loan_applications (loan_id, user_id, loan_amount, loan_type, loan_duration, interest_rate, annual_income, purpose, status)
VALUES 
    (1, 2, 500000.0, 'Home', '60 Months', 7.5, 1200000.0, 'Home extension and interior renovation', 'Pending')
ON DUPLICATE KEY UPDATE loan_id=loan_id;
