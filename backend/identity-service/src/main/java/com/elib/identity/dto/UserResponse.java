package com.elib.identity.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Builder
public record UserResponse(
    UUID id,
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
