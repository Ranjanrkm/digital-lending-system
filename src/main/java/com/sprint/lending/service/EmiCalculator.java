package com.sprint.lending.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class EmiCalculator {

    public BigDecimal calculateEmi(BigDecimal principal, double annualInterestRate, int tenureMonths) {
        double p = principal.doubleValue();
        double r = annualInterestRate / (12.0 * 100.0);
        double factor = Math.pow(1 + r, tenureMonths);
        double emi = (p * r * factor) / (factor - 1);
        return BigDecimal.valueOf(emi).setScale(2, RoundingMode.HALF_UP);
    }
}