package com.elib.catalog.controller;

import com.elib.catalog.dto.BookRequest;
import com.elib.catalog.dto.BookResponse;
import com.elib.catalog.dto.StockResponse;
import com.elib.catalog.service.BookService;
import com.elib.catalog.service.CatalogSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books") // ⚠️ check context-path later
@RequiredArgsConstructor
@Tag(name = "Books", description = "Book management endpoints")
public class BookController {

    private final BookService bookService;
    private final CatalogSearchService catalogSearchService;

    @PostMapping
    @Operation(summary = "Create a new book")
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest request) {
        BookResponse response = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        BookResponse response = bookService.getBookById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all books with pagination")
    public ResponseEntity<Page<BookResponse>> getAllBooks(
            @Parameter(description = "Pagination parameters")
            @PageableDefault(size = 20) Pageable pageable) {
        Page<BookResponse> response = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update book by ID")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request) {
        BookResponse response = bookService.updateBook(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete book by ID (soft delete)")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search books by title, author, or ISBN")
    public ResponseEntity<Page<BookResponse>> searchBooks(
            @RequestParam String query,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<BookResponse> response = catalogSearchService.searchBooks(query, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    @Operation(summary = "Get all distinct book categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = catalogSearchService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}/availability")
    @Operation(summary = "Check book stock availability")
    public ResponseEntity<StockResponse> checkAvailability(@PathVariable Long id) {
        StockResponse response = bookService.checkAvailability(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/decrement-stock")
    @Operation(summary = "Decrement book stock")
    public ResponseEntity<StockResponse> decrementStock(@PathVariable Long id) {
        StockResponse response = bookService.decrementStock(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/increment-stock")
    @Operation(summary = "Increment book stock")
    public ResponseEntity<StockResponse> incrementStock(@PathVariable Long id) {
        StockResponse response = bookService.incrementStock(id);
        return ResponseEntity.ok(response);
    }
}