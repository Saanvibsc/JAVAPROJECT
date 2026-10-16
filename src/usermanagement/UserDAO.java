package usermanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class UserDAO {

    // Register user
    public boolean registerUser(User user) {

        String sql = "INSERT INTO users "
                + "(fname, lname, mobile, email, location, "
                + "lastmodifieddate, status, username, password, failed_attempts) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getFname());
            ps.setString(2, user.getLname());
            ps.setString(3, user.getMobile());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getLocation());

            ps.setTimestamp(6,
                    new Timestamp(System.currentTimeMillis()));

            ps.setString(7, "Active");
            ps.setString(8, user.getUsername());
            ps.setString(9, user.getPassword());
            ps.setInt(10, 0);

            ps.executeUpdate();

            return true;

        } catch (Exception e) {
            System.out.println("Registration failed!");
            e.printStackTrace();
            return false;
        }
    }

    // Check username
    public boolean usernameExists(String username) {

        String sql = "SELECT username FROM users WHERE username = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Login
    public User login(String login, String password) {

        String sql = "SELECT * FROM users "
                + "WHERE (username = ? OR email = ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, login);
            ps.setString(2, login);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("User does not exist.");
                return null;
            }

            String status = rs.getString("status");
            int failedAttempts = rs.getInt("failed_attempts");

            // Check if account is locked
            if (status.equalsIgnoreCase("Inactive")) {
                System.out.println(
                        "Your account is locked. Please contact support.");
                return null;
            }

            String storedPassword = rs.getString("password");

            // Correct password
            if (storedPassword.equals(password)) {

                resetFailedAttempts(rs.getInt("id"));

                return new User(
                        rs.getString("fname"),
                        rs.getString("lname"),
                        rs.getString("mobile"),
                        rs.getString("email"),
                        rs.getString("location"),
                        rs.getString("username"),
                        rs.getString("password")
                );
            }

            // Wrong password
            failedAttempts++;

            if (failedAttempts >= 3) {

                lockAccount(rs.getInt("id"));

                System.out.println(
                        "Account locked after 3 failed login attempts.");

            } else {

                int remaining = 3 - failedAttempts;

                updateFailedAttempts(
                        rs.getInt("id"),
                        failedAttempts
                );

                System.out.println(
                        "Invalid password.");
                System.out.println(
                        "Attempts remaining: " + remaining);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Update failed attempts
    private void updateFailedAttempts(int id, int attempts) {

        String sql =
                "UPDATE users SET failed_attempts = ? WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, attempts);
            ps.setInt(2, id);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lock account
    private void lockAccount(int id) {

        String sql =
                "UPDATE users SET status = 'Inactive', "
                + "failed_attempts = 3 WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Reset failed attempts after successful login
    private void resetFailedAttempts(int id) {

        String sql =
                "UPDATE users SET failed_attempts = 0 WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean resetPassword(String login, String newPassword) {

        String sql = "UPDATE users SET password = ?, "
                + "lastmodifieddate = NOW() "
                + "WHERE username = ? OR email = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setString(2, login);
            ps.setString(3, login);

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}