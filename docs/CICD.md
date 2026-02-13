# CI/CD & Development Environment

## Overview

The E-Library project uses a comprehensive development environment with multiple tools for local development, testing, and deployment. This document outlines all CI/CD pipelines, development tools, and environment configurations.

## Development Environment Components

### 1. Containerized Development (DevContainer)

**Primary Development Method**: Uses Visual Studio Code DevContainers with JetBrains Gateway support.

**Configuration Files**:

- `.devcontainer/devcontainer.json` - Main devcontainer configuration
- `docker-compose.yml` - Core services (PostgreSQL, RabbitMQ)
- `docker-compose.dev.yml` - Development container with mounted volumes

**Features**:

- **Java 21** with Maven for backend development
- **Node.js LTS** for frontend development
- **Docker-in-Docker** for container management
- **Automatic port forwarding** (8080, 3000, 5005)
- **Post-creation setup** (Maven install, npm install)

### 2. Nix Development Environment

**Alternative Development Method**: Uses Nix flakes for reproducible development environments.

**Configuration Files**:

- `flake.nix` - Nix flake definition
- `flake.lock` - Dependency locking

**Available Shells**:

- `nix develop` - Full stack environment (Java + Node.js + IntelliJ)
- `nix develop .#backend` - Backend-only environment
- `nix develop .#frontend` - Frontend-only environment

**Included Tools**:

- JDK 21 (Temurin)
- Node.js 20
- Maven
- Docker & Docker Compose
- PostgreSQL client
- IntelliJ IDEA Ultimate

### 3. Docker Production Configuration

**Production Deployment**:

- `docker-compose.prod.yml` - Production services configuration
- `backend/Dockerfile` - Multi-stage Spring Boot build
- `frontend/Dockerfile` - Multi-stage React build with Nginx

**Services**:

- **Backend**: Spring Boot application (Port 8080)
- **Frontend**: React application with Nginx (Port 80)
- **PostgreSQL**: Database (Port 5432)
- **RabbitMQ**: Message broker (Ports 5672, 15672)

## IntelliJ IDEA Configuration

### Required Plugins

The project includes `.idea/plugin-dependencies.xml` which defines all required plugins. IntelliJ will prompt to install missing plugins when opening the project.

**Essential Plugins**:

- **Spring Boot & MVC** - Spring framework support
- **Lombok** - Annotation processing
- **Database Tools** - PostgreSQL database integration
- **JavaScript/TypeScript** - Frontend development
- **React** - React framework support
- **Docker** - Container management
- **Git/GitHub** - Version control
- **Terminal** - Integrated terminal
- **YAML** - Configuration file support
- **Markdown** - Documentation editing

**Code Quality Plugins**:

- **SonarLint** - Code quality analysis
- **CheckStyle-IDEA** - Java code style enforcement
- **ESLint** - JavaScript/TypeScript linting

### Project Settings

**Shared Configuration** (in `.idea/` directory):

- `codeStyles/` - Code formatting rules
- `inspectionProfiles/` - Code inspection settings
- `runConfigurations/` - Pre-configured run/debug setups

**Key Run Configurations**:

- **Docker Compose Dev** - Starts development environment
- **Backend All Services** - Builds and tests all backend modules
- **Frontend Dev Server** - Starts React development server

## Continuous Integration & Deployment

### 1. Render.com Deployment

**Configuration File**: `render.yaml`

**Services on Render**:

- **PostgreSQL Database**: Managed PostgreSQL instance
- **RabbitMQ**: Private service for message queuing
- **Backend Service**: Spring Boot application
- **Frontend Service**: React application with Nginx

**Environment Variables**:

- Database connection strings (auto-injected from managed database)
- RabbitMQ credentials
- API URLs for service communication

### 2. GitHub Actions (Implemented)

**Implemented Workflows**:

Three separate workflows that chain together:

1. **Test Suite** (`.github/workflows/test.yml`)

   - Runs on every push to `main`/`develop` branches and pull requests
   - Runs backend and frontend tests in parallel
   - Includes caching for Maven and npm dependencies
2. **Build Docker Images** (`.github/workflows/build-docker.yml`)

   - Triggers when Test Suite completes successfully on `main` branch
   - Builds and pushes Docker images to Docker Hub
   - Tags images with `latest` and commit SHA
