package com.loanapp.model;

import java.sql.Timestamp;

/**
 * User model representing users in the system across Stages 1 to 5.
 * Supports both regular borrowers (ROLE_USER) and loan officers / administrators (ROLE_ADMIN).
 */
public class User {
    private int userId;
    private String firstName;
    private String lastName;
    private String username;
    private String password; // May hold plain or hashed password
    private String email;
    private String mobile;
    private String location;
    private String pan;
    private int cibilScore;
    private String userRole; // "ROLE_USER" or "ROLE_ADMIN"
    private String status;   // "Active" or "Inactive"
    private int failedLoginAttempts;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public User() {
        this.userRole = "ROLE_USER";
        this.status = "Active";
        this.failedLoginAttempts = 0;
        this.cibilScore = 750;
    }

    public User(int userId, String firstName, String lastName, String username,
                String password, String email, String mobile, String location,
                String pan, int cibilScore, String userRole, String status,
                int failedLoginAttempts) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.mobile = mobile;
        this.location = location;
        this.pan = pan;
        this.cibilScore = cibilScore;
        this.userRole = userRole != null ? userRole : "ROLE_USER";
        this.status = status != null ? status : "Active";
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return ((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "")).trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public int getCibilScore() {
        return cibilScore;
    }

    public void setCibilScore(int cibilScore) {
        this.cibilScore = cibilScore;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public boolean isAdmin() {
        return "ROLE_ADMIN".equalsIgnoreCase(userRole) || "ADMIN".equalsIgnoreCase(userRole);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isActive() {
        return "Active".equalsIgnoreCase(status);
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", fullName='" + getFullName() + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", role='" + userRole + '\'' +
                ", status='" + status + '\'' +
                ", cibilScore=" + cibilScore +
                '}';
    }
}
