package com.elib.borrowing.dto;

import java.util.Map;

public record LoanEventMessage(
        String type,
        String recipientEmail,
        String subject,
        String body,
        Map<String, Object> payload
) {
}
