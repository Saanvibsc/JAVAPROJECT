package com.loanapp;

import com.loanapp.service.EMICalculator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EMICalculatorTest {

    @Test
    @DisplayName("Stage 5: Standard EMI Formula Calculation (Rs 500,000 @ 7.5% for 60 months)")
    void testStandardEMI() {
        double principal = 500000.0;
        double annualRate = 7.5;
        int tenureMonths = 60;

        double emi = EMICalculator.calculateEMI(principal, annualRate, tenureMonths);
        // Expected monthly EMI ~ Rs. 10,019.04
        assertEquals(10019.04, emi, 0.50, "Monthly EMI should match banking standard amortization formula");

        double totalPayable = EMICalculator.calculateTotalPayable(emi, tenureMonths);
        assertEquals(601142.40, totalPayable, 30.0, "Total payable should equal EMI * months");

        double totalInterest = EMICalculator.calculateTotalInterest(principal, emi, tenureMonths);
        assertEquals(101142.40, totalInterest, 30.0, "Total interest should equal Total Payable - Principal");
    }

    @Test
    @DisplayName("Stage 5: Zero Interest Loan EMI Calculation")
    void testZeroInterestEMI() {
        double principal = 120000.0;
        double annualRate = 0.0;
        int tenureMonths = 12;

        double emi = EMICalculator.calculateEMI(principal, annualRate, tenureMonths);
        assertEquals(10000.0, emi, 0.01);
        assertEquals(120000.0, EMICalculator.calculateTotalPayable(emi, tenureMonths), 0.01);
        assertEquals(0.0, EMICalculator.calculateTotalInterest(principal, emi, tenureMonths), 0.01);
    }

    @Test
    @DisplayName("Stage 5: Boundary and Invalid Inputs Validation")
    void testInvalidInputs() {
        assertThrows(IllegalArgumentException.class, () -> EMICalculator.calculateEMI(0, 8.0, 12));
        assertThrows(IllegalArgumentException.class, () -> EMICalculator.calculateEMI(-5000, 8.0, 12));
        assertThrows(IllegalArgumentException.class, () -> EMICalculator.calculateEMI(10000, -1.0, 12));
        assertThrows(IllegalArgumentException.class, () -> EMICalculator.calculateEMI(10000, 8.0, 0));
        assertThrows(IllegalArgumentException.class, () -> EMICalculator.calculateEMI(10000, 8.0, -6));
    }
}
