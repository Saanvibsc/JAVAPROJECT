package com.loanapp.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility for password hashing and verification using salted SHA-256.
 * Seamlessly verifies legacy plaintext passwords and auto-detects hash formats.
 */
public class PasswordUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    /**
     * Hashes password using SHA-256 with a random 16-byte salt.
     * Stored format: saltBase64:hashBase64
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null) return null;
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        byte[] hash = computeHash(plainPassword, salt);
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    /**
     * Verifies password against stored password string.
     * Supports salted hash format and fallback plaintext for seeded/demo accounts.
     */
    public static boolean verifyPassword(String plainPassword, String stored) {
        if (plainPassword == null || stored == null) return false;

        // If stored string contains salt delimiter ':'
        if (stored.contains(":")) {
            String[] parts = stored.split(":", 2);
            if (parts.length == 2) {
                try {
                    byte[] salt = Base64.getDecoder().decode(parts[0]);
                    byte[] expectedHash = Base64.getDecoder().decode(parts[1]);
                    byte[] actualHash = computeHash(plainPassword, salt);
                    return MessageDigest.isEqual(expectedHash, actualHash);
                } catch (IllegalArgumentException e) {
                    // Fallback to direct comparison if not base64
                }
            }
        }

        // Direct comparison for initial seed/demo passwords (e.g. "Admin@123", "User@123", "admin123")
        return stored.equals(plainPassword);
    }

    private static byte[] computeHash(String password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            return md.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available in runtime", e);
        }
    }
}
