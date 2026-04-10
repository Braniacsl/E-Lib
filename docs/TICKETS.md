# Assignment 3 — Strategic Architecture Development

> **30 Marks Total:** Part A (Tactical DDD + UML) 10 · Part B (Event Storming & Domain Storytelling) 10 · Part C (Implementation) 10
> Event Storming and Domain Storytelling are **complete** (see Miro board).
> Remaining work is organised below by **Bounded Context** (epic) + cross-cutting concerns.

---

## Bounded Contexts & Ownership

| Bounded Context | Owner | Service Name | Port |
|-----------------|-------|--------------|------|
| Identity | Benjamin Burt | `identity-service` | 8083 |
| Catalog | Stephen Walsh | `catalog-service` | 8084 |
| Borrowing | Elliot Vowles | `borrowing-service` | 8085 |
| Notification | Piotr Pawlowski | `notification-service` | 8082 |

Cross-context dependencies:
- Borrowing → Catalog (sync REST: check/update stock)
- Borrowing → Identity (needs userId; trusts gateway header)
- Borrowing → Notification (async: publishes domain events to RabbitMQ)
- Notification → Borrowing (consumes loan events)

---

## Epic: Identity Context

> **Owner:** Benjamin Burt
> **Ubiquitous Language:** User, Role, Credential, Authentication Token

### ELIB-01 · Tactical DDD Model for Identity Context
**Part A · Rubric: A2, A3, A5**

Produce a UML class diagram for the Identity bounded context using DDD notation from the referenced paper. Must include:
- [ ] **Entities:** `User`
- [ ] **Value Objects:** `EmailAddress`, `FullName`, `Role` (enum: USER, ADMIN, LIBRARIAN)
- [ ] **Aggregate:** `User` as aggregate root — document why (user is the transactional boundary for profile + roles)
- [ ] **Invariants:** email uniqueness, username uniqueness, must have at least one role
- [ ] **Repository:** `UserRepository`
- [ ] **Domain Services:** `AuthenticationService` (validates credentials, issues tokens)
- [ ] **Domain Events:** `UserRegistered`, `UserDeactivated`
- [ ] Correct UML multiplicities and DDD stereotypes (<<Entity>>, <<Value Object>>, <<Aggregate Root>>, etc.)
- [ ] Diagram versioned in repo under `docs/ddd/identity/`

### ELIB-02 · Inter-Context Contracts for Identity
**Part A · Rubric: A4**

Define how other contexts interact with Identity:
- [ ] **Published Language / API:** `GET /api/v1/users/{id}/summary` — returns `UserSummary` (id, name, email, roles) for use by Borrowing context
- [ ] **Anti-Corruption Layer:** Document that Borrowing stores only `userId` (a Long) and never imports Identity's domain model
- [ ] **Auth token contract:** JWT claims schema (sub, roles, exp) — consumed by Gateway and all services
- [ ] Document in `docs/ddd/identity/contracts.md`

### ELIB-03 · Implement Identity Service
**Part C · Rubric: C1**

Extract User + Auth logic from `lib-core` into `identity-service`:
- [ ] New Maven module `backend/identity-service`
- [ ] Own database: `elib_identity_db` (tables: `users`, `user_roles`)
- [ ] Migrate: `User` entity, `UserRepository`, `UserService`, `AuthService`, `JwtService`, `AuthController`, `UserController`, mappers, DTOs, security config
- [ ] Expose internal `UserSummary` endpoint per contract (ELIB-02)
- [ ] Register with Eureka, pull config from Config Server
- [ ] Build/run instructions in README
- [ ] Unit tests for auth and user service logic

---

## Epic: Catalog Context

> **Owner:** Stephen Walsh
> **Ubiquitous Language:** Book, ISBN, Inventory, Category, Stock

### ELIB-04 · Tactical DDD Model for Catalog Context
**Part A · Rubric: A2, A3, A5**

Produce a UML class diagram for the Catalog bounded context:
- [ ] **Entities:** `Book`
- [ ] **Value Objects:** `ISBN`, `BookMetadata` (title, author, publisher, year, pageCount, language), `CoverImage`
- [ ] **Aggregate:** `Book` as aggregate root — document why (book is the transactional boundary for metadata + stock)
- [ ] **Invariants:** ISBN uniqueness, `availableCopies >= 0`, `availableCopies <= totalCopies`
- [ ] **Repository:** `BookRepository`
- [ ] **Domain Services:** `InventoryService` (manages stock decrement/increment)
- [ ] **Domain Events:** `BookAdded`, `StockDecremented`, `StockIncremented`
- [ ] Correct UML multiplicities and DDD stereotypes
- [ ] Diagram versioned in repo under `docs/ddd/catalog/`

### ELIB-05 · Inter-Context Contracts for Catalog
**Part A · Rubric: A4**

Define how other contexts interact with Catalog:
- [ ] **Published Language / API consumed by Borrowing:**
  - `GET /api/v1/books/{id}/availability` → `{ available: boolean, availableCopies: int }`
  - `PUT /api/v1/books/{id}/decrement-stock`
  - `PUT /api/v1/books/{id}/increment-stock`
