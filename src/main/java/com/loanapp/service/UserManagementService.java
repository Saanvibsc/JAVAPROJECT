package com.loanapp.service;

import com.loanapp.dao.UserDAO;
import com.loanapp.model.User;
import com.loanapp.util.CredentialGenerator;
import com.loanapp.util.ValidationUtil;

import java.util.Scanner;

/**
 * Handles User Registration, Authentication, Account Lockout, and Password Reset (Stages 1 and 4).
 */
public class UserManagementService {

    private final UserDAO userDAO;

    public UserManagementService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Stage 1 & 4: Integrated User Registration with PAN, CIBIL, Auto Credentials, and Email Delivery.
     */
    public void registerUser(Scanner scanner) {
        System.out.println("\n=============================================================");
        System.out.println("                 NEW BORROWER REGISTRATION                   ");
        System.out.println("=============================================================");

        // 1. First Name
        String fname;
        while (true) {
            System.out.print("Enter First Name: ");
            fname = scanner.nextLine().trim();
            if (ValidationUtil.validateFirstName(fname)) break;
            System.out.println("[VALIDATION ERROR] First Name must contain only letters (min 2 characters).");
        }

        // 2. Last Name
        String lname;
        while (true) {
            System.out.print("Enter Last Name: ");
            lname = scanner.nextLine().trim();
            if (ValidationUtil.validateLastName(lname)) break;
            System.out.println("[VALIDATION ERROR] Last Name must contain only letters (min 2 characters).");
        }

        // 3. Mobile Number (10 digits)
        String mobile;
        while (true) {
            System.out.print("Enter Mobile Number (10 digits): ");
            mobile = scanner.nextLine().trim();
            if (ValidationUtil.validateMobile(mobile)) break;
            System.out.println("[VALIDATION ERROR] Mobile number must contain exactly 10 digits.");
        }

        // 4. Email Address
        String email;
        while (true) {
            System.out.print("Enter Email Address: ");
            email = scanner.nextLine().trim();
            if (!ValidationUtil.validateEmail(email)) {
                System.out.println("[VALIDATION ERROR] Invalid email format (e.g. name@example.com).");
                continue;
            }
            if (userDAO.emailExists(email)) {
                System.out.println("[VALIDATION ERROR] Email address is already registered. Please login or use forgot password.");
                return;
            }
            break;
        }

        // 5. Location
        String location;
        while (true) {
            System.out.print("Enter Location / City: ");
            location = scanner.nextLine().trim();
            if (ValidationUtil.validateLocation(location)) break;
            System.out.println("[VALIDATION ERROR] Location is required and cannot be empty.");
        }

        // 6. PAN Card (Stage 4 Requirement)
        String pan;
        while (true) {
            System.out.print("Enter PAN Card Number (e.g. ABCDE1234F): ");
            pan = scanner.nextLine().trim().toUpperCase();
            if (ValidationUtil.validatePAN(pan)) break;
            System.out.println("[VALIDATION ERROR] Invalid PAN format. PAN must be 5 uppercase letters, 4 digits, 1 uppercase letter.");
        }

        // Stage 4: Simulate CIBIL Score generation (600 - 900)
        int cibilScore = CibilService.generateCibilScore();

        // Stage 4: Generate or enter Username & Password
        System.out.println("\nSelect Credential Generation Method:");
        System.out.println("1. Automatically Generate Username & Secure Password (Stage 4)");
        System.out.println("2. Enter Custom Username and Password");
        System.out.print("Choice (1-2, default=1): ");
        String credChoice = scanner.nextLine().trim();

        String username;
        String password;

        if ("2".equals(credChoice)) {
            while (true) {
                System.out.print("Enter Desired Username: ");
                username = scanner.nextLine().trim();
                if (username.isEmpty()) {
                    System.out.println("[VALIDATION ERROR] Username cannot be empty.");
                    continue;
                }
                if (userDAO.usernameExists(username)) {
                    System.out.println("[VALIDATION ERROR] Username already exists. Please choose another.");
                    continue;
                }
                break;
            }

            while (true) {
                System.out.print("Enter Password (min 8 chars, 1 upper, 1 lower, 1 digit, 1 symbol): ");
                password = scanner.nextLine();
                if (ValidationUtil.validatePassword(password)) break;
                System.out.println("[VALIDATION ERROR] Password must have at least 8 chars, 1 uppercase, 1 lowercase, 1 digit, and 1 symbol.");
            }
        } else {
            // Automatic generation as per Stage 4
            String baseUsername = CredentialGenerator.generateUsername(fname, lname, mobile);
            username = baseUsername;
            int counter = 1;
            while (userDAO.usernameExists(username)) {
                username = baseUsername + counter;
                counter++;
            }
            password = CredentialGenerator.generatePassword();
        }

        // Create and persist user
        User newUser = new User(0, fname, lname, username, password, email, mobile, location, pan, cibilScore, "ROLE_USER", "Active", 0);
        boolean saved = userDAO.registerUser(newUser);

        if (saved) {
            System.out.println("\n=============================================================");
            System.out.println("                 REGISTRATION SUCCESSFUL!                    ");
            System.out.println("=============================================================");
            System.out.println("Full Name    : " + newUser.getFullName());
            System.out.println("Username     : " + username);
            System.out.println("Password     : " + password);
            System.out.println("PAN Number   : " + pan);
            System.out.println("CIBIL Score  : " + cibilScore + " (" + CibilService.getCreditTier(cibilScore) + ")");
            System.out.println("Account Status: Active");
            System.out.println("=============================================================");

            // Stage 4: Send credentials via JavaMail SMTP
            EmailService.sendCredentials(email, newUser.getFullName(), username, password, cibilScore);

        } else {
            System.out.println("[ERROR] Failed to save user details in database. Please check connection.");
        }
    }

