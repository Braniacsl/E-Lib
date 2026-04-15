package com.elib.catalog.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record BookResponse (

    UUID id,
    String title,
    String author,
    String isbn,
    String description,
    Integer publicationYear,
    String publisher,
    Integer totalCopies,
    Integer availableCopies,
    String category,
    String language,
    Integer pageCount,
    String coverImageUrl,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}