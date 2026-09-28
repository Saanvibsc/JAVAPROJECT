package com.loan;

public class InterestRateCalculator {
  
	
	 public static double calculateInterestRate(
	            LoanType loanType,
	            int cibil) {

	        switch (loanType) {

	            case HOME_LOAN:

	                if (cibil >= 800) {
	                    return 6.5;
	                } else if (cibil >= 750) {
	                    return 7.0;
	                } else if (cibil >= 700) {
	                    return 7.5;
	                } else if (cibil >= 650) {
	                    return 8.0;
	                } else {
	                    return 8.5;
	                }

	            case PERSONAL_LOAN:

	                if (cibil >= 800) {
	                    return 9.0;
	                } else if (cibil >= 750) {
	                    return 9.5;
	                } else if (cibil >= 700) {
	                    return 10.0;
	                } else if (cibil >= 650) {
	                    return 11.0;
	                } else {
	                    return 12.0;
	                }

	            case EDUCATION_LOAN:

	                if (cibil >= 800) {
	                    return 4.0;
	                } else if (cibil >= 750) {
	                    return 4.5;
	                } else if (cibil >= 700) {
	                    return 5.0;
	                } else if (cibil >= 650) {
	                    return 5.5;
	                } else {
	                    return 6.0;
	                }

	            case VEHICLE_LOAN:

	                if (cibil >= 800) {
	                    return 7.0;
	                } else if (cibil >= 750) {
	                    return 7.5;
	                } else if (cibil >= 700) {
	                    return 8.0;
	                } else if (cibil >= 650) {
	                    return 8.5;
	                } else {
	                    return 9.0;
	                }

	            default:
	                throw new IllegalArgumentException(
	                        "Invalid loan type"
	                );
	        }
	    }
}
