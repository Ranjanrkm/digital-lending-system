package com.sprint.lending.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class LoanApplyRequest {

    @NotNull(message = "Loan amount is required")
    @Positive(message = "Loan amount must be positive")
    @DecimalMin(value = "10000.00", message = "Minimum loan amount is 10,000")
    @DecimalMax(value = "5000000.00", message = "Maximum loan amount is 50,00,000")
    private BigDecimal amount;

    @NotNull(message = "Tenure is required")
    @Min(value = 3, message = "Tenure must be at least 3 months")
    @Max(value = 60, message = "Tenure cannot exceed 60 months")
    private Integer tenureMonths;

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Integer getTenureMonths() { return tenureMonths; }
    public void setTenureMonths(Integer tenureMonths) { this.tenureMonths = tenureMonths; }
}