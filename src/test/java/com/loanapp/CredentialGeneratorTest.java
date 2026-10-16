package com.loanapp;

import com.loanapp.util.CredentialGenerator;
import com.loanapp.util.ValidationUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CredentialGeneratorTest {

    @Test
    @DisplayName("Stage 4: Username Generation Pattern (3 fname + 3 lname + 4 mobile)")
    void testUsernameGeneration() {
        String username = CredentialGenerator.generateUsername("Rahul", "Sharma", "9876543210");
        assertEquals("rahsha3210", username);

        String username2 = CredentialGenerator.generateUsername("Amruta", "Patil", "9820112233");
        assertEquals("amrpat2233", username2);
    }

    @Test
    @DisplayName("Stage 4: Password Generation - Meets All Complexity Requirements")
    void testPasswordGeneration() {
        for (int i = 0; i < 50; i++) {
            String password = CredentialGenerator.generatePassword();
            assertNotNull(password);
            assertTrue(password.length() >= 8 && password.length() <= 10,
                    "Password length must be between 8 and 10: " + password);
            assertTrue(ValidationUtil.validatePassword(password),
                    "Generated password must satisfy strict password policy: " + password);
        }
    }
}
