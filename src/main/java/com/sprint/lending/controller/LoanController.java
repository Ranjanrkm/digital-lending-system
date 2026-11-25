package com.sprint.lending.controller;

import com.sprint.lending.dto.LoanApplyRequest;
import com.sprint.lending.dto.LoanResponse;
import com.sprint.lending.dto.RepaymentResponse;
import com.sprint.lending.service.LoanService;
import com.sprint.lending.service.RepaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LoanController {

    private final LoanService loanService;
    private final RepaymentService repaymentService;

    public LoanController(LoanService loanService, RepaymentService repaymentService) {
        this.loanService = loanService;
        this.repaymentService = repaymentService;
    }

    @PostMapping("/loans")
    public ResponseEntity<LoanResponse> applyLoan(@Valid @RequestBody LoanApplyRequest request,
                                                  Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(loanService.applyForLoan(request, auth.getName()));
    }

    @GetMapping("/loans")
    public ResponseEntity<List<LoanResponse>> getMyLoans(Authentication auth) {
        return ResponseEntity.ok(loanService.getMyLoans(auth.getName()));
    }

    @GetMapping("/loans/{id}/repayments")
    public ResponseEntity<List<RepaymentResponse>> getRepayments(@PathVariable Long id,
                                                                 Authentication auth) {
        return ResponseEntity.ok(repaymentService.getRepaymentsForLoan(id, auth.getName()));
    }

    @PutMapping("/repayments/{id}/pay")
    public ResponseEntity<RepaymentResponse> payInstallment(@PathVariable Long id,
                                                            Authentication auth) {
        return ResponseEntity.ok(repaymentService.payInstallment(id, auth.getName()));
    }
}