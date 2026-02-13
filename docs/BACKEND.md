# Backend Engineering Guide

## 1. Project Structure (Standard Spring Boot)

Every service follows this Layered Architecture:

```text
src/main/java/com/elib/core
  ├── config/          # Security, Swagger, CORS config
  ├── controller/      # REST Endpoints (@RestController)
  ├── service/         # Business Logic (@Service)
  ├── repository/      # DB Interfaces (@Repository)
  ├── entity/          # JPA Entities (@Entity)
  ├── dto/             # Data Transfer Objects (Request/Response classes)
  ├── mapper/          # MapStruct Object Mappers
  └── exception/       # Global Error Handling
```

## 2. lib-core Implementation Status

The foundational `lib-core` module has been fully implemented with the following components:

### **Application Entry Point**

- `ElibCoreApplication.java` - Spring Boot main class with `@SpringBootApplication`

### **Domain Entities (JPA)**

- `User.java` - Complete user entity with:
  - Email, username, password, personal details
  - Role-based authorization (Set<String> roles)
  - Timestamp auditing (createdAt, updatedAt)
  - Active/inactive status flag
- `Book.java` - Book inventory entity
- `BorrowRecord.java` - Loan tracking entity

### **Repository Layer (Spring Data JPA)**

- `UserRepository.java` - Custom queries for email/username lookup
- `BookRepository.java` - Book data access
- `BorrowRecordRepository.java` - Loan history management

### **Service Layer (Business Logic)**

- `AuthService.java` - Authentication and user registration
- `BookService.java` - Book inventory management
- `UserService.java` - User profile management
- `JwtService.java` - Complete JWT token generation/validation
- `JwtAuthenticationFilter.java` - Spring Security filter for JWT validation

### **Data Transfer Objects**

- `AuthRequest.java` / `AuthResponse.java` - Login/registration
- `BookRequest.java` / `BookResponse.java` - Book CRUD operations
- `UserRequest.java` / `UserResponse.java` - User management

### **Object Mapping (MapStruct)**

- `BookMapper.java` - Maps between Book entity and DTOs
- `UserMapper.java` - Maps between User entity and DTOs

### **Security Configuration**

- `SecurityConfig.java` - Complete Spring Security setup:
  - JWT-based stateless authentication
  - Role-based authorization (USER, ADMIN roles)
  - CORS configuration for frontend integration
  - Password encoding with BCrypt
  - CSRF protection disabled for API
- `OpenApiConfig.java` - Swagger/OpenAPI 3 documentation

### **Exception Handling**

- `GlobalExceptionHandler.java` - `@ControllerAdvice` for centralized error handling
- `ResourceNotFoundException.java` - Custom exception for missing resources
- `ErrorResponse.java` - Standardized error response format

### **Application Configuration**

- `application.yml` - Complete configuration:
  - PostgreSQL database connection
  - JWT secret and token expiration
  - SpringDoc OpenAPI settings
  - Logging configuration

## 3. Key Technical Decisions (Implemented)

### **Authentication & Authorization**

- **JWT-based stateless authentication** implemented
- **Access tokens (1hr)** and **refresh tokens (30 days)**
- **Role-based access control**:
  - `/api/v1/auth/**` - Public endpoints
  - `/api/v1/books/**` - Requires USER or ADMIN role
  - `/api/v1/users/**` - Requires ADMIN role only

### **Database Strategy**

- **PostgreSQL** as primary database
- **JPA/Hibernate** with `ddl-auto: update` for development
- **Lombok** for boilerplate reduction (getters/setters/builders)
- **Auditing fields** (createdAt, updatedAt) on all entities

### **API Design**

- **RESTful endpoints** with proper HTTP verbs
- **DTO pattern** to decouple API from database schema
- **Validation** using Spring Boot Validation annotations
- **OpenAPI 3 documentation** with Swagger UI

### **Security Implementation**

