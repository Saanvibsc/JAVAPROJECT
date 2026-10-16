package com.loanapp.service;

import com.loanapp.dao.AuditDAO;
import com.loanapp.dao.LoanDAO;
import com.loanapp.dao.UserDAO;
import com.loanapp.model.AuditLog;
import com.loanapp.model.LoanApplication;
import com.loanapp.model.User;
import com.loanapp.util.ValidationUtil;

import java.util.List;
import java.util.Scanner;

/**
 * Stage 2: Admin Management Service providing User CRUD, Search, Status management, and Audit Logging.
 */
public class AdminManagementService {

    private final UserDAO userDAO;
    private final LoanDAO loanDAO;
    private final AuditDAO auditDAO;

    public AdminManagementService(UserDAO userDAO, LoanDAO loanDAO, AuditDAO auditDAO) {
        this.userDAO = userDAO;
        this.loanDAO = loanDAO;
        this.auditDAO = auditDAO;
    }

    /**
     * Requirement 3.1: Search Users (ROLE_USER only).
     */
    public void searchUsers(Scanner scanner) {
        System.out.println("\n--- SEARCH USERS ---");
        System.out.println("Search By: 1.First Name  2.Last Name  3.Email  4.Mobile  5.Username  6.Status");
        System.out.print("Enter search criterion (1-6): ");

        int criterion;
        try {
            criterion = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[VALIDATION ERROR] Criterion must be a valid number between 1 and 6.");
            return;
        }

        if (criterion < 1 || criterion > 6) {
            System.out.println("[VALIDATION ERROR] Please choose a valid search option (1-6).");
            return;
        }

        System.out.print("Enter search value: ");
        String value = scanner.nextLine().trim();
        if (value.isEmpty()) {
            System.out.println("[VALIDATION ERROR] Search value cannot be empty.");
            return;
        }

        if (criterion == 6 && !value.equalsIgnoreCase("Active") && !value.equalsIgnoreCase("Inactive")) {
            System.out.println("[VALIDATION ERROR] Status must be 'Active' or 'Inactive'.");
            return;
        }

        List<User> results = userDAO.searchUsers(criterion, value);
        if (results.isEmpty()) {
            System.out.println("[INFO] No matching users found.");
            return;
        }

        displayUsersTable(results);
    }

    /**
     * Requirement 3.2: View User Details.
     */
    public void viewUserDetails(Scanner scanner) {
        System.out.println("\n--- VIEW USER DETAILS ---");
        System.out.print("Enter User ID to view: ");
        int userId = readIntInput(scanner);
        if (userId <= 0) return;

        User user = userDAO.getUserById(userId);
        if (user == null) {
            System.out.println("[INFO] No user found with ID: " + userId);
            return;
        }

        System.out.println("\n+--------------------------------------------------------------------------+");
        System.out.println("|                              USER DETAILS                                |");
        System.out.println("+--------------------------------------------------------------------------+");
        System.out.println("  User ID     : " + user.getUserId());
        System.out.println("  Full Name   : " + user.getFullName());
        System.out.println("  Username    : " + user.getUsername());
        System.out.println("  Email       : " + user.getEmail());
        System.out.println("  Mobile      : " + user.getMobile());
        System.out.println("  Location    : " + (user.getLocation() != null ? user.getLocation() : "N/A"));
        System.out.println("  PAN Card    : " + (user.getPan() != null ? user.getPan() : "N/A"));
        System.out.println("  CIBIL Score : " + user.getCibilScore() + " (" + CibilService.getCreditTier(user.getCibilScore()) + ")");
        System.out.println("  Role        : " + user.getUserRole());
        System.out.println("  Status      : " + user.getStatus());
        System.out.println("  Failed Logins: " + user.getFailedLoginAttempts());
        System.out.println("+--------------------------------------------------------------------------+");

        // Associated loans
        List<LoanApplication> loans = loanDAO.getLoansByUserId(userId);
        if (loans.isEmpty()) {
            System.out.println("  [Loans] No loan applications found for this user.");
        } else {
            System.out.println("  [Associated Loan Applications]:");
            for (LoanApplication l : loans) {
                System.out.printf("   - Loan #%d | %s | Rs. %,.2f | Status: %s | Tenure: %s%n",
                        l.getLoanId(), l.getLoanType(), l.getLoanAmount(), l.getStatus(), l.getLoanDuration());
            }
        }
        System.out.println("+--------------------------------------------------------------------------+\n");
    }

