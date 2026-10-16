package com.loanapp.service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Date;
import java.util.Properties;

/**
 * Real Java SMTP Email Notification Service using Jakarta Mail (Stages 1, 2, 3, 4, 5).
 * Configured via standard environment variables or system properties.
 */
public class EmailService {

    private static final String SMTP_HOST = System.getenv("SMTP_HOST") != null
            ? System.getenv("SMTP_HOST") : System.getProperty("smtp.host", "smtp.gmail.com");
    private static final String SMTP_PORT = System.getenv("SMTP_PORT") != null
            ? System.getenv("SMTP_PORT") : System.getProperty("smtp.port", "587");
    private static final String SMTP_USERNAME = System.getenv("SMTP_USERNAME") != null
            ? System.getenv("SMTP_USERNAME") : System.getProperty("smtp.username", "");
    private static final String SMTP_PASSWORD = System.getenv("SMTP_PASSWORD") != null
            ? System.getenv("SMTP_PASSWORD") : System.getProperty("smtp.password", "");
    private static final String SMTP_FROM = System.getenv("SMTP_FROM") != null
            ? System.getenv("SMTP_FROM") : (SMTP_USERNAME.isEmpty() ? "noreply@loansystem.com" : SMTP_USERNAME);

    private EmailService() {
    }

    /**
     * Checks if actual SMTP server credentials have been configured.
     */
    public static boolean isConfigured() {
        return SMTP_USERNAME != null && !SMTP_USERNAME.trim().isEmpty()
                && SMTP_PASSWORD != null && !SMTP_PASSWORD.trim().isEmpty();
    }

    /**
     * Dispatches real email over SMTP via Jakarta Mail.
     */
    public static boolean dispatchEmail(String recipientEmail, String subject, String body) {
        if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
            System.err.println("[EMAIL ERROR] Recipient email is empty.");
            return false;
        }

