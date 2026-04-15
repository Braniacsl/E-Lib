package com.elib.borrowing.dto;

import java.util.UUID;

public record CatalogStockResponse(UUID bookId, boolean available, int availableCopies) {
}
