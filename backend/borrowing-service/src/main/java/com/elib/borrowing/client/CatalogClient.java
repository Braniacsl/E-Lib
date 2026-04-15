package com.elib.borrowing.client;

import com.elib.borrowing.config.FeignConfig;
import com.elib.borrowing.dto.CatalogStockResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.UUID;

@FeignClient(
        name = "catalog-service",
        configuration = FeignConfig.class,
        fallbackFactory = CatalogClientFallbackFactory.class
)
public interface CatalogClient {

    @GetMapping("/api/v1/books/{id}/availability")
    CatalogStockResponse checkAvailability(@PathVariable("id") UUID bookId);

    @PutMapping("/api/v1/books/{id}/decrement-stock")
    CatalogStockResponse decrementStock(@PathVariable("id") UUID bookId);

    @PutMapping("/api/v1/books/{id}/increment-stock")
    CatalogStockResponse incrementStock(@PathVariable("id") UUID bookId);
}
