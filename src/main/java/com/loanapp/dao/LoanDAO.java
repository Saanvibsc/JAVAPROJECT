package com.loanapp.dao;

import com.loanapp.db.DatabaseConnection;
import com.loanapp.model.LoanApplication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for loan applications (Stages 3 and 5).
 */
public class LoanDAO {

    /**
     * Stage 3: Submits a new loan application in 'Pending' status.
     */
    public boolean createLoanApplication(LoanApplication loan) {
        String sql = "INSERT INTO loan_applications (user_id, loan_type, loan_amount, interest_rate, tenure_months, loan_duration, annual_income, purpose, loan_status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Pending')";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, loan.getUserId());
            ps.setString(2, loan.getLoanType());
            ps.setDouble(3, loan.getLoanAmount());
            ps.setDouble(4, loan.getInterestRate());
            ps.setInt(5, loan.getTenureMonths());
            ps.setString(6, loan.getLoanDuration());
            ps.setDouble(7, loan.getAnnualIncome());
            ps.setString(8, loan.getPurpose());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        loan.setLoanId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Failed to create loan application: " + e.getMessage());
        }
        return false;
    }

    /**
     * Fetches all loan applications submitted by a specific user.
     */
    public List<LoanApplication> getLoansByUserId(int userId) {
        List<LoanApplication> list = new ArrayList<>();
        String sql = "SELECT l.*, u.first_name, u.last_name, u.email, u.mobile "
                + "FROM loan_applications l "
                + "JOIN users u ON l.user_id = u.user_id "
                + "WHERE l.user_id = ? ORDER BY l.loan_id DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToLoan(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Error fetching user loans: " + e.getMessage());
        }
        return list;
    }

    /**
     * Stage 3 Requirement 17: Fetches all loan applications in the system for admin review.
     */
    public List<LoanApplication> getAllLoans() {
        List<LoanApplication> list = new ArrayList<>();
        String sql = "SELECT l.*, u.first_name, u.last_name, u.email, u.mobile "
                + "FROM loan_applications l "
                + "JOIN users u ON l.user_id = u.user_id "
                + "ORDER BY l.loan_id DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToLoan(rs));
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Error fetching all loans: " + e.getMessage());
        }
        return list;
    }

    /**
     * Fetches detailed loan record by ID including applicant contact info.
     */
    public LoanApplication getLoanById(int loanId) {
        String sql = "SELECT l.*, u.first_name, u.last_name, u.email, u.mobile "
                + "FROM loan_applications l "
                + "JOIN users u ON l.user_id = u.user_id "
                + "WHERE l.loan_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, loanId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToLoan(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Error fetching loan by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Stage 5: Updates loan status to 'Approved' and persists calculated EMI, total interest, and total payable.
     */
    public boolean updateLoanApproval(int loanId, double emi, double totalInterest, double totalPayable) {
        String sql = "UPDATE loan_applications SET loan_status = 'Approved', monthly_emi = ?, total_interest = ?, total_payable = ?, decision_date = ? WHERE loan_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, emi);
            ps.setDouble(2, totalInterest);
            ps.setDouble(3, totalPayable);
            ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            ps.setInt(5, loanId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Loan approval update failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Stage 3 & 5: Updates loan status to 'Rejected' with mandatory rejection reason.
     */
    public boolean updateLoanRejection(int loanId, String reason) {
        String sql = "UPDATE loan_applications SET loan_status = 'Rejected', rejection_reason = ?, decision_date = ? WHERE loan_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, reason);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setInt(3, loanId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Loan rejection update failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Stage 3: Requests more info from borrower.
     */
    public boolean updateLoanMoreInfo(int loanId, String remarks) {
        String sql = "UPDATE loan_applications SET loan_status = 'Needs More Info', admin_remarks = ? WHERE loan_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, remarks);
            ps.setInt(2, loanId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Loan more info update failed: " + e.getMessage());
            return false;
        }
    }

    private LoanApplication mapRowToLoan(ResultSet rs) throws SQLException {
        LoanApplication loan = new LoanApplication();
        loan.setLoanId(rs.getInt("loan_id"));
        loan.setUserId(rs.getInt("user_id"));
        loan.setLoanType(rs.getString("loan_type"));
        loan.setLoanAmount(rs.getDouble("loan_amount"));
        loan.setInterestRate(rs.getDouble("interest_rate"));
        loan.setTenureMonths(rs.getInt("tenure_months"));
        loan.setLoanDuration(rs.getString("loan_duration"));
        loan.setAnnualIncome(rs.getDouble("annual_income"));
        loan.setPurpose(rs.getString("purpose"));
        loan.setStatus(rs.getString("loan_status"));

        double emi = rs.getDouble("monthly_emi");
        if (!rs.wasNull()) loan.setMonthlyEmi(emi);

        double totalInt = rs.getDouble("total_interest");
        if (!rs.wasNull()) loan.setTotalInterest(totalInt);

        double totalPay = rs.getDouble("total_payable");
        if (!rs.wasNull()) loan.setTotalPayable(totalPay);

        loan.setRejectionReason(rs.getString("rejection_reason"));
        loan.setAdminRemarks(rs.getString("admin_remarks"));
        loan.setAppliedDate(rs.getTimestamp("applied_date"));
        loan.setDecisionDate(rs.getTimestamp("decision_date"));

        try {
            String fname = rs.getString("first_name");
            String lname = rs.getString("last_name");
            loan.setApplicantName(((fname != null ? fname : "") + " " + (lname != null ? lname : "")).trim());
            loan.setApplicantEmail(rs.getString("email"));
            loan.setApplicantMobile(rs.getString("mobile"));
        } catch (SQLException ignore) {
            // Join columns may not be present in all queries
        }

        return loan;
    }
}
