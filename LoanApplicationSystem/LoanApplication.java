/**
 * LoanApplication Model representing a loan submitted by a user for review.
 * Contains all required Stage 3 fields:
 * loan_id, user_id, loan_amount, loan_type, loan_duration, interest_rate,
 * annual_income, purpose, status, application_date, rejection_reason.
 */
public class LoanApplication {
    private int loanId;
    private int userId;
    private double loanAmount;
    private String loanType;
    private String loanDuration; // Supports "Months" or "Years" (e.g. "24 Months", "5 Years")
    private double interestRate;
    private double annualIncome;
    private String purpose;
    private String status; // "Pending", "Approved", "Rejected", "Needs More Info"
    private String applicationDate;
    private String rejectionReason;

    // Associated User Details (from JOIN queries)
    private String userName;
    private String userEmail;
    private String userMobileNumber;

    public LoanApplication() {
        this.status = "Pending";
    }

    public LoanApplication(int userId, double loanAmount, String loanType, String loanDuration,
                           double interestRate, double annualIncome, String purpose) {
        this.userId = userId;
        this.loanAmount = loanAmount;
        this.loanType = loanType;
        this.loanDuration = loanDuration;
        this.interestRate = interestRate;
        this.annualIncome = annualIncome;
        this.purpose = purpose;
        this.status = "Pending";
    }

    public LoanApplication(int userId, double loanAmount, String loanType, int loanDurationMonths,
                           double interestRate, double annualIncome, String purpose) {
        this(userId, loanAmount, loanType, loanDurationMonths + " Months", interestRate, annualIncome, purpose);
    }

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public String getLoanDuration() {
        return loanDuration;
    }

    public void setLoanDuration(String loanDuration) {
        this.loanDuration = loanDuration;
    }

    public void setLoanDuration(int months) {
        this.loanDuration = months + " Months";
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public double getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(double annualIncome) {
        this.annualIncome = annualIncome;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(String applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserMobileNumber() {
        return userMobileNumber;
    }

    public void setUserMobileNumber(String userMobileNumber) {
        this.userMobileNumber = userMobileNumber;
    }
}