    /**
     * Stage 1: Login using Username or Email + Password.
     * Enforces account lockout after 3 consecutive failed login attempts.
     */
    public User login(Scanner scanner) {
        System.out.println("\n=============================================================");
        System.out.println("                            LOGIN                            ");
        System.out.println("=============================================================");
        System.out.print("Enter Username or Email: ");
        String identifier = scanner.nextLine().trim();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        if (identifier.isEmpty() || password.isEmpty()) {
            System.out.println("[VALIDATION ERROR] Username/Email and Password cannot be empty.");
            return null;
        }

        User user = userDAO.findByUsernameOrEmail(identifier);
        if (user == null) {
            System.out.println("[AUTH ERROR] User does not exist.");
            return null;
        }

        // If user is locked/inactive
        if (!user.isActive()) {
            System.out.println("[ACCOUNT LOCKED] Your account is locked or inactive. Please contact support/administrator.");
            return null;
        }

        // Verify password
        boolean passwordMatches = com.loanapp.util.PasswordUtil.verifyPassword(password, user.getPassword());

        if (passwordMatches) {
            // Reset failed login attempts upon successful login
            if (user.getFailedLoginAttempts() > 0) {
                userDAO.resetFailedAttempts(user.getUserId());
                user.setFailedLoginAttempts(0);
            }
            System.out.println("\n[SUCCESS] Welcome, " + user.getFullName() + "!");
            return user;
        }

        // Incorrect password handling
        // Admins are exempt from failed login attempts lockout
        if (user.isAdmin()) {
            System.out.println("[AUTH ERROR] Invalid admin credentials.");
            return null;
        }

        int currentAttempts = user.getFailedLoginAttempts() + 1;
        userDAO.recordFailedAttempt(user.getUserId(), currentAttempts);
        user.setFailedLoginAttempts(currentAttempts);

        if (currentAttempts >= 3) {
            System.out.println("\n[ACCOUNT LOCKED] Your account has been locked after 3 failed login attempts.");
            System.out.println("Please contact administrator/support to unlock your account.");
        } else {
            int remaining = 3 - currentAttempts;
            System.out.println("[AUTH ERROR] Invalid password.");
            System.out.println("Attempts remaining: " + remaining);
        }

        return null;
    }

    /**
     * Stage 1: Forgot Password / Password Reset.
     */
    public void forgotPassword(Scanner scanner) {
        System.out.println("\n=============================================================");
        System.out.println("                       FORGOT PASSWORD                       ");
        System.out.println("=============================================================");
        System.out.print("Enter registered Username or Email: ");
        String identifier = scanner.nextLine().trim();

        if (identifier.isEmpty()) {
            System.out.println("[VALIDATION ERROR] Username or Email cannot be empty.");
            return;
        }

        User user = userDAO.findByUsernameOrEmail(identifier);
        if (user == null) {
            System.out.println("[INFO] User not found.");
            return;
        }

        String newPassword;
        while (true) {
            System.out.print("Enter New Password: ");
            newPassword = scanner.nextLine();
            if (ValidationUtil.validatePassword(newPassword)) break;
            System.out.println("[VALIDATION ERROR] Password must contain at least 8 characters, "
                    + "one uppercase, one lowercase, one digit, and one special character.");
        }

        boolean updated = userDAO.resetPassword(identifier, newPassword);
        if (updated) {
            System.out.println("[SUCCESS] Password reset successfully! You can now log in with your new password.");
            // Send SMTP notification
            EmailService.sendPasswordResetEmail(user.getEmail(), user.getFullName(), newPassword);
        } else {
            System.out.println("[ERROR] Failed to update password in database.");
        }
    }
}