- [ ] **Anti-Corruption Layer:** Borrowing only stores `bookId` and never imports Catalog's `Book` entity
- [ ] **Public API:** standard CRUD + search endpoints (consumed by frontend via Gateway)
- [ ] Document in `docs/ddd/catalog/contracts.md`

### ELIB-06 · Implement Catalog Service
**Part C · Rubric: C1**

Extract Book/inventory logic from `lib-core` into `catalog-service`:
- [ ] New Maven module `backend/catalog-service`
- [ ] Own database: `elib_catalog_db` (table: `books`)
- [ ] Migrate: `Book` entity, `BookRepository`, `BookService` (CRUD + search only), `BookController`, mapper, DTOs
- [ ] Expose internal stock endpoints per contract (ELIB-05)
- [ ] Register with Eureka, pull config from Config Server
- [ ] Build/run instructions in README
- [ ] Unit tests for inventory logic (especially stock invariants)

---

## Epic: Borrowing Context

> **Owner:** Elliot Vowles
> **Ubiquitous Language:** Loan, Borrow, Return, Due Date, Overdue

### ELIB-07 · Tactical DDD Model for Borrowing Context
**Part A · Rubric: A2, A3, A5**

Produce a UML class diagram for the Borrowing bounded context:
- [ ] **Entities:** `Loan` (renamed from `BorrowRecord` to match ubiquitous language)
- [ ] **Value Objects:** `LoanPeriod` (borrowDate + dueDate), `LoanStatus` (enum: BORROWED, RETURNED, OVERDUE)
- [ ] **Aggregate:** `Loan` as aggregate root — document why (loan is the transactional boundary; each loan is independent)
- [ ] **Invariants:** cannot borrow if stock unavailable, cannot return an already-returned loan, loan period must be positive
- [ ] **Repository:** `LoanRepository`
- [ ] **Domain Services:** `BorrowingService` (orchestrates the borrow flow — calls Catalog, creates Loan, publishes event)
- [ ] **Domain Events:** `LoanCreated`, `LoanReturned`, `LoanOverdue`
- [ ] Correct UML multiplicities and DDD stereotypes
- [ ] Diagram versioned in repo under `docs/ddd/borrowing/`

### ELIB-08 · Inter-Context Contracts for Borrowing
**Part A · Rubric: A4**

Define Borrowing's dependencies and published interfaces:
- [ ] **Consumed APIs (upstream):**
  - Catalog: availability check, stock decrement/increment (via Feign client with Resilience4j — see ELIB-14)
  - Identity: userId from JWT/gateway header (no direct call needed)
- [ ] **Published Events (downstream):**
  - `LoanCreated` → RabbitMQ → consumed by Notification context
  - `LoanReturned` → RabbitMQ → consumed by Notification context
- [ ] **Anti-Corruption Layer:** Feign client returns a `CatalogStockResponse` DTO local to Borrowing — never imports Catalog's domain model
- [ ] **Public API:** `POST /borrow`, `POST /return`, `GET /user/{userId}`, `GET /overdue`
- [ ] Document in `docs/ddd/borrowing/contracts.md`

### ELIB-09 · Implement Borrowing Service
**Part C · Rubric: C1**

Build the new `borrowing-service`:
- [ ] New Maven module `backend/borrowing-service`
- [ ] Own database: `elib_borrowing_db` (table: `loans` — no FKs to other DBs, stores `userId` and `bookId` as plain Longs)
- [ ] `Loan` entity, `LoanRepository`, `BorrowingService`, `BorrowController`, DTOs
- [ ] Feign client to `catalog-service` for stock check + update
- [ ] Publish `LoanCreated` / `LoanReturned` events to RabbitMQ
- [ ] Borrow flow: check availability → decrement stock → create loan → publish event
- [ ] Return flow: update loan status → increment stock → publish event
- [ ] Register with Eureka, pull config from Config Server
- [ ] Build/run instructions in README
- [ ] Unit tests for borrow/return logic

---

## Epic: Notification Context

> **Owner:** Piotr Pawlowski
> **Ubiquitous Language:** Notification, Email, Event Subscription

### ELIB-10 · Tactical DDD Model for Notification Context
**Part A · Rubric: A2, A3, A5**

Produce a UML class diagram for the Notification bounded context:
- [ ] **Entities:** `Notification` (if persisting notification history; optional)
- [ ] **Value Objects:** `EmailMessage` (to, subject, body), `NotificationType` (enum: LOAN_CREATED, LOAN_RETURNED, LOAN_OVERDUE, WELCOME)
- [ ] **Aggregate:** `Notification` or treat as stateless event handler — document the rationale
- [ ] **Domain Services:** `NotificationDispatcher` (routes events to correct email templates)
- [ ] **Domain Events consumed:** `LoanCreated`, `LoanReturned`, `LoanOverdue` (from Borrowing context)
- [ ] Correct UML stereotypes
- [ ] Diagram versioned in repo under `docs/ddd/notification/`

