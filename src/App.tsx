import React, { useState, useEffect, useRef } from 'react';
import { 
  Terminal, 
  Code2, 
  Send, 
  CheckCircle2, 
  XCircle, 
  Mail, 
  FileText, 
  Copy, 
  Check, 
  RefreshCw, 
  Eye,
  IndianRupee,
  Layers,
  HelpCircle
} from 'lucide-react';

interface UserRecord {
  userId: number;
  fullName: string;
  email: string;
  mobileNumber: string;
  role: 'USER' | 'ADMIN';
}

interface LoanRecord {
  loanId: number;
  userId: number;
  userName: string;
  userEmail: string;
  userMobile: string;
  loanAmount: number;
  loanType: string;
  loanDuration: string;
  interestRate: number;
  annualIncome: number;
  purpose: string;
  status: 'Pending' | 'Approved' | 'Rejected' | 'Needs More Info';
  applicationDate: string;
  rejectionReason: string | null;
}

interface EmailRecord {
  id: string;
  to: string;
  subject: string;
  body: string;
  timestamp: string;
  status: string;
}

interface JavaFile {
  name: string;
  role: string;
  content: string;
}

export function App() {
  const [activeTab, setActiveTab] = useState<'terminal' | 'manager' | 'code' | 'emails'>('terminal');
  const [loans, setLoans] = useState<LoanRecord[]>([]);
  const [users, setUsers] = useState<UserRecord[]>([]);
  const [emails, setEmails] = useState<EmailRecord[]>([]);
  const [javaFiles, setJavaFiles] = useState<JavaFile[]>([]);
  const [selectedJavaFile, setSelectedJavaFile] = useState<string>('Main.java');
  const [copied, setCopied] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(false);

  // Terminal state
  const [terminalHistory, setTerminalHistory] = useState<string[]>([
    '=============================================================',
    '       WELCOME TO LOAN APPLICATION SYSTEM (STAGE 3)          ',
    '       Indian Banking System Edition (Rs. / INR)             ',
    '=============================================================',
    '',
    '---------------- MAIN MENU ----------------',
    '1. Login',
    '2. Register New User',
    '3. Exit',
    'Please enter your choice (1-3) or use the quick buttons below:'
  ]);
  const [terminalInput, setTerminalInput] = useState<string>('');
  const [consoleState, setConsoleState] = useState<{
    stage: 'MAIN_MENU' | 'LOGIN_EMAIL' | 'LOGIN_PASSWORD' | 'USER_MENU' | 'ADMIN_MENU' | 'APPLY_AMOUNT' | 'APPLY_TYPE' | 'APPLY_DURATION_UNIT' | 'APPLY_DURATION_VAL' | 'APPLY_INCOME' | 'APPLY_PURPOSE' | 'ADMIN_DETAILS_ID' | 'ADMIN_APPROVE_ID' | 'ADMIN_REJECT_ID' | 'ADMIN_REJECT_REASON' | 'ADMIN_MORE_INFO_ID' | 'ADMIN_MORE_INFO_TEXT';
    currentUser: UserRecord | null;
    tempLoan: Partial<LoanRecord>;
    targetRejectId?: number;
    targetMoreInfoId?: number;
    tempDurationUnit?: string;
  }>({
    stage: 'MAIN_MENU',
    currentUser: null,
    tempLoan: {}
  });

  const terminalEndRef = useRef<HTMLDivElement>(null);

  // Modals state for Manager tab
  const [rejectModalLoan, setRejectModalLoan] = useState<LoanRecord | null>(null);
  const [rejectionReasonInput, setRejectionReasonInput] = useState<string>('');
  const [moreInfoModalLoan, setMoreInfoModalLoan] = useState<LoanRecord | null>(null);
  const [moreInfoInput, setMoreInfoInput] = useState<string>('');
  const [selectedLoanDetails, setSelectedLoanDetails] = useState<LoanRecord | null>(null);

  // Fetch initial data
  const fetchData = async () => {
    setLoading(true);
    try {
      const [loansRes, usersRes, emailsRes, filesRes] = await Promise.all([
        fetch('/api/loans').then(r => r.json()),
        fetch('/api/users').then(r => r.json()),
        fetch('/api/emails').then(r => r.json()),
        fetch('/api/java-files').then(r => r.json())
      ]);

      if (loansRes.success) setLoans(loansRes.loans);
      if (usersRes.success) setUsers(usersRes.users);
      if (emailsRes.success) setEmails(emailsRes.emails);
      if (filesRes.success) setJavaFiles(filesRes.files);
    } catch (err) {
      console.error('Fetch error:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  useEffect(() => {
    terminalEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [terminalHistory]);

  const addTerminalLines = (lines: string[]) => {
    setTerminalHistory(prev => [...prev, ...lines]);
  };

  const formatRupees = (amount: number) => {
    return 'Rs. ' + amount.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  };

  // Terminal command handler
  const handleTerminalSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const input = terminalInput.trim();
    if (!input && consoleState.stage === 'MAIN_MENU') return;

    addTerminalLines([`> ${input}`]);
    setTerminalInput('');

    switch (consoleState.stage) {
      case 'MAIN_MENU':
        if (input === '1') {
          addTerminalLines([
            '',
            '================== LOGIN ==================',
            'Enter Email (e.g. rahul.sharma@example.com or admin@loanapp.com):'
          ]);
          setConsoleState(prev => ({ ...prev, stage: 'LOGIN_EMAIL' }));
        } else if (input === '2') {
          addTerminalLines([
            '',
            '================ REGISTER ================',
            'Demo Note: Use quick demo logins or select from pre-registered users:',
            '1) Admin: admin@loanapp.com (admin123)',
            '2) User: rahul.sharma@example.com (user123)',
            '',
            '---------------- MAIN MENU ----------------',
            '1. Login',
            '2. Register New User',
            '3. Exit',
            'Please enter your choice (1-3):'
          ]);
        } else if (input === '3') {
          addTerminalLines([
            '',
            'Thank you for using the Loan Application System. Goodbye!',
            '[Console Session Terminated - Type 1 to restart session]'
          ]);
        } else {
          addTerminalLines(['Invalid choice. Please select 1, 2, or 3.']);
        }
        break;

      case 'LOGIN_EMAIL':
        const matchedUser = users.find(u => u.email.toLowerCase() === input.toLowerCase());
        if (!matchedUser) {
          addTerminalLines([
            `User with email "${input}" not found.`,
            'Available demo accounts:',
            '  - rahul.sharma@example.com (User)',
            '  - admin@loanapp.com (Admin)',
            'Enter Email:'
          ]);
        } else {
          addTerminalLines(['Enter Password:']);
          setConsoleState(prev => ({ ...prev, stage: 'LOGIN_PASSWORD', currentUser: matchedUser }));
        }
        break;

      case 'LOGIN_PASSWORD':
        const user = consoleState.currentUser;
        if (!user) {
          setConsoleState({ stage: 'MAIN_MENU', currentUser: null, tempLoan: {} });
          return;
        }

        addTerminalLines([
          '',
          `SUCCESS: Welcome, ${user.fullName}! [Role: ${user.role}]`
        ]);

        if (user.role === 'ADMIN') {
          addTerminalLines([
            '',
            '--------------- ADMIN MENU ---------------',
            `Logged in as: ${user.fullName} [ADMIN]`,
            '1. View Loan Applications',
            '2. View Loan Details',
            '3. Approve Loan',
            '4. Reject Loan',
            '5. Request More Info',
            '6. Logout',
            'Enter choice (1-6):'
          ]);
          setConsoleState(prev => ({ ...prev, stage: 'ADMIN_MENU' }));
        } else {
          addTerminalLines([
            '',
            '---------------- USER MENU ----------------',
            `Logged in as: ${user.fullName} (User ID: ${user.userId})`,
            '1. Apply for Loan',
            '2. View My Loan Applications',
            '3. Logout',
            'Enter choice (1-3):'
          ]);
          setConsoleState(prev => ({ ...prev, stage: 'USER_MENU' }));
        }
        break;

      case 'USER_MENU':
        if (input === '1') {
          addTerminalLines([
            '',
            '=============================================================',
            '             APPLY FOR A NEW LOAN (STAGE 3)                  ',
            '=============================================================',
            'Enter Loan Amount (in INR, e.g. 500000 for Rs. 5 Lakhs):'
          ]);
          setConsoleState(prev => ({ ...prev, stage: 'APPLY_AMOUNT', tempLoan: {} }));
        } else if (input === '2') {
          const myLoans = loans.filter(l => l.userId === consoleState.currentUser?.userId);
          if (myLoans.length === 0) {
            addTerminalLines(['No loan applications found for your account.']);
          } else {
            addTerminalLines([
              '',
              `Showing ${myLoans.length} loan application(s) for ${consoleState.currentUser?.fullName}:`,
              '-'.repeat(78),
              String('ID').padEnd(6) + String('Amount').padEnd(18) + String('Type').padEnd(12) + String('Duration').padEnd(14) + String('Rate').padEnd(10) + 'Status',
              '-'.repeat(78),
              ...myLoans.map(l => 
                String(l.loanId).padEnd(6) + 
                formatRupees(l.loanAmount).padEnd(18) + 
                l.loanType.padEnd(12) + 
                l.loanDuration.padEnd(14) + 
                (l.interestRate + '%').padEnd(10) + 
                l.status
              ),
              '-'.repeat(78)
            ]);
          }
          addTerminalLines([
            '',
            '---------------- USER MENU ----------------',
            '1. Apply for Loan',
            '2. View My Loan Applications',
            '3. Logout',
            'Enter choice (1-3):'
          ]);
        } else if (input === '3') {
          addTerminalLines([
            'Logged out successfully.',
            '',
            '---------------- MAIN MENU ----------------',
            '1. Login',
            '2. Register New User',
            '3. Exit',
            'Please enter your choice (1-3):'
          ]);
          setConsoleState({ stage: 'MAIN_MENU', currentUser: null, tempLoan: {} });
        } else {
          addTerminalLines(['Invalid selection. Please enter 1, 2, or 3.']);
        }
        break;

      case 'APPLY_AMOUNT':
        const amount = parseFloat(input);
        if (isNaN(amount) || amount <= 0) {
          addTerminalLines(['Error: Loan amount must be greater than 0. Please re-enter:']);
        } else {
          addTerminalLines([
            `Amount entered: ${formatRupees(amount)}`,
            '',
            'Select Loan Type:',
            '1. Personal Loan (Interest: 10.5% p.a.)',
            '2. Home Loan (Interest: 7.5% p.a.)',
            '3. Education Loan (Interest: 8.0% p.a.)',
            '4. Car Loan (Interest: 8.5% p.a.)',
            'Choose option (1-4) or enter loan type name:'
          ]);
          setConsoleState(prev => ({
            ...prev,
            stage: 'APPLY_TYPE',
            tempLoan: { ...prev.tempLoan, loanAmount: amount }
          }));
        }
        break;

      case 'APPLY_TYPE':
        if (!input.trim()) {
          addTerminalLines(['Error: Loan type is required and cannot be empty. Please select 1, 2, 3, or 4:']);
          return;
        }

        let selectedType = '';
        let rate = 9.0;
        if (input === '1' || input.toLowerCase() === 'personal') {
          selectedType = 'Personal';
          rate = 10.5;
        } else if (input === '2' || input.toLowerCase() === 'home') {
          selectedType = 'Home';
          rate = 7.5;
        } else if (input === '3' || input.toLowerCase() === 'education') {
          selectedType = 'Education';
          rate = 8.0;
        } else if (input === '4' || input.toLowerCase() === 'car') {
          selectedType = 'Car';
          rate = 8.5;
        } else {
          selectedType = CharacterCapitalize(input.trim());
          rate = 9.0;
        }

        addTerminalLines([
          `Selected Loan Type: ${selectedType} | Determined Interest Rate: ${rate}% p.a.`,
          '',
          'Select Duration Unit:',
          '1. Months',
          '2. Years',
          'Choose unit (1-2):'
        ]);
        setConsoleState(prev => ({
          ...prev,
          stage: 'APPLY_DURATION_UNIT',
          tempLoan: { ...prev.tempLoan, loanType: selectedType, interestRate: rate }
        }));
        break;

      case 'APPLY_DURATION_UNIT':
        let unit = '';
        if (input === '1' || input.toLowerCase() === 'months' || input.toLowerCase() === 'month') {
          unit = 'Months';
        } else if (input === '2' || input.toLowerCase() === 'years' || input.toLowerCase() === 'year') {
          unit = 'Years';
        } else {
          addTerminalLines(['Error: Please select 1 for Months or 2 for Years:']);
          return;
        }

        addTerminalLines([
          `Duration unit: ${unit}`,
          `Enter Loan Duration in ${unit} (greater than 0):`
        ]);
        setConsoleState(prev => ({
          ...prev,
          stage: 'APPLY_DURATION_VAL',
          tempDurationUnit: unit
        }));
        break;

      case 'APPLY_DURATION_VAL':
        const durationNum = parseInt(input);
        if (isNaN(durationNum) || durationNum <= 0) {
          addTerminalLines(['Error: Duration must be greater than 0. Please re-enter:']);
        } else {
          const finalDurationStr = `${durationNum} ${consoleState.tempDurationUnit || 'Months'}`;
          addTerminalLines([
            `Duration entered: ${finalDurationStr}`,
            '',
            'Enter Annual Income (in INR, e.g. 1200000 for Rs. 12 Lakhs):'
          ]);
          setConsoleState(prev => ({
            ...prev,
            stage: 'APPLY_INCOME',
            tempLoan: { ...prev.tempLoan, loanDuration: finalDurationStr }
          }));
        }
        break;

      case 'APPLY_INCOME':
        const income = parseFloat(input);
        if (isNaN(income) || income <= 0) {
          addTerminalLines(['Error: Annual income must be greater than 0. Please re-enter:']);
        } else {
          addTerminalLines([
            `Annual Income: ${formatRupees(income)}`,
            '',
            'Enter Purpose of Loan (Required):'
          ]);
          setConsoleState(prev => ({
            ...prev,
            stage: 'APPLY_PURPOSE',
            tempLoan: { ...prev.tempLoan, annualIncome: income }
          }));
        }
        break;

      case 'APPLY_PURPOSE':
        if (!input.trim()) {
          addTerminalLines(['Error: Purpose is required and cannot be empty. Please enter:']);
          return;
        }

        const completeLoanPayload = {
          userId: consoleState.currentUser?.userId,
          loanAmount: consoleState.tempLoan.loanAmount,
          loanType: consoleState.tempLoan.loanType,
          loanDuration: consoleState.tempLoan.loanDuration,
          annualIncome: consoleState.tempLoan.annualIncome,
          purpose: input.trim()
        };

        try {
          const res = await fetch('/api/loans', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(completeLoanPayload)
          }).then(r => r.json());

          if (res.success) {
            addTerminalLines([
              '',
              '-------------------------------------------------------------',
              'SUCCESS: Your loan application has been submitted successfully!',
              `Application Reference ID: ${res.loan.loanId}`,
              `User ID: ${res.loan.userId} (${consoleState.currentUser?.fullName})`,
              `Amount: ${formatRupees(res.loan.loanAmount)}`,
              `Type: ${res.loan.loanType}`,
              `Duration: ${res.loan.loanDuration}`,
              `Interest Rate: ${res.loan.interestRate}% p.a.`,
              `Status: ${res.loan.status} (Saved in MySQL via JDBC)`,
              '-------------------------------------------------------------',
              '',
              '---------------- USER MENU ----------------',
              '1. Apply for Loan',
              '2. View My Loan Applications',
              '3. Logout',
              'Enter choice (1-3):'
            ]);
            fetchData();
          } else {
            addTerminalLines([`Error submitting loan: ${res.message}`]);
          }
        } catch (err) {
          addTerminalLines([`Network error: ${err}`]);
        }

        setConsoleState(prev => ({ ...prev, stage: 'USER_MENU', tempLoan: {} }));
        break;

      case 'ADMIN_MENU':
        if (input === '1') {
          addTerminalLines([
            '',
            '========================================================================================',
            '                                LOAN APPLICATIONS LIST                                  ',
            '========================================================================================',
            String('ID').padEnd(6) + 
            String('Applicant Name').padEnd(24) + 
            String('Amount (INR)').padEnd(18) + 
            String('Type').padEnd(12) + 
            String('Date').padEnd(12) + 
            'Status',
            '-'.repeat(88),
            ...loans.map(l => 
              String(l.loanId).padEnd(6) + 
              l.userName.padEnd(24) + 
              formatRupees(l.loanAmount).padEnd(18) + 
              l.loanType.padEnd(12) + 
              new Date(l.applicationDate).toLocaleDateString().padEnd(12) + 
              l.status
            ),
            '-'.repeat(88),
            '',
            '--------------- ADMIN MENU ---------------',
            '1. View Loan Applications',
            '2. View Loan Details',
            '3. Approve Loan',
            '4. Reject Loan',
            '5. Request More Info',
            '6. Logout',
            'Enter choice (1-6):'
          ]);
        } else if (input === '2') {
          addTerminalLines([
            '',
            '==========================================',
            '            VIEW LOAN DETAILS             ',
            '==========================================',
            'Enter Loan ID to view full details:'
          ]);
          setConsoleState(prev => ({ ...prev, stage: 'ADMIN_DETAILS_ID' }));
        } else if (input === '3') {
          addTerminalLines([
            '',
            '==========================================',
            '               APPROVE LOAN               ',
            '==========================================',
            'Enter Loan ID to approve:'
          ]);
          setConsoleState(prev => ({ ...prev, stage: 'ADMIN_APPROVE_ID' }));
        } else if (input === '4') {
          addTerminalLines([
            '',
            '==========================================',
            '               REJECT LOAN                ',
            '==========================================',
            'Enter Loan ID to reject:'
          ]);
          setConsoleState(prev => ({ ...prev, stage: 'ADMIN_REJECT_ID' }));
        } else if (input === '5') {
          addTerminalLines([
            '',
            '==========================================',
            '          REQUEST MORE INFORMATION        ',
            '==========================================',
            'Enter Loan ID to request more information:'
          ]);
          setConsoleState(prev => ({ ...prev, stage: 'ADMIN_MORE_INFO_ID' }));
        } else if (input === '6') {
          addTerminalLines([
            'Admin logged out successfully.',
            '',
            '---------------- MAIN MENU ----------------',
            '1. Login',
            '2. Register New User',
            '3. Exit',
            'Please enter your choice (1-3):'
          ]);
          setConsoleState({ stage: 'MAIN_MENU', currentUser: null, tempLoan: {} });
        } else {
          addTerminalLines(['Invalid selection. Please choose an option from 1 to 6.']);
        }
        break;

      case 'ADMIN_DETAILS_ID':
        const targetId = parseInt(input);
        const targetLoan = loans.find(l => l.loanId === targetId);
        if (!targetLoan) {
          addTerminalLines([`Error: Loan application with ID ${input} not found.`]);
        } else {
          addTerminalLines([
            '',
            '---------------- FULL LOAN APPLICATION DETAILS ----------------',
            `User's Name      : ${targetLoan.userName}`,
            `Email            : ${targetLoan.userEmail}`,
            `Mobile Number    : ${targetLoan.userMobile}`,
            `Loan Amount      : ${formatRupees(targetLoan.loanAmount)}`,
            `Loan Type        : ${targetLoan.loanType}`,
            `Loan Duration    : ${targetLoan.loanDuration}`,
            `Interest Rate    : ${targetLoan.interestRate}% p.a.`,
            `Annual Income    : ${formatRupees(targetLoan.annualIncome)}`,
            `Purpose          : ${targetLoan.purpose}`,
            `Application Date : ${new Date(targetLoan.applicationDate).toLocaleString()}`,
            `Status           : ${targetLoan.status}`,
            targetLoan.rejectionReason 
              ? (targetLoan.status === 'Needs More Info' ? `Info Requested   : ${targetLoan.rejectionReason}` : `Rejection Reason : ${targetLoan.rejectionReason}`)
              : '',
            '---------------------------------------------------------------'
          ].filter(Boolean));
        }
        addTerminalLines([
          '',
          '--------------- ADMIN MENU ---------------',
          '1. View Loan Applications',
          '2. View Loan Details',
          '3. Approve Loan',
          '4. Reject Loan',
          '5. Request More Info',
          '6. Logout',
          'Enter choice (1-6):'
        ]);
        setConsoleState(prev => ({ ...prev, stage: 'ADMIN_MENU' }));
        break;

      case 'ADMIN_APPROVE_ID':
        const approveId = parseInt(input);
        const toApprove = loans.find(l => l.loanId === approveId);
        if (!toApprove) {
          addTerminalLines([`Error: Loan application with ID ${input} not found.`]);
        } else {
          try {
            const res = await fetch(`/api/loans/${approveId}/approve`, { method: 'POST' }).then(r => r.json());
            if (res.success) {
              addTerminalLines([
                `Status updated to 'Approved' in database for Loan ID: ${approveId}`,
                `Dispatching approval email to: ${toApprove.userEmail}`,
                '-------------------------------------------------------------',
                `Subject : ${res.email.subject}`,
                `Body    : ${res.email.body}`,
                `Status  : SUCCESS (Email dispatched via SMTP)`,
                '-------------------------------------------------------------'
              ]);
              fetchData();
            } else {
              addTerminalLines([`Error: ${res.message}`]);
            }
          } catch (err) {
            addTerminalLines([`Network error: ${err}`]);
          }
        }
        addTerminalLines([
          '',
          '--------------- ADMIN MENU ---------------',
          '1. View Loan Applications',
          '2. View Loan Details',
          '3. Approve Loan',
          '4. Reject Loan',
          '5. Request More Info',
          '6. Logout',
          'Enter choice (1-6):'
        ]);
        setConsoleState(prev => ({ ...prev, stage: 'ADMIN_MENU' }));
        break;

      case 'ADMIN_REJECT_ID':
        const rejectId = parseInt(input);
        const toReject = loans.find(l => l.loanId === rejectId);
        if (!toReject) {
          addTerminalLines([`Error: Loan application with ID ${input} not found.`]);
          addTerminalLines([
            '',
            '--------------- ADMIN MENU ---------------',
            '1. View Loan Applications',
            '2. View Loan Details',
            '3. Approve Loan',
            '4. Reject Loan',
            '5. Request More Info',
            '6. Logout',
            'Enter choice (1-6):'
          ]);
          setConsoleState(prev => ({ ...prev, stage: 'ADMIN_MENU' }));
        } else {
          addTerminalLines([
            `Selected Loan ID ${rejectId} for ${toReject.userName} (${formatRupees(toReject.loanAmount)})`,
            'Enter reason for rejection (Required):'
          ]);
          setConsoleState(prev => ({ ...prev, stage: 'ADMIN_REJECT_REASON', targetRejectId: rejectId }));
        }
        break;

      case 'ADMIN_REJECT_REASON':
        if (!input.trim()) {
          addTerminalLines(['Error: Rejection reason is required. Please enter:']);
          return;
        }
        const rejLoanId = consoleState.targetRejectId;
        const targetLoanToReject = loans.find(l => l.loanId === rejLoanId);

        try {
          const res = await fetch(`/api/loans/${rejLoanId}/reject`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ reason: input.trim() })
          }).then(r => r.json());

          if (res.success) {
            addTerminalLines([
              `Status updated to 'Rejected' in database for Loan ID: ${rejLoanId}`,
              `Dispatching rejection email to: ${targetLoanToReject?.userEmail}`,
              '-------------------------------------------------------------',
              `Subject : ${res.email.subject}`,
              `Body    : ${res.email.body}`,
              `Status  : SUCCESS (Email dispatched via SMTP)`,
              '-------------------------------------------------------------'
            ]);
            fetchData();
          } else {
            addTerminalLines([`Error: ${res.message}`]);
          }
        } catch (err) {
          addTerminalLines([`Network error: ${err}`]);
        }

        addTerminalLines([
          '',
          '--------------- ADMIN MENU ---------------',
          '1. View Loan Applications',
          '2. View Loan Details',
          '3. Approve Loan',
          '4. Reject Loan',
          '5. Request More Info',
          '6. Logout',
          'Enter choice (1-6):'
        ]);
        setConsoleState(prev => ({ ...prev, stage: 'ADMIN_MENU', targetRejectId: undefined }));
        break;

      case 'ADMIN_MORE_INFO_ID':
        const infoId = parseInt(input);
        const toRequest = loans.find(l => l.loanId === infoId);
        if (!toRequest) {
          addTerminalLines([`Error: Loan application with ID ${input} not found.`]);
          addTerminalLines([
            '',
            '--------------- ADMIN MENU ---------------',
            '1. View Loan Applications',
            '2. View Loan Details',
            '3. Approve Loan',
            '4. Reject Loan',
            '5. Request More Info',
            '6. Logout',
            'Enter choice (1-6):'
          ]);
          setConsoleState(prev => ({ ...prev, stage: 'ADMIN_MENU' }));
        } else {
          addTerminalLines([
            `Selected Loan ID ${infoId} for ${toRequest.userName}`,
            'Enter information needed from applicant (Required):'
          ]);
          setConsoleState(prev => ({ ...prev, stage: 'ADMIN_MORE_INFO_TEXT', targetMoreInfoId: infoId }));
        }
        break;

      case 'ADMIN_MORE_INFO_TEXT':
        if (!input.trim()) {
          addTerminalLines(['Error: Information request note cannot be empty. Please enter:']);
          return;
        }
        const mInfoId = consoleState.targetMoreInfoId;

        try {
          const res = await fetch(`/api/loans/${mInfoId}/needs-more-info`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ reason: input.trim() })
          }).then(r => r.json());

          if (res.success) {
            addTerminalLines([
              `Status updated to 'Needs More Info' in database for Loan ID: ${mInfoId}`,
              `Information request recorded: ${input.trim()}`
            ]);
            fetchData();
          } else {
            addTerminalLines([`Error: ${res.message}`]);
          }
        } catch (err) {
          addTerminalLines([`Network error: ${err}`]);
        }

        addTerminalLines([
          '',
          '--------------- ADMIN MENU ---------------',
          '1. View Loan Applications',
          '2. View Loan Details',
          '3. Approve Loan',
          '4. Reject Loan',
          '5. Request More Info',
          '6. Logout',
          'Enter choice (1-6):'
        ]);
        setConsoleState(prev => ({ ...prev, stage: 'ADMIN_MENU', targetMoreInfoId: undefined }));
        break;
    }
  };

  const CharacterCapitalize = (str: string) => {
    return str.charAt(0).toUpperCase() + str.slice(1);
  };

  const handleCopyCode = (text: string) => {
    navigator.clipboard.writeText(text);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const currentFileContent = javaFiles.find(f => f.name === selectedJavaFile);

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col font-sans">
      {/* Header */}
      <header className="border-b border-slate-800 bg-slate-900/90 backdrop-blur sticky top-0 z-30">
        <div className="max-w-7xl mx-auto px-4 py-3 flex flex-wrap items-center justify-between gap-4">
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 rounded-lg bg-emerald-600 flex items-center justify-center font-bold text-white shadow-lg shadow-emerald-900/40">
              <IndianRupee className="w-5 h-5" />
            </div>
            <div>
              <div className="flex items-center space-x-2">
                <span className="font-bold text-lg text-white tracking-tight">Stage 3 Loan Management</span>
                <span className="px-2 py-0.5 text-xs font-semibold bg-emerald-500/10 text-emerald-400 border border-emerald-500/30 rounded">
                  Pure Java Console + JDBC
                </span>
              </div>
              <p className="text-xs text-slate-400">Indian Banking System • 5-File Architecture • Real SMTP Notifications</p>
            </div>
          </div>

          {/* Navigation Tabs */}
          <div className="flex items-center bg-slate-800/80 p-1 rounded-lg border border-slate-700 text-xs">
            <button
              onClick={() => setActiveTab('terminal')}
              className={`flex items-center space-x-1.5 px-3 py-1.5 rounded-md font-medium transition ${
                activeTab === 'terminal' 
                  ? 'bg-emerald-600 text-white shadow-sm' 
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <Terminal className="w-4 h-4" />
              <span>Interactive Console</span>
            </button>

            <button
              onClick={() => setActiveTab('manager')}
              className={`flex items-center space-x-1.5 px-3 py-1.5 rounded-md font-medium transition ${
                activeTab === 'manager' 
                  ? 'bg-emerald-600 text-white shadow-sm' 
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <Layers className="w-4 h-4" />
              <span>Loan Applications ({loans.length})</span>
            </button>

            <button
              onClick={() => setActiveTab('code')}
              className={`flex items-center space-x-1.5 px-3 py-1.5 rounded-md font-medium transition ${
                activeTab === 'code' 
                  ? 'bg-emerald-600 text-white shadow-sm' 
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <Code2 className="w-4 h-4" />
              <span>Java Source (5 Files)</span>
            </button>

            <button
              onClick={() => setActiveTab('emails')}
              className={`flex items-center space-x-1.5 px-3 py-1.5 rounded-md font-medium transition relative ${
                activeTab === 'emails' 
                  ? 'bg-emerald-600 text-white shadow-sm' 
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <Mail className="w-4 h-4" />
              <span>SMTP Logs</span>
              {emails.length > 0 && (
                <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse ml-1" />
              )}
            </button>
          </div>
        </div>
      </header>

      {/* Main Content Area */}
      <main className="flex-1 max-w-7xl w-full mx-auto p-4 flex flex-col">
        {activeTab === 'terminal' && (
          <div className="flex-1 flex flex-col bg-slate-900 border border-slate-800 rounded-xl overflow-hidden shadow-2xl">
            {/* Terminal Top bar */}
            <div className="bg-slate-950 px-4 py-2.5 border-b border-slate-800 flex items-center justify-between text-xs">
              <div className="flex items-center space-x-2">
                <span className="w-3 h-3 rounded-full bg-red-500/80 inline-block"></span>
                <span className="w-3 h-3 rounded-full bg-amber-500/80 inline-block"></span>
                <span className="w-3 h-3 rounded-full bg-emerald-500/80 inline-block"></span>
                <span className="text-slate-400 ml-2 font-mono">java -cp target/LoanApplicationSystem-1.0-SNAPSHOT.jar Main</span>
              </div>
              <div className="flex items-center space-x-3 text-slate-400">
                <span>Scanner: Active</span>
                <span>•</span>
                <span>JDBC: Connected</span>
                <button
                  onClick={() => {
                    setTerminalHistory([
                      '=============================================================',
                      '       WELCOME TO LOAN APPLICATION SYSTEM (STAGE 3)          ',
                      '       Indian Banking System Edition (Rs. / INR)             ',
                      '=============================================================',
                      '',
                      '---------------- MAIN MENU ----------------',
                      '1. Login',
                      '2. Register New User',
                      '3. Exit',
                      'Please enter your choice (1-3):'
                    ]);
                    setConsoleState({ stage: 'MAIN_MENU', currentUser: null, tempLoan: {} });
                  }}
                  className="hover:text-emerald-400 transition"
                  title="Reset Terminal"
                >
                  <RefreshCw className="w-3.5 h-3.5" />
                </button>
              </div>
            </div>

            {/* Quick Action Buttons for Console shortcuts */}
            <div className="bg-slate-800/60 border-b border-slate-700/60 px-4 py-2 flex flex-wrap items-center gap-2 text-xs">
              <span className="text-slate-400 font-medium">Quick Shortcuts:</span>
              <button
                onClick={() => {
                  setTerminalInput('1');
                }}
                className="bg-slate-700 hover:bg-slate-600 text-slate-200 px-2.5 py-1 rounded transition"
              >
                1 (Login)
              </button>
              <button
                onClick={() => {
                  setTerminalInput('rahul.sharma@example.com');
                }}
                className="bg-emerald-950/70 hover:bg-emerald-900 border border-emerald-600/40 text-emerald-300 px-2.5 py-1 rounded transition"
              >
                Demo User: Rahul (Home Loan)
              </button>
              <button
                onClick={() => {
                  setTerminalInput('admin@loanapp.com');
                }}
                className="bg-purple-950/70 hover:bg-purple-900 border border-purple-600/40 text-purple-300 px-2.5 py-1 rounded transition"
              >
                Demo Admin: Admin
              </button>
              <button
                onClick={() => {
                  setTerminalInput('user123');
                }}
                className="bg-slate-700 hover:bg-slate-600 text-slate-300 px-2.5 py-1 rounded transition"
              >
                Password: user123
              </button>
              <button
                onClick={() => {
                  setTerminalInput('admin123');
                }}
                className="bg-slate-700 hover:bg-slate-600 text-slate-300 px-2.5 py-1 rounded transition"
              >
                Password: admin123
              </button>
            </div>

            {/* Console Output Screen */}
            <div className="flex-1 p-4 font-mono text-sm overflow-y-auto max-h-[62vh] space-y-1 select-text bg-black/40">
              {terminalHistory.map((line, idx) => (
                <div 
                  key={idx} 
                  className={`${
                    line.startsWith('>') 
                      ? 'text-emerald-400 font-semibold' 
                      : line.includes('SUCCESS') || line.includes('Approved')
                      ? 'text-emerald-300'
                      : line.includes('Error') || line.includes('Rejected')
                      ? 'text-rose-400'
                      : line.includes('Needs More Info')
                      ? 'text-sky-300'
                      : line.startsWith('=') || line.startsWith('-')
                      ? 'text-slate-500'
                      : 'text-slate-200'
                  }`}
                >
                  {line || '\u00A0'}
                </div>
              ))}
              <div ref={terminalEndRef} />
            </div>

            {/* Terminal Input Form */}
            <form onSubmit={handleTerminalSubmit} className="border-t border-slate-800 bg-slate-950 p-3 flex items-center gap-2">
              <span className="text-emerald-500 font-mono text-sm font-bold pl-2">$</span>
              <input
                type="text"
                value={terminalInput}
                onChange={e => setTerminalInput(e.target.value)}
                placeholder="Enter console command / Scanner input here (e.g. 1, 2, amount, reason)..."
                className="flex-1 bg-transparent border-0 text-slate-100 font-mono text-sm focus:ring-0 focus:outline-none placeholder:text-slate-600"
                autoFocus
              />
              <button
                type="submit"
                className="bg-emerald-600 hover:bg-emerald-500 text-white px-4 py-1.5 rounded-lg text-xs font-medium flex items-center space-x-1 transition"
              >
                <span>Enter</span>
                <Send className="w-3.5 h-3.5" />
              </button>
            </form>
          </div>
        )}

        {activeTab === 'manager' && (
          <div className="flex-1 flex flex-col space-y-4">
            <div className="flex items-center justify-between">
              <div>
                <h2 className="text-lg font-bold text-white">Loan Applications Management (MySQL JDBC)</h2>
                <p className="text-xs text-slate-400">Linked to users via foreign key `userId`. Demonstrates Stage 3 Admin review, approval, rejection, and Needs More Info.</p>
              </div>
              <button
                onClick={fetchData}
                className="flex items-center space-x-1.5 text-xs bg-slate-800 hover:bg-slate-700 px-3 py-1.5 rounded-lg border border-slate-700 text-slate-300 transition"
              >
                <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin' : ''}`} />
                <span>Refresh Table</span>
              </button>
            </div>

            {/* Loan Table */}
            <div className="bg-slate-900 border border-slate-800 rounded-xl overflow-hidden shadow-xl">
              <div className="overflow-x-auto">
                <table className="w-full text-left text-xs">
                  <thead className="bg-slate-800/60 text-slate-400 uppercase tracking-wider border-b border-slate-800">
                    <tr>
                      <th className="py-3 px-4">Loan ID</th>
                      <th className="py-3 px-4">Applicant</th>
                      <th className="py-3 px-4">Loan Amount</th>
                      <th className="py-3 px-4">Type</th>
                      <th className="py-3 px-4">Duration</th>
                      <th className="py-3 px-4">Interest Rate</th>
                      <th className="py-3 px-4">Date</th>
                      <th className="py-3 px-4">Status</th>
                      <th className="py-3 px-4 text-right">Actions</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-800">
                    {loans.map(loan => (
                      <tr key={loan.loanId} className="hover:bg-slate-800/40 transition">
                        <td className="py-3 px-4 font-mono font-semibold text-slate-300">#{loan.loanId}</td>
                        <td className="py-3 px-4">
                          <div className="font-medium text-slate-100">{loan.userName}</div>
                          <div className="text-slate-400 text-[11px]">{loan.userEmail}</div>
                        </td>
                        <td className="py-3 px-4 font-semibold text-emerald-400">
                          {formatRupees(loan.loanAmount)}
                        </td>
                        <td className="py-3 px-4">
                          <span className="px-2 py-0.5 bg-slate-800 border border-slate-700 rounded text-slate-300">
                            {loan.loanType}
                          </span>
                        </td>
                        <td className="py-3 px-4 text-slate-300">{loan.loanDuration}</td>
                        <td className="py-3 px-4 font-mono text-slate-300">{loan.interestRate}% p.a.</td>
                        <td className="py-3 px-4 text-slate-400">
                          {new Date(loan.applicationDate).toLocaleDateString()}
                        </td>
                        <td className="py-3 px-4">
                          <span className={`px-2 py-0.5 rounded text-[11px] font-semibold ${
                            loan.status === 'Approved'
                              ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/30'
                              : loan.status === 'Rejected'
                              ? 'bg-rose-500/10 text-rose-400 border border-rose-500/30'
                              : loan.status === 'Needs More Info'
                              ? 'bg-sky-500/10 text-sky-400 border border-sky-500/30'
                              : 'bg-amber-500/10 text-amber-400 border border-amber-500/30'
                          }`}>
                            {loan.status}
                          </span>
                          {loan.rejectionReason && (
                            <div className="text-[10px] text-slate-400 mt-1 max-w-xs truncate" title={loan.rejectionReason}>
                              {loan.status === 'Needs More Info' ? 'Info: ' : 'Reason: '}{loan.rejectionReason}
                            </div>
                          )}
                        </td>
                        <td className="py-3 px-4 text-right">
                          <div className="flex items-center justify-end space-x-1.5">
                            <button
                              onClick={() => setSelectedLoanDetails(loan)}
                              className="p-1.5 hover:bg-slate-800 rounded text-slate-400 hover:text-slate-200 transition"
                              title="View Full Details (Requirement 19)"
                            >
                              <Eye className="w-4 h-4" />
                            </button>

                            {loan.status !== 'Approved' && (
                              <button
                                onClick={async () => {
                                  await fetch(`/api/loans/${loan.loanId}/approve`, { method: 'POST' });
                                  fetchData();
                                }}
                                className="p-1.5 bg-emerald-950/60 hover:bg-emerald-900/80 border border-emerald-600/40 text-emerald-300 rounded transition"
                                title="Approve Loan & Send Live Email (Requirement 20 & 21)"
                              >
                                <CheckCircle2 className="w-4 h-4" />
                              </button>
                            )}

                            {loan.status !== 'Rejected' && (
                              <button
                                onClick={() => setRejectModalLoan(loan)}
                                className="p-1.5 bg-rose-950/60 hover:bg-rose-900/80 border border-rose-600/40 text-rose-300 rounded transition"
                                title="Reject Loan with Reason & Send Email (Requirement 22 & 23)"
                              >
                                <XCircle className="w-4 h-4" />
                              </button>
                            )}

                            {loan.status !== 'Needs More Info' && loan.status !== 'Approved' && (
                              <button
                                onClick={() => setMoreInfoModalLoan(loan)}
                                className="p-1.5 bg-sky-950/60 hover:bg-sky-900/80 border border-sky-600/40 text-sky-300 rounded transition"
                                title="Request More Information (Requirement 6)"
                              >
                                <HelpCircle className="w-4 h-4" />
                              </button>
                            )}
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>

            {/* Quick Loan Submission Form (User Simulation) */}
            <div className="bg-slate-900 border border-slate-800 rounded-xl p-4">
              <h3 className="text-sm font-bold text-white mb-2 flex items-center space-x-2">
                <FileText className="w-4 h-4 text-emerald-400" />
                <span>Simulate Stage 3 User Loan Submission (Form Validation Check)</span>
              </h3>
              <p className="text-xs text-slate-400 mb-4">
                Validates: Amount &gt; 0, Duration &gt; 0 (Months/Years), Income &gt; 0, Purpose not empty, auto Status='Pending', and links to logged-in user.
              </p>

              <form 
                onSubmit={async (e) => {
                  e.preventDefault();
                  const form = e.target as HTMLFormElement;
                  const data = new FormData(form);
                  const durationVal = Number(data.get('durationVal'));
                  const durationUnit = String(data.get('durationUnit'));
                  const payload = {
                    userId: Number(data.get('userId')),
                    loanAmount: Number(data.get('loanAmount')),
                    loanType: String(data.get('loanType')),
                    loanDuration: `${durationVal} ${durationUnit}`,
                    annualIncome: Number(data.get('annualIncome')),
                    purpose: String(data.get('purpose'))
                  };
                  const res = await fetch('/api/loans', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                  }).then(r => r.json());
                  if (res.success) {
                    alert(`Loan application #${res.loan.loanId} submitted successfully with status: Pending!`);
                    form.reset();
                    fetchData();
                  } else {
                    alert(`Error: ${res.message}`);
                  }
                }}
                className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-6 gap-3 text-xs"
              >
                <div>
                  <label className="text-slate-400 block mb-1">User (Linked ID)</label>
                  <select name="userId" className="w-full bg-slate-800 border border-slate-700 rounded px-2.5 py-1.5 text-slate-100">
                    {users.filter(u => u.role === 'USER').map(u => (
                      <option key={u.userId} value={u.userId}>
                        {u.fullName} (ID: {u.userId})
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="text-slate-400 block mb-1">Loan Amount (INR)</label>
                  <input
                    type="number"
                    name="loanAmount"
                    defaultValue={350000}
                    min={1}
                    required
                    className="w-full bg-slate-800 border border-slate-700 rounded px-2.5 py-1.5 text-slate-100"
                  />
                </div>

                <div>
                  <label className="text-slate-400 block mb-1">Loan Type</label>
                  <select name="loanType" className="w-full bg-slate-800 border border-slate-700 rounded px-2.5 py-1.5 text-slate-100">
                    <option value="Personal">Personal (10.5%)</option>
                    <option value="Home">Home (7.5%)</option>
                    <option value="Education">Education (8.0%)</option>
                    <option value="Car">Car (8.5%)</option>
                  </select>
                </div>

                <div>
                  <label className="text-slate-400 block mb-1">Duration & Unit</label>
                  <div className="flex space-x-1">
                    <input
                      type="number"
                      name="durationVal"
                      defaultValue={36}
                      min={1}
                      required
                      className="w-16 bg-slate-800 border border-slate-700 rounded px-2 py-1.5 text-slate-100"
                    />
                    <select name="durationUnit" className="flex-1 bg-slate-800 border border-slate-700 rounded px-1.5 py-1.5 text-slate-100">
                      <option value="Months">Months</option>
                      <option value="Years">Years</option>
                    </select>
                  </div>
                </div>

                <div>
                  <label className="text-slate-400 block mb-1">Annual Income (INR)</label>
                  <input
                    type="number"
                    name="annualIncome"
                    defaultValue={900000}
                    min={1}
                    required
                    className="w-full bg-slate-800 border border-slate-700 rounded px-2.5 py-1.5 text-slate-100"
                  />
                </div>

                <div>
                  <label className="text-slate-400 block mb-1">Purpose of Loan</label>
                  <input
                    type="text"
                    name="purpose"
                    defaultValue="Purchase of electric vehicle"
                    required
                    className="w-full bg-slate-800 border border-slate-700 rounded px-2.5 py-1.5 text-slate-100"
                  />
                </div>

                <div className="md:col-span-3 lg:col-span-6 flex justify-end">
                  <button
                    type="submit"
                    className="bg-emerald-600 hover:bg-emerald-500 text-white font-medium px-4 py-2 rounded-lg text-xs transition"
                  >
                    Submit Loan Application
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

        {activeTab === 'code' && (
          <div className="flex-1 flex flex-col space-y-3">
            <div className="flex flex-wrap items-center justify-between gap-3">
              <div>
                <h2 className="text-base font-bold text-white flex items-center space-x-2">
                  <span>Source Code Inspection</span>
                  <span className="text-xs font-normal text-slate-400">• Directory: LoanApplicationSystem/</span>
                </h2>
                <p className="text-xs text-slate-400">
                  Strictly 5 Java files + database.sql + pom.xml as prescribed in Stage 3 requirement.
                </p>
              </div>

              <button
                onClick={() => currentFileContent && handleCopyCode(currentFileContent.content)}
                className="flex items-center space-x-1.5 bg-slate-800 hover:bg-slate-700 text-slate-200 px-3 py-1.5 rounded-lg border border-slate-700 text-xs transition"
              >
                {copied ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
                <span>{copied ? 'Copied!' : 'Copy Current File'}</span>
              </button>
            </div>

            {/* File Switcher Tabs */}
            <div className="flex flex-wrap gap-1.5 bg-slate-900 p-1.5 border border-slate-800 rounded-lg">
              {javaFiles.map(file => (
                <button
                  key={file.name}
                  onClick={() => setSelectedJavaFile(file.name)}
                  className={`px-3 py-1.5 rounded-md text-xs font-mono font-medium transition flex items-center space-x-2 ${
                    selectedJavaFile === file.name
                      ? 'bg-emerald-600 text-white shadow-sm'
                      : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800'
                  }`}
                >
                  <FileText className="w-3.5 h-3.5" />
                  <span>{file.name}</span>
                </button>
              ))}
            </div>

            {/* File Role Description */}
            {currentFileContent && (
              <div className="bg-slate-900/60 border border-slate-800 px-3 py-2 rounded-lg text-xs text-slate-300 flex items-center space-x-2">
                <span className="font-semibold text-emerald-400">{currentFileContent.name}:</span>
                <span>{currentFileContent.role}</span>
              </div>
            )}

            {/* Code Viewer */}
            <div className="flex-1 bg-slate-950 border border-slate-800 rounded-xl p-4 overflow-auto max-h-[64vh]">
              <pre className="font-mono text-xs text-slate-300 leading-relaxed whitespace-pre select-text">
                {currentFileContent?.content || '// Loading source...'}
              </pre>
            </div>
          </div>
        )}

        {activeTab === 'emails' && (
          <div className="flex-1 flex flex-col space-y-4">
            <div>
              <h2 className="text-base font-bold text-white flex items-center space-x-2">
                <Mail className="w-4 h-4 text-emerald-400" />
                <span>SMTP Email Notification Logs (Stage 3 Requirement 20-23)</span>
              </h2>
              <p className="text-xs text-slate-400">
                Sends live emails via JavaMail/SMTP. Dispatched immediately upon Admin approval or rejection.
              </p>
            </div>

            <div className="grid grid-cols-1 gap-3">
              {emails.length === 0 ? (
                <div className="bg-slate-900 border border-slate-800 rounded-xl p-8 text-center text-slate-400 text-sm">
                  No email notifications dispatched yet. Try approving or rejecting a loan in the Interactive Console or Loan Applications tab!
                </div>
              ) : (
                emails.map(email => (
                  <div key={email.id} className="bg-slate-900 border border-slate-800 rounded-xl p-4 shadow-lg">
                    <div className="flex flex-wrap items-center justify-between gap-2 border-b border-slate-800 pb-2 mb-3">
                      <div className="flex items-center space-x-2">
                        <span className="px-2 py-0.5 rounded text-[11px] font-semibold bg-emerald-500/10 text-emerald-400 border border-emerald-500/30">
                          {email.status}
                        </span>
                        <span className="text-xs font-semibold text-slate-200">To: {email.to}</span>
                      </div>
                      <span className="text-[11px] text-slate-500">{new Date(email.timestamp).toLocaleString()}</span>
                    </div>

                    <div className="space-y-2 text-xs">
                      <div>
                        <span className="text-slate-400 font-medium">Subject: </span>
                        <span className="text-slate-100 font-semibold">{email.subject}</span>
                      </div>
                      <div className="bg-slate-950 p-3 rounded border border-slate-800 text-slate-300 font-mono text-[11px]">
                        {email.body}
                      </div>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>
        )}
      </main>

      {/* Modal: View Loan Details (Requirement 19) */}
      {selectedLoanDetails && (
        <div className="fixed inset-0 bg-black/75 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-xl max-w-lg w-full p-5 space-y-4 shadow-2xl">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <h3 className="font-bold text-white text-sm">Loan Application #{selectedLoanDetails.loanId} Details</h3>
              <button 
                onClick={() => setSelectedLoanDetails(null)} 
                className="text-slate-400 hover:text-slate-200"
              >
                ✕
              </button>
            </div>

            <div className="grid grid-cols-2 gap-3 text-xs">
              <div>
                <span className="text-slate-400 block">User Full Name</span>
                <span className="font-semibold text-slate-200">{selectedLoanDetails.userName}</span>
              </div>
              <div>
                <span className="text-slate-400 block">Email Address</span>
                <span className="font-semibold text-slate-200">{selectedLoanDetails.userEmail}</span>
              </div>
              <div>
                <span className="text-slate-400 block">Mobile Number</span>
                <span className="font-semibold text-slate-200">{selectedLoanDetails.userMobile}</span>
              </div>
              <div>
                <span className="text-slate-400 block">Loan Amount</span>
                <span className="font-semibold text-emerald-400">{formatRupees(selectedLoanDetails.loanAmount)}</span>
              </div>
              <div>
                <span className="text-slate-400 block">Loan Type</span>
                <span className="font-semibold text-slate-200">{selectedLoanDetails.loanType}</span>
              </div>
              <div>
                <span className="text-slate-400 block">Loan Duration</span>
                <span className="font-semibold text-slate-200">{selectedLoanDetails.loanDuration}</span>
              </div>
              <div>
                <span className="text-slate-400 block">Interest Rate</span>
                <span className="font-semibold text-slate-200">{selectedLoanDetails.interestRate}% p.a.</span>
              </div>
              <div>
                <span className="text-slate-400 block">Annual Income</span>
                <span className="font-semibold text-slate-200">{formatRupees(selectedLoanDetails.annualIncome)}</span>
              </div>
              <div className="col-span-2">
                <span className="text-slate-400 block">Purpose</span>
                <span className="font-medium text-slate-200">{selectedLoanDetails.purpose}</span>
              </div>
              <div>
                <span className="text-slate-400 block">Application Date</span>
                <span className="font-medium text-slate-200">{new Date(selectedLoanDetails.applicationDate).toLocaleString()}</span>
              </div>
              <div>
                <span className="text-slate-400 block">Status</span>
                <span className={`font-semibold ${
                  selectedLoanDetails.status === 'Approved' ? 'text-emerald-400' :
                  selectedLoanDetails.status === 'Rejected' ? 'text-rose-400' :
                  selectedLoanDetails.status === 'Needs More Info' ? 'text-sky-400' : 'text-amber-400'
                }`}>{selectedLoanDetails.status}</span>
              </div>
              {selectedLoanDetails.rejectionReason && (
                <div className={`col-span-2 p-2.5 rounded border ${
                  selectedLoanDetails.status === 'Needs More Info'
                    ? 'bg-sky-950/40 border-sky-800/50 text-sky-300'
                    : 'bg-rose-950/40 border-rose-800/50 text-rose-300'
                }`}>
                  <span className="font-semibold block">
                    {selectedLoanDetails.status === 'Needs More Info' ? 'Information Request:' : 'Rejection Reason:'}
                  </span>
                  <span>{selectedLoanDetails.rejectionReason}</span>
                </div>
              )}
            </div>

            <div className="flex justify-end pt-2 border-t border-slate-800">
              <button
                onClick={() => setSelectedLoanDetails(null)}
                className="bg-slate-800 hover:bg-slate-700 text-slate-200 px-4 py-1.5 rounded-lg text-xs"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal: Rejection Reason (Requirement 22) */}
      {rejectModalLoan && (
        <div className="fixed inset-0 bg-black/75 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-xl max-w-md w-full p-5 space-y-4 shadow-2xl">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <h3 className="font-bold text-white text-sm">Reject Loan #{rejectModalLoan.loanId}</h3>
              <button 
                onClick={() => {
                  setRejectModalLoan(null);
                  setRejectionReasonInput('');
                }} 
                className="text-slate-400 hover:text-slate-200"
              >
                ✕
              </button>
            </div>

            <p className="text-xs text-slate-300">
              Enter rejection reason for <strong className="text-white">{rejectModalLoan.userName}</strong>. An email notification will be immediately sent to <code className="text-emerald-400">{rejectModalLoan.userEmail}</code>.
            </p>

            <textarea
              rows={3}
              value={rejectionReasonInput}
              onChange={e => setRejectionReasonInput(e.target.value)}
              placeholder="e.g. Insufficient documented income or unable to verify primary residence."
              className="w-full bg-slate-950 border border-slate-700 rounded-lg p-3 text-xs text-slate-100 focus:outline-none focus:border-rose-500"
            />

            <div className="flex justify-end space-x-2 pt-2 border-t border-slate-800">
              <button
                onClick={() => {
                  setRejectModalLoan(null);
                  setRejectionReasonInput('');
                }}
                className="bg-slate-800 hover:bg-slate-700 text-slate-300 px-3 py-1.5 rounded-lg text-xs"
              >
                Cancel
              </button>
              <button
                onClick={async () => {
                  if (!rejectionReasonInput.trim()) {
                    alert('Please provide a reason for rejection.');
                    return;
                  }
                  await fetch(`/api/loans/${rejectModalLoan.loanId}/reject`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ reason: rejectionReasonInput.trim() })
                  });
                  setRejectModalLoan(null);
                  setRejectionReasonInput('');
                  fetchData();
                }}
                className="bg-rose-600 hover:bg-rose-500 text-white font-medium px-4 py-1.5 rounded-lg text-xs transition"
              >
                Confirm Rejection & Send Email
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal: Request More Info (Requirement 6) */}
      {moreInfoModalLoan && (
        <div className="fixed inset-0 bg-black/75 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-xl max-w-md w-full p-5 space-y-4 shadow-2xl">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <h3 className="font-bold text-white text-sm">Request More Info for Loan #{moreInfoModalLoan.loanId}</h3>
              <button 
                onClick={() => {
                  setMoreInfoModalLoan(null);
                  setMoreInfoInput('');
                }} 
                className="text-slate-400 hover:text-slate-200"
              >
                ✕
              </button>
            </div>

            <p className="text-xs text-slate-300">
              Enter information requested from applicant <strong className="text-white">{moreInfoModalLoan.userName}</strong>. Status will be updated to <code className="text-sky-400">Needs More Info</code>.
            </p>

            <textarea
              rows={3}
              value={moreInfoInput}
              onChange={e => setMoreInfoInput(e.target.value)}
              placeholder="e.g. Please clarify annual bonus component or provide recent utility bill."
              className="w-full bg-slate-950 border border-slate-700 rounded-lg p-3 text-xs text-slate-100 focus:outline-none focus:border-sky-500"
            />

            <div className="flex justify-end space-x-2 pt-2 border-t border-slate-800">
              <button
                onClick={() => {
                  setMoreInfoModalLoan(null);
                  setMoreInfoInput('');
                }}
                className="bg-slate-800 hover:bg-slate-700 text-slate-300 px-3 py-1.5 rounded-lg text-xs"
              >
                Cancel
              </button>
              <button
                onClick={async () => {
                  if (!moreInfoInput.trim()) {
                    alert('Please specify the information requested.');
                    return;
                  }
                  await fetch(`/api/loans/${moreInfoModalLoan.loanId}/needs-more-info`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ reason: moreInfoInput.trim() })
                  });
                  setMoreInfoModalLoan(null);
                  setMoreInfoInput('');
                  fetchData();
                }}
                className="bg-sky-600 hover:bg-sky-500 text-white font-medium px-4 py-1.5 rounded-lg text-xs transition"
              >
                Save Information Request
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default App;
