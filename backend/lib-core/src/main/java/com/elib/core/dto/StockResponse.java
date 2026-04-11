package com.elib.core.dto;

public record StockResponse(
        Long bookId,
        boolean available,
        int availableCopies
) {}