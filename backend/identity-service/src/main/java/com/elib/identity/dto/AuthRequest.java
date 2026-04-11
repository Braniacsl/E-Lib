package com.elib.identity.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(

    @NotBlank(message = "Email or username is required")
    String emailOrUsername,

    @NotBlank(message = "Password is required")
    String password
) {}
