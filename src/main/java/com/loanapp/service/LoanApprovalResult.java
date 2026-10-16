package com.loanapp.service;

/**
 * Stage 5: DTO encapsulating loan approval decision, EMI calculations, and amortization totals.
 */
public class LoanApprovalResult {
    private final String status;
    private final double loanAmount;
    private final double interestRate;
    private final int tenureMonths;
    private final double emi;
    private final double totalInterest;
    private final double totalPayable;

    public LoanApprovalResult(String status, double loanAmount, double interestRate,
                              int tenureMonths, double emi, double totalInterest, double totalPayable) {
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
        System.out.println("\n=============================================================");
        System.out.println("                 LOAN APPROVAL & EMI BREAKDOWN               ");
        System.out.println("=============================================================");
        System.out.println("Status              : " + status);
        System.out.printf("Approved Principal  : Rs. %,.2f%n", loanAmount);
        System.out.printf("Annual Interest     : %.2f%%%n", interestRate);
        System.out.println("Tenure              : " + tenureMonths + " Months");
        System.out.printf("Monthly EMI         : Rs. %,.2f%n", emi);
        System.out.printf("Total Interest      : Rs. %,.2f%n", totalInterest);
        System.out.printf("Total Payable       : Rs. %,.2f%n", totalPayable);
        System.out.println("=============================================================");
    }
}
