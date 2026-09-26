export interface LoanProduct {
  code: string;
  name: string;
  category: 'Retail' | 'Education' | 'Housing' | 'Commercial';
  baseInterestRate: number;
  minAmount: number;
  maxAmount: number;
  minTenureMonths: number;
  maxTenureMonths: number;
  minCreditScore: number;
  maxFoirPercent: number;
  processingFeePercent: number;
  collateralRequired: boolean;
}

export interface UnderwritingCheck {
  ruleName: string;
  passed: boolean;
  actualValue: string;
  thresholdValue: string;
  weight: string;
}

export interface LoanApplication {
  id: string;
  applicantName: string;
  email: string;
  phone: string;
  panOrTaxId: string;
  employmentType: 'Salaried' | 'Self-Employed' | 'Student' | 'Business Owner';
  employerOrInstitution: string;
  workExperienceYears: number;
  monthlyNetIncome: number;
  existingMonthlyObligations: number;
  creditScore: number;
  loanProductCode: string;
  loanProductName: string;
  requestedAmount: number;
  tenureMonths: number;
  annualInterestRate: number;
  monthlyEmi: number;
  totalInterestPayable: number;
  totalAmountPayable: number;
  foirPercent: number;
  riskGrade: 'A+' | 'A' | 'B' | 'C' | 'D';
  status: 'Approved' | 'Under Review' | 'Conditional' | 'Rejected' | 'Disbursed';
  underwritingScore: number;
  underwritingChecks: UnderwritingCheck[];
  decisionSummary: string;
  purpose: string;
  collateralDescription: string;
  submittedAt: string;
  updatedAt: string;
}

export interface AmortizationRow {
  month: number;
  year: number;
  openingBalance: number;
  emi: number;
  principalComponent: number;
  interestComponent: number;
  extraPrepayment: number;
  closingBalance: number;
}
