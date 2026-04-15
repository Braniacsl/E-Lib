package com.elib.borrowing.service;

import com.elib.borrowing.config.RabbitMQConfig;
import com.elib.borrowing.dto.LoanEventMessage;
import com.elib.borrowing.entity.Loan;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class LoanEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public LoanEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishLoanCreated(Loan loan, String recipientEmail) {
        LoanEventMessage message = new LoanEventMessage(
                "LOAN_CREATED",
                sanitizeEmail(recipientEmail),
                "Book borrowed successfully",
                "Your borrowing request has been processed successfully.",
                buildPayload(loan)
        );
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.LOAN_CREATED_KEY, message);
    }

    public void publishLoanReturned(Loan loan, String recipientEmail) {
        LoanEventMessage message = new LoanEventMessage(
                "LOAN_RETURNED",
                sanitizeEmail(recipientEmail),
                "Book returned successfully",
                "Your return has been processed successfully.",
                buildPayload(loan)
        );
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.LOAN_RETURNED_KEY, message);
    }

    public void publishLoanOverdue(Loan loan, String recipientEmail) {
        LoanEventMessage message = new LoanEventMessage(
                "LOAN_OVERDUE",
                sanitizeEmail(recipientEmail),
                "Loan overdue notice",
                "Your loan is overdue and may incur additional fines.",
                buildPayload(loan)
        );
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.LOAN_OVERDUE_KEY, message);
    }

    private Map<String, Object> buildPayload(Loan loan) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("loanId", loan.getId());
        payload.put("userId", loan.getUserId());
        payload.put("bookId", loan.getBookId());
        payload.put("status", loan.getStatus().name());
        payload.put("borrowDate", loan.getBorrowDate());
        payload.put("dueDate", loan.getDueDate());
        payload.put("returnDate", loan.getReturnDate());
        payload.put("fineAmount", loan.getFineAmount());
        return payload;
    }

    private String sanitizeEmail(String recipientEmail) {
        if (recipientEmail == null || recipientEmail.isBlank()) {
            // TODO: confirm with Ben whether gateway always passes X-User-Email (Option A).
            return "unknown@elib.local";
        }
        return Objects.requireNonNull(recipientEmail);
    }
}
