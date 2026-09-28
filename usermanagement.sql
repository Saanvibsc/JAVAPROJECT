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