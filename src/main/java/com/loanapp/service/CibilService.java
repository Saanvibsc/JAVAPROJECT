package com.loanapp.service;

import java.util.Random;

/**
 * Stage 4: CIBIL Credit Score Simulator and Evaluator.
 * Simulates scores strictly between 600 and 900 as specified in the assignment.
 */
public class CibilService {

    private static final Random RANDOM = new Random();

    private CibilService() {
    }

    /**
     * Generates simulated CIBIL score in the range [600, 900].
     */
    public static int generateCibilScore() {
        return 600 + RANDOM.nextInt(301);
    }

    /**
     * Categorizes score into descriptive credit tiers.
     */
    public static String getCreditTier(int score) {
        if (score >= 800) return "Excellent (Prime Applicant)";
        if (score >= 750) return "Very Good (Preferred Applicant)";
        if (score >= 700) return "Good (Standard Approval)";
        if (score >= 650) return "Fair (Higher Risk)";
        return "Subprime (Maximum Rate)";
    }
}
