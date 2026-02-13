# Architecture Decision Records (ADR)

## ADR-001: Monorepo Structure

- **Status:** Accepted
- **Context:** We have multiple microservices and a frontend. Managing 4+ git repos is overhead we cannot afford.
- **Decision:** We will use a single Git repository (Monorepo) with folders for each service.
- **Consequences:** Easier code sharing, unified CI pipeline, easier to run locally.

## ADR-002: PostgreSQL for Persistence

- **Status:** Accepted
- **Context:** We need a relational database to handle structured data (Books, Loans) and transactions.
- **Decision:** Use PostgreSQL 15.
- **Consequences:** Strong ACID compliance for loan transactions. Requires Docker for local development.

## ADR-003: React + Vite

- **Status:** Accepted
- **Context:** Create-React-App is deprecated/slow. We need a modern build tool.
- **Decision:** Use Vite with React.
- **Consequences:** Faster startup times and hot module replacement (HMR).

## ADR-004: JWT (Stateless) Authentication

- **Status:** Accepted
- **Context:** Microservices cannot easily share "Session" state without a distributed cache (Redis).
- **Decision:** Use JSON Web Tokens (JWT).
- **Consequences:** The Gateway validates the token. Services trust the Gateway.
