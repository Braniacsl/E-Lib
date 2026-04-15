package com.elib.catalog.dto;

import java.util.UUID;

public record StockResponse(
        UUID bookId,
        boolean available,
        int availableCopies
) {}