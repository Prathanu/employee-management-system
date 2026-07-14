# Employee Management System — Architecture Design (Step 1)

> **Status:** Approved for implementation planning  
> **Author:** DevOps + QA Automation Learning Project  
> **Version:** 1.0

---

## 1. High-Level System Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              END USERS / QA                                  │
│                    (Browser, Jenkins-triggered Selenium)                     │
└─────────────────────────────────────────────────────────────────────────────┘
                    │                                    │
                    ▼                                    ▼
┌──────────────────────────────┐          ┌──────────────────────────────────┐
│   React Frontend (Port 3000) │          │  Selenium + TestNG Framework     │
│   Login, Dashboard, CRUD UI  │          │  Page Object Model, Allure       │
└──────────────────────────────┘          └──────────────────────────────────┘
                    │ Axios REST                       │ UI + API validation
                    ▼                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│              Spring Boot Backend API (Port 8080)                             │
│   Auth │ Employee CRUD │ Validation │ Exception Handling │ Actuator         │
└─────────────────────────────────────────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                         MySQL Database (Port 3306)                           │
│              users │ employees │ (optional audit tables)                     │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                         DEVOPS / CI-CD LAYER                                 │
│  GitHub → Jenkins → Maven Build/Test → SonarQube → Docker → Kubernetes      │
│  → Rollout Verify → Selenium Smoke → Allure → Slack/Teams/Email             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Component Communication Flow

| From | To | Protocol | Purpose |
|------|-----|----------|---------|
| React UI | Spring Boot API | HTTP/REST (JSON) | Login, CRUD operations |
| Spring Boot | MySQL | JDBC/JPA | Persist users & employees |
| Jenkins | GitHub | HTTPS/Git | Pull latest code |
| Jenkins | Maven | CLI | Build, test, package |
| Jenkins | SonarQube | HTTP API | Static code analysis |
| Jenkins | Docker Registry | Docker API | Push images |
| Jenkins | Kubernetes | kubectl/helm | Deploy application |
| Selenium Tests | React UI | WebDriver | UI automation |
| Selenium Tests | Spring Boot API | HTTP (optional) | API assertions |
| Jenkins | Allure | File system | Publish test reports |
| Jenkins | Slack/Teams/Email | Webhooks/SMTP | Notifications |

---

## 3. Planned Repository Structure (Monorepo)

```
employee-management-system/
├── docs/                          # Architecture, runbooks, diagrams
├── frontend/                      # React application
├── backend/                       # Spring Boot API
├── automation-tests/            # Selenium + TestNG + Allure
├── docker/                        # Dockerfiles, docker-compose (local dev)
├── k8s/                           # Kubernetes manifests
├── jenkins/                       # Jenkinsfile, shared library (optional)
├── scripts/                       # Helper scripts (deploy, seed DB)
├── .gitignore
└── README.md
```

---

## 4. Technology Mapping

| Layer | Technology | Responsibility |
|-------|------------|----------------|
| Presentation | React, Bootstrap/MUI, Axios | User interface |
| API | Spring Boot, Spring MVC | REST endpoints |
| Business | Spring Service layer | Business rules |
| Data | Spring Data JPA, Hibernate | ORM, repositories |
| Database | MySQL | Persistent storage |
| Build | Maven | Compile, test, package |
| CI/CD | Jenkins | Automated pipeline |
| Quality | SonarQube, JUnit, Mockito | Code quality & unit tests |
| Container | Docker | Portable runtime |
| Orchestration | Kubernetes | Scalable deployment |
| E2E Testing | Selenium, TestNG, POM | UI automation |
| Reporting | Allure | Test dashboards |
| Notifications | Slack, Teams, Email | Pipeline alerts |

---

## 5. Security Overview (Production-Oriented)

- JWT or session-based authentication between React and Spring Boot
- Kubernetes Secrets for DB credentials and API keys
- ConfigMaps for non-sensitive configuration
- HTTPS via Ingress (production)
- CORS configured explicitly for frontend origin
- Password hashing with BCrypt
- No secrets committed to Git

---

## 6. CI/CD Pipeline Stages (Preview for Step 7)

1. **Checkout** — Pull code from GitHub
2. **Build Backend** — `mvn clean package`
3. **Build Frontend** — `npm run build`
4. **Unit Tests** — JUnit + Mockito
5. **Static Analysis** — SonarQube scan
6. **Docker Build** — Multi-stage images (frontend + backend)
7. **Docker Push** — Push to registry
8. **K8s Deploy** — Apply manifests, rollout status
9. **Smoke Tests** — Selenium TestNG suite
10. **Allure Report** — Generate and archive
11. **Notify** — Slack, Teams, Email

---

## 7. Environment Strategy

| Environment | Purpose | Infrastructure |
|-------------|---------|----------------|
| Local | Development | Docker Compose (MySQL + apps) |
| CI | Build & test | Jenkins agents |
| Staging/Dev K8s | Pre-prod validation | Kubernetes cluster |
| Production | Live users | Kubernetes + Ingress + TLS |

---

## 8. Branch Strategy (Preview for Step 6)

- `main` — production-ready code
- `develop` — integration branch
- `feature/*` — new features
- `hotfix/*` — urgent production fixes

---

*This document is the foundation for Steps 2–13. No application code is created until Step 2 (Frontend).*
