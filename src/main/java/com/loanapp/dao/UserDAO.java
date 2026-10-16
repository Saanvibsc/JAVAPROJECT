package com.loanapp.dao;

import com.loanapp.db.DatabaseConnection;
import com.loanapp.model.User;
import com.loanapp.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * User Data Access Object handling all user queries and transactional updates (Stages 1 and 2).
 */
public class UserDAO {

    /**
     * Stage 1: Registers a new user with status 'Active' and failed_login_attempts = 0.
     */
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (first_name, last_name, username, password, email, mobile, location, pan, cibil_score, user_role, status, failed_login_attempts) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getUsername());
            ps.setString(4, PasswordUtil.hashPassword(user.getPassword()));
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getMobile());
            ps.setString(7, user.getLocation());
            ps.setString(8, user.getPan());
            ps.setInt(9, user.getCibilScore() > 0 ? user.getCibilScore() : 750);
            ps.setString(10, user.getUserRole() != null ? user.getUserRole() : "ROLE_USER");
            ps.setString(11, "Active");
            ps.setInt(12, 0);

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setUserId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] User registration failed: " + e.getMessage());
        }
        return false;
    }

    /**
     * Checks if a username already exists in the system.
     */
    public boolean usernameExists(String username) {
        String sql = "SELECT user_id FROM users WHERE username = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Error checking username: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if an email already exists in the system.
     */
    public boolean emailExists(String email) {
        String sql = "SELECT user_id FROM users WHERE email = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Error checking email: " + e.getMessage());
            return false;
        }
    }

    /**
     * Finds a user by either username or email.
     */
    public User findByUsernameOrEmail(String loginIdentifier) {
        String sql = "SELECT * FROM users WHERE username = ? OR email = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, loginIdentifier);
            ps.setString(2, loginIdentifier);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Find user error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Finds a user by ID.
     */
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Get user by ID failed: " + e.getMessage());
        }
        return null;
    }

    /**
     * Stage 1: Resets failed login attempts back to 0.
     */
    public void resetFailedAttempts(int userId) {
        String sql = "UPDATE users SET failed_login_attempts = 0 WHERE user_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Failed to reset attempts: " + e.getMessage());
        }
    }

    /**
     * Stage 1: Increments failed login attempts and locks account if threshold (3) reached.
     */
    public void recordFailedAttempt(int userId, int newAttempts) {
        String sql = newAttempts >= 3
                ? "UPDATE users SET failed_login_attempts = ?, status = 'Inactive' WHERE user_id = ?"
                : "UPDATE users SET failed_login_attempts = ? WHERE user_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, newAttempts);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Failed to update attempts: " + e.getMessage());
        }
    }

    /**
     * Stage 1: Resets user password by username or email.
     */
    public boolean resetPassword(String identifier, String newPassword) {
        String sql = "UPDATE users SET password = ?, failed_login_attempts = 0 WHERE username = ? OR email = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, PasswordUtil.hashPassword(newPassword));
            ps.setString(2, identifier);
            ps.setString(3, identifier);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Password reset error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Stage 2 Requirement 3.1: Search Users (ROLE_USER only).
     * Criteria: 1=First Name, 2=Last Name, 3=Email, 4=Mobile, 5=Username, 6=Status
     */
    public List<User> searchUsers(int criterion, String value) {
        List<User> list = new ArrayList<>();
        String column;
        boolean exact = false;

        switch (criterion) {
            case 1: column = "first_name"; break;
            case 2: column = "last_name"; break;
            case 3: column = "email"; break;
            case 4: column = "mobile"; break;
            case 5: column = "username"; break;
            case 6:
                column = "status";
                exact = true;
                break;
            default:
                return list;
        }

        String sql = exact
                ? "SELECT * FROM users WHERE user_role = 'ROLE_USER' AND " + column + " = ? ORDER BY user_id ASC"
                : "SELECT * FROM users WHERE user_role = 'ROLE_USER' AND " + column + " LIKE ? ORDER BY user_id ASC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (exact) {
                ps.setString(1, value.trim().equalsIgnoreCase("Active") ? "Active" : "Inactive");
            } else {
                ps.setString(1, "%" + value.trim() + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Search users failed: " + e.getMessage());
        }
        return list;
    }

    /**
     * Stage 2 Requirement 3.3: Edit user information with database update.
     */
    public boolean updateUser(int userId, String fname, String lname, String email, String mobile, String location) {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ?, mobile = ?, location = ? WHERE user_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, fname);
            ps.setString(2, lname);
            ps.setString(3, email);
            ps.setString(4, mobile);
            ps.setString(5, location);
            ps.setInt(6, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Edit user failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Stage 2: Activate or Deactivate user.
     */
    public boolean updateStatus(int userId, String status) {
        String sql = "Active".equalsIgnoreCase(status)
                ? "UPDATE users SET status = 'Active', failed_login_attempts = 0 WHERE user_id = ?"
                : "UPDATE users SET status = 'Inactive' WHERE user_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Status update failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Stage 2 Requirement 3.4: Transactional User Deletion with Audit Logging.
     * Enforces strict ACID transaction: user deletion + audit log insertion.
     */
    public boolean deleteUserWithAudit(User targetUser, String adminUsername, String remarks) {
        String deleteSql = "DELETE FROM users WHERE user_id = ? AND user_role = 'ROLE_USER'";
        String auditSql = "INSERT INTO audit_log (admin_username, deleted_user_id, deleted_username, deleted_full_name, deleted_email, action_type, remarks) "
                + "VALUES (?, ?, ?, ?, ?, 'DELETE_USER', ?)";

        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            con.setAutoCommit(false); // Begin Transaction

            // 1. Delete user
            try (PreparedStatement deletePs = con.prepareStatement(deleteSql)) {
                deletePs.setInt(1, targetUser.getUserId());
                int rows = deletePs.executeUpdate();
                if (rows == 0) {
                    con.rollback();
                    return false;
                }
            }

            // 2. Insert into audit_log
            try (PreparedStatement auditPs = con.prepareStatement(auditSql)) {
                auditPs.setString(1, adminUsername);
                auditPs.setInt(2, targetUser.getUserId());
                auditPs.setString(3, targetUser.getUsername());
                auditPs.setString(4, targetUser.getFullName());
                auditPs.setString(5, targetUser.getEmail());
                auditPs.setString(6, remarks != null ? remarks : "Administrative deletion");
                auditPs.executeUpdate();
            }

            con.commit(); // Commit Transaction
            return true;

        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException rbEx) {
                    System.err.println("[TRANSACTION ERROR] Rollback failed: " + rbEx.getMessage());
                }
            }
            System.err.println("[TRANSACTION ERROR] Delete user transaction failed: " + e.getMessage());
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException cEx) {
                    // Ignore
                }
            }
        }
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setMobile(rs.getString("mobile"));
        user.setLocation(rs.getString("location"));
        user.setPan(rs.getString("pan"));
        user.setCibilScore(rs.getInt("cibil_score"));
        user.setUserRole(rs.getString("user_role"));
        user.setStatus(rs.getString("status"));
        user.setFailedLoginAttempts(rs.getInt("failed_login_attempts"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        return user;
    }
}
