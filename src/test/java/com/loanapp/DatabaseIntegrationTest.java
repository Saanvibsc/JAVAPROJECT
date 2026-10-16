package com.loanapp;

import com.loanapp.dao.AuditDAO;
import com.loanapp.dao.LoanDAO;
import com.loanapp.dao.UserDAO;
import com.loanapp.db.DatabaseConnection;
import com.loanapp.model.AuditLog;
import com.loanapp.model.LoanApplication;
import com.loanapp.model.User;
import com.loanapp.service.LoanApprovalResult;
import com.loanapp.service.LoanApprovalService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseIntegrationTest {

    private static UserDAO userDAO;
    private static LoanDAO loanDAO;
    private static AuditDAO auditDAO;

    @BeforeAll
    static void setUp() {
        DatabaseConnection.initializeDatabase();
        userDAO = new UserDAO();
        loanDAO = new LoanDAO();
        auditDAO = new AuditDAO();
    }

    @Test
    @DisplayName("Database Connection is Available")
    void testConnection() {
        try (Connection con = DatabaseConnection.getConnection()) {
            assertNotNull(con);
            assertFalse(con.isClosed());
        } catch (SQLException e) {
            fail("JDBC Connection failed: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Stage 1 & 4: User Registration and Account Lockout after 3 Failed Attempts")
    void testUserLockout() {
        long timestamp = System.currentTimeMillis();
        String username = "testuser_" + timestamp;
        String email = "test_" + timestamp + "@example.com";

        User user = new User(0, "Test", "User", username, "Test@1234", email, "9876500000",
                "Mumbai", "ABCDE9999F", 750, "ROLE_USER", "Active", 0);

        boolean registered = userDAO.registerUser(user);
        assertTrue(registered, "User should register successfully");
        assertTrue(user.getUserId() > 0);

        // Fetch user
        User fetched = userDAO.getUserById(user.getUserId());
        assertNotNull(fetched);
        assertEquals("Active", fetched.getStatus());
        assertEquals(0, fetched.getFailedLoginAttempts());

        // Simulate 3 failed login attempts
        userDAO.recordFailedAttempt(fetched.getUserId(), 1);
        userDAO.recordFailedAttempt(fetched.getUserId(), 2);
        userDAO.recordFailedAttempt(fetched.getUserId(), 3);

        User lockedUser = userDAO.getUserById(user.getUserId());
        assertEquals("Inactive", lockedUser.getStatus(), "Account must be locked to Inactive after 3 failed attempts");
        assertEquals(3, lockedUser.getFailedLoginAttempts());

        // Unlock user
        userDAO.updateStatus(user.getUserId(), "Active");
        User activeUser = userDAO.getUserById(user.getUserId());
        assertEquals("Active", activeUser.getStatus());
        assertEquals(0, activeUser.getFailedLoginAttempts());
    }

    @Test
    @DisplayName("Stage 3 & 5: Loan Origination and Approval with EMI Calculation")
    void testLoanOriginationAndApproval() {
        long timestamp = System.currentTimeMillis();
        String username = "borrower_" + timestamp;
        String email = "borrower_" + timestamp + "@bank.com";

        User borrower = new User(0, "Vikram", "Bose", username, "Borrower@123", email, "9820098200",
                "Delhi", "XYZPK1234M", 800, "ROLE_USER", "Active", 0);
        userDAO.registerUser(borrower);

        // Create Loan Application
        LoanApplication loan = new LoanApplication(
                borrower.getUserId(),
                "Home Loan",
                600000.0,
                6.5,
                60,
                "60 Months",
                1500000.0,
                "House renovation"
        );
        loan.setApplicantEmail(borrower.getEmail());
        loan.setApplicantName(borrower.getFullName());

        boolean created = loanDAO.createLoanApplication(loan);
        assertTrue(created);
        assertTrue(loan.getLoanId() > 0);

        // Approve loan (Stage 5)
        LoanApprovalResult approvalResult = LoanApprovalService.approveLoan(loan, loanDAO);
        assertNotNull(approvalResult);
        assertEquals("APPROVED", approvalResult.getStatus());
        assertTrue(approvalResult.getEmi() > 0);
        assertTrue(approvalResult.getTotalInterest() > 0);
        assertTrue(approvalResult.getTotalPayable() > loan.getLoanAmount());

        // Verify stored in MySQL
        LoanApplication stored = loanDAO.getLoanById(loan.getLoanId());
        assertNotNull(stored);
        assertEquals("Approved", stored.getStatus());
        assertNotNull(stored.getMonthlyEmi());
        assertNotNull(stored.getTotalInterest());
        assertNotNull(stored.getTotalPayable());
    }

    @Test
    @DisplayName("Stage 2: Transactional User Deletion with Audit Logging")
    void testTransactionalDeleteWithAudit() {
        long timestamp = System.currentTimeMillis();
        String username = "del_user_" + timestamp;
        String email = "del_" + timestamp + "@example.com";

        User user = new User(0, "Delete", "Me", username, "Pass@1234", email, "9876543201",
                "Pune", "DELME1234K", 700, "ROLE_USER", "Active", 0);
        userDAO.registerUser(user);
        int userId = user.getUserId();

        boolean deleted = userDAO.deleteUserWithAudit(user, "admin", "Student unit test deletion");
        assertTrue(deleted, "User should be deleted transactionally");

        // Verify user is gone
        assertNull(userDAO.getUserById(userId));

        // Verify audit log entry exists
        List<AuditLog> logs = auditDAO.getAllAuditLogs();
        boolean foundAudit = logs.stream().anyMatch(l -> l.getDeletedUserId() == userId && "DELETE_USER".equals(l.getActionType()));
        assertTrue(foundAudit, "Audit log must contain transaction record of deleted user");
    }
}
