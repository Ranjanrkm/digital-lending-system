package com.sprint.lending.dto;

import com.sprint.lending.entity.BorrowerProfile;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BorrowerProfileResponse {
    private String fullName;
    private String email;
    private String pan;
    private BigDecimal monthlyIncome;
    private String employmentType;
    private BigDecimal maxAffordableEmi;

    public static BorrowerProfileResponse fromEntity(BorrowerProfile profile) {
        BorrowerProfileResponse dto = new BorrowerProfileResponse();
        dto.fullName = profile.getFullName();
        dto.email = profile.getUser().getEmail();
        dto.pan = profile.getPan();
        dto.monthlyIncome = profile.getMonthlyIncome();
        dto.employmentType = profile.getEmploymentType();
        dto.maxAffordableEmi = profile.getMonthlyIncome()
                .multiply(BigDecimal.valueOf(0.50))
                .setScale(2, RoundingMode.HALF_UP);
        return dto;
    }

    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPan() { return pan; }
    public BigDecimal getMonthlyIncome() { return monthlyIncome; }
    public String getEmploymentType() { return employmentType; }
    public BigDecimal getMaxAffordableEmi() { return maxAffordableEmi; }
}