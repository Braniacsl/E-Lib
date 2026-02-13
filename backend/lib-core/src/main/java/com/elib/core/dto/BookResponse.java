package com.elib.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
public record BookResponse (

    Long id,
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