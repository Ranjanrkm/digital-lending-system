package com.sprint.lending.dto;

import com.sprint.lending.entity.Loan;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LoanResponse {
    private Long id;
    private BigDecimal amount;
    private Double interestRate;
    private Integer tenureMonths;
    private BigDecimal emi;
    private String status;
    private String adminRemark;
    private LocalDateTime appliedAt;
    private String borrowerName;
    private String borrowerEmail;
    private BigDecimal borrowerMonthlyIncome;

    public static LoanResponse fromEntity(Loan loan) {
        LoanResponse dto = new LoanResponse();
        dto.id = loan.getId();
        dto.amount = loan.getAmount();
        dto.interestRate = loan.getInterestRate();
        dto.tenureMonths = loan.getTenureMonths();
        dto.emi = loan.getEmi();
        dto.status = loan.getStatus().name();
        dto.adminRemark = loan.getAdminRemark();
        dto.appliedAt = loan.getAppliedAt();
        if (loan.getBorrower() != null) {
            dto.borrowerName = loan.getBorrower().getFullName();
            dto.borrowerMonthlyIncome = loan.getBorrower().getMonthlyIncome();
            if (loan.getBorrower().getUser() != null) {
                dto.borrowerEmail = loan.getBorrower().getUser().getEmail();
            }
        }
        return dto;
    }

    public Long getId() { return id; }
    public BigDecimal getAmount() { return amount; }
    public Double getInterestRate() { return interestRate; }
    public Integer getTenureMonths() { return tenureMonths; }
    public BigDecimal getEmi() { return emi; }
    public String getStatus() { return status; }
    public String getAdminRemark() { return adminRemark; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public String getBorrowerName() { return borrowerName; }
    public String getBorrowerEmail() { return borrowerEmail; }
    public BigDecimal getBorrowerMonthlyIncome() { return borrowerMonthlyIncome; }
}