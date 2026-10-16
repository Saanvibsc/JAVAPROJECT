package com.loan;

import java.util.Scanner;

public class LoanApplication {
	
	public static void applyForLoan(Scanner scanner, int cibil) {


	
	  System.out.println();
      System.out.println("=================================");
      System.out.println("         APPLY FOR LOAN");
      System.out.println("=================================");

      System.out.println("1. Home Loan");
      System.out.println("2. Personal Loan");
      System.out.println("3. Education Loan");
      System.out.println("4. Vehicle Loan");

      System.out.print("Enter your choice: ");

      int choice = scanner.nextInt();

      LoanType loanType;

      switch (choice) {

          case 1:
              loanType = LoanType.HOME_LOAN;
              break;

          case 2:
              loanType = LoanType.PERSONAL_LOAN;
              break;

          case 3:
              loanType = LoanType.EDUCATION_LOAN;
              break;

          case 4:
              loanType = LoanType.VEHICLE_LOAN;
              break;

          default:
              System.out.println("Invalid loan type selected.");
              return;
      }

      double interestRate =
              InterestRateCalculator.calculateInterestRate(
                      loanType,
                      cibil
              );

      System.out.println();
      System.out.println("=================================");
      System.out.println("          LOAN RESULT");
      System.out.println("=================================");

      System.out.println("CIBIL Score: " + cibil);

      System.out.println("Loan Type: " + loanType);

      System.out.println(
              "Interest Rate: " + interestRate + "%"
      );

      System.out.println("=================================");
  }
}
