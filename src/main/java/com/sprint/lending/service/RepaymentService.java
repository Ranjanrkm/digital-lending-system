package com.sprint.lending.service;

import com.sprint.lending.dto.RepaymentResponse;
import com.sprint.lending.entity.Loan;
import com.sprint.lending.entity.LoanStatus;
import com.sprint.lending.entity.Repayment;
import com.sprint.lending.entity.RepaymentStatus;
import com.sprint.lending.exception.InvalidLoanStateException;
import com.sprint.lending.exception.LoanNotFoundException;
import com.sprint.lending.repository.LoanRepository;
import com.sprint.lending.repository.RepaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RepaymentService {

    private final RepaymentRepository repaymentRepository;
    private final LoanRepository loanRepository;

    public RepaymentService(RepaymentRepository repaymentRepository, LoanRepository loanRepository) {
        this.repaymentRepository = repaymentRepository;
        this.loanRepository = loanRepository;
    }

    @Transactional
    public void generateSchedule(Loan loan) {
        List<Repayment> schedule = new ArrayList<>();
        LocalDate startDate = LocalDate.now();

        for (int i = 1; i <= loan.getTenureMonths(); i++) {
            Repayment r = new Repayment();
            r.setInstallmentNo(i);
            r.setDueDate(startDate.plusMonths(i));
            r.setAmount(loan.getEmi());
            r.setStatus(RepaymentStatus.PENDING);
            r.setLoan(loan);
            schedule.add(r);
        }
        repaymentRepository.saveAll(schedule);
    }

    public List<RepaymentResponse> getRepaymentsForLoan(Long loanId, String borrowerEmail) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + loanId));

        if (!loan.getBorrower().getUser().getEmail().equals(borrowerEmail)) {
            throw new InvalidLoanStateException("You can only view repayments for your own loans");
        }

        return repaymentRepository.findByLoanIdOrderByInstallmentNoAsc(loanId)
                .stream()
                .map(RepaymentResponse::fromEntity)
                .toList();
    }

    @Transactional
    public RepaymentResponse payInstallment(Long repaymentId, String borrowerEmail) {
        Repayment repayment = repaymentRepository.findById(repaymentId)
                .orElseThrow(() -> new LoanNotFoundException("Repayment not found with ID: " + repaymentId));

        Loan loan = repayment.getLoan();
        if (!loan.getBorrower().getUser().getEmail().equals(borrowerEmail)) {
            throw new InvalidLoanStateException("You cannot pay another borrower's installment");
        }
        if (repayment.getStatus() == RepaymentStatus.PAID) {
            throw new InvalidLoanStateException("Installment #" + repayment.getInstallmentNo() + " is already paid");
        }

        repayment.setStatus(RepaymentStatus.PAID);
        repayment.setPaidAt(LocalDateTime.now());
        repaymentRepository.save(repayment);

        // Close the loan automatically if all installments are now PAID
        boolean allPaid = repaymentRepository.findByLoanIdOrderByInstallmentNoAsc(loan.getId())
                .stream()
                .allMatch(r -> r.getStatus() == RepaymentStatus.PAID);

        if (allPaid) {
            loan.setStatus(LoanStatus.CLOSED);
            loanRepository.save(loan);
        }

        return RepaymentResponse.fromEntity(repayment);
    }
}