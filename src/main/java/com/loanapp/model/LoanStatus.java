package com.loanapp.model;

/**
 * Lifecycle states of a loan application.
 */
public enum LoanStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    NEEDS_MORE_INFO("Needs More Info");

    private final String displayStatus;

    LoanStatus(String displayStatus) {
        this.displayStatus = displayStatus;
    }

    public String getDisplayStatus() {
        return displayStatus;
    }

    public static LoanStatus fromString(String text) {
        if (text == null) return PENDING;
        String clean = text.trim().toLowerCase();
        if (clean.equals("approved")) return APPROVED;
        if (clean.equals("rejected")) return REJECTED;
        if (clean.contains("info") || clean.contains("more")) return NEEDS_MORE_INFO;
        return PENDING;
    }
}
