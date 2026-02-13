package com.elib.notifications.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmailNotificationDto(
        @NotBlank String notificationType,
        @NotBlank @Email String recipientEmail,
        @NotBlank String subject,
        @NotBlank String body,
        @NotNull Object payload
) {}