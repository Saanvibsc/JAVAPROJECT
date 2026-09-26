/**
 * EmailNotificationService.java
 * Sends user email notifications after Admin operations with exact PDF wording.
 * Subject: Account Update Notification
 */
public class EmailNotificationService {

    public static final String SUBJECT = "Account Update Notification";

    public static void sendActivationEmail(String toEmail, String firstName) {
        String body = "Dear " + firstName + ", your account has been successfully activated by the admin. You can now log in to the system.";
        sendEmail(toEmail, SUBJECT, body);
    }

    public static void sendDeactivationEmail(String toEmail, String firstName) {
        String body = "Dear " + firstName + ", your account has been deactivated. Please contact support for more information.";
        sendEmail(toEmail, SUBJECT, body);
    }

    public static void sendDeletionEmail(String toEmail, String firstName) {
        String body = "Dear " + firstName + ", your account has been deleted from the system. If this was a mistake, please contact support immediately.";
        sendEmail(toEmail, SUBJECT, body);
    }

    public static void sendEditEmail(String toEmail, String firstName) {
        String body = "Dear " + firstName + ", your account details have been updated. If you did not request this change, please contact support immediately.";
        sendEmail(toEmail, SUBJECT, body);
    }

    private static void sendEmail(String toEmail, String subject, String body) {
        System.out.println("\n+--------------------------------------------------------------------------+");
        System.out.println("|                        EMAIL NOTIFICATION SENT                           |");
        System.out.println("+--------------------------------------------------------------------------+");
        System.out.println("  To      : " + toEmail);
        System.out.println("  Subject : " + subject);
        System.out.println("  Message : " + body);
        System.out.println("+--------------------------------------------------------------------------+\n");
    }
}
