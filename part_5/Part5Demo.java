package part_5;

public class Part5Demo {

    public static void main(String[] args) {

        // Temporary test data
        String customerName = "Avanti";
        String email = "customer@example.com";

        double loanAmount = 500000;
        double interestRate = 7.5;
        int tenureMonths = 60;

        System.out.println("==========================================");
        System.out.println("       PART 5 LOAN APPROVAL SYSTEM");
        System.out.println("==========================================");

        System.out.println();
        System.out.println("Processing loan application...");

        // Approve loan
        LoanApprovalResult result =
                LoanApprovalService.approveLoan(
                        customerName,
                        email,
                        loanAmount,
                        interestRate,
                        tenureMonths);

        // Display approval details
        result.displayResult();

        System.out.println();
        System.out.println("Part 5 test completed successfully.");
    }
}