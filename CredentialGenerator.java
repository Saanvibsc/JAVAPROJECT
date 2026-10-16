package com.loan;

import java.security.SecureRandom;

public class CredentialGenerator {

    // Generate Username
    public static String generateUsername(
            String fname,
            String lname,
            String mobile) {

        String firstThree = fname.substring(0, 3);
        String lastThree = lname.substring(0, 3);
        String lastFour = mobile.substring(mobile.length() - 4);

        return firstThree + lastThree + lastFour;
    }

    // Generate Password
    public static String generatePassword() {

        String letters =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        String numbers =
                "0123456789";

        String symbols =
                "!@#$%^&*";

        String allCharacters =
                letters + numbers + symbols;

        SecureRandom random = new SecureRandom();

        // Random length between 8 and 10
        int length = 8 + random.nextInt(3);

        StringBuilder password = new StringBuilder();

        // Make sure password contains at least
        // one letter, one number and one symbol

        password.append(
                letters.charAt(
                        random.nextInt(letters.length())
                )
        );

        password.append(
                numbers.charAt(
                        random.nextInt(numbers.length())
                )
        );

        password.append(
                symbols.charAt(
                        random.nextInt(symbols.length())
                )
        );

        // Fill remaining characters randomly
        while (password.length() < length) {

            password.append(
                    allCharacters.charAt(
                            random.nextInt(allCharacters.length())
                    )
            );
        }

        return password.toString();
    }
}