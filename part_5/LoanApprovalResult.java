package part_5;

public class LoanApprovalResult {

    private String status;
    private double loanAmount;
    private double interestRate;
    private int tenureMonths;
    private double emi;
    private double totalInterest;
    private double totalPayable;

    public LoanApprovalResult(
            String status,
            double loanAmount,
            double interestRate,
            int tenureMonths,
            double emi,
            double totalInterest,
            double totalPayable) {

        this.status = status;
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.tenureMonths = tenureMonths;
        this.emi = emi;
        this.totalInterest = totalInterest;
        this.totalPayable = totalPayable;
    }

    public String getStatus() {
        return status;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public int getTenureMonths() {
        return tenureMonths;
    }

    public double getEmi() {
        return emi;
    }

    public double getTotalInterest() {
        return totalInterest;
    }

    public double getTotalPayable() {
        return totalPayable;
    }

    public void displayResult() {

        System.out.println();
        System.out.println("==========================================");
        System.out.println("             LOAN APPROVAL");
        System.out.println("==========================================");

        System.out.println("Status            : " + status);

        System.out.printf(
                "Loan Amount       : ₹%.2f%n",
                loanAmount);

        System.out.printf(
                "Interest Rate     : %.2f%%%n",
                interestRate);

        System.out.println(
                "Tenure            : "
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

        System.out.println(
                "==========================================");
    }
}