3. **Deploy to Production** (`.github/workflows/deploy.yml`)

   - Triggers when Build Docker Images completes successfully on `main` branch
   - Deploys to Render.com using their API
   - Deploys backend first, waits 30 seconds, then deploys frontend

**Required GitHub Secrets**:

Set up the following secrets in your GitHub repository settings:

- `DOCKER_USERNAME` - Your Docker Hub username
- `DOCKER_PASSWORD` - Your Docker Hub password/token
- `RENDER_API_KEY` - Your Render.com API key
- `RENDER_SERVICE_ID_BACKEND` - Render service ID for backend
- `RENDER_SERVICE_ID_FRONTEND` - Render service ID for frontend

## Development Tools & Requirements

### Required Software

**Core Development**:

- **Docker & Docker Compose**: Version 24.0+
- **Git**: Version 2.40+
- **JDK**: Version 21
- Node.js Version 20.x LTS

**Package Managers**:

- **Maven**: Version 3.9+ (included in devcontainer)
- **npm**: Version 10+

### IDE Setup

The recommended IDE is Idea ultimate. Instructions to below:

## Git Hooks (Implemented)

Standard Git hooks have been implemented in `.git/hooks/` directory:

### 1. Pre-commit Hook (`pre-commit`)

**Purpose**: Runs code quality checks before allowing a commit

**Checks performed**:

1. **Frontend Code Formatting**:

   - Automatically formats staged TypeScript/JavaScript files with Prettier
   - Runs ESLint on staged files and applies fixes
   - Requires: `prettier` installed in frontend (`npm install --save-dev prettier`)
2. **Backend Code Quality**:

   - Runs CheckStyle if `checkstyle.xml` is configured in backend
   - Runs Maven tests for changed Java files
   - Skips tests if no Java files were modified
3. **IDE Workspace Protection**:

   - Prevents committing IDE workspace files (`.idea/workspace.xml`, `.vscode/*`, `.idea/*.iml`)
   - Provides clear error message with instructions to remove files

### 2. Commit Message Hook (`commit-msg`)

**Purpose**: Enforces conventional commit message format

**Validation rules**:

1. **Format**: Must follow `<type>(<scope>): <description>` pattern
2. **Valid types**: feat, fix, docs, style, refactor, test, chore, perf, build, ci, revert
3. **Length**: First line must be ≤ 100 characters
4. **Examples**:
   - `feat(auth): add login functionality`
   - `fix(api): resolve null pointer exception`
   - `docs: update README with setup instructions`

### 3. Pre-push Hook (`pre-push`) - Optional

**Purpose**: Runs comprehensive tests before pushing to remote

**Checks performed**:

1. Runs full backend test suite with Maven
2. Runs frontend tests (if configured)
3. Prevents pushing if any tests fail

### 4. Prepare Commit Message Hook (`prepare-commit-msg`) - Optional

**Purpose**: Provides commit message template

**Features**:

- Adds conventional commit format instructions
- Shows examples of valid commit messages
- Only activates for new commits (not merges or cherry-picks)

### Installation Instructions:

1. **Make hooks executable**:

   ```bash
   chmod +x .git/hooks/pre-commit
   chmod +x .git/hooks/commit-msg
   chmod +x .git/hooks/pre-push
   chmod +x .git/hooks/prepare-commit-msg
   ```
2. **Install required tools**:

   ```bash
   cd frontend
   npm install --save-dev prettier
   ```
3. **Optional CheckStyle setup**:

   - Create `backend/checkstyle.xml` with preferred rules
   - Add maven-checkstyle-plugin to backend `pom.xml`

## Environment Variables

### Dev Environment

```
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/library_db
SPRING_DATASOURCE_USERNAME=user
SPRING_DATASOURCE_PASSWORD=password
SPRING_RABBITMQ_HOST=localhost
```

### Prod

- Database credentials injected from managed database
- RabbitMQ credentials from private service
- Service URLs from Render service discovery

## Troubleshooting

### Common Issues

1. Devcontainer won't start:
   1. Ensure Docker is running
   2. Ensure all containers are running (see below)
   3. Restart Idea
   4. Check all dependencies are met
2. Plugin installation issues:
   1. Check Internet connection
   2. Check plugins installed (see below)
   3. Install plugins in marketplace (see below)
