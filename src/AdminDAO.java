import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * AdminDAO.java
 * Performs all JDBC database operations using PreparedStatement for Stage 2.
 */
public class AdminDAO {

    /**
     * 1. Admin Direct Login & Role-Based Access Control (ROLE_ADMIN only).
     * Excludes Admin accounts from user-level failed login attempts tracking/lockout.
     */
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

                // Role-Based Access Control: Only ROLE_ADMIN can log into the Admin System
                if (!"ROLE_ADMIN".equalsIgnoreCase(role)) {
                    System.out.println("[ACCESS DENIED] Regular users (ROLE_USER) are not allowed to perform admin actions.");
                    return null;
                }

                if (!"Active".equalsIgnoreCase(status)) {
                    System.out.println("[AUTH ERROR] This admin account is currently inactive.");
                    return null;
                }

                // Admin Exclusion from Failed Login Attempts:
                // Never increment failed_login_attempts or lock ROLE_ADMIN accounts.
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

    /**
     * 3.1 Search Users (ROLE_USER only; admins excluded from user management)
     * Criteria: 1=First Name, 2=Last Name, 3=Email, 4=Mobile, 5=Username, 6=Status
     */
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

    /**
     * Fetch a single regular user (ROLE_USER only) by user_id.
     */
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

    /**
     * 3.2 View User Details - Display associated Loan Applications if applicable.
     */
    public void displayLoanApplications(int userId) {
        String sql = "SELECT loan_id, loan_type, loan_amount, interest_rate, tenure_months, loan_status, applied_date "
                + "FROM loan_applications WHERE user_id = ? ORDER BY applied_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                boolean hasLoans = false;
                System.out.println("\n  --- Loan Applications ---");
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

    /**
     * Check if email is already taken by another user during edit.
     */
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

    /**
     * Edit User Information (ROLE_USER only).
     */
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

    /**
     * 3.3 Change User Status (Activate / Deactivate) for ROLE_USER only.
     */
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

    /**
     * 3.4 Delete ROLE_USER & Record Deletion in audit_log within a JDBC Transaction.
     */
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
}