### ELIB-11 · Inter-Context Contracts for Notification
**Part A · Rubric: A4**

Define how Notification consumes events:
- [ ] **Consumed Events (subscriptions):** `LoanCreated`, `LoanReturned`, `LoanOverdue` — document expected message schema
- [ ] **Anti-Corruption Layer:** `NotificationEventDto` maps from the RabbitMQ message — never depends on Borrowing's internal model
- [ ] Document in `docs/ddd/notification/contracts.md`

### ELIB-12 · Update Notification Service for Infrastructure
**Part C · Rubric: C1**

The service mostly exists. Wire it into the new infrastructure:
- [ ] Add Eureka client + Config Server client dependencies
- [ ] Move config to `config-repo/notification-service.yml`
- [ ] Handle `LoanReturned` event type (if not already)
- [ ] Register with Eureka as `NOTIFICATION-SERVICE`
- [ ] Verify end-to-end: Borrowing publishes event → Notification consumes and sends email
- [ ] Build/run instructions in README

---

## Epic: Cross-Cutting / Infrastructure

### ELIB-13 · Context Map
**Part A · Rubric: A1**

Create a DDD Context Map showing all bounded contexts and their relationships:
- [ ] Visual diagram showing Identity, Catalog, Borrowing, Notification contexts
- [ ] Relationship types labelled (Customer-Supplier, Conformist, Published Language, ACL, etc.)
- [ ] Ownership table with team member names
- [ ] Cross-context dependencies explicitly acknowledged
- [ ] Committed to `docs/ddd/context-map.md` (or image)

### ELIB-14 · Implement Eureka + Config Server
**Part C · Rubric: C2, C3**

- [ ] `backend/eureka-server` module (port 8761)
- [ ] `backend/config-server` module (port 8888) with `config-repo/` directory
- [ ] Config files per service in `config-repo/`
- [ ] Environment separation (at minimum: dev profile)
- [ ] All services register with Eureka and pull config

### ELIB-15 · Update API Gateway for Service Discovery
**Part C · Rubric: C2**

- [ ] Replace hardcoded URIs with `lb://SERVICE-NAME` routes
- [ ] Register Gateway with Eureka + Config Server
- [ ] Route mapping: auth/users → Identity, books → Catalog, loans → Borrowing

### ELIB-16 · Resilience4j on Borrowing → Catalog
**Part C · Rubric: C4**

- [ ] Circuit Breaker on Feign calls from Borrowing → Catalog
- [ ] Timeout configuration (e.g., 3s)
- [ ] Fallback methods (reject borrow gracefully when Catalog is down)
- [ ] Demonstrate: normal flow, circuit open after failures, fallback triggered
- [ ] Log circuit breaker state transitions

### ELIB-17 · Integration Tests & End-to-End Proof
**Part C · Rubric: C5**

- [ ] Integration/contract tests proving inter-service communication
- [ ] Traceable test scenarios: borrow flow end-to-end (Gateway → Borrowing → Catalog → RabbitMQ → Notification)
- [ ] Document test plan and results

### ELIB-18 · Translate Event Storming & Storytelling Insights
**Part B · Rubric: B2, B4**

- [ ] Link event storming outputs (Miro) to model changes — show what changed or was added to the DDD models as a result
- [ ] Document deltas: what insights from event storming / domain storytelling led to which backlog items or model updates
- [ ] Capture session evidence in wiki/docs: Miro board screenshots, participant list, dates, key decisions
- [ ] Commit to `docs/event-storming/` and `docs/domain-storytelling/`

### ELIB-19 · Database Separation
**Part C · Rubric: C1**

- [ ] Update `docker-compose.yml` to create 3 databases: `elib_identity_db`, `elib_catalog_db`, `elib_borrowing_db`
- [ ] Init scripts create all DBs on startup
- [ ] Seed data split per database
- [ ] `borrow_records`/`loans` table drops FKs to `users`/`books`

### ELIB-20 · Decommission `lib-core` & Update Docs
**Part C + Part A · Rubric: A5, C1**

- [ ] All endpoints verified through new services
- [ ] Remove `lib-core` from parent POM and delete module
- [ ] Update `ARCHITECTURE.md`, `BACKEND.md`, `ADR.md` for new architecture
- [ ] Rudimentary UI proves end-to-end flow

---

## Suggested Division of Labour

| Person | Bounded Context (owns modelling + implementation) | Also handles |
|--------|--------------------------------------------------|--------------|
| Benjamin Burt | **Identity** (ELIB-01, 02, 03) | Infra: Eureka + Config + Gateway (ELIB-14, 15), Context Map (ELIB-13) |
| Stephen Walsh | **Catalog** (ELIB-04, 05, 06) | DB separation (ELIB-19) |
| Elliot Vowles | **Borrowing** (ELIB-07, 08, 09) | Resilience4j (ELIB-16), Integration tests (ELIB-17) |
| Piotr Pawlowski | **Notification** (ELIB-10, 11, 12) | Event Storming translation (ELIB-18), Docs update (ELIB-20) |
