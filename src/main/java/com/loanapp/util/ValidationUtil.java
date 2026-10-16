package com.loanapp.util;

import java.util.regex.Pattern;

/**
 * Input validation utilities across Stages 1, 2, 3, and 4.
 */
public class ValidationUtil {

    private static final Pattern PAN_PATTERN = Pattern.compile("^[A-Z]{5}[0-9]{4}[A-Z]$");
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[0-9]{10}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z]{2,50}$");

    private ValidationUtil() {
    }

    /**
     * Stage 4: Validates Indian Permanent Account Number (PAN).
     * Format: 5 uppercase letters, 4 digits, 1 uppercase letter (e.g. ABCDE1234F).
     */
    public static boolean validatePAN(String pan) {
        return pan != null && PAN_PATTERN.matcher(pan.trim().toUpperCase()).matches();
    }

    /**
     * Stage 1: Validates First Name (letters only, min 2 chars).
     */
    public static boolean validateFirstName(String fname) {
        return fname != null && NAME_PATTERN.matcher(fname.trim()).matches();
    }

    /**
     * Stage 1: Validates Last Name (letters only, min 2 chars).
     */
    public static boolean validateLastName(String lname) {
        return lname != null && NAME_PATTERN.matcher(lname.trim()).matches();
    }

    /**
     * Stage 1: Validates 10-digit mobile number.
     */
    public static boolean validateMobile(String mobile) {
        return mobile != null && MOBILE_PATTERN.matcher(mobile.trim()).matches();
    }

    /**
     * Stage 1: Validates email address format.
     */
    public static boolean validateEmail(String email) {
        if (email == null) return false;
        String trimmed = email.trim();
        return trimmed.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$") && trimmed.contains(".");
    }

    /**
     * Validates non-empty location.
     */
    public static boolean validateLocation(String location) {
        return location != null && !location.trim().isEmpty();
    }

    /**
     * Stage 1: Validates strong password policy:
     * - Minimum 8 characters
     * - At least 1 uppercase letter
     * - At least 1 lowercase letter
     * - At least 1 digit
     * - At least 1 special character
     */
    public static boolean validatePassword(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[^a-zA-Z0-9].*");
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    /**
     * Stage 3: Validates positive loan amount.
     */
    public static boolean validateLoanAmount(double amount) {
        return amount > 0;
    }

    /**
     * Stage 3: Validates positive loan tenure in months.
     */
    public static boolean validateTenureMonths(int months) {
        return months > 0;
    }

    /**
     * Stage 3: Validates positive annual income.
     */
    public static boolean validateAnnualIncome(double income) {
        return income > 0;
    }
}
