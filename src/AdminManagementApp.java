import java.util.List;
import java.util.Scanner;

/**
 * AdminManagementApp.java
 * Stage 2: Console-Based Admin Management System using Scanner, Validation, and JDBC.
 */
public class AdminManagementApp {

    private static final AdminDAO adminDAO = new AdminDAO();
    private static final Scanner scanner = new Scanner(System.in);
    private static User loggedInAdmin = null;

    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println("             LOAN APPLICATION SYSTEM - STAGE 2: ADMIN PORTAL              ");
        System.out.println("==========================================================================");

        while (true) {
            if (loggedInAdmin == null) {
                if (!performAdminLogin()) {
                    System.out.print("\nWould you like to try logging in again? (yes/no): ");
                    String retry = scanner.nextLine().trim();
                    if (!retry.equalsIgnoreCase("yes") && !retry.equalsIgnoreCase("y")) {
                        System.out.println("Exiting system. Goodbye!");
                        break;
                    }
                }
            } else {
                showAdminDashboard();
            }
        }
        scanner.close();
    }

    private static boolean performAdminLogin() {
        System.out.println("\n--- ADMIN DIRECT LOGIN ---");
        System.out.print("Enter Admin Username : ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter Admin Password : ");
        String password = scanner.nextLine().trim();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("[VALIDATION ERROR] Username and password cannot be empty.");
            return false;
        }

        User admin = adminDAO.authenticateAdmin(username, password);
        if (admin != null) {
            loggedInAdmin = admin;
            System.out.println("\n[SUCCESS] Welcome, " + loggedInAdmin.getFullName()
                    + " (" + loggedInAdmin.getUserRole() + ")!");
            return true;
        }
        return false;
    }

    private static void showAdminDashboard() {
        if (!isAuthorizedAdmin()) return;

        System.out.println("\n==========================================================================");
        System.out.println("                             ADMIN DASHBOARD                              ");
        System.out.println("==========================================================================");
        System.out.println("  1. Search Users");
        System.out.println("  2. View User Details");
        System.out.println("  3. Edit User Information");
        System.out.println("  4. Activate User");
        System.out.println("  5. Deactivate User");
        System.out.println("  6. Delete User");
        System.out.println("  7. Logout");
        System.out.println("==========================================================================");
        System.out.print("Select an option (1-7): ");

        int choice = readIntInput();
        switch (choice) {
            case 1: handleSearchUsers(); break;
            case 2: handleViewUserDetails(); break;
            case 3: handleEditUser(); break;
            case 4: handleChangeStatus("Active"); break;
            case 5: handleChangeStatus("Inactive"); break;
            case 6: handleDeleteUser(); break;
            case 7:
                System.out.println("[LOGOUT] Admin " + loggedInAdmin.getUsername() + " logged out successfully.");
                loggedInAdmin = null;
                break;
            default:
                System.out.println("[VALIDATION ERROR] Invalid choice. Please enter a number between 1 and 7.");
        }
    }

    private static void handleSearchUsers() {
        if (!isAuthorizedAdmin()) return;

        System.out.println("\n--- SEARCH USERS ---");
        System.out.println("Search By: 1.First Name  2.Last Name  3.Email  4.Mobile  5.Username  6.Status");
        System.out.print("Enter search criterion (1-6): ");

        int criterion = readIntInput();
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

        List<User> results = adminDAO.searchUsers(criterion, value);
        if (results.isEmpty()) {
            System.out.println("[INFO] No matching users found.");
            return;
        }

        System.out.println("\n------------------------------------------------------------------------------------------");
        System.out.printf("%-6s %-18s %-14s %-28s %-13s %-10s%n",
                "ID", "Full Name", "Username", "Email", "Mobile", "Status");
        System.out.println("------------------------------------------------------------------------------------------");
        for (User u : results) {
            System.out.printf("%-6d %-18s %-14s %-28s %-13s %-10s%n",
                    u.getUserId(), u.getFullName(), u.getUsername(),
                    u.getEmail(), u.getMobile(), u.getStatus());
        }
        System.out.println("------------------------------------------------------------------------------------------");
    }

    private static void handleViewUserDetails() {
        if (!isAuthorizedAdmin()) return;

        System.out.println("\n--- VIEW USER DETAILS ---");
        System.out.print("Enter User ID to view: ");
        int userId = readIntInput();
        if (userId <= 0) {
            System.out.println("[VALIDATION ERROR] User ID must be a positive integer.");
            return;
        }

        User user = adminDAO.getUserById(userId);
        if (user == null) {
            System.out.println("[INFO] No regular user (ROLE_USER) found with ID: " + userId);
            return;
        }

        System.out.println("\n+--------------------------------------------------------------------------+");
        System.out.println("|                              USER DETAILS                                |");
        System.out.println("+--------------------------------------------------------------------------+");
        System.out.println("  User ID   : " + user.getUserId());
        System.out.println("  Full Name : " + user.getFullName());
        System.out.println("  Username  : " + user.getUsername());
        System.out.println("  Email     : " + user.getEmail());
        System.out.println("  Mobile    : " + user.getMobile());
        System.out.println("  Role      : " + user.getUserRole());
        System.out.println("  Status    : " + user.getStatus());
        adminDAO.displayLoanApplications(user.getUserId());
        System.out.println("+--------------------------------------------------------------------------+");
    }

    private static void handleEditUser() {
        if (!isAuthorizedAdmin()) return;

        System.out.println("\n--- EDIT USER INFORMATION ---");
        System.out.print("Enter User ID to edit: ");
        int userId = readIntInput();
        if (userId <= 0) {
            System.out.println("[VALIDATION ERROR] Invalid User ID.");
            return;
        }

        User user = adminDAO.getUserById(userId);
        if (user == null) {
            System.out.println("[INFO] No regular user (ROLE_USER) found with ID: " + userId);
            return;
        }

        System.out.println("Editing User: " + user.getFullName() + " (Press Enter to keep current value)");

        System.out.print("First Name [" + user.getFirstName() + "]: ");
        String firstName = scanner.nextLine().trim();
        if (!firstName.isEmpty()) {
            if (!firstName.matches("^[A-Za-z]{2,50}$")) {
                System.out.println("[VALIDATION ERROR] First Name must contain only letters (2-50 chars).");
                return;
            }
            user.setFirstName(firstName);
        }

        System.out.print("Last Name [" + user.getLastName() + "]: ");
        String lastName = scanner.nextLine().trim();
        if (!lastName.isEmpty()) {
            if (!lastName.matches("^[A-Za-z]{2,50}$")) {
                System.out.println("[VALIDATION ERROR] Last Name must contain only letters (2-50 chars).");
                return;
            }
            user.setLastName(lastName);
        }

        System.out.print("Email [" + user.getEmail() + "]: ");
        String email = scanner.nextLine().trim();
        if (!email.isEmpty()) {
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
                System.out.println("[VALIDATION ERROR] Invalid email format.");
                return;
            }
            if (adminDAO.isEmailTakenByOther(email, user.getUserId())) {
                System.out.println("[VALIDATION ERROR] Email is already registered to another user.");
                return;
            }
            user.setEmail(email);
        }

        System.out.print("Mobile [" + user.getMobile() + "]: ");
        String mobile = scanner.nextLine().trim();
        if (!mobile.isEmpty()) {
            if (!mobile.matches("^[6-9][0-9]{9}$")) {
                System.out.println("[VALIDATION ERROR] Mobile must be a valid 10-digit number starting with 6-9.");
                return;
            }
            user.setMobile(mobile);
        }

        if (adminDAO.updateUserDetails(user)) {
            System.out.println("[SUCCESS] User details updated successfully.");
            EmailNotificationService.sendEditEmail(user.getEmail(), user.getFirstName());
        } else {
            System.out.println("[ERROR] Failed to update user information.");
        }
    }

    private static void handleChangeStatus(String targetStatus) {
        if (!isAuthorizedAdmin()) return;

        System.out.println("\n--- " + targetStatus.toUpperCase() + " USER ---");
        System.out.print("Enter User ID to set as " + targetStatus + ": ");
        int userId = readIntInput();
        if (userId <= 0) {
            System.out.println("[VALIDATION ERROR] Invalid User ID.");
            return;
        }

        User user = adminDAO.getUserById(userId);
        if (user == null) {
            System.out.println("[INFO] No regular user (ROLE_USER) found with ID: " + userId);
            return;
        }

        if (user.getStatus().equalsIgnoreCase(targetStatus)) {
            System.out.println("[INFO] User '" + user.getUsername() + "' is already " + targetStatus + ".");
            return;
        }

        if (adminDAO.updateUserStatus(userId, targetStatus)) {
            System.out.println("[SUCCESS] User '" + user.getUsername() + "' status changed to " + targetStatus + ".");
            if ("Active".equalsIgnoreCase(targetStatus)) {
                EmailNotificationService.sendActivationEmail(user.getEmail(), user.getFirstName());
            } else {
                EmailNotificationService.sendDeactivationEmail(user.getEmail(), user.getFirstName());
            }
        } else {
            System.out.println("[ERROR] Could not change user status.");
        }
    }

    private static void handleDeleteUser() {
        if (!isAuthorizedAdmin()) return;

        System.out.println("\n--- DELETE USER ---");
        System.out.print("Enter User ID to delete: ");
        int userId = readIntInput();
        if (userId <= 0) {
            System.out.println("[VALIDATION ERROR] Invalid User ID.");
            return;
        }

        User user = adminDAO.getUserById(userId);
        if (user == null) {
            System.out.println("[INFO] No regular user (ROLE_USER) found with ID: " + userId + " (Admins cannot be deleted).");
            return;
        }

        System.out.println("Selected User: " + user.getFullName() + " | Username: "
                + user.getUsername() + " | Email: " + user.getEmail());
        System.out.print("Enter reason for deletion (for audit_log): ");
        String reason = scanner.nextLine().trim();
        if (reason.isEmpty()) {
            reason = "Deleted by Admin via Console Dashboard";
        }

        System.out.print("Are you sure you want to permanently delete this user? (yes/no): ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("[CANCELLED] User deletion aborted.");
            return;
        }

        if (adminDAO.deleteUserWithAudit(user, loggedInAdmin.getUsername(), reason)) {
            System.out.println("[SUCCESS] User deleted and action recorded in 'audit_log' table.");
            EmailNotificationService.sendDeletionEmail(user.getEmail(), user.getFirstName());
        } else {
            System.out.println("[ERROR] Failed to delete user.");
        }
    }

    private static boolean isAuthorizedAdmin() {
        if (loggedInAdmin == null || !"ROLE_ADMIN".equalsIgnoreCase(loggedInAdmin.getUserRole())) {
            System.out.println("[SECURITY ALERT] Unauthorized access! Only ROLE_ADMIN can perform this action.");
            return false;
        }
        return true;
    }

    private static int readIntInput() {
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
