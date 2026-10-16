package com.loanapp.model;

import java.sql.Timestamp;

/**
 * Model representing audit records for administrative deletion operations (Stage 2 Requirement 3.4).
 */
public class AuditLog {
    private int auditId;
    private String adminUsername;
    private int deletedUserId;
    private String deletedUsername;
    private String deletedFullName;
    private String deletedEmail;
    private String actionType;
    private String remarks;
    private Timestamp actionTimestamp;

    public AuditLog() {
    }

    public AuditLog(String adminUsername, int deletedUserId, String deletedUsername,
                    String deletedFullName, String deletedEmail, String actionType,
                    String remarks) {
        this.adminUsername = adminUsername;
        this.deletedUserId = deletedUserId;
        this.deletedUsername = deletedUsername;
        this.deletedFullName = deletedFullName;
        this.deletedEmail = deletedEmail;
        this.actionType = actionType;
        this.remarks = remarks;
    }

    public int getAuditId() {
        return auditId;
    }

    public void setAuditId(int auditId) {
        this.auditId = auditId;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public int getDeletedUserId() {
        return deletedUserId;
    }

    public void setDeletedUserId(int deletedUserId) {
        this.deletedUserId = deletedUserId;
    }

    public String getDeletedUsername() {
        return deletedUsername;
    }

    public void setDeletedUsername(String deletedUsername) {
        this.deletedUsername = deletedUsername;
    }

    public String getDeletedFullName() {
        return deletedFullName;
    }

    public void setDeletedFullName(String deletedFullName) {
        this.deletedFullName = deletedFullName;
    }

    public String getDeletedEmail() {
        return deletedEmail;
    }

    public void setDeletedEmail(String deletedEmail) {
        this.deletedEmail = deletedEmail;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Timestamp getActionTimestamp() {
        return actionTimestamp;
    }

    public void setActionTimestamp(Timestamp actionTimestamp) {
        this.actionTimestamp = actionTimestamp;
    }
}
