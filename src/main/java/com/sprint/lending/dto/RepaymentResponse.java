package com.sprint.lending.dto;

import com.sprint.lending.entity.Repayment;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RepaymentResponse {
    private Long id;
    private Integer installmentNo;
    private LocalDate dueDate;
    private BigDecimal amount;
    private String status;
    private LocalDateTime paidAt;

    public static RepaymentResponse fromEntity(Repayment r) {
        RepaymentResponse dto = new RepaymentResponse();
        dto.id = r.getId();
        dto.installmentNo = r.getInstallmentNo();
        dto.dueDate = r.getDueDate();
        dto.amount = r.getAmount();
        dto.status = r.getStatus().name();
        dto.paidAt = r.getPaidAt();
        return dto;
    }

    public Long getId() { return id; }
    public Integer getInstallmentNo() { return installmentNo; }
    public LocalDate getDueDate() { return dueDate; }
    public BigDecimal getAmount() { return amount; }
    public String getStatus() { return status; }
    public LocalDateTime getPaidAt() { return paidAt; }
}