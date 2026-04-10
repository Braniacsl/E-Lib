# Identity Context: Inter-Context Contracts

## Published APIs

### Authentication (Public)

**POST** `/api/v1/auth/register`
```json
// Request
{
  "email": "student@ul.ie",
  "username": "jdoe",
  "password": "securePassword123",
  "firstName": "John",
  "lastName": "Doe"
}
// Response 201 Created
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

**POST** `/api/v1/auth/login`
```json
// Request
{ "email": "student@ul.ie", "password": "securePassword123" }
// Response 200 OK
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

**POST** `/api/v1/auth/refresh`
```json
// Request
{ "refreshToken": "eyJhbGciOiJIUzI1NiJ9..." }
// Response 200 OK
{ "accessToken": "eyJhbGciOiJIUzI1NiJ9...", "tokenType": "Bearer", "expiresIn": 3600 }
```

### User Summary (Internal, consumed by Borrowing)

**GET** `/api/v1/users/{id}/summary`
```json
// Response 200 OK
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "jdoe",
  "email": "student@ul.ie",
  "roles": ["USER"]
}
```

### User Management (Admin only)

**GET** `/api/v1/users` (list all users)
**GET** `/api/v1/users/{id}` (get user details)
**PUT** `/api/v1/users/{id}` (update user)
**DELETE** `/api/v1/users/{id}` (deactivate user)
**PUT** `/api/v1/users/{id}/roles` (assign/remove roles)

## JWT Token Contract

All services and the Gateway consume JWTs issued by the Identity service. The JWT claims schema:

```json
{
  "sub": "550e8400-e29b-41d4-a716-446655440000",
  "email": "student@ul.ie",
  "roles": ["USER"],
  "iat": 1700000000,
  "exp": 1700003600
}
```

- **Access token expiry:** 1 hour
- **Refresh token expiry:** 30 days
- **Signing algorithm:** HS256 with shared secret (`JWT_SECRET` environment variable)

## Anti-Corruption Notes

- The Borrowing context stores only `userId` (a UUID) and never imports Identity's `User` entity or DTOs.
- The Gateway validates JWT tokens independently using the shared secret; it does not call the Identity service for validation. It propagates user info downstream via `X-User-Id` and `X-User-Roles` headers.

## Published Events (via RabbitMQ)

| Event | Routing Key | Payload |
|-------|-------------|---------|
| `UserRegistered` | `email.user.welcome` | `{ "type": "WELCOME", "recipientEmail": "...", "subject": "Welcome to E-Library", "body": "...", "payload": { "username": "..." } }` |
| `UserDeactivated` | `email.user.deactivated` | `{ "type": "ACCOUNT_REMOVED", "recipientEmail": "...", "subject": "Account Deactivated", "body": "...", "payload": {} }` |
