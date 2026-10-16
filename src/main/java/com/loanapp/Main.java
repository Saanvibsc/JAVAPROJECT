package com.loanapp;

import com.loanapp.dao.AuditDAO;
import com.loanapp.dao.LoanDAO;
import com.loanapp.dao.UserDAO;
import com.loanapp.db.DatabaseConnection;
import com.loanapp.model.LoanType;
import com.loanapp.model.User;
import com.loanapp.service.AdminManagementService;
import com.loanapp.service.CibilService;
import com.loanapp.service.EMICalculator;
import com.loanapp.service.InterestRateCalculator;
import com.loanapp.service.LoanManager;
import com.loanapp.service.UserManagementService;
import com.loanapp.util.ValidationUtil;

import java.util.Scanner;

/**
 * Unified Main Entry Point for Loan Application Management System.
 * Pure Core Java Console Application using Scanner, PreparedStatement, and JDBC.
 * Completely integrates Stages 1 to 5 into a single coherent application.
 */
public class Main {

    private static final UserDAO userDAO = new UserDAO();
    private static final LoanDAO loanDAO = new LoanDAO();
    private static final AuditDAO auditDAO = new AuditDAO();

    private static final UserManagementService userService = new UserManagementService(userDAO);
    private static final AdminManagementService adminService = new AdminManagementService(userDAO, loanDAO, auditDAO);
    private static final LoanManager loanManager = new LoanManager(loanDAO);

    public static void main(String[] args) {
        // Initialize MySQL Database tables & seed accounts
        DatabaseConnection.initializeDatabase();

        Scanner scanner = new Scanner(System.in);

        printSystemBanner();

        while (true) {
            System.out.println("\n=============================================================");
            System.out.println("                         MAIN MENU                           ");
            System.out.println("=============================================================");
            System.out.println("  1. Login (Borrower / Administrator)");
            System.out.println("  2. Register New User (with PAN & CIBIL Verification)");
            System.out.println("  3. Forgot Password / Reset Password");
            System.out.println("  4. Quick Loan EMI & Interest Rate Calculator");
            System.out.println("  5. Exit");
            System.out.println("=============================================================");
            System.out.print("Please enter your choice (1-5): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleLoginFlow(scanner);
                    break;
                case "2":
                    userService.registerUser(scanner);
                    break;
                case "3":
                    userService.forgotPassword(scanner);
                    break;
                case "4":
                    handleQuickEmiCalculator(scanner);
                    break;
                case "5":
                    System.out.println("\nThank you for using the Loan Application Management System. Goodbye!");
                    scanner.close();
                    System.exit(0);
                    return;
                default:
                    System.out.println("[VALIDATION ERROR] Invalid choice. Please enter 1, 2, 3, 4, or 5.");
            }
        }
    }

    private static void handleLoginFlow(Scanner scanner) {
        User user = userService.login(scanner);
        if (user == null) {
            return;
        }

        if (user.isAdmin()) {
            handleAdminDashboard(scanner, user);
        } else {
            handleBorrowerDashboard(scanner, user);
        }
    }

    /**
     * Stage 1 & 3: Borrower Dashboard
     */
    private static void handleBorrowerDashboard(Scanner scanner, User loggedInUser) {
        while (true) {
            System.out.println("\n=============================================================");
            System.out.println("                     BORROWER DASHBOARD                      ");
            System.out.println("=============================================================");
            System.out.println("  Logged in as: " + loggedInUser.getFullName() + " (" + loggedInUser.getUsername() + ")");
            System.out.println("  User ID     : " + loggedInUser.getUserId());
            System.out.println("  CIBIL Score : " + loggedInUser.getCibilScore() + " [" + CibilService.getCreditTier(loggedInUser.getCibilScore()) + "]");
            System.out.println("-------------------------------------------------------------");
            System.out.println("  1. Apply for a Loan");
            System.out.println("  2. View My Loan Applications & Status");
            System.out.println("  3. View My Profile Details");
            System.out.println("  4. Change Password");
            System.out.println("  5. Quick EMI Calculator");
            System.out.println("  6. Logout");
            System.out.println("=============================================================");
            System.out.print("Enter option (1-6): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    loanManager.applyForLoan(scanner, loggedInUser);
                    break;
                case "2":
                    loanManager.viewMyApplications(loggedInUser);
                    break;
                case "3":
                    displayUserProfile(loggedInUser);
                    break;
                case "4":
                    handleUserPasswordChange(scanner, loggedInUser);
                    break;
                case "5":
                    handleQuickEmiCalculator(scanner);
                    break;
                case "6":
                    System.out.println("[LOGOUT] You have logged out successfully.");
                    return;
                default:
                    System.out.println("[VALIDATION ERROR] Invalid option. Please enter 1-6.");
            }
        }
    }

