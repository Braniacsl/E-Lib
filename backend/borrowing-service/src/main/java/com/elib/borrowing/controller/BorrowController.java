package com.elib.borrowing.controller;

import com.elib.borrowing.dto.BalanceResponse;
import com.elib.borrowing.dto.BorrowRequest;
import com.elib.borrowing.dto.LoanResponse;
import com.elib.borrowing.service.BorrowingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowingService borrowingService;

    @PostMapping("/borrow")
    public ResponseEntity<LoanResponse> borrowBook(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader(value = "X-User-Email", required = false) String userEmail,
            @Valid @RequestBody BorrowRequest request
    ) {
        LoanResponse response = borrowingService.borrowBook(UUID.fromString(userId), userEmail, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<LoanResponse> returnBook(
            @PathVariable("id") UUID loanId,
            @RequestHeader(value = "X-User-Email", required = false) String userEmail
    ) {
        LoanResponse response = borrowingService.returnBook(loanId, userEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LoanResponse>> getLoansForUser(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(borrowingService.getLoansForUser(userId));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<LoanResponse>> getOverdueLoans() {
        return ResponseEntity.ok(borrowingService.getOverdueLoans());
    }

    @GetMapping("/user/{userId}/balance")
    public ResponseEntity<BalanceResponse> getUserBalance(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(borrowingService.getUserBalance(userId));
    }
}
