package com.loanapp.service;

/**
 * Stage 5: Mathematical EMI (Equated Monthly Installment) Calculator.
 * Formula: EMI = [P * r * (1 + r)^n] / [(1 + r)^n - 1]
 * where:
 *   P = Principal loan amount
 *   r = Monthly interest rate = (Annual Rate / 12) / 100
 *   n = Tenure in months
 */
public class EMICalculator {

    private EMICalculator() {
    }

    /**
     * Calculates monthly EMI for given principal, annual interest rate, and tenure.
     */
    public static double calculateEMI(double principal, double annualInterestRate, int tenureMonths) {
        if (principal <= 0) {
            throw new IllegalArgumentException("Loan amount must be greater than 0.");
        }
        if (annualInterestRate < 0) {
            throw new IllegalArgumentException("Interest rate cannot be negative.");
        }
        if (tenureMonths <= 0) {
            throw new IllegalArgumentException("Loan tenure must be greater than 0.");
        }

        double monthlyRate = annualInterestRate / 12.0 / 100.0;

        if (monthlyRate == 0) {
            return principal / tenureMonths;
        }

        double factor = Math.pow(1.0 + monthlyRate, tenureMonths);
        return (principal * monthlyRate * factor) / (factor - 1.0);
    }

    /**
     * Calculates total payable amount over the full loan tenure.
     */
    public static double calculateTotalPayable(double emi, int tenureMonths) {
        return emi * tenureMonths;
    }

    /**
     * Calculates total interest charged over the full tenure.
     */
    public static double calculateTotalInterest(double principal, double emi, int tenureMonths) {
        double totalPayable = calculateTotalPayable(emi, tenureMonths);
        return totalPayable - principal;
    }
}
