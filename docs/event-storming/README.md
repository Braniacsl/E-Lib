# Event Storming and Domain Storytelling

## Session Details

| | |
|---|---|
| **Date** | Friday, April 4, 2026 |
| **Tool** | Miro (digital whiteboard) |
| **Participants** | Benjamin Burt (22360255), Elliot Vowles (22299211), Stephen Walsh (21334234), Piotr Pawlowski (21304858) |
| **Duration** | Approximately 1 hour |
| **Method** | Big-picture Event Storming followed by Domain Storytelling |

## Event Storming Notation

| Colour | Meaning | Example |
|--------|---------|---------|
| Orange | Domain Event (past tense) | "Book Borrowed", "Loan Created" |
| Blue | Command (action) | "Borrow Book", "Return Book" |
| Yellow | Actor | "Student", "Librarian" |
| Pink | Hotspot (uncertainty, risk, potential bug) | "Multiple users request same book?" |

See [legend screenshot](screenshots/legend.png).

## Event Storming Outcomes

### Domain Events Identified

| Domain Event | Actor | Triggering Command | Context |
|---|---|---|---|
| Account Created | Student | Register Account | Identity |
| Registration Rejected | Student | Register Account | Identity |
| Signed In | Student / Librarian | Sign In | Identity |
| Welcome Email Sent | System | (triggered by Account Created) | Notification |
| Stock Checked | System | Borrow Book | Catalog |
| Book Reserved | System | Borrow Book | Catalog |
| Borrow Rejected | System | Borrow Book | Borrowing |
| Booking Confirmation Sent | System | (triggered by Book Reserved) | Notification |
| Rejection Email Sent | System | (triggered by Borrow Rejected) | Notification |
| Loan Created | System | (triggered by Book Reserved) | Borrowing |
| Loan Overdue | System | (scheduled check) | Borrowing |
| Overdue Notification Sent | System | (triggered by Loan Overdue) | Notification |
| Book Returned | Student | Return Book | Borrowing / Catalog |
| Loan Updated | System | (triggered by Book Returned) | Borrowing |
| Return Confirmation Sent | System | (triggered by Book Returned) | Notification |
| Book Added | Librarian | Add Book | Catalog |
| Book Removed | Librarian | Remove Book | Catalog |
| Account Removed | Librarian | Remove User | Identity |
| Account Removal Email Sent | System | (triggered by Account Removed) | Notification |
| Balance Calculated | Librarian | Calculate User Balance | Borrowing |
| Budget Marked | Librarian | (balance review) | Borrowing |
| Manual Notification Sent | Librarian | Send Manual Overdue Reminder | Notification |

### Commands Identified

| Command | Actor | Target Context |
|---------|-------|----------------|
| Register Account | Student | Identity |
| Sign In | Student / Librarian | Identity |
| Borrow Book | Student | Borrowing (orchestrates with Catalog) |
| Return Book | Student | Borrowing (orchestrates with Catalog) |
| Add Book | Librarian | Catalog |
| Remove Book | Librarian | Catalog |
| Remove User | Librarian | Identity |
| Calculate User Balance | Librarian | Borrowing |
| Send Manual Overdue Reminder | Librarian | Notification |

### Policies (Automatic Business Rules)

| Policy | Trigger | Action |
|--------|---------|--------|
| Stock validation before borrow | Borrow Book command | Check Catalog for available copies; reject if 0 |
| Loan limit enforcement | Borrow Book command | Reject if user has 5 or more active loans |
| Automatic overdue detection | Scheduled (daily) | Mark loans past due date as OVERDUE; publish LoanOverdue event |
| Fine accrual | Loan marked OVERDUE | Calculate fine: 1 GBP base + 1 GBP per day overdue |
| Notification dispatch | Any domain event with notification routing key | Consume event, compose email, send |

### Hotspots and Uncertainties

| Hotspot | Location on Board | Resolution |
|---------|-------------------|------------|
| "Multiple users request same book?" | Near Borrow Book command | Stock decrement is atomic at the database level. Concurrent requests are handled by the Catalog service; the second user receives a rejection if available copies reach 0. (Discussed by Ben and Elliot.) |
| "What defines 'significantly overdue'?" | Near Loan Overdue event | Automated notification on first overdue. Librarian can send manual reminders at their discretion. No specific day threshold defined. (Discussed by Ben and Elliot.) |
| "If librarian removes a user, what about active loans?" | Near Remove User command | Prevent removal of users with outstanding loans. (Proposed by Stephen.) |

### Insights

- Stock is checked at borrow time, not browse time, since availability can change between browsing and borrowing.
- Fines are displayed in the system but paid in person at the library. The business rule is preserved without implementing a payment gateway.
- Email-sending code is written but no real SMTP server is connected. Notifications are logged and can be displayed in the UI.

TODO: Add any additional insights or conflicts from the actual session that are not captured here.

## Translation of Insights to Architecture

The event storming session produced the Miro board, which was then used to derive the backlog items in TICKETS.md. The following table traces how specific discoveries on the board map to architectural decisions and tickets.

