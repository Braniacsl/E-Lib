# Notification Context: Inter-Context Contracts

## Consumed Events (Subscriptions)

The Notification context is a pure consumer. It subscribes to domain events published by other contexts via RabbitMQ and dispatches notifications accordingly.

### RabbitMQ Configuration

- **Exchange:** `elib.notifications` (Topic Exchange)
- **Queue:** `email.queue` (durable)
- **Routing Key Pattern:** `email.#`

### Expected Message Schema

All events conform to the `NotificationEventDto` structure:

```json
{
  "type": "LOAN_CREATED",
  "recipientEmail": "student@ul.ie",
  "subject": "Booking Confirmation",
  "body": "Your loan has been confirmed.",
  "payload": {
    "bookTitle": "Clean Code",
    "dueDate": "2026-04-24"
  }
}
```

**Fields:**

| Field | Type | Description |
|-------|------|-------------|
| `type` | String (enum) | One of: `LOAN_CREATED`, `LOAN_RETURNED`, `LOAN_OVERDUE`, `WELCOME`, `ACCOUNT_REMOVED` |
| `recipientEmail` | String | Email address of the notification recipient |
| `subject` | String | Email subject line |
| `body` | String | Email body content |
| `payload` | Map | Additional key-value data for templates (optional) |

### Consumed Events

| Routing Key | Source Context | Event Type | Action |
|-------------|---------------|------------|--------|
| `email.loan.created` | Borrowing | `LOAN_CREATED` | Send booking confirmation |
| `email.loan.returned` | Borrowing | `LOAN_RETURNED` | Send return confirmation |
| `email.loan.overdue` | Borrowing | `LOAN_OVERDUE` | Send overdue reminder |
| `email.user.welcome` | Identity | `WELCOME` | Send welcome email |
| `email.user.deactivated` | Identity | `ACCOUNT_REMOVED` | Send deactivation notice |

## Anti-Corruption Layer

The `NotificationEventDto` is defined locally within the Notification context. It maps from the RabbitMQ JSON message into a local representation. The Notification context:

- Never imports `Loan`, `User`, `Book`, or any entity from other contexts.
- Never queries other contexts' databases.
- Receives only the information needed to compose and send a notification.
- Treats the `payload` map as opaque data for template interpolation.

## Context Responsibilities

The Notification context is responsible for:
- Consuming domain events from the RabbitMQ queue
- Routing events to the correct notification template based on `type`
- Composing email content (subject, body) from event data
- Sending emails via SMTP (simulated for this project; logs output instead)
- Error handling and logging for failed deliveries

It has no published API and no database. It is a pure event-driven consumer.
