package com.elib.borrowing.dto;

import com.elib.borrowing.entity.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record LoanResponse(
        UUID id,
        UUID userId,
        UUID bookId,
        LocalDateTime borrowDate,
        LocalDateTime dueDate,
        LocalDateTime returnDate,
        LoanStatus status,
        BigDecimal fineAmount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
