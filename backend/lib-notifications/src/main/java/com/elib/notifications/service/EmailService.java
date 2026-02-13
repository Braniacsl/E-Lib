package com.elib.notifications.service;

import com.elib.notifications.dto.EmailNotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(EmailNotificationDto emailDto) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailDto.recipientEmail());
            message.setSubject(emailDto.subject());
            message.setText(emailDto.body());

            mailSender.send(message);
            log.info("Email sent successfully to {}", emailDto.recipientEmail());
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", emailDto.recipientEmail(), e.getMessage());
            throw new EmailSendingException("Failed to send email", e);
        }
    }

    public static class EmailSendingException extends RuntimeException {
        public EmailSendingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}