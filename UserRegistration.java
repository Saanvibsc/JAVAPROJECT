package com.loan;

	import java.util.Scanner;
	
	import java.sql.Connection;
	import java.sql.PreparedStatement;
	import java.sql.SQLException;

	public class UserRegistration {

	    public static void registerUser(Scanner scanner) {

	        System.out.println();
	        System.out.println("=================================");
	        System.out.println("       USER REGISTRATION");
	        System.out.println("=================================");

	        // First Name
	        System.out.print("Enter First Name: ");
	        String fname = scanner.nextLine();

	        if (!ValidationUtil.validateFirstName(fname)) {
	            System.out.println("Invalid First Name.");
	            return;
	        }

	        // Last Name
	        System.out.print("Enter Last Name: ");
	        String lname = scanner.nextLine();

	        if (!ValidationUtil.validateLastName(lname)) {
	            System.out.println("Invalid Last Name.");
	            return;
	        }

	        // Mobile Number
	        System.out.print("Enter Mobile Number: ");
	        String mobile = scanner.nextLine();

	        if (!ValidationUtil.validateMobile(mobile)) {
	            System.out.println("Invalid Mobile Number.");
	            return;
	        }

	        // Email
	        System.out.print("Enter Email Address: ");
	        String email = scanner.nextLine();

	        if (!ValidationUtil.validateEmail(email)) {
	            System.out.println("Invalid Email Address.");
	            return;
	        }

	        // Location
	        System.out.print("Enter Location: ");
	        String location = scanner.nextLine();

	        if (!ValidationUtil.validateLocation(location)) {
	            System.out.println("Location is required.");
	            return;
	        }

	        // PAN
	        System.out.print("Enter PAN Card Number: ");
	        String pan = scanner.nextLine().trim().toUpperCase();

	        if (!ValidationUtil.validatePAN(pan)) {
	            System.out.println(
	                "Invalid PAN format. Please enter a valid PAN."
	            );
	            return;
	        }

	        System.out.println();
	        System.out.println("=================================");
	        System.out.println("    REGISTRATION SUCCESSFUL");
	        System.out.println("=================================");

	        System.out.println("First Name: " + fname);
	        System.out.println("Last Name: " + lname);
	        System.out.println("Mobile: " + mobile);
	        System.out.println("Email: " + email);
	        System.out.println("Location: " + location);
	        System.out.println("PAN: " + pan);
	     // Generate Username
	        String username =
	                CredentialGenerator.generateUsername(
	                        fname,
	                        lname,
	                        mobile
	                );

	        // Generate Password
	        String password =
	                CredentialGenerator.generatePassword();

	        // Display Generated Credentials
	        System.out.println();
	        System.out.println("=================================");
	        System.out.println("    GENERATED CREDENTIALS");
	        System.out.println("=================================");
	        System.out.println("Username: " + username);
	        System.out.println("Password: " + password);
	        System.out.println("=================================");

	        // Send Credentials by Email
	        EmailService.sendCredentials(
	                email,
	                username,
	                password
	        
	        );
	     // Store user registration details in MySQL
	        String sql = "INSERT INTO users "
	                + "(first_name, last_name, mobile, email, location, pan, username, password) "
	                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

	        try (Connection connection = DatabaseConnection.getConnection();
	             PreparedStatement statement = connection.prepareStatement(sql)) {

	            statement.setString(1, fname);
	            statement.setString(2, lname);
	            statement.setString(3, mobile);
	            statement.setString(4, email);
	            statement.setString(5, location);
	            statement.setString(6, pan);
	            statement.setString(7, username);
	            statement.setString(8, password);

	            int rowsInserted = statement.executeUpdate();

	            if (rowsInserted > 0) {
	                System.out.println("User details stored successfully in MySQL!");
	            }

	        } catch (SQLException e) {
	            System.out.println("Failed to store user details in MySQL.");
	            e.printStackTrace();
	            
	         // Generate CIBIL Score
	            int cibil = CibilService.generateCibilScore();

	            System.out.println();
	            System.out.println("=================================");
	            System.out.println("       CIBIL SCORE GENERATED");
	            System.out.println("=================================");
	            System.out.println("CIBIL Score: " + cibil);
	            System.out.println("=================================");
	        }
	    }
	}

