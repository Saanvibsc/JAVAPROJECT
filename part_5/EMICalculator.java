package part_5;

public class EMICalculator {

    private EMICalculator() {
        // Utility class
    }

    public static double calculateEMI(
            double principal,
            double annualInterestRate,
            int tenureMonths) {

        if (principal <= 0) {
            throw new IllegalArgumentException(
                    "Loan amount must be greater than 0.");
        }

        if (annualInterestRate < 0) {
            throw new IllegalArgumentException(
                    "Interest rate cannot be negative.");
        }

        if (tenureMonths <= 0) {
            throw new IllegalArgumentException(
                    "Loan tenure must be greater than 0.");
        }

        // Convert annual interest rate to monthly rate
        double monthlyRate = annualInterestRate / 12 / 100;

        // If interest rate is 0%
        if (monthlyRate == 0) {
            return principal / tenureMonths;
        }

        double factor = Math.pow(
                1 + monthlyRate,
                tenureMonths);

        double emi = (principal * monthlyRate * factor)
                / (factor - 1);

        return emi;
    }

    public static double calculateTotalPayable(
            double emi,
            int tenureMonths) {

        return emi * tenureMonths;
    }

    public static double calculateTotalInterest(
            double principal,
            double emi,
            int tenureMonths) {

        double totalPayable =
                calculateTotalPayable(emi, tenureMonths);

        return totalPayable - principal;
    }
}