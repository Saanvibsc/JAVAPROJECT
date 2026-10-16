package com.loanapp;

import com.loanapp.model.LoanType;
import com.loanapp.service.CibilService;
import com.loanapp.service.InterestRateCalculator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CibilAndInterestTest {

    @Test
    @DisplayName("Stage 4: CIBIL Score range is strictly between 600 and 900")
    void testCibilScoreRange() {
        for (int i = 0; i < 100; i++) {
            int score = CibilService.generateCibilScore();
            assertTrue(score >= 600 && score <= 900, "CIBIL score out of range: " + score);
        }
    }

    @Test
    @DisplayName("Stage 4: Home Loan Interest Rate Tiers based on CIBIL")
    void testHomeLoanRates() {
        assertEquals(6.5, InterestRateCalculator.calculateInterestRate(LoanType.HOME_LOAN, 820));
        assertEquals(7.0, InterestRateCalculator.calculateInterestRate(LoanType.HOME_LOAN, 760));
        assertEquals(7.5, InterestRateCalculator.calculateInterestRate(LoanType.HOME_LOAN, 710));
        assertEquals(8.0, InterestRateCalculator.calculateInterestRate(LoanType.HOME_LOAN, 660));
        assertEquals(8.5, InterestRateCalculator.calculateInterestRate(LoanType.HOME_LOAN, 620));
    }

    @Test
    @DisplayName("Stage 4: Personal Loan Interest Rate Tiers based on CIBIL")
    void testPersonalLoanRates() {
        assertEquals(9.0, InterestRateCalculator.calculateInterestRate(LoanType.PERSONAL_LOAN, 820));
        assertEquals(9.5, InterestRateCalculator.calculateInterestRate(LoanType.PERSONAL_LOAN, 760));
        assertEquals(10.0, InterestRateCalculator.calculateInterestRate(LoanType.PERSONAL_LOAN, 710));
        assertEquals(11.0, InterestRateCalculator.calculateInterestRate(LoanType.PERSONAL_LOAN, 660));
        assertEquals(12.0, InterestRateCalculator.calculateInterestRate(LoanType.PERSONAL_LOAN, 620));
    }

    @Test
    @DisplayName("Stage 4: Education Loan Interest Rate Tiers based on CIBIL")
    void testEducationLoanRates() {
        assertEquals(4.0, InterestRateCalculator.calculateInterestRate(LoanType.EDUCATION_LOAN, 820));
        assertEquals(4.5, InterestRateCalculator.calculateInterestRate(LoanType.EDUCATION_LOAN, 760));
        assertEquals(5.0, InterestRateCalculator.calculateInterestRate(LoanType.EDUCATION_LOAN, 710));
        assertEquals(5.5, InterestRateCalculator.calculateInterestRate(LoanType.EDUCATION_LOAN, 660));
        assertEquals(6.0, InterestRateCalculator.calculateInterestRate(LoanType.EDUCATION_LOAN, 620));
    }

    @Test
    @DisplayName("Stage 4: Vehicle Loan Interest Rate Tiers based on CIBIL")
    void testVehicleLoanRates() {
        assertEquals(7.0, InterestRateCalculator.calculateInterestRate(LoanType.VEHICLE_LOAN, 820));
        assertEquals(7.5, InterestRateCalculator.calculateInterestRate(LoanType.VEHICLE_LOAN, 760));
        assertEquals(8.0, InterestRateCalculator.calculateInterestRate(LoanType.VEHICLE_LOAN, 710));
        assertEquals(8.5, InterestRateCalculator.calculateInterestRate(LoanType.VEHICLE_LOAN, 660));
        assertEquals(9.0, InterestRateCalculator.calculateInterestRate(LoanType.VEHICLE_LOAN, 620));
    }
}