- **BCrypt password encoding**
- **CORS configured** for local development (localhost:3000, 8080)
- **Stateless session management** (`SessionCreationPolicy.STATELESS`)
- **JWT validation filter** that extracts and validates tokens

## 4. Dependencies (lib-core pom.xml)

### **Spring Boot Starters**

- `spring-boot-starter-web` - REST API foundation
- `spring-boot-starter-validation` - Request validation
- `spring-boot-starter-data-jpa` - Database access
- `spring-boot-starter-security` - Authentication/authorization
- `spring-boot-starter-amqp` - RabbitMQ messaging
- `spring-boot-devtools` - Development hot reload

### **Database**

- `postgresql` - PostgreSQL driver

### **Security**

- `jjwt-api`, `jjwt-impl`, `jjwt-jackson` - JWT implementation

### **API Documentation**

- `springdoc-openapi-starter-webmvc-ui` - OpenAPI 3 + Swagger UI

### **Object Mapping**

- `mapstruct` - Type-safe object mapping

### **Microservices Communication**

- `spring-cloud-starter-openfeign` - Declarative REST client

## 5. Running the Application

### **Prerequisites**

```bash
export POSTGRES_USER=elib_user
export POSTGRES_PASSWORD=elib_password
export JWT_SECRET=your-secret-key-here
```

### **Database Setup**

```sql
CREATE DATABASE elibrary;
-- The application will create tables automatically via Hibernate ddl-auto
```

### **Running lib-core**

```bash
cd backend/lib-core
mvn spring-boot:run
```

### **Accessing APIs**

- **Application**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI Docs**: http://localhost:8080/api/v3/api-docs

## 6. lib-gateway Implementation Status

The `lib-gateway` module is now implemented and serves as the API Gateway for the E-Library system.

### **Application Entry Point**

- `GatewayApplication.java` - Spring Boot main class with `@SpringBootApplication`

### **Gateway Configuration**

- `application.yml` - Complete gateway routing configuration:
  - Routes requests to lib-core service
  - Public routes for authentication endpoints
  - Protected routes with JWT validation
  - CORS configuration for frontend integration

### **JWT Authentication Filter**

- `JwtAuthenticationFilter.java` - Validates JWT tokens at gateway level
  - Extracts and validates Bearer tokens
  - Passes user info to downstream services via headers
  - Returns 401 for invalid or missing tokens

### **CORS Configuration**

- `CorsConfig.java` - Centralized CORS configuration
  - Allows requests from frontend origins
  - Configures allowed methods and headers
  - Enables credentials for authentication

### **Key Technical Decisions (Implemented)**

#### **Gateway Routing Strategy**

- **Single entry point** on port 8081 for all client requests
- **Path-based routing** to appropriate services
- **JWT validation** at gateway level before forwarding requests
- **Header propagation** of user info to downstream services

#### **Security Implementation**

- **Centralized JWT validation** reduces duplicate code in services
- **Stateless design** aligns with microservices architecture
- **CORS configuration** at gateway level simplifies service configuration

#### **Request Flow**

1. Client request → Gateway (port 8081)
2. JWT validation → Gateway filter
3. Route determination → Path predicates
4. Request forwarding → Target service (lib-core)
5. Response routing → Back to client

## 7. Running the Gateway

### **Prerequisites**

```bash
export JWT_SECRET=your-secret-key-here
```

### **Running lib-gateway**

```bash
cd backend/lib-gateway
mvn spring-boot:run
```

### **Accessing APIs via Gateway**

- **Gateway**: http://localhost:8081
- **Public routes**: http://localhost:8081/api/v1/auth/**
- **Protected routes**: http://localhost:8081/api/v1/books/**
- **Admin routes**: http://localhost:8081/api/v1/users/**

## 8. lib-notifications 

The `lib-notifications` module is now implemented as an asynchronous event-driven service that handles email notifications for the E-Library system.

### **Application Entry Point**

- `NotificationsApplication.java` - Spring Boot main class with `@SpringBootApplication`

### **RabbitMQ Configuration**

