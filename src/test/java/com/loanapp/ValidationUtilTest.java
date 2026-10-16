package com.loanapp;

import com.loanapp.util.ValidationUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilTest {

    @Test
    @DisplayName("Stage 4: PAN Validation - Valid Format")
    void testValidPAN() {
        assertTrue(ValidationUtil.validatePAN("ABCDE1234F"));
        assertTrue(ValidationUtil.validatePAN("BKZPS9876K"));
        assertTrue(ValidationUtil.validatePAN("abcde1234f")); // auto uppercase trimming
    }

    @Test
    @DisplayName("Stage 4: PAN Validation - Invalid Formats")
    void testInvalidPAN() {
        assertFalse(ValidationUtil.validatePAN(null));
        assertFalse(ValidationUtil.validatePAN(""));
        assertFalse(ValidationUtil.validatePAN("ABC1234F")); // too short
        assertFalse(ValidationUtil.validatePAN("12345ABCDE")); // numbers first
        assertFalse(ValidationUtil.validatePAN("ABCDE12345")); // ends in number
        assertFalse(ValidationUtil.validatePAN("ABCDEF1234G")); // 6 letters
    }

    @Test
    @DisplayName("Stage 1: Mobile Validation - 10 Digits")
    void testMobileValidation() {
        assertTrue(ValidationUtil.validateMobile("9876543210"));
        assertTrue(ValidationUtil.validateMobile("9123456789"));
        assertFalse(ValidationUtil.validateMobile("987654321")); // 9 digits
        assertFalse(ValidationUtil.validateMobile("98765432100")); // 11 digits
        assertFalse(ValidationUtil.validateMobile("98765abcd0")); // letters
        assertFalse(ValidationUtil.validateMobile(null));
    }

    @Test
    @DisplayName("Stage 1: Email Validation")
    void testEmailValidation() {
        assertTrue(ValidationUtil.validateEmail("borrower@bank.com"));
        assertTrue(ValidationUtil.validateEmail("rahul.sharma@example.co.in"));
        assertFalse(ValidationUtil.validateEmail("borrower@"));
        assertFalse(ValidationUtil.validateEmail("borrower.com"));
        assertFalse(ValidationUtil.validateEmail(""));
        assertFalse(ValidationUtil.validateEmail(null));
    }

    @Test
    @DisplayName("Stage 1: Strong Password Policy Validation")
    void testPasswordValidation() {
        assertTrue(ValidationUtil.validatePassword("Admin@123"));
        assertTrue(ValidationUtil.validatePassword("Secure#2026"));
        assertTrue(ValidationUtil.validatePassword("Pass$word1"));

        assertFalse(ValidationUtil.validatePassword("short1!")); // < 8 chars
        assertFalse(ValidationUtil.validatePassword("alllowercase123!")); // no upper
        assertFalse(ValidationUtil.validatePassword("ALLUPPERCASE123!")); // no lower
        assertFalse(ValidationUtil.validatePassword("NoDigitsAtAll!")); // no digit
        assertFalse(ValidationUtil.validatePassword("NoSpecialChars123")); // no symbol
        assertFalse(ValidationUtil.validatePassword(null));
    }
}
