# Step 10: Selenium + TestNG Automation Framework

## Files Created

```
automation-tests/
├── pom.xml                          # Maven deps: Selenium, TestNG, WebDriverManager, Allure
├── testng.xml                       # Smoke test suite
└── src/test/
    ├── java/com/company/ems/
    │   ├── config/
    │   │   ├── ConfigReader.java    # Loads config.properties + -D overrides
    │   │   └── WebDriverFactory.java # Chrome/Firefox/Edge + WebDriverManager
    │   ├── pages/                   # Page Object Model (POM)
    │   │   ├── BasePage.java
    │   │   ├── LoginPage.java
    │   │   ├── DashboardPage.java
    │   │   └── EmployeesPage.java
    │   ├── tests/
    │   │   ├── BaseTest.java        # WebDriver setup/teardown + login helper
    │   │   ├── ApiSmokeTest.java    # API health + login (no browser)
    │   │   ├── LoginSmokeTest.java
    │   │   ├── DashboardSmokeTest.java
    │   │   └── EmployeeSmokeTest.java
    │   └── utils/
    │       └── ScreenshotUtil.java  # Screenshots on failure + Allure attach
    └── resources/
        └── config.properties
```

**Total: 10 smoke tests** (2 API + 8 UI)

---

## Page Object Model (POM) Explained

| Layer | Class | Responsibility |
|-------|-------|----------------|
| **Pages** | `LoginPage`, `DashboardPage`, `EmployeesPage` | Locators + UI actions (click, type, verify) |
| **Tests** | `*SmokeTest` | Test scenarios only — no locators in tests |
| **Base** | `BasePage`, `BaseTest` | Shared waits, driver lifecycle, login helper |
| **Config** | `ConfigReader`, `WebDriverFactory` | URLs, browser, headless mode |

**Why POM?** When the UI changes, update one Page class — not every test.

---

## Smoke Test Coverage

| Test Class | Tests | What it verifies |
|------------|-------|------------------|
| `ApiSmokeTest` | 2 | `/actuator/health` UP, `/api/auth/login` returns token |
| `LoginSmokeTest` | 3 | Page loads, valid login → dashboard, invalid login error |
| `DashboardSmokeTest` | 3 | Welcome message, stat cards, sidebar navigation |
| `EmployeeSmokeTest` | 3 | Page loads, add modal opens, search employees |

---

## Prerequisites

1. **Application running** (pick one):

| Deployment | Frontend URL | API URL for tests |
|------------|--------------|-------------------|
| **Kubernetes / Docker (nginx)** | `http://localhost` | `http://localhost` |
| **Docker Compose (split ports)** | `http://localhost` | `http://localhost:8080` |
| **Local dev (npm + mvn)** | `http://localhost:3000` | `http://localhost:8080` |

2. **Google Chrome** installed (WebDriverManager downloads ChromeDriver automatically)
3. **JDK 17** and **Maven**

---

## Run Tests

### Against Kubernetes / Docker (recommended)

```powershell
cd c:\Devops\automation-tests

mvn clean test -Dbase.url=http://localhost -Dapi.url=http://localhost
```

> Use `api.url=http://localhost` when backend is only reachable via nginx proxy (K8s).

### Against local dev servers

```powershell
mvn clean test -Dbase.url=http://localhost:3000 -Dapi.url=http://localhost:8080
```

### Headless mode (CI / Jenkins)

```powershell
mvn clean test -Dbase.url=http://localhost -Dapi.url=http://localhost -Dheadless=true
```

### Run a single test class

```powershell
mvn test -Dtest=LoginSmokeTest -Dbase.url=http://localhost -Dapi.url=http://localhost
```

---

## Expected Output

```
Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Reports:
- **Surefire XML:** `automation-tests/target/surefire-reports/`
- **Screenshots (on failure):** `automation-tests/screenshots/`
- **Allure results (Step 11):** `automation-tests/target/allure-results/`

---

## Jenkins Pipeline Integration

Stage 10 in `Jenkinsfile` runs:

```groovy
mvn clean test -B \
  -Dbase.url=${APP_BASE_URL} \
  -Dapi.url=${API_BASE_URL}
```

**Update Jenkins environment variables for your deployment:**

| Variable | Docker/K8s value | Local dev value |
|----------|------------------|-----------------|
| `APP_BASE_URL` | `http://localhost` | `http://localhost:3000` |
| `API_BASE_URL` | `http://localhost` | `http://localhost:8080` |

Set `SKIP_SMOKE_TESTS=false` when app is deployed and reachable from Jenkins agent.

---

## Configuration Reference

`src/test/resources/config.properties`:

| Property | Default | Description |
|----------|---------|-------------|
| `base.url` | `http://localhost` | Frontend URL |
| `api.url` | `http://localhost:8080` | Backend API URL |
| `browser` | `chrome` | `chrome`, `firefox`, or `edge` |
| `headless` | `false` | Run browser without UI |
| `username` | `admin` | Login username |
| `password` | `admin123` | Login password |

Override any property via Maven: `-Dbase.url=...`

---

## Troubleshooting

| Problem | Fix |
|---------|-----|
| `Connection refused` on UI tests | Start app first (`kubectl get pods` or `docker compose up`) |
| API health test fails on K8s | Use `-Dapi.url=http://localhost` (not `:8080`) |
| ChromeDriver version mismatch | WebDriverManager auto-resolves — update Chrome browser |
| `Element not found` | App may still be loading — increase `explicit.wait` in config |
| Login button disabled | Backend health check failing on login page — ensure API is up |
| Tests pass locally, fail in Jenkins | Set `APP_BASE_URL` / `API_BASE_URL` to Jenkins-accessible URL |

---

## Next Step

**Step 11 — Allure Reporting** — see `docs/STEP11-ALLURE.md` for report generation and Jenkins publishing.

**Step 12 — Notifications** — see `docs/STEP12-NOTIFICATIONS.md` (Teams + Email only).
