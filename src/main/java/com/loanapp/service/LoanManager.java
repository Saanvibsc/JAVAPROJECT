package com.loanapp.service;

import com.loanapp.dao.LoanDAO;
import com.loanapp.model.LoanApplication;
import com.loanapp.model.LoanType;
import com.loanapp.model.User;
import com.loanapp.util.ValidationUtil;

import java.util.List;
import java.util.Scanner;

/**
 * Stage 3: Loan Application Workflows for Borrowers and Administrators.
 */
public class LoanManager {

    private final LoanDAO loanDAO;

    public LoanManager(LoanDAO loanDAO) {
        this.loanDAO = loanDAO;
    }

    /**
     * User Side: Apply for Loan with automatic user binding and comprehensive validation.
     */
    public void applyForLoan(Scanner scanner, User loggedInUser) {
        System.out.println("\n=============================================================");
        System.out.println("             APPLY FOR A NEW LOAN (STAGE 3)                  ");
        System.out.println("=============================================================");
        System.out.println("Applicant Name : " + loggedInUser.getFullName());
        System.out.println("User ID        : " + loggedInUser.getUserId() + " (Auto-assigned)");
        System.out.println("CIBIL Score    : " + loggedInUser.getCibilScore() + " (" + CibilService.getCreditTier(loggedInUser.getCibilScore()) + ")");
        System.out.println("-------------------------------------------------------------");

        // 1. Loan Amount (Must be a positive number)
        double loanAmount;
        while (true) {
            System.out.print("Enter Loan Amount (in INR, e.g. 500000 for Rs. 5 Lakhs): ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("[VALIDATION ERROR] Loan amount is required and cannot be empty.");
                continue;
            }
            try {
                loanAmount = Double.parseDouble(input);
                if (loanAmount > 0) {
                    break;
                } else {
                    System.out.println("[VALIDATION ERROR] Loan amount must be greater than 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("[VALIDATION ERROR] Invalid numeric input. Please enter a valid number.");
            }
        }

        // 2. Loan Type (Home, Personal, Education, Vehicle - Must NOT silently default to Personal if empty)
        LoanType selectedType;
        while (true) {
            System.out.println("\nSelect Loan Type:");
            System.out.println("1. Home Loan");
            System.out.println("2. Personal Loan");
            System.out.println("3. Education Loan");
            System.out.println("4. Vehicle Loan");
            System.out.print("Choose option (1-4): ");
            String typeChoice = scanner.nextLine().trim();

            if (typeChoice.isEmpty()) {
                System.out.println("[VALIDATION ERROR] Loan type selection is required.");
                continue;
            }

            if ("1".equals(typeChoice) || "home".equalsIgnoreCase(typeChoice)) {
                selectedType = LoanType.HOME_LOAN;
                break;
            } else if ("2".equals(typeChoice) || "personal".equalsIgnoreCase(typeChoice)) {
                selectedType = LoanType.PERSONAL_LOAN;
                break;
            } else if ("3".equals(typeChoice) || "education".equalsIgnoreCase(typeChoice)) {
                selectedType = LoanType.EDUCATION_LOAN;
                break;
            } else if ("4".equals(typeChoice) || "vehicle".equalsIgnoreCase(typeChoice) || "car".equalsIgnoreCase(typeChoice)) {
                selectedType = LoanType.VEHICLE_LOAN;
                break;
            } else {
                System.out.println("[VALIDATION ERROR] Please select a valid option (1, 2, 3, or 4).");
            }
        }

        // 3. Automated Interest Rate based on Loan Type and CIBIL score (Stages 3 & 4)
        double interestRate = InterestRateCalculator.calculateInterestRate(selectedType, loggedInUser.getCibilScore());
        System.out.printf("[INFO] Selected: %s | Determined Interest Rate: %.2f%% p.a. (CIBIL: %d)%n",
                selectedType.getDisplayName(), interestRate, loggedInUser.getCibilScore());

        // 4. Loan Duration (Months or Years, > 0)
        int tenureMonths;
        String formattedDuration;
        while (true) {
            System.out.println("\nSelect Duration Unit:");
            System.out.println("1. Months");
            System.out.println("2. Years");
            System.out.print("Choose unit (1-2): ");
            String unitChoice = scanner.nextLine().trim();

            if (!"1".equals(unitChoice) && !"2".equals(unitChoice)) {
                System.out.println("[VALIDATION ERROR] Please select 1 for Months or 2 for Years.");
                continue;
            }

            boolean isYears = "2".equals(unitChoice);
            System.out.print(isYears ? "Enter Duration in Years (e.g. 5): " : "Enter Duration in Months (e.g. 60): ");
            String durationInput = scanner.nextLine().trim();

            try {
                int durationVal = Integer.parseInt(durationInput);
                if (durationVal <= 0) {
                    System.out.println("[VALIDATION ERROR] Duration must be greater than 0.");
                    continue;
                }
                if (isYears) {
                    tenureMonths = durationVal * 12;
                    formattedDuration = durationVal + " Years (" + tenureMonths + " Months)";
                } else {
                    tenureMonths = durationVal;
                    formattedDuration = durationVal + " Months";
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("[VALIDATION ERROR] Invalid duration number.");
            }
        }

        // 5. Annual Income (> 0)
        double annualIncome;
        while (true) {
            System.out.print("\nEnter Annual Income (in INR, e.g. 1200000): ");
            String incomeInput = scanner.nextLine().trim();
            try {
                annualIncome = Double.parseDouble(incomeInput);
                if (ValidationUtil.validateAnnualIncome(annualIncome)) {
                    break;
                } else {
                    System.out.println("[VALIDATION ERROR] Annual income must be greater than 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("[VALIDATION ERROR] Invalid numeric input for annual income.");
            }
        }

        // 6. Loan Purpose (Non-empty)
        String purpose;
        while (true) {
            System.out.print("Enter Purpose of Loan: ");
            purpose = scanner.nextLine().trim();
            if (!purpose.isEmpty()) {
                break;
            }
            System.out.println("[VALIDATION ERROR] Loan purpose cannot be empty.");
        }

        // 7. Preview Estimated EMI before submission
        double estimatedEmi = EMICalculator.calculateEMI(loanAmount, interestRate, tenureMonths);
        System.out.println("\n-------------------------------------------------------------");
        System.out.println("                APPLICATION ESTIMATE SUMMARY                 ");
        System.out.println("-------------------------------------------------------------");
        System.out.printf("Loan Amount      : Rs. %,.2f%n", loanAmount);
        System.out.println("Loan Type        : " + selectedType.getDisplayName());
        System.out.println("Duration         : " + formattedDuration);
        System.out.printf("Interest Rate    : %.2f%% p.a.%n", interestRate);
        System.out.printf("Estimated EMI    : Rs. %,.2f / month%n", estimatedEmi);
        System.out.println("Initial Status   : Pending (Official Review)");
        System.out.println("-------------------------------------------------------------");

        // Construct Application Object
        LoanApplication application = new LoanApplication(
                loggedInUser.getUserId(),
                selectedType.getDisplayName(),
                loanAmount,
                interestRate,
                tenureMonths,
                formattedDuration,
                annualIncome,
                purpose
        );

        boolean submitted = loanDAO.createLoanApplication(application);
        if (submitted) {
            System.out.println("\n[SUCCESS] Loan application submitted successfully! Application ID: #" + application.getLoanId());
            System.out.println("Your application is now under review by our credit officers.");
        } else {
            System.out.println("[ERROR] Failed to save loan application. Please try again later.");
        }
    }

    /**
     * User Side: View My Loan Applications.
     */
    public void viewMyApplications(User loggedInUser) {
        System.out.println("\n==========================================================================================");
        System.out.println("                              MY LOAN APPLICATIONS                                        ");
        System.out.println("==========================================================================================");
        List<LoanApplication> loans = loanDAO.getLoansByUserId(loggedInUser.getUserId());
        if (loans.isEmpty()) {
            System.out.println("[INFO] You have not submitted any loan applications yet.");
            return;
        }

        System.out.printf("%-6s %-16s %-16s %-14s %-10s %-14s %-12s%n",
                "ID", "Type", "Amount", "Tenure", "Rate", "Monthly EMI", "Status");
        System.out.println("------------------------------------------------------------------------------------------");
        for (LoanApplication l : loans) {
            String emiStr = l.getMonthlyEmi() != null ? String.format("Rs. %,.2f", l.getMonthlyEmi()) : "Pending";
            System.out.printf("%-6d %-16s Rs. %-12.2f %-14s %-9.2f%% %-14s %-12s%n",
                    l.getLoanId(), l.getLoanType(), l.getLoanAmount(), l.getLoanDuration(),
                    l.getInterestRate(), emiStr, l.getStatus());

            if ("Rejected".equalsIgnoreCase(l.getStatus()) && l.getRejectionReason() != null) {
                System.out.println("   --> Rejection Reason: " + l.getRejectionReason());
            }
            if ("Needs More Info".equalsIgnoreCase(l.getStatus()) && l.getAdminRemarks() != null) {
                System.out.println("   --> Action Required: " + l.getAdminRemarks());
            }
        }
        System.out.println("==========================================================================================\n");
    }

    /**
     * Admin Side: View All Loan Applications (Requirement 17).
     */
    public void viewAllLoanApplications() {
        System.out.println("\n==========================================================================================");
        System.out.println("                                ALL LOAN APPLICATIONS                                     ");
        System.out.println("==========================================================================================");
        List<LoanApplication> loans = loanDAO.getAllLoans();
        if (loans.isEmpty()) {
            System.out.println("[INFO] No loan applications in the system.");
            return;
        }

        System.out.printf("%-6s %-20s %-16s %-14s %-14s %-12s%n",
                "ID", "Applicant", "Type", "Amount", "Applied Date", "Status");
        System.out.println("------------------------------------------------------------------------------------------");
        for (LoanApplication l : loans) {
            String dateStr = l.getAppliedDate() != null ? l.getAppliedDate().toString().substring(0, 10) : "N/A";
            System.out.printf("%-6d %-20s %-16s Rs. %-10.2f %-14s %-12s%n",
                    l.getLoanId(),
                    (l.getApplicantName() != null ? l.getApplicantName() : ("User #" + l.getUserId())),
                    l.getLoanType(),
                    l.getLoanAmount(),
                    dateStr,
                    l.getStatus());
        }
        System.out.println("==========================================================================================\n");
    }

    /**
     * Admin Side: View Loan Details.
     */
    public void viewLoanDetails(Scanner scanner) {
        System.out.println("\n--- VIEW LOAN APPLICATION DETAILS ---");
        System.out.print("Enter Loan Application ID: ");
        int loanId = readIntInput(scanner);
        if (loanId <= 0) return;

        LoanApplication loan = loanDAO.getLoanById(loanId);
        if (loan == null) {
            System.out.println("[INFO] No loan application found with ID: " + loanId);
            return;
        }

        System.out.println("\n+--------------------------------------------------------------------------+");
        System.out.println("|                         LOAN APPLICATION DETAILS                         |");
        System.out.println("+--------------------------------------------------------------------------+");
        System.out.println("  Loan ID          : #" + loan.getLoanId());
        System.out.println("  Applicant Name   : " + loan.getApplicantName());
        System.out.println("  Applicant Email  : " + loan.getApplicantEmail());
        System.out.println("  Applicant Mobile : " + loan.getApplicantMobile());
        System.out.println("  Loan Type        : " + loan.getLoanType());
        System.out.printf("  Loan Amount      : Rs. %,.2f%n", loan.getLoanAmount());
        System.out.println("  Loan Duration    : " + loan.getLoanDuration());
        System.out.printf("  Interest Rate    : %.2f%% p.a.%n", loan.getInterestRate());
        System.out.printf("  Annual Income    : Rs. %,.2f%n", loan.getAnnualIncome());
        System.out.println("  Purpose          : " + loan.getPurpose());
        System.out.println("  Status           : " + loan.getStatus());
        System.out.println("  Applied Date     : " + loan.getAppliedDate());

        if (loan.getMonthlyEmi() != null) {
            System.out.println("----------------------------------------------------------------------------");
            System.out.printf("  Approved EMI     : Rs. %,.2f / month%n", loan.getMonthlyEmi());
            System.out.printf("  Total Interest   : Rs. %,.2f%n", loan.getTotalInterest());
            System.out.printf("  Total Payable    : Rs. %,.2f%n", loan.getTotalPayable());
            System.out.println("  Decision Date    : " + loan.getDecisionDate());
        }

        if (loan.getRejectionReason() != null) {
            System.out.println("  Rejection Reason : " + loan.getRejectionReason());
        }
        if (loan.getAdminRemarks() != null) {
            System.out.println("  Admin Remarks    : " + loan.getAdminRemarks());
        }
        System.out.println("+--------------------------------------------------------------------------+\n");
    }

    /**
     * Admin Side: Approve Loan (Stages 3 & 5).
     */
    public void approveLoan(Scanner scanner) {
        System.out.println("\n--- APPROVE LOAN APPLICATION (STAGES 3 & 5) ---");
        System.out.print("Enter Loan ID to Approve: ");
        int loanId = readIntInput(scanner);
        if (loanId <= 0) return;

        LoanApplication loan = loanDAO.getLoanById(loanId);
        if (loan == null) {
            System.out.println("[INFO] Loan application #" + loanId + " not found.");
            return;
        }

        if ("Approved".equalsIgnoreCase(loan.getStatus())) {
            System.out.println("[INFO] This loan application has already been approved.");
            return;
        }

        System.out.println("\nReviewing Loan #" + loan.getLoanId() + " for " + loan.getApplicantName());
        System.out.printf("Amount: Rs. %,.2f | Type: %s | Tenure: %s | Rate: %.2f%%%n",
                loan.getLoanAmount(), loan.getLoanType(), loan.getLoanDuration(), loan.getInterestRate());

        System.out.print("Confirm approval and trigger EMI generation & email notification? (yes/no): ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("yes") && !confirm.equalsIgnoreCase("y")) {
            System.out.println("[CANCELLED] Approval cancelled.");
            return;
        }

        LoanApprovalResult result = LoanApprovalService.approveLoan(loan, loanDAO);
        if (result != null) {
            result.displayResult();
            System.out.println("[SUCCESS] Loan approved, amortization recorded in MySQL, and confirmation email dispatched.");
        }
    }

    /**
     * Admin Side: Reject Loan (Stages 3 & 5).
     */
    public void rejectLoan(Scanner scanner) {
        System.out.println("\n--- REJECT LOAN APPLICATION ---");
        System.out.print("Enter Loan ID to Reject: ");
        int loanId = readIntInput(scanner);
        if (loanId <= 0) return;

        LoanApplication loan = loanDAO.getLoanById(loanId);
        if (loan == null) {
            System.out.println("[INFO] Loan application #" + loanId + " not found.");
            return;
        }

        if ("Rejected".equalsIgnoreCase(loan.getStatus())) {
            System.out.println("[INFO] This loan application is already rejected.");
            return;
        }

        System.out.print("Enter Mandatory Reason for Rejection: ");
        String reason = scanner.nextLine().trim();
        if (reason.isEmpty()) {
            System.out.println("[VALIDATION ERROR] Rejection reason is required.");
            return;
        }

        boolean rejected = LoanApprovalService.rejectLoan(loan, reason, loanDAO);
        if (rejected) {
            System.out.println("[SUCCESS] Loan #" + loanId + " rejected. Rejection reason recorded and borrower notified via email.");
        }
    }

    /**
     * Admin Side: Request More Info (Stage 3).
     */
    public void requestMoreInfo(Scanner scanner) {
        System.out.println("\n--- REQUEST MORE INFORMATION ---");
        System.out.print("Enter Loan ID: ");
        int loanId = readIntInput(scanner);
        if (loanId <= 0) return;

        LoanApplication loan = loanDAO.getLoanById(loanId);
        if (loan == null) {
            System.out.println("[INFO] Loan application #" + loanId + " not found.");
            return;
        }

        System.out.print("Enter information or documentation requested from borrower: ");
        String remarks = scanner.nextLine().trim();
        if (remarks.isEmpty()) {
            System.out.println("[VALIDATION ERROR] Information details are required.");
            return;
        }

        boolean requested = LoanApprovalService.requestMoreInfo(loan, remarks, loanDAO);
        if (requested) {
            System.out.println("[SUCCESS] Status updated to 'Needs More Info' and borrower notified via email.");
        }
    }

    private int readIntInput(Scanner scanner) {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[VALIDATION ERROR] Please enter a valid number.");
            return -1;
        }
    }
}
