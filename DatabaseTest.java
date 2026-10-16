package com.loan;

import java.sql.Connection;

public class DatabaseTest {

    public static void main(String[] args) {

        Connection connection = DatabaseConnection.getConnection();

        if (connection != null) {
            System.out.println("JDBC TEST SUCCESSFUL!");
        } else {
            System.out.println("JDBC TEST FAILED!");
           
        }
    }
}