    /**
     * Requirement 3.3: Edit User Information.
     */
    public void editUser(Scanner scanner) {
        System.out.println("\n--- EDIT USER INFORMATION ---");
        System.out.print("Enter User ID to edit: ");
        int userId = readIntInput(scanner);
        if (userId <= 0) return;

        User user = userDAO.getUserById(userId);
        if (user == null) {
            System.out.println("[INFO] User not found with ID: " + userId);
            return;
        }

        System.out.println("Current details for " + user.getFullName() + " (Press Enter to keep current):");

        System.out.print("New First Name [" + user.getFirstName() + "]: ");
        String fname = scanner.nextLine().trim();
        if (fname.isEmpty()) fname = user.getFirstName();
        else if (!ValidationUtil.validateFirstName(fname)) {
            System.out.println("[VALIDATION ERROR] Invalid first name format.");
            return;
        }

        System.out.print("New Last Name [" + user.getLastName() + "]: ");
        String lname = scanner.nextLine().trim();
        if (lname.isEmpty()) lname = user.getLastName();
        else if (!ValidationUtil.validateLastName(lname)) {
            System.out.println("[VALIDATION ERROR] Invalid last name format.");
            return;
        }

        System.out.print("New Email [" + user.getEmail() + "]: ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty()) email = user.getEmail();
        else if (!ValidationUtil.validateEmail(email)) {
            System.out.println("[VALIDATION ERROR] Invalid email format.");
            return;
        }

        System.out.print("New Mobile [" + user.getMobile() + "]: ");
        String mobile = scanner.nextLine().trim();
        if (mobile.isEmpty()) mobile = user.getMobile();
        else if (!ValidationUtil.validateMobile(mobile)) {
            System.out.println("[VALIDATION ERROR] Invalid mobile number format (10 digits).");
            return;
        }

        System.out.print("New Location [" + user.getLocation() + "]: ");
        String location = scanner.nextLine().trim();
        if (location.isEmpty()) location = user.getLocation();

        boolean updated = userDAO.updateUser(userId, fname, lname, email, mobile, location);
        if (updated) {
            System.out.println("[SUCCESS] User information updated successfully in database.");
            EmailService.sendAccountNotification(email, fname, "EDIT_USER",
                    "Your account details have been updated. If you did not request this change, please contact support immediately.");
        } else {
            System.out.println("[ERROR] Failed to update user.");
        }
    }

    /**
     * Activate User (status -> Active, reset failed attempts).
     */
    public void activateUser(Scanner scanner) {
        System.out.println("\n--- ACTIVATE USER ---");
        System.out.print("Enter User ID to activate: ");
        int userId = readIntInput(scanner);
        if (userId <= 0) return;

        User user = userDAO.getUserById(userId);
        if (user == null) {
            System.out.println("[INFO] User not found.");
            return;
        }

        if (user.isActive()) {
            System.out.println("[INFO] User is already Active.");
            return;
        }

        boolean ok = userDAO.updateStatus(userId, "Active");
        if (ok) {
            System.out.println("[SUCCESS] User " + user.getUsername() + " has been activated.");
            EmailService.sendAccountNotification(user.getEmail(), user.getFirstName(), "ACTIVATE_USER",
                    "Your account has been successfully activated by the admin. You can now log in to the system.");
        } else {
            System.out.println("[ERROR] Failed to activate user.");
        }
    }

    /**
     * Deactivate User (status -> Inactive).
     */
    public void deactivateUser(Scanner scanner) {
        System.out.println("\n--- DEACTIVATE USER ---");
        System.out.print("Enter User ID to deactivate: ");
        int userId = readIntInput(scanner);
        if (userId <= 0) return;

        User user = userDAO.getUserById(userId);
        if (user == null) {
            System.out.println("[INFO] User not found.");
            return;
        }

        if (!user.isActive()) {
            System.out.println("[INFO] User is already Inactive.");
            return;
        }

        boolean ok = userDAO.updateStatus(userId, "Inactive");
        if (ok) {
            System.out.println("[SUCCESS] User " + user.getUsername() + " has been deactivated.");
            EmailService.sendAccountNotification(user.getEmail(), user.getFirstName(), "DEACTIVATE_USER",
                    "Your account has been deactivated. Please contact support for more information.");
        } else {
            System.out.println("[ERROR] Failed to deactivate user.");
        }
    }

    /**
     * Requirement 3.4: Delete User with Confirmation and ACID Transactional Audit Logging.
     */
    public void deleteUser(Scanner scanner, String adminUsername) {
        System.out.println("\n--- DELETE USER (TRANSACTIONAL) ---");
        System.out.print("Enter User ID to delete: ");
        int userId = readIntInput(scanner);
        if (userId <= 0) return;

        User user = userDAO.getUserById(userId);
        if (user == null) {
            System.out.println("[INFO] User not found.");
            return;
        }

        if (user.isAdmin()) {
            System.out.println("[SECURITY] Cannot delete an administrator account.");
            return;
        }

        System.out.print("Are you sure you want to PERMANENTLY delete user '" + user.getFullName() + "' (" + user.getUsername() + ")? (yes/no): ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("yes") && !confirm.equalsIgnoreCase("y")) {
            System.out.println("[CANCELLED] Deletion cancelled by admin.");
            return;
        }

        System.out.print("Enter remarks / reason for deletion: ");
        String remarks = scanner.nextLine().trim();

        boolean deleted = userDAO.deleteUserWithAudit(user, adminUsername, remarks);
        if (deleted) {
            System.out.println("[SUCCESS] User deleted and audit log recorded in transaction.");
            EmailService.sendAccountNotification(user.getEmail(), user.getFirstName(), "DELETE_USER",
                    "Your account has been deleted from the system. If this was a mistake, please contact support immediately.");
        } else {
            System.out.println("[ERROR] Transaction failed. User was not deleted.");
        }
    }

    /**
     * View Audit Logs.
     */
    public void viewAuditLogs() {
        System.out.println("\n==========================================================================================");
        System.out.println("                                ADMINISTRATIVE AUDIT LOGS                                 ");
        System.out.println("==========================================================================================");
        List<AuditLog> logs = auditDAO.getAllAuditLogs();
        if (logs.isEmpty()) {
            System.out.println("[INFO] No audit log records found.");
            return;
        }

        System.out.printf("%-6s %-14s %-12s %-20s %-14s %-20s %-20s%n",
                "Log ID", "Admin", "User ID", "Deleted User", "Action", "Remarks", "Timestamp");
        System.out.println("------------------------------------------------------------------------------------------");
        for (AuditLog l : logs) {
            System.out.printf("%-6d %-14s %-12d %-20s %-14s %-20s %-20s%n",
                    l.getAuditId(), l.getAdminUsername(), l.getDeletedUserId(),
                    l.getDeletedFullName(), l.getActionType(),
                    (l.getRemarks() != null ? l.getRemarks() : "N/A"),
                    l.getActionTimestamp());
        }
        System.out.println("==========================================================================================\n");
    }

    private void displayUsersTable(List<User> list) {
        System.out.println("\n------------------------------------------------------------------------------------------");
        System.out.printf("%-6s %-20s %-14s %-28s %-13s %-10s %-8s%n",
                "ID", "Full Name", "Username", "Email", "Mobile", "Status", "CIBIL");
        System.out.println("------------------------------------------------------------------------------------------");
        for (User u : list) {
            System.out.printf("%-6d %-20s %-14s %-28s %-13s %-10s %-8d%n",
                    u.getUserId(), u.getFullName(), u.getUsername(),
                    u.getEmail(), u.getMobile(), u.getStatus(), u.getCibilScore());
        }
        System.out.println("------------------------------------------------------------------------------------------\n");
    }

    private int readIntInput(Scanner scanner) {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[VALIDATION ERROR] Please enter a valid number.");
            return -1;
        }
    }
}