| Insight from Event Storming | Resulting Change | Ticket |
|---|---|---|
| Borrow Book requires synchronous stock check from Catalog | Borrowing service calls Catalog via Feign client with Resilience4j circuit breaker | ELIB-09, ELIB-16 |
| Notification events follow every major domain action | All domain events are published to RabbitMQ with routing keys matching notification types | ELIB-09, ELIB-12 |
| Student and Librarian are distinct actors with different commands | Role-based access control (USER, ADMIN, LIBRARIAN) enforced at Gateway and service level | ELIB-01, ELIB-03 |
| "Multiple users request same book" hotspot | Optimistic locking on Book's `availableCopies` field; atomic decrement operation | ELIB-06 |
| Overdue detection is a system-triggered event, not a user action | Scheduled job in Borrowing service to scan for overdue loans and publish events | ELIB-09 |
| Fine calculation depends on overdue duration | LoanPeriod Value Object with `daysOverdue()` method; fine = 1 GBP * daysOverdue | ELIB-07 |
| Account removal has cascading effects on active loans | Invariant added: cannot remove user with active loans | ELIB-01 |
| Catalog stock operations (decrement/increment) are internal API, not user-facing | Catalog exposes separate internal endpoints for stock management vs public browse/search API | ELIB-05, ELIB-06 |
| Event storming revealed 4 natural bounded contexts | System decomposed into Identity, Catalog, Borrowing, Notification services with clear ownership | ELIB-13 |

## Board Screenshots

- [Full board overview](screenshots/full-board.png)
- [Student flow (zoomed)](screenshots/student-flow.png)
- [Librarian flow (zoomed)](screenshots/librarian-flow.png)
- [Legend](screenshots/legend.png)

---

# Domain Storytelling

The following domain stories were derived from the event storming session. They capture key interactions between system actors and the E-Library platform and were used to refine the domain model and improve the shared understanding of system behaviour.

## Domain Story 1: Borrowing a Book

**Actor:** Student
**Goal:** Borrow a book from the library
**Trigger:** Student initiates a borrow request

1. The student signs into the system.
2. The student browses the book catalog.
3. The student selects a book and initiates a borrow request.
4. The system checks the availability of the selected book (via Catalog service).
5. If the book is available, the system reserves the book and decrements stock.
6. A loan record is created for the student with a 2-week due date.
7. A booking confirmation notification is sent to the student.

**Outcome:** The book is successfully borrowed and recorded in the system.

## Domain Story 2: Borrow Request Rejected

**Actor:** Student
**Goal:** Attempt to borrow a book
**Trigger:** Student attempts to borrow an unavailable book

1. The student selects a book and initiates a borrow request.
2. The system checks the stock of the book (via Catalog service).
3. The system determines that the book is unavailable (stock = 0).
4. The borrow request is rejected.
5. A rejection notification is sent to the student.

**Outcome:** The student is informed that the book cannot be borrowed.

## Domain Story 3: Returning a Book

**Actor:** Student
**Goal:** Return a borrowed book
**Trigger:** Student selects a book to return

1. The student accesses their active loans.
2. The student selects a book to return.
3. The system processes the return request.
4. The loan record is updated to status RETURNED with the return date.
5. The book stock is incremented in the Catalog service.
6. A return confirmation notification is sent.

**Outcome:** The book is returned and becomes available for other users.

## Domain Story 4: Overdue Notification

**Actor:** System (scheduled job)
**Goal:** Notify the student of overdue books
**Trigger:** Loan due date is exceeded

1. The system monitors active loans via a scheduled daily job.
2. The due date for a loan is exceeded.
3. The system marks the loan as OVERDUE and calculates the fine.
4. A LoanOverdue event is published to RabbitMQ.
5. The Notification service consumes the event and sends an overdue notification.

**Outcome:** The student is informed that the book is overdue and a fine is accruing.

## Domain Story 5: Managing Library Stock

**Actor:** Librarian (Admin)
**Goal:** Manage book inventory
**Trigger:** Librarian updates stock or adds a new book

1. The librarian logs into the admin portal.
2. The librarian adds a new book to the catalog with metadata and initial stock count.
3. The system creates the book record in the Catalog service.
4. The updated stock is reflected in the catalog for students to browse.

**Outcome:** The library inventory is updated and accurately maintained.

## Domain Story 6: Account Registration

**Actor:** Student
**Goal:** Create a new account
**Trigger:** Student selects the register option

1. The student selects the register option.
2. The student enters their email, username, password, and name.
3. The system validates the information (email/username uniqueness).
4. A new user account is created in the Identity service.
5. A UserRegistered event is published to RabbitMQ.
6. A welcome email notification is sent to the student.

**Outcome:** The student account is successfully created and ready for use.

## Validation

These domain stories were reviewed and validated by all team members during collaborative sessions. The team ensured that each story accurately reflects expected system behaviour and aligns with the event storming outcomes.

## Ubiquitous Language

The domain storytelling process established a shared vocabulary across the team:

| Term | Definition | Context |
|------|-----------|---------|
| Loan | A record of a user borrowing a specific book, tracking dates and status | Borrowing |
| Borrow | The act of checking out a book, creating a Loan and decrementing stock | Borrowing |
| Return | The act of giving back a borrowed book, updating Loan status and incrementing stock | Borrowing |
| Stock | The count of available physical copies of a book | Catalog |
| Overdue | A Loan whose due date has passed without the book being returned | Borrowing |
| Fine | A monetary penalty for an overdue Loan (1 GBP + 1 GBP/day) | Borrowing |
| Role | Authorization level (USER, ADMIN, LIBRARIAN) determining permitted operations | Identity |
| Notification | A message dispatched to a user in response to a domain event | Notification |
