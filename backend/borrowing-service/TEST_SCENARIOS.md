# Borrowing Service Test Scenarios (ELIB-17)

## Implemented Automated Scenarios

1. Happy path borrow:
   - Borrow request succeeds when stock is available.
   - Verifies loan status is `BORROWED`.
   - Verifies RabbitMQ publish invocation (`convertAndSend`).

2. No stock:
   - Catalog availability response with `available=false` returns `400`.
   - Verifies message: `No copies available`.

3. Max loans:
   - User with 5 active loans (`BORROWED`/`OVERDUE`) gets `400` on borrow.
   - Verifies message: `Maximum active loans (5) reached`.

4. Double return:
   - First return succeeds.
   - Second return for same loan returns `400`.
   - Verifies message: `Loan is already returned`.

5. Circuit breaker fallback + recovery:
   - Stop catalog stub server.
   - Borrow request returns `503` with fallback message.
   - Restart catalog stub server, wait for open-state duration, borrow succeeds again.

6. Unit-level borrow/return/fine coverage:
   - Borrow flow with stock checks and event publish.
   - Return flow and stock increment.
   - Max loan enforcement.
   - Overdue fine calculation path.

## Manual Rubric Evidence Checklist (C4/C5)

1. Run test suite and capture terminal output:
   - `mvn -f backend/pom.xml -pl borrowing-service -am test`
2. Capture logs while catalog is down/up to show fallback and recovery.
3. Screenshot resilience/circuit-breaker logs and successful post-recovery borrow.

## Current Environment Note

Automated test execution in this session was blocked by restricted outbound network (Maven dependency download), so screenshots/log artifacts are not included yet.
