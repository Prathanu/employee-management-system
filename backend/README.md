# Employee Management — Spring Boot Backend

## Prerequisites

- Java 17+
- Maven 3.8+
- Docker Desktop *(optional for local dev — see below)*

## Quick Start (NO Docker required)

```powershell
cd c:\Devops\backend
mvn spring-boot:run
```

Uses **H2 in-memory database** (profile `h2`). Perfect if Docker is not installed.

## With MySQL + Docker (production-like)

```powershell
# Terminal 1 — MySQL (requires Docker Desktop)
cd c:\Devops\docker
docker compose up -d

# Terminal 2 — Backend with MySQL
cd c:\Devops\backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

> Docker not installed? See `docs/TROUBLESHOOTING-DOCKER.md`

API available at: **http://localhost:8080**

## Default Login

| Username | Password  |
|----------|-----------|
| admin    | admin123  |

## API Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/login` | No | Login |
| GET | `/api/employees` | Yes | List/search employees |
| GET | `/api/employees/{id}` | Yes | Get by ID |
| POST | `/api/employees` | Yes | Add employee |
| PUT | `/api/employees/{id}` | Yes | Update employee |
| DELETE | `/api/employees/{id}` | Yes | Delete employee |
| GET | `/actuator/health` | No | Health check |

**Auth header:** `Authorization: Bearer <token>`

## Test with curl

```powershell
# Login
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body '{"username":"admin","password":"admin123"}'
$token = $response.token

# List employees
Invoke-RestMethod -Uri "http://localhost:8080/api/employees" -Headers @{ Authorization = "Bearer $token" }
```

## MySQL Connection Details

| Setting | Value |
|---------|-------|
| Host | localhost:3306 |
| Database | employee_db |
| Username | ems_user |
| Password | ems_pass |
