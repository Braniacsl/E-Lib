package com.elib.notifications.consumer;

import com.elib.notifications.dto.EmailNotificationDto;
import com.elib.notifications.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = "${rabbitmq.queue.email:email.queue}")
    public void handleEmailNotification(EmailNotificationDto emailDto) {
        log.info("Received email notification for {}", emailDto.recipientEmail());

        try {
            emailService.sendEmail(emailDto);
            log.info("Successfully processed email notification for {}", emailDto.recipientEmail());
        } catch (EmailService.EmailSendingException e) {
            log.error("Failed to process email notification for {}: {}",
                    emailDto.recipientEmail(), e.getMessage());
            throw e;
        }
    }
}