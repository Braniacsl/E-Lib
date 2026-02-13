package com.elib.notifications.exception;

import com.elib.notifications.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailService.EmailSendingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleEmailSendingException(EmailService.EmailSendingException e) {
        log.error("Email sending failed: {}", e.getMessage());
        return new ErrorResponse("EMAIL_SENDING_FAILED", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception e) {
        log.error("Unexpected error: {}", e.getMessage());
        return new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred");
    }

    public record ErrorResponse(String code, String message) {}
}