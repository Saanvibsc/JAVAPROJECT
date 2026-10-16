import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * LoanManager handles business operations according to Stage 3 specifications:
 * - User loan application input collection and validation (Scanner)
 * - Auto-determination of interest rate (Personal, Home, Education, Car)
 * - Months or years duration representation
 * - Status automatically set to "Pending"
 * - User ID automatically from logged-in user
 * - Admin loan review, approval, rejection, and "Needs More Info"
 * - Real SMTP email dispatching
 */
public class LoanManager {

    /**
     * Determines interest rate based on loan type.
     */
    public static double determineInterestRate(String loanType) {
        if (loanType == null) return 9.0;
        switch (loanType.trim().toLowerCase()) {
            case "personal":
                return 10.5;
            case "home":
                return 7.5;
            case "education":
                return 8.0;
            case "car":
                return 8.5;
            default:
                return 9.0;
        }
    }

    /**
     * Format Indian currency helper (Rupees)
     */
    public static String formatRupees(double amount) {
        return String.format("Rs. %,.2f", amount);
    }

    /**
     * Stage 3: User Side - Apply for Loan
     */
    public static void applyForLoan(Scanner scanner, User loggedInUser) {
        System.out.println("\n=============================================================");
        System.out.println("             APPLY FOR A NEW LOAN (STAGE 3)                  ");
        System.out.println("=============================================================");

        // 1. Loan Amount (Must be a positive number)
        double loanAmount = 0;
        while (true) {
            System.out.print("Enter Loan Amount (in INR, e.g. 500000 for Rs. 5 Lakhs): ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Error: Loan amount is required and cannot be empty.");
                continue;
            }
            try {
                loanAmount = Double.parseDouble(input);
                if (loanAmount > 0) {
                    break;
                } else {
                    System.out.println("Error: Loan amount must be greater than 0. Please re-enter.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid numeric input. Please enter a valid positive number.");
            }
        }

        // 2. Loan Type (Personal, Home, Education, Car - Must NOT silently default to Personal if empty)
        String loanType = "";
        while (true) {
            System.out.println("\nSelect Loan Type:");
            System.out.println("1. Personal Loan (Interest: 10.5% p.a.)");
            System.out.println("2. Home Loan (Interest: 7.5% p.a.)");
            System.out.println("3. Education Loan (Interest: 8.0% p.a.)");
            System.out.println("4. Car Loan (Interest: 8.5% p.a.)");
            System.out.print("Choose option (1-4) or enter loan type name: ");
            String typeChoice = scanner.nextLine().trim();

            if (typeChoice.isEmpty()) {
                System.out.println("Error: Loan type is required and cannot be empty. Please select 1, 2, 3, or 4.");
                continue;
            }

            if ("1".equals(typeChoice) || "personal".equalsIgnoreCase(typeChoice)) {
                loanType = "Personal";
                break;
            } else if ("2".equals(typeChoice) || "home".equalsIgnoreCase(typeChoice)) {
                loanType = "Home";
                break;
            } else if ("3".equals(typeChoice) || "education".equalsIgnoreCase(typeChoice)) {
                loanType = "Education";
                break;
            } else if ("4".equals(typeChoice) || "car".equalsIgnoreCase(typeChoice)) {
                loanType = "Car";
                break;
            } else {
                // Support other valid non-empty loan types specified by applicant
                loanType = Character.toUpperCase(typeChoice.charAt(0)) + typeChoice.substring(1);
                break;
            }
        }

        // 3. Interest Rate automatically determined based on selected loan type
        double interestRate = determineInterestRate(loanType);
        System.out.printf("Selected Loan Type: %s | Determined Interest Rate: %.2f%% p.a.\n", loanType, interestRate);

        // 4. Loan Duration (Must support Months or Years and clearly store/represent unit, > 0)
        String loanDuration = "";
        while (true) {
            System.out.println("\nSelect Duration Unit:");
            System.out.println("1. Months");
            System.out.println("2. Years");
            System.out.print("Choose unit (1-2): ");
            String unitChoice = scanner.nextLine().trim();
            String unit = "";
            if ("1".equals(unitChoice) || "months".equalsIgnoreCase(unitChoice) || "month".equalsIgnoreCase(unitChoice)) {
                unit = "Months";
            } else if ("2".equals(unitChoice) || "years".equalsIgnoreCase(unitChoice) || "year".equalsIgnoreCase(unitChoice)) {
                unit = "Years";
            } else {
                System.out.println("Error: Please select 1 for Months or 2 for Years.");
                continue;
            }

            System.out.print("Enter Loan Duration in " + unit + " (greater than 0): ");
            String durationInput = scanner.nextLine().trim();
            if (durationInput.isEmpty()) {
                System.out.println("Error: Loan duration cannot be empty.");
                continue;
            }
            try {
                int durationVal = Integer.parseInt(durationInput);
                if (durationVal > 0) {
                    loanDuration = durationVal + " " + unit;
                    break;
                } else {
                    System.out.println("Error: Duration must be greater than 0. Please re-enter.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid number. Please enter a valid positive integer.");
            }
        }

        // 5. Annual Income (Must be a positive number)
        double annualIncome = 0;
        while (true) {
            System.out.print("\nEnter Annual Income (in INR, e.g. 1200000 for Rs. 12 Lakhs): ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Error: Annual income is required and cannot be empty.");
                continue;
            }
            try {
                annualIncome = Double.parseDouble(input);
                if (annualIncome > 0) {
                    break;
                } else {
                    System.out.println("Error: Annual income must be greater than 0. Please re-enter.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid number. Please enter a valid positive number.");
            }
        }

        // 6. Purpose (Required, must not be empty)
        String purpose = "";
        while (true) {
            System.out.print("\nEnter Purpose of Loan: ");
            purpose = scanner.nextLine().trim();
            if (!purpose.isEmpty()) {
                break;
            } else {
                System.out.println("Error: Purpose is required and cannot be empty. Please enter.");
            }
        }

        // 7. Status automatically set to "Pending" & User ID automatically from loggedInUser
        LoanApplication loan = new LoanApplication(
                loggedInUser.getUserId(),
                loanAmount,
                loanType,
                loanDuration,
                interestRate,
                annualIncome,
                purpose
        );
        loan.setStatus("Pending");

        // 8. Save to MySQL via JDBC PreparedStatement
        int generatedId = Database.saveLoanApplication(loan);

        if (generatedId > 0) {
            System.out.println("\n-------------------------------------------------------------");
            System.out.println("SUCCESS: Your loan application has been submitted successfully!");
            System.out.printf("Application Reference ID: %d\n", generatedId);
            System.out.printf("Applicant: %s (User ID: %d)\n", loggedInUser.getFullName(), loggedInUser.getUserId());
            System.out.printf("Principal Amount: %s | Type: %s | Duration: %s\n", formatRupees(loanAmount), loanType, loanDuration);
            System.out.printf("Interest Rate: %.2f%% p.a. | Initial Status: Pending\n", interestRate);
            System.out.println("Your loan application has been recorded in the database.");
            System.out.println("-------------------------------------------------------------");
        } else {
            System.out.println("Error: Failed to record loan application in the database.");
        }
    }

    /**
     * User Side: View My Loan Applications
     */
    public static void viewMyApplications(User loggedInUser) {
        List<LoanApplication> userLoans = Database.getLoansByUserId(loggedInUser.getUserId());

        if (userLoans.isEmpty()) {
            System.out.println("\nYou currently have no loan applications on file.");
            return;
        }

        System.out.println("\n===================================================================================================");
        System.out.printf("                       LOAN APPLICATIONS FOR: %s (ID: %d)\n", loggedInUser.getFullName(), loggedInUser.getUserId());
        System.out.println("===================================================================================================");
        System.out.printf("%-8s | %-16s | %-12s | %-14s | %-10s | %-16s | %s\n",
                "Loan ID", "Loan Amount", "Loan Type", "Duration", "Rate", "Status", "Date");
        System.out.println("---------------------------------------------------------------------------------------------------");

        for (LoanApplication l : userLoans) {
            System.out.printf("%-8d | %-16s | %-12s | %-14s | %-9.2f%% | %-16s | %s\n",
                    l.getLoanId(),
                    formatRupees(l.getLoanAmount()),
                    l.getLoanType(),
                    l.getLoanDuration(),
                    l.getInterestRate(),
                    l.getStatus(),
                    l.getApplicationDate() != null ? l.getApplicationDate() : "N/A");
        }
        System.out.println("---------------------------------------------------------------------------------------------------");
    }

    /**
     * Stage 3: Admin Side - 1. View Loan Applications
     * Displays: Loan ID, User's Full Name, Loan Amount, Loan Type, Application Date, Status
     */
    public static void viewAllLoanApplications() {
        List<LoanApplication> allLoans = Database.getAllLoanApplications();

        if (allLoans.isEmpty()) {
            System.out.println("\nNo loan applications found in the database.");
            return;
        }

        System.out.println("\n===================================================================================================");
        System.out.println("                                    ALL LOAN APPLICATIONS                                          ");
        System.out.println("===================================================================================================");
        System.out.printf("%-8s | %-22s | %-16s | %-12s | %-20s | %s\n",
                "Loan ID", "User's Full Name", "Loan Amount", "Loan Type", "Application Date", "Status");
        System.out.println("---------------------------------------------------------------------------------------------------");

        for (LoanApplication loan : allLoans) {
            System.out.printf("%-8d | %-22s | %-16s | %-12s | %-20s | %s\n",
                    loan.getLoanId(),
                    loan.getUserName() != null ? loan.getUserName() : "User #" + loan.getUserId(),
                    formatRupees(loan.getLoanAmount()),
                    loan.getLoanType(),
                    loan.getApplicationDate() != null ? loan.getApplicationDate() : "N/A",
                    loan.getStatus());
        }
        System.out.println("---------------------------------------------------------------------------------------------------");
    }

    /**
     * Stage 3: Admin Side - 2. View Loan Details
     * Displays: User's Name, Email, Mobile Number, Loan Amount, Loan Type, Loan Duration,
     *           Annual Income, Purpose, Application Date, Status
     */
    public static void viewLoanDetails(Scanner scanner) {
        System.out.println("\n==========================================");
        System.out.println("            VIEW LOAN DETAILS             ");
        System.out.println("==========================================");

        System.out.print("Enter Loan ID: ");
        String input = scanner.nextLine().trim();
        int loanId;
        try {
            loanId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid Loan ID.");
            return;
        }

        LoanApplication loan = Database.getLoanApplicationById(loanId);
        if (loan == null) {
            System.out.println("Error: Loan application with ID " + loanId + " not found.");
            return;
        }

        System.out.println("\n------------------------------------------");
        System.out.println("User's Name      : " + loan.getUserName());
        System.out.println("Email            : " + loan.getUserEmail());
        System.out.println("Mobile Number    : " + loan.getUserMobileNumber());
        System.out.printf("Loan Amount      : %s\n", formatRupees(loan.getLoanAmount()));
        System.out.println("Loan Type        : " + loan.getLoanType());
        System.out.println("Loan Duration    : " + loan.getLoanDuration());
        System.out.printf("Interest Rate    : %.2f%% p.a.\n", loan.getInterestRate());
        System.out.printf("Annual Income    : %s\n", formatRupees(loan.getAnnualIncome()));
        System.out.println("Purpose          : " + loan.getPurpose());
        System.out.println("Application Date : " + loan.getApplicationDate());
        System.out.println("Status           : " + loan.getStatus());
        if ("Rejected".equalsIgnoreCase(loan.getStatus()) && loan.getRejectionReason() != null) {
            System.out.println("Rejection Reason : " + loan.getRejectionReason());
        } else if ("Needs More Info".equalsIgnoreCase(loan.getStatus()) && loan.getRejectionReason() != null) {
            System.out.println("Info Requested   : " + loan.getRejectionReason());
        }
        System.out.println("------------------------------------------");
    }

    /**
     * Stage 3: Admin Side - 3. Approve Loan
     */
    public static void approveLoan(Scanner scanner) {
        System.out.println("\n==========================================");
        System.out.println("               APPROVE LOAN               ");
        System.out.println("==========================================");

        System.out.print("Enter Loan ID to approve: ");
        String input = scanner.nextLine().trim();
        int loanId;
        try {
            loanId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid Loan ID.");
            return;
        }

        LoanApplication loan = Database.getLoanApplicationById(loanId);
        if (loan == null) {
            System.out.println("Error: Loan application with ID " + loanId + " not found.");
            return;
        }

        if ("Approved".equalsIgnoreCase(loan.getStatus())) {
            System.out.println("Notice: This loan is already Approved.");
            return;
        }

        // 1. Update status to Approved in MySQL using JDBC
        boolean updated = Database.updateLoanStatus(loanId, "Approved", null);
        if (!updated) {
            System.out.println("Error: Failed to update loan status in database.");
            return;
        }

        System.out.println("Status updated to 'Approved' in database for Loan ID: " + loanId);

        // 2. Retrieve user's registered email and name
        String recipientEmail = loan.getUserEmail();
        String recipientName = loan.getUserName();
        String formattedAmount = formatRupees(loan.getLoanAmount());

        // 3. Send approval email (Requirement 21)
        String subject = "Your Loan Application has been Approved!";
        String body = String.format("Dear %s, your loan application for %s has been approved. Please check your dashboard for further details.",
                recipientName, formattedAmount);

        System.out.println("\nDispatching approval email to: " + recipientEmail);
        sendEmail(recipientEmail, subject, body);
    }

    /**
     * Stage 3: Admin Side - 4. Reject Loan
     */
    public static void rejectLoan(Scanner scanner) {
        System.out.println("\n==========================================");
        System.out.println("               REJECT LOAN                ");
        System.out.println("==========================================");

        System.out.print("Enter Loan ID to reject: ");
        String input = scanner.nextLine().trim();
        int loanId;
        try {
            loanId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid Loan ID.");
            return;
        }

        LoanApplication loan = Database.getLoanApplicationById(loanId);
        if (loan == null) {
            System.out.println("Error: Loan application with ID " + loanId + " not found.");
            return;
        }

        // Ask admin for required rejection reason
        String reason = "";
        while (true) {
            System.out.print("Enter reason for rejection: ");
            reason = scanner.nextLine().trim();
            if (!reason.isEmpty()) {
                break;
            } else {
                System.out.println("Error: Rejection reason is required. Please enter.");
            }
        }

        // 1. Update status to Rejected and store reason in MySQL
        boolean updated = Database.updateLoanStatus(loanId, "Rejected", reason);
        if (!updated) {
            System.out.println("Error: Failed to update loan status in database.");
            return;
        }

        System.out.println("Status updated to 'Rejected' in database for Loan ID: " + loanId);

        // 2. Retrieve user's registered email and name
        String recipientEmail = loan.getUserEmail();
        String recipientName = loan.getUserName();

        // 3. Send rejection email (Requirement 23)
        String subject = "Your Loan Application has been Rejected";
        String body = String.format("Dear %s, we regret to inform you that your loan application has been rejected. Reason: %s. Please contact support for more details.",
                recipientName, reason);

        System.out.println("\nDispatching rejection email to: " + recipientEmail);
        sendEmail(recipientEmail, subject, body);
    }

    /**
     * Stage 3: Admin Side - 5. Request More Information (Status: Needs More Info)
     * Requirement 6: Minimal admin action to change loan status to "Needs More Info"
     * and capture a short information request.
     */
    public static void requestMoreInfo(Scanner scanner) {
        System.out.println("\n==========================================");
        System.out.println("          REQUEST MORE INFORMATION        ");
        System.out.println("==========================================");

        System.out.print("Enter Loan ID to request more information: ");
        String input = scanner.nextLine().trim();
        int loanId;
        try {
            loanId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid Loan ID.");
            return;
        }

        LoanApplication loan = Database.getLoanApplicationById(loanId);
        if (loan == null) {
            System.out.println("Error: Loan application with ID " + loanId + " not found.");
            return;
        }

        String infoNeeded = "";
        while (true) {
            System.out.print("Enter information needed from applicant (Required): ");
            infoNeeded = scanner.nextLine().trim();
            if (!infoNeeded.isEmpty()) {
                break;
            } else {
                System.out.println("Error: Information request note cannot be empty. Please enter.");
            }
        }

        // Update status to "Needs More Info" in MySQL via JDBC
        boolean updated = Database.updateLoanStatus(loanId, "Needs More Info", infoNeeded);
        if (!updated) {
            System.out.println("Error: Failed to update loan status in database.");
            return;
        }

        System.out.println("Status updated to 'Needs More Info' in database for Loan ID: " + loanId);
        System.out.println("Information request recorded: " + infoNeeded);
    }

    /**
     * Real SMTP Email Dispatching
     */
    public static void sendEmail(String recipientEmail, String subject, String body) {
        String host = System.getenv("SMTP_HOST") != null ? System.getenv("SMTP_HOST") : "smtp.gmail.com";
        String port = System.getenv("SMTP_PORT") != null ? System.getenv("SMTP_PORT") : "587";
        final String username = System.getenv("SMTP_USERNAME");
        final String password = System.getenv("SMTP_PASSWORD");
        String fromEmail = System.getenv("SMTP_FROM") != null ? System.getenv("SMTP_FROM") : (username != null ? username : "noreply@loanapp.com");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.out.println("\n[SMTP Notification Notice]");
            System.out.println("SMTP credentials (SMTP_USERNAME, SMTP_PASSWORD) are not set.");
            System.out.println("Live email delivery was NOT sent. To configure live SMTP dispatch, set environment variables:");
            System.out.println("  export SMTP_USERNAME=\"your_email@gmail.com\"");
            System.out.println("  export SMTP_PASSWORD=\"your_app_password\"");
            System.out.println("[Prepared Email Content]");
            System.out.println("To      : " + recipientEmail);
            System.out.println("Subject : " + subject);
            System.out.println("Body    : " + body);
            System.out.println("-------------------------------------------------------------");
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("SUCCESS: Live email sent successfully to " + recipientEmail);
        } catch (MessagingException e) {
            System.err.println("SMTP Error: Failed to send email via SMTP server (" + e.getMessage() + ")");
            System.out.println("[Delivery failed for recipient: " + recipientEmail + "]");
        }
    }
}
