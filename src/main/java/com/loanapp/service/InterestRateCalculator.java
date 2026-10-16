package com.loanapp.service;

import com.loanapp.model.LoanType;

/**
 * Stage 4: Interest Rate Calculator based on CIBIL Score and Loan Type.
 * Implements exact interest rate tiers required by Stage 4 specification.
 */
public class InterestRateCalculator {

    private InterestRateCalculator() {
    }

    /**
     * Calculates annual interest rate (%) based on LoanType and CIBIL score.
     */
    public static double calculateInterestRate(LoanType loanType, int cibil) {
        if (loanType == null) {
            loanType = LoanType.PERSONAL_LOAN;
        }

        switch (loanType) {
            case HOME_LOAN:
                if (cibil >= 800) return 6.5;
                if (cibil >= 750) return 7.0;
                if (cibil >= 700) return 7.5;
                if (cibil >= 650) return 8.0;
                return 8.5;

            case PERSONAL_LOAN:
                if (cibil >= 800) return 9.0;
                if (cibil >= 750) return 9.5;
                if (cibil >= 700) return 10.0;
                if (cibil >= 650) return 11.0;
                return 12.0;

            case EDUCATION_LOAN:
                if (cibil >= 800) return 4.0;
                if (cibil >= 750) return 4.5;
                if (cibil >= 700) return 5.0;
                if (cibil >= 650) return 5.5;
                return 6.0;

            case VEHICLE_LOAN:
                if (cibil >= 800) return 7.0;
                if (cibil >= 750) return 7.5;
                if (cibil >= 700) return 8.0;
                if (cibil >= 650) return 8.5;
                return 9.0;

            default:
                throw new IllegalArgumentException("Unknown loan type: " + loanType);
        }
    }

    /**
     * Overload accepting loan type as String.
     */
    public static double calculateInterestRate(String loanTypeStr, int cibil) {
        LoanType type = LoanType.fromString(loanTypeStr);
        return calculateInterestRate(type != null ? type : LoanType.PERSONAL_LOAN, cibil);
    }
}
