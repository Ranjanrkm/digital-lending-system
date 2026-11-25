package com.sprint.lending.repository;

import com.sprint.lending.entity.Repayment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RepaymentRepository extends JpaRepository<Repayment, Long> {
    List<Repayment> findByLoanIdOrderByInstallmentNoAsc(Long loanId);
}