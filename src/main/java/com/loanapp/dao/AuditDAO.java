package com.loanapp.dao;

import com.loanapp.db.DatabaseConnection;
import com.loanapp.model.AuditLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for audit logging (Stage 2 Requirement 3.4).
 */
public class AuditDAO {

    /**
     * Retrieves all administrative audit log records.
     */
    public List<AuditLog> getAllAuditLogs() {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT * FROM audit_log ORDER BY audit_id DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                AuditLog log = new AuditLog();
                log.setAuditId(rs.getInt("audit_id"));
                log.setAdminUsername(rs.getString("admin_username"));
                log.setDeletedUserId(rs.getInt("deleted_user_id"));
                log.setDeletedUsername(rs.getString("deleted_username"));
                log.setDeletedFullName(rs.getString("deleted_full_name"));
                log.setDeletedEmail(rs.getString("deleted_email"));
                log.setActionType(rs.getString("action_type"));
                log.setRemarks(rs.getString("remarks"));
                log.setActionTimestamp(rs.getTimestamp("action_timestamp"));
                list.add(log);
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Error fetching audit logs: " + e.getMessage());
        }
        return list;
    }
}
