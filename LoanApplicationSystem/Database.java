import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Database utility class managing JDBC connections and queries.
 * Strictly uses PreparedStatement for all parameter-bound database operations.
 */
public class Database {
    private static final String URL = System.getenv("DB_URL") != null 
            ? System.getenv("DB_URL") 
            : "jdbc:mysql://localhost:3306/loan_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = System.getenv("DB_USER") != null 
            ? System.getenv("DB_USER") 
            : "root";
    private static final String PASSWORD = System.getenv("DB_PASSWORD") != null 
            ? System.getenv("DB_PASSWORD") 
            : "root";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // Driver loaded automatically in modern JDBC
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Initializes database tables if not already present.
     */
    public static void initializeDatabase() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "user_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "full_name VARCHAR(100) NOT NULL, " +
                "email VARCHAR(100) NOT NULL UNIQUE, " +
                "mobile_number VARCHAR(20) NOT NULL, " +
                "password VARCHAR(255) NOT NULL, " +
                "role VARCHAR(20) NOT NULL DEFAULT 'USER'" +
                ")";

        String createLoansTable = "CREATE TABLE IF NOT EXISTS loan_applications (" +
                "loan_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id INT NOT NULL, " +
                "loan_amount DOUBLE NOT NULL, " +
                "loan_type VARCHAR(50) NOT NULL, " +
                "loan_duration VARCHAR(50) NOT NULL, " +
                "interest_rate DOUBLE NOT NULL, " +
                "annual_income DOUBLE NOT NULL, " +
                "purpose TEXT NOT NULL, " +
                "status VARCHAR(30) NOT NULL DEFAULT 'Pending', " +
                "application_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "rejection_reason TEXT DEFAULT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE" +
                ")";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createLoansTable);

            // Insert default Admin if missing
            String checkAdmin = "SELECT COUNT(*) FROM users WHERE email = 'admin@loanapp.com'";
            ResultSet rs = stmt.executeQuery(checkAdmin);
            if (rs.next() && rs.getInt(1) == 0) {
                String insertAdmin = "INSERT INTO users (full_name, email, mobile_number, password, role) " +
                        "VALUES ('System Administrator', 'admin@loanapp.com', '9876543210', 'admin123', 'ADMIN')";
                stmt.executeUpdate(insertAdmin);
            }

            // Insert sample user if missing
            String checkUser = "SELECT COUNT(*) FROM users WHERE email = 'rahul.sharma@example.com'";
            rs = stmt.executeQuery(checkUser);
            if (rs.next() && rs.getInt(1) == 0) {
                String insertUser = "INSERT INTO users (full_name, email, mobile_number, password, role) " +
                        "VALUES ('Rahul Sharma', 'rahul.sharma@example.com', '9876543211', 'user123', 'USER')";
                stmt.executeUpdate(insertUser);
            }
        } catch (SQLException e) {
            System.err.println("Notice: Could not connect to MySQL server at " + URL + " (" + e.getMessage() + ").");
            System.err.println("Ensure MySQL is running and database 'loan_db' is created.");
        }
    }

    /**
     * Authenticates a user by email and password.
     */
    public static User authenticateUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("mobile_number"),
                            rs.getString("password"),
                            rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Registers a new user.
     */
    public static boolean registerUser(User user) {
        String sql = "INSERT INTO users (full_name, email, mobile_number, password, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getMobileNumber());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getRole());

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
            System.err.println("Registration error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Submits and saves a new loan application to MySQL.
     */
    public static int saveLoanApplication(LoanApplication loan) {
        String sql = "INSERT INTO loan_applications (user_id, loan_amount, loan_type, loan_duration, " +
                "interest_rate, annual_income, purpose, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, loan.getUserId());
            ps.setDouble(2, loan.getLoanAmount());
            ps.setString(3, loan.getLoanType());
            ps.setString(4, loan.getLoanDuration());
            ps.setDouble(5, loan.getInterestRate());
            ps.setDouble(6, loan.getAnnualIncome());
            ps.setString(7, loan.getPurpose());
            ps.setString(8, loan.getStatus()); // "Pending"

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        loan.setLoanId(id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error saving loan application: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Retrieves all loan applications with borrower details (via JDBC JOIN).
     */
    public static List<LoanApplication> getAllLoanApplications() {
        List<LoanApplication> list = new ArrayList<>();
        String sql = "SELECT l.*, u.full_name, u.email, u.mobile_number " +
                "FROM loan_applications l " +
                "JOIN users u ON l.user_id = u.user_id " +
                "ORDER BY l.loan_id DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LoanApplication loan = mapResultSetToLoan(rs);
                list.add(loan);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching loan applications: " + e.getMessage());
        }
        return list;
    }

    /**
     * Retrieves an individual loan application by ID (with borrower details).
     */
    public static LoanApplication getLoanApplicationById(int loanId) {
        String sql = "SELECT l.*, u.full_name, u.email, u.mobile_number " +
                "FROM loan_applications l " +
                "JOIN users u ON l.user_id = u.user_id " +
                "WHERE l.loan_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, loanId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLoan(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching loan details: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves loan applications submitted by a specific user.
     */
    public static List<LoanApplication> getLoansByUserId(int userId) {
        List<LoanApplication> list = new ArrayList<>();
        String sql = "SELECT l.*, u.full_name, u.email, u.mobile_number " +
                "FROM loan_applications l " +
                "JOIN users u ON l.user_id = u.user_id " +
                "WHERE l.user_id = ? " +
                "ORDER BY l.loan_id DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToLoan(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user loans: " + e.getMessage());
        }
        return list;
    }

    /**
     * Updates loan status (e.g. Approved, Rejected) and records rejection reason if applicable.
     */
    public static boolean updateLoanStatus(int loanId, String newStatus, String rejectionReason) {
        String sql = "UPDATE loan_applications SET status = ?, rejection_reason = ? WHERE loan_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, rejectionReason);
            ps.setInt(3, loanId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating loan status: " + e.getMessage());
        }
        return false;
    }

    private static LoanApplication mapResultSetToLoan(ResultSet rs) throws SQLException {
        LoanApplication loan = new LoanApplication();
        loan.setLoanId(rs.getInt("loan_id"));
        loan.setUserId(rs.getInt("user_id"));
        loan.setLoanAmount(rs.getDouble("loan_amount"));
        loan.setLoanType(rs.getString("loan_type"));
        loan.setLoanDuration(rs.getString("loan_duration"));
        loan.setInterestRate(rs.getDouble("interest_rate"));
        loan.setAnnualIncome(rs.getDouble("annual_income"));
        loan.setPurpose(rs.getString("purpose"));
        loan.setStatus(rs.getString("status"));
        loan.setApplicationDate(rs.getString("application_date"));
        loan.setRejectionReason(rs.getString("rejection_reason"));
        loan.setUserName(rs.getString("full_name"));
        loan.setUserEmail(rs.getString("email"));
        loan.setUserMobileNumber(rs.getString("mobile_number"));
        return loan;
    }
}
