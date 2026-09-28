package usermanagement;

import java.util.Scanner;

public class UserManagementSystem {

    static Scanner sc = new Scanner(System.in);
    static UserDAO userDAO = new UserDAO();

    public static void main(String[] args) {

        while (true) {

            System.out.println("\n===== USER MANAGEMENT SYSTEM =====");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Forgot Password");
            System.out.println("4. Exit");

            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    register();
                    break;

                case 2:
                    login();
                    break;

                case 3:
                    forgotPassword();
                    break;

                case 4:
                    System.out.println("Thank you!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // ================= REGISTER =================

    static void register() {

        System.out.println("\n===== USER REGISTRATION =====");

        System.out.print("First Name: ");
        String fname = sc.nextLine();

        System.out.print("Last Name: ");
        String lname = sc.nextLine();

        System.out.print("Mobile Number: ");
        String mobile = sc.nextLine();

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Location: ");
        String location = sc.nextLine();

        System.out.print("Username: ");
        String username = sc.nextLine();

        System.out.print("Password: ");
        String password = sc.nextLine();

        if (fname.isEmpty() || lname.isEmpty()
                || mobile.isEmpty() || email.isEmpty()
                || username.isEmpty() || password.isEmpty()) {

            System.out.println("All mandatory fields are required.");
            return;
        }

        if (!mobile.matches("\\d{10}")) {

            System.out.println("Mobile number must contain 10 digits.");
            return;
        }

        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

            System.out.println("Invalid email format.");
            return;
        }

        if (!isValidPassword(password)) {

            System.out.println(
                    "Password must contain at least 8 characters, "
                    + "one uppercase, one lowercase, one digit "
                    + "and one special character.");

            return;
        }

        if (userDAO.usernameExists(username)) {

            System.out.println("Username already exists.");
            return;
        }

        User user = new User(
                fname,
                lname,
                mobile,
                email,
                location,
                username,
                password
        );

        if (userDAO.registerUser(user)) {

            System.out.println("Registration successful!");
            System.out.println("Your account is Active.");
        }
    }

    // ================= PASSWORD VALIDATION =================

    static boolean isValidPassword(String password) {

        return password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*\\d.*")
                && password.matches(".*[^a-zA-Z0-9].*");
    }

    // ================= LOGIN =================

    static void login() {

        System.out.println("\n===== LOGIN =====");

        System.out.print("Enter Username or Email: ");
        String login = sc.nextLine();

        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        if (login.isEmpty() || password.isEmpty()) {

            System.out.println(
                    "Username/Email and Password cannot be empty.");

            return;
        }

        User user = userDAO.login(login, password);

        if (user != null) {

            System.out.println(
                    "\nWelcome "
                    + user.getFname()
                    + " "
                    + user.getLname());

            postLoginMenu(user);
        }
    }

    // ================= POST LOGIN MENU =================

    static void postLoginMenu(User user) {

        while (true) {

            System.out.println(
                    "\nWelcome, "
                    + user.getFname()
                    + " "
                    + user.getLname());

            System.out.println("1. Logout");

            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {

                System.out.println("You have been logged out.");
                return;

            } else {

                System.out.println("Invalid choice.");
            }
        }
    }

    // ================= FORGOT PASSWORD =================

    static void forgotPassword() {

        System.out.println("\n===== FORGOT PASSWORD =====");

        System.out.print("Enter Username or Email: ");
        String login = sc.nextLine();

        if (login.isEmpty()) {

            System.out.println("Username or Email cannot be empty.");
            return;
        }

        System.out.print("Enter New Password: ");
        String newPassword = sc.nextLine();

        if (!isValidPassword(newPassword)) {

            System.out.println(
                    "Password must contain at least 8 characters, "
                    + "one uppercase, one lowercase, one digit "
                    + "and one special character.");

            return;
        }

        boolean result = userDAO.resetPassword(login, newPassword);

        if (result) {

            System.out.println("Password reset successfully!");

        } else {

            System.out.println("User not found.");
        }
    }
}