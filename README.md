# E-Library System

Microservices-based digital library platform built for CS4135 Software Architectures.

## Stack

- **Java 21** + **Spring Boot 3.2.3** + **Spring Cloud 2023.0.0**
- **PostgreSQL 15** (3 isolated databases)
- **RabbitMQ 3** (async messaging)
- **React** (frontend, port 3000)

## Services

| Service | Port | Database |
|---------|------|----------|
| API Gateway (`lib-gateway`) | 8081 | — |
| Identity Service | 8083 | `elib_identity_db` |
| Catalog Service | 8084 | `elib_catalog_db` |
| Borrowing Service | 8085 | `elib_borrowing_db` |
| Notification Service (`lib-notifications`) | 8082 | — |
| Eureka Server | 8761 | — |
| Config Server | 8888 | — |

## Prerequisites

- Java 21
- Maven 3.9+
- Docker + Docker Compose

## Build

```bash
cd backend
mvn clean install -DskipTests
```

## Run

**1. Start infrastructure (PostgreSQL + RabbitMQ):**
```bash
docker compose up -d
```
This creates three PostgreSQL databases (`elib_identity_db`, `elib_catalog_db`, `elib_borrowing_db`) via `backend/docker/init-databases.sql`.

**2. Start services in order:**
```bash
# Infrastructure first
mvn -f backend/eureka-server/pom.xml spring-boot:run &
mvn -f backend/config-server/pom.xml spring-boot:run &

# Wait ~10 seconds, then start services (they register with Eureka + pull config)
mvn -f backend/identity-service/pom.xml spring-boot:run &
mvn -f backend/catalog-service/pom.xml spring-boot:run &
mvn -f backend/borrowing-service/pom.xml spring-boot:run &
mvn -f backend/lib-notifications/pom.xml spring-boot:run &

# Gateway last
mvn -f backend/lib-gateway/pom.xml spring-boot:run &
```

**3. Verify:**
- Eureka dashboard: http://localhost:8761
- Config Server health: http://localhost:8888/actuator/health
- Login: `POST http://localhost:8081/api/v1/auth/login` with `{"email":"admin@elibrary.com","password":"admin123"}`

## Tests

**Unit + integration tests (borrowing service):**
```bash
mvn -f backend/pom.xml -pl borrowing-service -am test
```

Tests use H2 in-memory database and WireMock to stub catalog service calls. Scenarios covered:

- Happy-path borrow flow (stock check, loan creation, RabbitMQ publish)
- No stock available -> 400
- Maximum active loans (5) reached-> 400
- Double return -> 400
- Circuit breaker: catalog unavailable -> 503 fallback, recovery after open-state timeout

See `backend/borrowing-service/TEST_SCENARIOS.md` for full scenario descriptions.

## Architecture

See `docs/ddd/context-map.md` for the bounded context map and cross-context dependency diagram.

DDD models and inter-context contracts: `docs/ddd/{identity,catalog,borrowing,notification}/`.

Event storming outcomes and domain stories: `docs/event-storming/README.md`.