    /**
     * Stage 2, 3 & 5: Administrator Dashboard
     */
    private static void handleAdminDashboard(Scanner scanner, User loggedInAdmin) {
        while (true) {
            System.out.println("\n==========================================================================");
            System.out.println("                      ADMINISTRATOR PORTAL DASHBOARD                      ");
            System.out.println("==========================================================================");
            System.out.println("  Logged in as: " + loggedInAdmin.getFullName() + " [ROLE: ADMIN]");
            System.out.println("--------------------------------------------------------------------------");
            System.out.println("  [USER MANAGEMENT - STAGE 2]");
            System.out.println("   1. Search Users (by Name, Email, Mobile, Username, Status)");
            System.out.println("   2. View User Details & Loan History");
            System.out.println("   3. Edit User Information");
            System.out.println("   4. Activate User Account");
            System.out.println("   5. Deactivate User Account");
            System.out.println("   6. Delete User Account (ACID Transaction + Audit Log)");
            System.out.println("   7. View Administrative Audit Logs");
            System.out.println("--------------------------------------------------------------------------");
            System.out.println("  [LOAN SERVICING & DECISIONS - STAGES 3 & 5]");
            System.out.println("   8. View All Loan Applications");
            System.out.println("   9. View Loan Application Details");
            System.out.println("  10. Approve Loan (Calculates EMI, Persists to MySQL & Sends Email)");
            System.out.println("  11. Reject Loan (Mandatory Reason & Rejection Email)");
            System.out.println("  12. Request Additional Information from Applicant");
            System.out.println("--------------------------------------------------------------------------");
            System.out.println("  13. Logout");
            System.out.println("==========================================================================");
            System.out.print("Select an option (1-13): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": adminService.searchUsers(scanner); break;
                case "2": adminService.viewUserDetails(scanner); break;
                case "3": adminService.editUser(scanner); break;
                case "4": adminService.activateUser(scanner); break;
                case "5": adminService.deactivateUser(scanner); break;
                case "6": adminService.deleteUser(scanner, loggedInAdmin.getUsername()); break;
                case "7": adminService.viewAuditLogs(); break;
                case "8": loanManager.viewAllLoanApplications(); break;
                case "9": loanManager.viewLoanDetails(scanner); break;
                case "10": loanManager.approveLoan(scanner); break;
                case "11": loanManager.rejectLoan(scanner); break;
                case "12": loanManager.requestMoreInfo(scanner); break;
                case "13":
                    System.out.println("[LOGOUT] Administrator " + loggedInAdmin.getUsername() + " logged out.");
                    return;
                default:
                    System.out.println("[VALIDATION ERROR] Invalid selection. Please enter a number between 1 and 13.");
            }
        }
    }

    private static void displayUserProfile(User user) {
        System.out.println("\n+-------------------------------------------------------------+");
        System.out.println("|                     MY BORROWER PROFILE                     |");
        System.out.println("+-------------------------------------------------------------+");
        System.out.println("  Full Name   : " + user.getFullName());
        System.out.println("  Username    : " + user.getUsername());
        System.out.println("  Email       : " + user.getEmail());
        System.out.println("  Mobile      : " + user.getMobile());
        System.out.println("  Location    : " + (user.getLocation() != null ? user.getLocation() : "N/A"));
        System.out.println("  PAN Card    : " + (user.getPan() != null ? user.getPan() : "N/A"));
        System.out.println("  CIBIL Score : " + user.getCibilScore() + " (" + CibilService.getCreditTier(user.getCibilScore()) + ")");
        System.out.println("  Status      : " + user.getStatus());
        System.out.println("+-------------------------------------------------------------+\n");
    }

    private static void handleUserPasswordChange(Scanner scanner, User loggedInUser) {
        System.out.println("\n--- CHANGE PASSWORD ---");
        System.out.print("Enter current password: ");
        String current = scanner.nextLine();

        if (!com.loanapp.util.PasswordUtil.verifyPassword(current, loggedInUser.getPassword())) {
            System.out.println("[AUTH ERROR] Current password does not match.");
            return;
        }

        String newPass;
        while (true) {
            System.out.print("Enter new password: ");
            newPass = scanner.nextLine();
            if (ValidationUtil.validatePassword(newPass)) break;
            System.out.println("[VALIDATION ERROR] Password must have at least 8 chars, 1 uppercase, 1 lowercase, 1 digit, 1 symbol.");
        }

        boolean updated = userDAO.resetPassword(loggedInUser.getUsername(), newPass);
        if (updated) {
            loggedInUser.setPassword(com.loanapp.util.PasswordUtil.hashPassword(newPass));
            System.out.println("[SUCCESS] Password changed successfully.");
        } else {
            System.out.println("[ERROR] Failed to update password in database.");
        }
    }

    private static void handleQuickEmiCalculator(Scanner scanner) {
        System.out.println("\n=============================================================");
        System.out.println("             QUICK LOAN EMI & INTEREST CALCULATOR            ");
        System.out.println("=============================================================");
        try {
            System.out.print("Enter Principal Loan Amount (INR): ");
            double principal = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Enter Annual Interest Rate (%): ");
            double rate = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Enter Tenure in Months: ");
            int months = Integer.parseInt(scanner.nextLine().trim());

            if (principal <= 0 || rate < 0 || months <= 0) {
                System.out.println("[VALIDATION ERROR] Principal and tenure must be > 0, and rate must be >= 0.");
                return;
            }

            double emi = EMICalculator.calculateEMI(principal, rate, months);
            double totalPayable = EMICalculator.calculateTotalPayable(emi, months);
            double totalInterest = EMICalculator.calculateTotalInterest(principal, emi, months);

            System.out.println("\n----------------- EMI CALCULATION RESULTS -------------------");
            System.out.printf("Principal Amount   : Rs. %,.2f%n", principal);
            System.out.printf("Annual Rate        : %.2f%%%n", rate);
            System.out.printf("Tenure             : %d Months%n", months);
            System.out.printf("Monthly EMI        : Rs. %,.2f%n", emi);
            System.out.printf("Total Interest     : Rs. %,.2f%n", totalInterest);
            System.out.printf("Total Repayment    : Rs. %,.2f%n", totalPayable);
            System.out.println("-------------------------------------------------------------");

        } catch (NumberFormatException e) {
            System.out.println("[VALIDATION ERROR] Invalid numeric input.");
        }
    }

    private static void printSystemBanner() {
        System.out.println("==========================================================================");
        System.out.println("     LOAN APPLICATION MANAGEMENT SYSTEM (CORE JAVA & JDBC CONSOLE)        ");
        System.out.println("          Stages 1-5 Integrated | Indian Banking System (INR / Rs.)       ");
        System.out.println("==========================================================================");
    }
}
