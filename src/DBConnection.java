import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection.java
 * Provides a centralized JDBC connection to the MySQL database.
 */
public class DBConnection {

    // Update DB_USER and DB_PASSWORD according to your local MySQL setup
    private static final String DB_URL = "jdbc:mysql://localhost:3306/loan_system_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    static {
        try {
            // Load MySQL Type-4 JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[ERROR] MySQL JDBC Driver not found. Add mysql-connector-j JAR to lib/ folder.");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
