package com.elib.catalog.dto;

public record StockResponse(
        Long bookId,
        boolean available,
        int availableCopies
) {}