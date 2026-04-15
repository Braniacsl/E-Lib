package com.elib.catalog.dto;

import jakarta.validation.constraints.*;

public record BookRequest (

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    String title,

    @NotBlank(message = "Author is required")
    @Size(max = 255, message = "Author name cannot exceed 255 characters")
    String author,

    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "^(97(8|9))?\\d{9}(\\d|X)$", message = "Invalid ISBN format")
    String isbn,

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    String description,

    @NotNull(message = "Publication year is required")
    @Min(value = 1000, message = "Publication year must be valid")
    @Max(value = 2100, message = "Publication year must be valid")
    Integer publicationYear,

    @NotBlank(message = "Publisher is required")
    @Size(max = 255, message = "Publisher name cannot exceed 255 characters")
    String publisher,

    @NotNull(message = "Total copies is required")
    @Min(value = 1, message = "Total copies must be at least 1")
    Integer totalCopies,

    @NotBlank(message = "Category is required")
    String category,

    @NotBlank(message = "Language is required")
    String language,

    @NotNull(message = "Page count is required")
    @Min(value = 1, message = "Page count must be at least 1")
    Integer pageCount,

    @NotBlank(message = "Cover image URL is required")
    @Pattern(regexp = "^(http|https)://.*$", message = "Cover image must be a valid URL")
    String coverImageUrl
) {}