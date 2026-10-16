package part_5;

public class EmailNotificationService {

    public static void sendApprovalEmail(
            String customerName,
            String email,
            double loanAmount,
            double interestRate,
            int tenureMonths,
            double emi,
            double totalInterest,
            double totalPayable) {

        System.out.println();
        System.out.println("==========================================");
        System.out.println("          LOAN APPROVAL EMAIL");
        System.out.println("==========================================");

        System.out.println("To      : " + email);
        System.out.println("Customer: " + customerName);
        System.out.println();

        System.out.println("Dear " + customerName + ",");
        System.out.println();

        System.out.println(
                "Congratulations! Your loan application "
                + "has been APPROVED.");

        System.out.println();

        System.out.printf(
                "Loan Amount       : ₹%.2f%n",
                loanAmount);

        System.out.printf(
                "Interest Rate     : %.2f%%%n",
                interestRate);

        System.out.println(
                "Loan Tenure       : "
                + tenureMonths + " months");

        System.out.printf(
                "Monthly EMI       : ₹%.2f%n",
                emi);

        System.out.printf(
                "Total Interest    : ₹%.2f%n",
                totalInterest);

        System.out.printf(
                "Total Payable     : ₹%.2f%n",
                totalPayable);

        System.out.println();
        System.out.println(
                "Thank you for choosing our bank.");

        System.out.println(
                "==========================================");
    }

    public static void sendRejectionEmail(
            String customerName,
            String email,
            String reason) {

        System.out.println();
        System.out.println("==========================================");
        System.out.println("          LOAN REJECTION EMAIL");
        System.out.println("==========================================");

        System.out.println("To      : " + email);
        System.out.println("Customer: " + customerName);
        System.out.println();

        System.out.println("Dear " + customerName + ",");
        System.out.println();

        System.out.println(
                "We regret to inform you that your loan "
                + "application has been REJECTED.");

        if (reason != null && !reason.trim().isEmpty()) {
            System.out.println();
            System.out.println("Reason: " + reason);
        }

        System.out.println();
        System.out.println("Thank you for applying.");

        System.out.println(
                "==========================================");
    }
}