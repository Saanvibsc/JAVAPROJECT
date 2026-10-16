package part_5;

public class LoanApprovalService {

    public static LoanApprovalResult approveLoan(
            String customerName,
            String email,
            double loanAmount,
            double interestRate,
            int tenureMonths) {

        // Calculate EMI
        double emi = EMICalculator.calculateEMI(
                loanAmount,
                interestRate,
                tenureMonths);

        // Calculate total payable amount
        double totalPayable =
                EMICalculator.calculateTotalPayable(
                        emi,
                        tenureMonths);

        // Calculate total interest
        double totalInterest =
                EMICalculator.calculateTotalInterest(
                        loanAmount,
                        emi,
                        tenureMonths);

        // Send approval notification
        EmailNotificationService.sendApprovalEmail(
                customerName,
                email,
                loanAmount,
                interestRate,
                tenureMonths,
                emi,
                totalInterest,
                totalPayable);

        // Return the complete approval result
        return new LoanApprovalResult(
                "APPROVED",
                loanAmount,
                interestRate,
                tenureMonths,
                emi,
                totalInterest,
                totalPayable);
    }

    public static String rejectLoan(
            String customerName,
            String email,
            String reason) {

        // Send rejection notification
        EmailNotificationService.sendRejectionEmail(
                customerName,
                email,
                reason);

        return "REJECTED";
    }
}