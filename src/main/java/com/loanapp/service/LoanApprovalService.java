package com.loanapp.service;

import com.loanapp.dao.LoanDAO;
import com.loanapp.model.LoanApplication;

/**
 * Stage 5: Loan Approval, EMI Processing, Database Persistence, and SMTP Dispatch Service.
 */
public class LoanApprovalService {

    private LoanApprovalService() {
    }

    /**
     * Approves a loan application, calculates EMI & interest totals, persists to MySQL, and emails the user.
     */
    public static LoanApprovalResult approveLoan(LoanApplication loan, LoanDAO loanDAO) {
        if (loan == null) {
            System.out.println("[ERROR] Loan record not found.");
            return null;
        }

        double emi = EMICalculator.calculateEMI(
                loan.getLoanAmount(),
                loan.getInterestRate(),
                loan.getTenureMonths()
        );
        double totalPayable = EMICalculator.calculateTotalPayable(emi, loan.getTenureMonths());
        double totalInterest = EMICalculator.calculateTotalInterest(loan.getLoanAmount(), emi, loan.getTenureMonths());

        boolean updated = loanDAO.updateLoanApproval(loan.getLoanId(), emi, totalInterest, totalPayable);

        if (updated) {
            loan.setStatus("Approved");
            loan.setMonthlyEmi(emi);
            loan.setTotalInterest(totalInterest);
            loan.setTotalPayable(totalPayable);

            // Send Real SMTP email notification
            EmailService.sendLoanApprovalEmail(
                    loan.getApplicantEmail(),
                    loan.getApplicantName(),
                    loan.getLoanId(),
                    loan.getLoanAmount(),
                    loan.getInterestRate(),
                    loan.getTenureMonths(),
                    emi,
                    totalInterest,
                    totalPayable
            );

            return new LoanApprovalResult(
                    "APPROVED",
                    loan.getLoanAmount(),
                    loan.getInterestRate(),
                    loan.getTenureMonths(),
                    emi,
                    totalInterest,
                    totalPayable
            );
        } else {
            System.out.println("[DB ERROR] Failed to record loan approval in database.");
            return null;
        }
    }

    /**
     * Rejects a loan application with mandatory reason, persists to MySQL, and emails the user.
     */
    public static boolean rejectLoan(LoanApplication loan, String reason, LoanDAO loanDAO) {
        if (loan == null) {
            System.out.println("[ERROR] Loan record not found.");
            return false;
        }
        if (reason == null || reason.trim().isEmpty()) {
            System.out.println("[VALIDATION ERROR] Rejection reason is mandatory and cannot be empty.");
            return false;
        }

        boolean updated = loanDAO.updateLoanRejection(loan.getLoanId(), reason.trim());

        if (updated) {
            loan.setStatus("Rejected");
            loan.setRejectionReason(reason.trim());

            // Send Real SMTP email notification
            EmailService.sendLoanRejectionEmail(
                    loan.getApplicantEmail(),
                    loan.getApplicantName(),
                    loan.getLoanId(),
                    reason.trim()
            );
            return true;
        } else {
            System.out.println("[DB ERROR] Failed to update rejection in database.");
            return false;
        }
    }

    /**
     * Updates loan application to 'Needs More Info' with remarks and sends email to borrower.
     */
    public static boolean requestMoreInfo(LoanApplication loan, String remarks, LoanDAO loanDAO) {
        if (loan == null) {
            System.out.println("[ERROR] Loan record not found.");
            return false;
        }
        if (remarks == null || remarks.trim().isEmpty()) {
            System.out.println("[VALIDATION ERROR] Details of required information must be provided.");
            return false;
        }

        boolean updated = loanDAO.updateLoanMoreInfo(loan.getLoanId(), remarks.trim());

        if (updated) {
            loan.setStatus("Needs More Info");
            loan.setAdminRemarks(remarks.trim());

            // Send Real SMTP notification
            EmailService.sendMoreInfoEmail(
                    loan.getApplicantEmail(),
                    loan.getApplicantName(),
                    loan.getLoanId(),
                    remarks.trim()
            );
            return true;
        } else {
            System.out.println("[DB ERROR] Failed to update status in database.");
            return false;
        }
    }
}
