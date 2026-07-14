# Employee Management System

A full-stack **Employee Management Application** with a modern **DevOps CI/CD pipeline** and **Selenium test automation** framework.

Built as a learning and portfolio project demonstrating end-to-end SDLC practices.

---

## Features

- User Login / Logout
- Dashboard with employee statistics
- Add, Search, Update, Delete Employees
- Responsive React UI
- REST API with Spring Boot
- Automated unit & integration tests
- CI/CD ready (Jenkins, Docker, Kubernetes)
- Selenium smoke tests + Allure reporting

---

## Tech Stack

| Layer | Technology |
|-------|------------|
| Frontend | React, Vite, Bootstrap, Axios, React Router |
| Backend | Java 17, Spring Boot, Spring Data JPA, Hibernate |
| Database | MySQL (prod) / H2 (dev & tests) |
| Build | Maven, npm |
| CI/CD | Jenkins, Docker, Kubernetes |
| Testing | JUnit, Mockito, Selenium, TestNG, Allure |
| Source Control | Git, GitHub |

---

## Project Structure

```
employee-management-system/
├── .github/                 # GitHub templates & workflows
├── backend/                 # Spring Boot REST API
├── frontend/                # React SPA
├── automation-tests/        # Selenium + TestNG (Step 10)
├── docker/                  # Docker Compose & Dockerfiles
├── k8s/                     # Kubernetes manifests (Step 9)
├── jenkins/                 # Jenkinsfile (Step 7)
├── docs/                    # Architecture & guides
├── scripts/                 # Helper scripts
├── pom.xml                  # Maven parent POM
└── README.md
```

---

## Quick Start

### Prerequisites

- Java 17+
- Maven 3.8+
- Node.js 18+
- Git

### 1. Clone repository

```bash
git clone https://github.com/YOUR_USERNAME/employee-management-system.git
cd employee-management-system
```

### 2. Start Backend

```powershell
cd backend
mvn spring-boot:run
```

API: http://localhost:8080

### 3. Start Frontend

```powershell
cd frontend
npm install
npm run dev
```

App: http://localhost:3000

### 4. Login

| Username | Password |
|----------|----------|
| admin    | admin123 |

### 5. Run Tests

```powershell
cd backend
mvn clean test
```

---

## Documentation

| Document | Description |
|----------|-------------|
| [Architecture](docs/ARCHITECTURE.md) | System design & diagrams |
| [Step 4 Integration](docs/STEP4-INTEGRATION.md) | Frontend ↔ Backend |
| [Step 5 Unit Tests](docs/STEP5-UNIT-TESTS.md) | Testing guide |
| [Git Workflow](docs/GIT-WORKFLOW.md) | Branching & commits |
| [Docker Troubleshooting](docs/TROUBLESHOOTING-DOCKER.md) | Docker setup help |

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | Login |
| GET | `/api/employees` | List/search employees |
| GET | `/api/employees/{id}` | Get by ID |
| POST | `/api/employees` | Add employee |
| PUT | `/api/employees/{id}` | Update employee |
| DELETE | `/api/employees/{id}` | Delete employee |
| GET | `/actuator/health` | Health check |

---

## Development Progress

| Step | Status | Description |
|------|--------|-------------|
| 1 | Done | Architecture design |
| 2 | Done | React frontend |
| 3 | Done | Spring Boot backend |
| 4 | Done | Frontend ↔ Backend integration |
| 5 | Done | Unit & API tests (32 tests) |
| 6 | Done | Git & GitHub configuration |
| 7 | Pending | Jenkins CI/CD pipeline |
| 8 | Pending | Dockerfile |
| 9 | Pending | Kubernetes manifests |
| 10 | Pending | Selenium automation |
| 11 | Pending | Allure reporting |
| 12 | Pending | Notifications |
| 13 | Pending | Final review |

---

## License

This project is for educational and portfolio purposes.

---

## Author

QA Automation Engineer — DevOps Learning Project
