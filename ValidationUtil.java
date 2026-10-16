package com.loan;

public class ValidationUtil {

    // PAN validation
    public static boolean validatePAN(String pan) {
        return pan != null &&
               pan.matches("[A-Z]{5}[0-9]{4}[A-Z]");
    }

    // First Name validation
    public static boolean validateFirstName(String fname) {
        return fname != null &&
               !fname.isEmpty() &&
               fname.matches("[A-Za-z]+");
    }

    // Last Name validation
    public static boolean validateLastName(String lname) {
        return lname != null &&
               !lname.isEmpty() &&
               lname.matches("[A-Za-z]+");
    }

    // Mobile Number validation
    public static boolean validateMobile(String mobile) {
        return mobile != null &&
               mobile.matches("[0-9]{10}");
    }

    // Email validation
    public static boolean validateEmail(String email) {
        return email != null &&
               email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    // Location validation
    public static boolean validateLocation(String location) {
        return location != null &&
               !location.trim().isEmpty();
    }
}