package com.elib.borrowing.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BalanceResponse(UUID userId, BigDecimal totalFineAmount) {
}
