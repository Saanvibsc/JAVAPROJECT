package com.loanapp.util;

import java.security.SecureRandom;

/**
 * Stage 4: Automatic credential generator.
 * Generates username based on first name, last name, and mobile number.
 * Generates secure random password with letters, digits, and symbols.
 */
public class CredentialGenerator {

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*";
    private static final String ALL_CHARS = UPPER + LOWER + DIGITS + SPECIAL;

    private static final SecureRandom RANDOM = new SecureRandom();

    private CredentialGenerator() {
    }

    /**
     * Stage 4: Generates username using:
     * - First 3 characters of first name (lowercase)
     * - First 3 characters of last name (lowercase)
     * - Last 4 digits of mobile number
     */
    public static String generateUsername(String fname, String lname, String mobile) {
        String cleanFname = fname != null ? fname.trim().replaceAll("[^A-Za-z]", "").toLowerCase() : "usr";
        String cleanLname = lname != null ? lname.trim().replaceAll("[^A-Za-z]", "").toLowerCase() : "app";
        String cleanMobile = mobile != null ? mobile.trim().replaceAll("[^0-9]", "") : "0000";

        String firstThree = cleanFname.length() >= 3 ? cleanFname.substring(0, 3) : String.format("%-3s", cleanFname).replace(' ', 'x');
        String lastThree = cleanLname.length() >= 3 ? cleanLname.substring(0, 3) : String.format("%-3s", cleanLname).replace(' ', 'x');
        String lastFour = cleanMobile.length() >= 4 ? cleanMobile.substring(cleanMobile.length() - 4) : "1234";

        return firstThree + lastThree + lastFour;
    }

    /**
     * Stage 4: Generates compliant password (8-10 characters):
     * Guarantees at least 1 uppercase, 1 lowercase, 1 digit, and 1 special symbol.
     */
    public static String generatePassword() {
        int length = 8 + RANDOM.nextInt(3); // 8, 9, or 10 characters
        StringBuilder sb = new StringBuilder(length);

        // Guarantee all 4 required character categories
        sb.append(UPPER.charAt(RANDOM.nextInt(UPPER.length())));
        sb.append(LOWER.charAt(RANDOM.nextInt(LOWER.length())));
        sb.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        sb.append(SPECIAL.charAt(RANDOM.nextInt(SPECIAL.length())));

        // Fill remaining length
        while (sb.length() < length) {
            sb.append(ALL_CHARS.charAt(RANDOM.nextInt(ALL_CHARS.length())));
        }

        // Shuffle characters
        char[] chars = sb.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }

        return new String(chars);
    }
}
