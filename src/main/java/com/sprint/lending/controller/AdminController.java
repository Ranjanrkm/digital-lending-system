package com.sprint.lending.controller;

import com.sprint.lending.dto.AdminActionRequest;
import com.sprint.lending.dto.LoanResponse;
import com.sprint.lending.service.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/loans")
public class AdminController {

    private final LoanService loanService;

    public AdminController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public ResponseEntity<List<LoanResponse>> getAllLoans(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(loanService.getAllLoansForAdmin(status));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<LoanResponse> approveLoan(@PathVariable Long id,
                                                    @RequestBody(required = false) AdminActionRequest body) {
        String remark = (body != null) ? body.getRemark() : null;
        return ResponseEntity.ok(loanService.approveLoan(id, remark));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<LoanResponse> rejectLoan(@PathVariable Long id,
                                                   @RequestBody(required = false) AdminActionRequest body) {
        String remark = (body != null) ? body.getRemark() : null;
        return ResponseEntity.ok(loanService.rejectLoan(id, remark));
    }
}