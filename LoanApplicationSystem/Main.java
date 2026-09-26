import java.util.Scanner;

/**
 * Main Entry Point for Stage 3 - Loan Application Management System.
 * Pure Java Console Application using Scanner and JDBC.
 */
public class Main {

    public static void main(String[] args) {
        // Initialize Database tables if connected to MySQL
        Database.initializeDatabase();

        Scanner scanner = new Scanner(System.in);
        System.out.println("=============================================================");
        System.out.println("       WELCOME TO LOAN APPLICATION SYSTEM (STAGE 3)          ");
        System.out.println("=============================================================");

        while (true) {
            System.out.println("\n---------------- MAIN MENU ----------------");
            System.out.println("1. Login");
            System.out.println("2. Register New User");
            System.out.println("3. Exit");
            System.out.print("Please enter your choice (1-3): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleLogin(scanner);
                    break;
                case "2":
                    handleRegister(scanner);
                    break;
                case "3":
                    System.out.println("\nThank you for using the Loan Application System. Goodbye!");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please select 1, 2, or 3.");
            }
        }
    }

    private static void handleLogin(Scanner scanner) {
        System.out.println("\n================== LOGIN ==================");
        System.out.print("Enter Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        if (email.isEmpty() || password.isEmpty()) {
            System.out.println("Error: Email and password cannot be empty.");
            return;
        }

        User user = Database.authenticateUser(email, password);

        if (user == null) {
            System.out.println("Login Failed: Invalid email or password.");
            return;
        }

        System.out.println("\nSUCCESS: Welcome, " + user.getFullName() + "! [Role: " + user.getRole() + "]");

        if (user.isAdmin()) {
            handleAdminMenu(scanner, user);
        } else {
            handleUserMenu(scanner, user);
        }
    }

    private static void handleRegister(Scanner scanner) {
        System.out.println("\n================ REGISTER ================");
        System.out.print("Enter Full Name: ");
        String fullName = scanner.nextLine().trim();

        System.out.print("Enter Email Address: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter Mobile Number: ");
        String mobile = scanner.nextLine().trim();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        if (fullName.isEmpty() || email.isEmpty() || mobile.isEmpty() || password.isEmpty()) {
            System.out.println("Error: All fields are required.");
            return;
        }

        User newUser = new User(fullName, email, mobile, password, "USER");
        boolean success = Database.registerUser(newUser);

        if (success) {
            System.out.println("Registration Successful! You can now log in with your credentials.");
        } else {
            System.out.println("Registration Failed. Email may already be in use.");
        }
    }

    /**
     * User Console Flow
     */
    private static void handleUserMenu(Scanner scanner, User loggedInUser) {
        while (true) {
            System.out.println("\n---------------- USER MENU ----------------");
            System.out.println("Logged in as: " + loggedInUser.getFullName() + " (User ID: " + loggedInUser.getUserId() + ")");
            System.out.println("1. Apply for Loan");
            System.out.println("2. View My Loan Applications");
            System.out.println("3. Logout");
            System.out.print("Enter choice (1-3): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    LoanManager.applyForLoan(scanner, loggedInUser);
                    break;
                case "2":
                    LoanManager.viewMyApplications(loggedInUser);
                    break;
                case "3":
                    System.out.println("Logged out successfully.");
                    return;
                default:
                    System.out.println("Invalid selection. Please enter 1, 2, or 3.");
            }
        }
    }

    /**
     * Admin Console Flow (Requirement 17)
     */
    private static void handleAdminMenu(Scanner scanner, User loggedInAdmin) {
        while (true) {
            System.out.println("\n--------------- ADMIN MENU ---------------");
            System.out.println("Logged in as: " + loggedInAdmin.getFullName() + " [ADMIN]");
            System.out.println("1. View Loan Applications");
            System.out.println("2. View Loan Details");
            System.out.println("3. Approve Loan");
            System.out.println("4. Reject Loan");
            System.out.println("5. Request More Info");
            System.out.println("6. Logout");
            System.out.print("Enter choice (1-6): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    LoanManager.viewAllLoanApplications();
                    break;
                case "2":
                    LoanManager.viewLoanDetails(scanner);
                    break;
                case "3":
                    LoanManager.approveLoan(scanner);
                    break;
                case "4":
                    LoanManager.rejectLoan(scanner);
                    break;
                case "5":
                    LoanManager.requestMoreInfo(scanner);
                    break;
                case "6":
                    System.out.println("Admin logged out successfully.");
                    return;
                default:
                    System.out.println("Invalid selection. Please choose an option from 1 to 6.");
            }
        }
    }
}
