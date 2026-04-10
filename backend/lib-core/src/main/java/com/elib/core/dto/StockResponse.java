package com.elib.core.dto;

public record StockResponse(
        Long bookId,
        Integer availableCopies,
        Integer totalCopies,
        Boolean available
) {
}