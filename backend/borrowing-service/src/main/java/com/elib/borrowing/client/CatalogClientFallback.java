package com.elib.borrowing.client;

import com.elib.borrowing.dto.CatalogStockResponse;
import com.elib.borrowing.exception.ServiceUnavailableException;

import java.util.UUID;

public class CatalogClientFallback implements CatalogClient {

    private static final String MESSAGE =
            "Catalog service is currently unavailable. Please try again later.";

    @SuppressWarnings("unused")
    private final Throwable cause;

    public CatalogClientFallback(Throwable cause) {
        this.cause = cause;
    }

    @Override
    public CatalogStockResponse checkAvailability(UUID bookId) {
        throw new ServiceUnavailableException(MESSAGE);
    }

    @Override
    public CatalogStockResponse decrementStock(UUID bookId) {
        throw new ServiceUnavailableException(MESSAGE);
    }

    @Override
    public CatalogStockResponse incrementStock(UUID bookId) {
        throw new ServiceUnavailableException(MESSAGE);
    }
}
