package com.elib.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record UserResponse (

    Long id,
    String email,
    String firstName,
    String lastName,
    String username,
    String phoneNumber,
    String address,
    Boolean isActive,
    Set<String> roles,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}