package com.loanapp.model;

/**
 * Supported loan types with standard display names and default baseline interest rates.
 */
public enum LoanType {
    HOME_LOAN("Home Loan", 7.5),
    PERSONAL_LOAN("Personal Loan", 10.5),
    EDUCATION_LOAN("Education Loan", 8.0),
    VEHICLE_LOAN("Vehicle Loan", 8.5);

    private final String displayName;
    private final double baseInterestRate;

    LoanType(String displayName, double baseInterestRate) {
        this.displayName = displayName;
        this.baseInterestRate = baseInterestRate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getBaseInterestRate() {
        return baseInterestRate;
    }

    public static LoanType fromString(String text) {
        if (text == null) return null;
        String clean = text.trim().toLowerCase();
        if (clean.contains("home")) return HOME_LOAN;
        if (clean.contains("personal")) return PERSONAL_LOAN;
        if (clean.contains("education") || clean.contains("student")) return EDUCATION_LOAN;
        if (clean.contains("vehicle") || clean.contains("car") || clean.contains("auto")) return VEHICLE_LOAN;
        return null;
    }
}
