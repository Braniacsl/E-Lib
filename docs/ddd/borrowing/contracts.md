# Borrowing Context: Inter-Context Contracts

## Consumed APIs (Upstream Dependencies)

### From Catalog (sync REST via Feign, wrapped in Resilience4j Circuit Breaker)

- `GET /api/v1/books/{id}/availability` (check stock before borrowing)
- `PUT /api/v1/books/{id}/decrement-stock` (after loan creation)
- `PUT /api/v1/books/{id}/increment-stock` (after loan return)

**Anti-Corruption Layer:** The Borrowing service defines a local Feign client interface and a `CatalogStockResponse` DTO:

```java
// Local to borrowing-service, never imports Catalog domain model
public record CatalogStockResponse(UUID bookId, boolean available, int availableCopies) {}
```

**Resilience4j Circuit Breaker:** The Feign calls to Catalog are wrapped in a circuit breaker. When the Catalog service is unavailable, the fallback rejects the borrow request with a message: "Catalog service is currently unavailable. Please try again later."

### From Identity (via Gateway header, no direct call)

- The `userId` is extracted from the JWT by the Gateway and passed via the `X-User-Id` header.
- The Borrowing service trusts this header and stores `userId` as a plain UUID.
- No direct REST call to the Identity service is needed.

## Public API (via Gateway, consumed by Frontend)

**POST** `/api/v1/loans/borrow`
```json
// Request (userId extracted from JWT/header)
{ "bookId": "550e8400-e29b-41d4-a716-446655440000" }
// Response 201 Created
{
  "id": "...",
  "userId": "...",
  "bookId": "...",
  "borrowDate": "2026-04-10T14:00:00",
  "dueDate": "2026-04-24T14:00:00",
  "status": "BORROWED",
  "fineAmount": 0.00
}
// Response 400 Bad Request
{ "error": "No copies available" }
// Response 400 Bad Request
{ "error": "Maximum active loans (5) reached" }
// Response 503 Service Unavailable (circuit breaker open)
{ "error": "Catalog service is currently unavailable. Please try again later." }
```

**POST** `/api/v1/loans/{id}/return`
```json
// Response 200 OK
{
  "id": "...",
  "userId": "...",
  "bookId": "...",
  "borrowDate": "2026-04-10T14:00:00",
  "dueDate": "2026-04-24T14:00:00",
  "returnDate": "2026-04-20T10:00:00",
  "status": "RETURNED",
  "fineAmount": 0.00
}
// Response 400 Bad Request
{ "error": "Loan is already returned" }
```

**GET** `/api/v1/loans/user/{userId}`
Returns all loans for a given user.

**GET** `/api/v1/loans/overdue`
Returns all loans with status `OVERDUE`. (Admin/Librarian only)

**GET** `/api/v1/loans/user/{userId}/balance`
```json
// Response 200 OK
{ "userId": "...", "totalFines": 5.00, "overdueCount": 2 }
```

## Published Events (Downstream, via RabbitMQ)

Published to exchange `elib.notifications` (Topic Exchange):

| Event | Routing Key | Payload |
|-------|-------------|---------|
| `LoanCreated` | `email.loan.created` | `{ "type": "LOAN_CREATED", "recipientEmail": "...", "subject": "Booking Confirmation", "body": "...", "payload": { "bookTitle": "...", "dueDate": "..." } }` |
| `LoanReturned` | `email.loan.returned` | `{ "type": "LOAN_RETURNED", "recipientEmail": "...", "subject": "Return Confirmation", "body": "...", "payload": { "bookTitle": "..." } }` |
| `LoanOverdue` | `email.loan.overdue` | `{ "type": "LOAN_OVERDUE", "recipientEmail": "...", "subject": "Overdue Notice", "body": "...", "payload": { "bookTitle": "...", "daysOverdue": 3, "fineAmount": 3.00 } }` |

## Context Responsibilities

The Borrowing context is the sole authority on:
- Loan lifecycle (creation, status tracking, return processing)
- Fine calculation (based on overdue duration)
- Enforcing the 5-active-loans-per-user limit
- Orchestrating the borrow flow (availability check, stock update, loan creation, event publication)

It delegates stock management to Catalog and notification delivery to the Notification context.
