package com.loan;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailService {

    public static void sendCredentials(
            String recipientEmail,
            String username,
            String password) {

        // YOUR GMAIL ACCOUNT
        final String senderEmail = "diyapoppy8@gmail.com";

        // YOUR GOOGLE APP PASSWORD
        final String senderPassword = "JAVAJAVAPROJECT2#%";

        Properties properties = new Properties();

        // Gmail SMTP settings
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "true");

        Session session = Session.getInstance(
                properties,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                senderEmail,
                                senderPassword
                        );
                    }
                });

        try {

            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(senderEmail));

            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail)
            );

            message.setSubject(
                    "Your Username and Password for Loan Application"
            );

            String emailBody =
                    "Dear User,\n\n"
                    + "Your Loan Application account has been created successfully.\n\n"
                    + "Your login credentials are:\n\n"
                    + "Username: " + username + "\n"
                    + "Password: " + password + "\n\n"
                    + "Please keep these credentials secure.\n\n"
                    + "Regards,\n"
                    + "Loan Application System";

            message.setText(emailBody);

            Transport.send(message);

            System.out.println();
            System.out.println("=================================");
            System.out.println("     EMAIL SENT SUCCESSFULLY");
            System.out.println("=================================");

        } catch (Exception e) {

            System.out.println();
            System.out.println("=================================");
            System.out.println("      EMAIL SENDING FAILED");
            System.out.println("=================================");
            System.out.println("Error: " + e.getMessage());
            System.out.println("=================================");

            e.printStackTrace();
        }
    }
}