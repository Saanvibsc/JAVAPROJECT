package usermanagement;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/user_management";

    private static final String USER = "root";

    private static final String PASSWORD = "MySQL@1234";

    public static Connection getConnection() {

        Connection con = null;

        try {
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connected successfully!");
        } catch (Exception e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }

        return con;
    }
}