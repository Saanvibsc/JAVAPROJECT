package com.loan;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        UserRegistration.registerUser(scanner);

        scanner.close();
    }
}