- `RabbitMQConfig.java` - Complete RabbitMQ setup with Topic Exchange
  - Exchange: `elib.notifications` for flexible routing
  - Queue: `email.queue` with durable persistence
  - Routing Key: `email.#` for email-related events
  - JSON message converter for structured payloads

### **Email Service**

- `EmailService.java` - SMTP email sending with comprehensive error handling
  - Uses Spring Boot Mail Starter for SMTP integration
  - Handles email sending failures with custom exceptions
  - Logs success/failure for monitoring and debugging

### **Notification Consumer**

- `NotificationConsumer.java` - RabbitMQ message listener
  - Listens to `email.queue` for email notifications
  - Processes `EmailNotificationDto` payloads
  - Calls `EmailService` for email delivery
  - Handles failures with proper error propagation

### **Data Transfer Objects**

- `EmailNotificationDto.java` - Record for email notification data
  - Notification type, recipient email, subject, body
  - Generic payload for event-specific data

### **Error Handling**

- `GlobalExceptionHandler.java` - Centralized exception handling
  - Maps email sending failures to standardized error responses
  - Provides consistent error logging and response format

### **Configuration**

- `application.yml` - Complete service configuration
  - Port 8082 for notifications service
  - RabbitMQ connection settings
  - SMTP configuration with TLS support
  - Environment-specific profiles (dev/prod)

### **Key Technical Decisions (Implemented)**

#### **Message Routing Strategy**

- **Topic Exchange** for flexible event routing
- **Durable queues** ensure message persistence
- **JSON serialization** for structured payloads
- **Error handling** with proper exception propagation

#### **Email Service Design**

- **SMTP integration** via Spring Boot Mail Starter
- **TLS support** for secure email transmission
- **Error resilience** with custom exception handling
- **Logging** for monitoring and debugging

#### **Event Types Supported**

- `email.loan.created` → Sends "Booking Confirmation" email
- `email.loan.overdue` → Sends "Overdue Alert" email
- `email.user.welcome` → Sends "Welcome" email for new registrations
- `email.user.password_reset` → Sends "Password Reset" email

#### **Message Flow**

1. lib-core publishes event to RabbitMQ exchange `elib.notifications`
2. RabbitMQ routes message to `email.queue` based on routing key
3. NotificationConsumer receives and processes the message
4. EmailService sends email via configured SMTP server
5. Success/failure logged for monitoring

### **Running lib-notifications**

#### **Prerequisites**

```bash
export RABBITMQ_HOST=localhost
export RABBITMQ_PORT=5672
export SMTP_HOST=smtp.gmail.com
export SMTP_PORT=587
export SMTP_USERNAME=your-email@gmail.com
export SMTP_PASSWORD=your-app-password
```

#### **Running the Service**

```bash
cd backend/lib-notifications
mvn spring-boot:run
```

#### **Service Endpoints**

- **Application**: http://localhost:8082
- **Health Check**: http://localhost:8082/actuator/health
- **RabbitMQ Management**: http://localhost:15672 (if RabbitMQ management plugin enabled)

### **Integration with lib-core**

The lib-notifications service is ready to receive events from lib-core. To integrate:

1. **Configure lib-core** to publish events to RabbitMQ
2. **Use routing key** `email.loan.created` for loan creation events
3. **Use routing key** `email.loan.overdue` for overdue loan events
4. **Payload format** should match `EmailNotificationDto` structure

### **Development Environment**

For local development, use:

- **MailHog** as SMTP server (port 1025)
- **RabbitMQ** with default credentials
- **Spring profiles** (`dev`) for local configuration

## 9. Backend Architecture Complete

All three backend services are now implemented:

1. **lib-core** (port 8080) - Core business logic and data management
2. **lib-gateway** (port 8081) - API Gateway with centralized security
3. **lib-notifications** (port 8082) - Asynchronous email notifications

The backend architecture follows microservices principles with clear separation of concerns, event-driven communication, and centralized security.