        if (!isConfigured()) {
            System.out.println("\n+--------------------------------------------------------------------------+");
            System.out.println("|                     SMTP EMAIL DISPATCH (STANDALONE)                     |");
            System.out.println("+--------------------------------------------------------------------------+");
            System.out.println("  To      : " + recipientEmail);
            System.out.println("  Subject : " + subject);
            System.out.println("  Notice  : Real SMTP credentials not set in SMTP_USERNAME / SMTP_PASSWORD.");
            System.out.println("            Set env vars to deliver to actual inboxes over port " + SMTP_PORT + ".");
            System.out.println("-------------------------------- Message Content --------------------------");
            System.out.println(body);
            System.out.println("+--------------------------------------------------------------------------+\n");
            return true;
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        if ("465".equals(SMTP_PORT)) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        } else {
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
        }
        props.put("mail.smtp.connectiontimeout", "7000");
        props.put("mail.smtp.timeout", "7000");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail.trim()));
            message.setSubject(subject);
            message.setSentDate(new Date());
            message.setText(body);

            Transport.send(message);

            System.out.println("[EMAIL SUCCESS] Live email delivered to " + recipientEmail + " via SMTP server " + SMTP_HOST);
            return true;

        } catch (MessagingException e) {
            System.err.println("[EMAIL ERROR] Failed to send email via SMTP to " + recipientEmail + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Stage 4: Delivers auto-generated credentials and CIBIL score.
     */
    public static void sendCredentials(String recipientEmail, String fullName, String username, String password, int cibilScore) {
        String subject = "Your Credentials for Loan Application System";
        String body = "Dear " + fullName + ",\n\n"
                + "Welcome to the Loan Application System.\n"
                + "Your account has been successfully registered with PAN verification.\n\n"
                + "========================================\n"
                + "         YOUR LOGIN CREDENTIALS         \n"
                + "========================================\n"
                + "Username   : " + username + "\n"
                + "Password   : " + password + "\n"
                + "CIBIL Score: " + cibilScore + "\n"
                + "========================================\n\n"
                + "You may now log in to view your profile and apply for loans.\n"
                + "Please keep your password confidential.\n\n"
                + "Best Regards,\n"
                + "Loan Application Management Team";

        dispatchEmail(recipientEmail, subject, body);
    }

    /**
     * Stage 2: Account Update Notifications (Activate, Deactivate, Edit, Delete).
     */
    public static void sendAccountNotification(String recipientEmail, String firstName, String actionType, String customMessage) {
        String subject = "Account Update Notification";
        String body = "Dear " + firstName + ",\n\n" + customMessage + "\n\n"
                + "Action Performed: " + actionType + "\n"
                + "Date: " + new Date() + "\n\n"
                + "Regards,\n"
                + "Loan Administration Department";

        dispatchEmail(recipientEmail, subject, body);
    }

    /**
     * Stage 5: Loan Approval Notification with EMI, Total Interest, and Total Payable.
     */
    public static void sendLoanApprovalEmail(String recipientEmail, String customerName, int loanId,
                                            double loanAmount, double interestRate, int tenureMonths,
                                            double emi, double totalInterest, double totalPayable) {
        String subject = "Your Loan Application has been Approved!";
        String body = "Dear " + customerName + ",\n\n"
                + "Congratulations! We are pleased to inform you that your loan application (Loan ID: " + loanId + ") has been APPROVED.\n\n"
                + "========================================\n"
                + "         APPROVED LOAN DETAILS          \n"
                + "========================================\n"
                + String.format("Approved Loan Amount : Rs. %,.2f%n", loanAmount)
                + String.format("Annual Interest Rate : %.2f%%%n", interestRate)
                + String.format("Tenure               : %d Months%n", tenureMonths)
                + String.format("Monthly EMI          : Rs. %,.2f%n", emi)
                + String.format("Total Interest       : Rs. %,.2f%n", totalInterest)
                + String.format("Total Amount Payable : Rs. %,.2f%n", totalPayable)
                + "========================================\n\n"
                + "The disbursement process will begin shortly. Please log in to your account for repayment schedules.\n\n"
                + "Thank you for banking with us.\n\n"
                + "Regards,\n"
                + "Loan Servicing Team";

        dispatchEmail(recipientEmail, subject, body);
    }

    /**
     * Stage 3 & 5: Loan Rejection Notification with mandatory reason.
     */
    public static void sendLoanRejectionEmail(String recipientEmail, String customerName, int loanId, String reason) {
        String subject = "Your Loan Application has been Rejected";
        String body = "Dear " + customerName + ",\n\n"
                + "We regret to inform you that your loan application (Loan ID: " + loanId + ") has been rejected.\n\n"
                + "Reason for Rejection: " + (reason != null ? reason : "Does not meet credit criteria") + "\n\n"
                + "If you have questions or wish to appeal this decision, please contact our support department.\n\n"
                + "Regards,\n"
                + "Credit Evaluation Team";

        dispatchEmail(recipientEmail, subject, body);
    }

    /**
     * Stage 3: Request for additional documentation / information.
     */
    public static void sendMoreInfoEmail(String recipientEmail, String customerName, int loanId, String remarks) {
        String subject = "Action Required: Additional Information for Loan Application #" + loanId;
        String body = "Dear " + customerName + ",\n\n"
                + "Your loan application #" + loanId + " is currently under review.\n"
                + "Our credit officer requires the following additional information/documents:\n\n"
                + remarks + "\n\n"
                + "Please provide the requested details at your earliest convenience to proceed.\n\n"
                + "Regards,\n"
                + "Loan Processing Team";

        dispatchEmail(recipientEmail, subject, body);
    }

    /**
     * Stage 1: Password Reset Notification.
     */
    public static void sendPasswordResetEmail(String recipientEmail, String fullName, String newPassword) {
        String subject = "Your Password Has Been Reset";
        String body = "Dear " + fullName + ",\n\n"
                + "Your password for the Loan Application System has been successfully reset.\n"
                + "Your new temporary password is: " + newPassword + "\n\n"
                + "Please log in and update your password immediately.\n\n"
                + "Regards,\n"
                + "Security Team";

        dispatchEmail(recipientEmail, subject, body);
    }
}
