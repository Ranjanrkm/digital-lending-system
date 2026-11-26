package com.sprint.lending.service;

import com.sprint.lending.dto.BorrowerProfileResponse;
import com.sprint.lending.dto.LoanApplyRequest;
import com.sprint.lending.dto.LoanResponse;
import com.sprint.lending.entity.BorrowerProfile;
import com.sprint.lending.entity.Loan;
import com.sprint.lending.entity.LoanStatus;
import com.sprint.lending.exception.InvalidLoanStateException;
import com.sprint.lending.exception.LoanNotFoundException;
import com.sprint.lending.repository.BorrowerProfileRepository;
import com.sprint.lending.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LoanService {

    private static final double DEFAULT_ANNUAL_INTEREST_RATE = 12.0;

    private final LoanRepository loanRepository;
    private final BorrowerProfileRepository profileRepository;
    private final EmiCalculator emiCalculator;
    private final RepaymentService repaymentService;

    public LoanService(LoanRepository loanRepository,
                       BorrowerProfileRepository profileRepository,
                       EmiCalculator emiCalculator,
                       RepaymentService repaymentService) {
        this.loanRepository = loanRepository;
        this.profileRepository = profileRepository;
        this.emiCalculator = emiCalculator;
        this.repaymentService = repaymentService;
    }

    public BorrowerProfileResponse getMyProfile(String borrowerEmail) {
        BorrowerProfile borrower = profileRepository.findByUserEmail(borrowerEmail)
                .orElseThrow(() -> new LoanNotFoundException("Borrower profile not found"));
        return BorrowerProfileResponse.fromEntity(borrower);
    }

    @Transactional
    public LoanResponse applyForLoan(LoanApplyRequest request, String borrowerEmail) {
        BorrowerProfile borrower = profileRepository.findByUserEmail(borrowerEmail)
                .orElseThrow(() -> new LoanNotFoundException("Borrower profile not found"));

        boolean hasActiveLoan = loanRepository.existsByBorrowerIdAndStatusIn(
                borrower.getId(), List.of(LoanStatus.APPLIED, LoanStatus.APPROVED)
        );
        if (hasActiveLoan) {
            throw new InvalidLoanStateException(
                    "You already have an active or pending loan application. Close it before applying again.");
        }

        BigDecimal emi = emiCalculator.calculateEmi(
                request.getAmount(), DEFAULT_ANNUAL_INTEREST_RATE, request.getTenureMonths());

        BigDecimal maxAffordableEmi = borrower.getMonthlyIncome().multiply(BigDecimal.valueOf(0.50));
        if (emi.compareTo(maxAffordableEmi) > 0) {
            throw new InvalidLoanStateException(
                    "Monthly income is too low for this loan. Calculated EMI (₹" + emi +
                            ") exceeds 50% of your monthly income (₹" + maxAffordableEmi + ").");
        }

        Loan loan = new Loan();
        loan.setAmount(request.getAmount());
        loan.setInterestRate(DEFAULT_ANNUAL_INTEREST_RATE);
        loan.setTenureMonths(request.getTenureMonths());
        loan.setEmi(emi);
        loan.setStatus(LoanStatus.APPLIED);
        loan.setBorrower(borrower);

        Loan saved = loanRepository.save(loan);
        return LoanResponse.fromEntity(saved);
    }

    public List<LoanResponse> getMyLoans(String borrowerEmail) {
        BorrowerProfile borrower = profileRepository.findByUserEmail(borrowerEmail)
                .orElseThrow(() -> new LoanNotFoundException("Borrower profile not found"));

        return loanRepository.findByBorrowerIdOrderByAppliedAtDesc(borrower.getId())
                .stream()
                .map(LoanResponse::fromEntity)
                .toList();
    }

    public List<LoanResponse> getAllLoansForAdmin(String status) {
        List<Loan> allLoans = loanRepository.findAll();

        return allLoans.stream()
                .filter(loan -> status == null || status.isBlank()
                        || loan.getStatus().name().equalsIgnoreCase(status.trim()))
                .sorted((a, b) -> b.getAppliedAt().compareTo(a.getAppliedAt()))
                .map(LoanResponse::fromEntity)
                .toList();
    }

    @Transactional
    public LoanResponse approveLoan(Long loanId, String remark) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + loanId));

        if (loan.getStatus() != LoanStatus.APPLIED) {
            throw new InvalidLoanStateException("Only APPLIED loans can be approved. Current status: " + loan.getStatus());
        }

        loan.setStatus(LoanStatus.APPROVED);
        loan.setAdminRemark((remark == null || remark.isBlank()) ? "Approved by Credit Officer" : remark.trim());
        Loan saved = loanRepository.save(loan);

        repaymentService.generateSchedule(saved);
        return LoanResponse.fromEntity(saved);
    }

    @Transactional
    public LoanResponse rejectLoan(Long loanId, String remark) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + loanId));

        if (loan.getStatus() != LoanStatus.APPLIED) {
            throw new InvalidLoanStateException("Only APPLIED loans can be rejected. Current status: " + loan.getStatus());
        }

        loan.setStatus(LoanStatus.REJECTED);
        loan.setAdminRemark((remark == null || remark.isBlank()) ? "Rejected after underwriting review" : remark.trim());
        return LoanResponse.fromEntity(loanRepository.save(loan));
    }
}