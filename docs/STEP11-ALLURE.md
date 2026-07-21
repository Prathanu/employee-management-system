# Step 11: Allure Test Reporting

## What Was Added

| File | Purpose |
|------|---------|
| `src/test/resources/allure.properties` | Results directory + GitHub issue link patterns |
| `src/test/resources/categories.json` | Failure categorization in reports |
| `listeners/AllureEnvironmentListener.java` | Writes browser/URL/OS to report Environment tab |
| `@Step` on Page Objects | Step-by-step traces in report timeline |
| `@Severity`, `@Story` on tests | Priority and grouping in dashboards |
| `pom.xml` | `allure-maven` plugin + `allure.results.directory` |

---

## Report Hierarchy (Allure Labels)

```
Epic:    Employee Management System
  └── Feature:  Login / Dashboard / API / Employee Smoke Tests
        └── Story:  UI Authentication, Backend Health, etc.
              └── Test:  individual @Test methods
                    └── Steps:  @Step methods in Page Objects
```

| Label | Example | Where set |
|-------|---------|-----------|
| `@Epic` | Employee Management System | Test class |
| `@Feature` | Login Smoke Tests | Test class |
| `@Story` | UI Authentication | Test method |
| `@Severity` | BLOCKER, CRITICAL, NORMAL | Test method |
| `@Description` | Human-readable detail | Test method |
| `@Step` | "Login with username 'admin'" | Page Object method |

---

## Generate Reports Locally

### Prerequisites

1. App running at `http://localhost` (K8s/Docker) or adjust URLs
2. JDK 17 + Maven
3. Chrome (for UI tests)

### Step 1 — Run tests (produces raw results)

```powershell
cd c:\Devops\automation-tests

mvn clean test -B `
  "-Dbase.url=http://localhost" `
  "-Dapi.url=http://localhost" `
  "-Dheadless=true"
```

**Output:** `automation-tests/target/allure-results/` (JSON files)

### Step 2 — Generate HTML report

```powershell
mvn allure:report -B
```

**Output:** `automation-tests/target/site/allure-maven-plugin/index.html`

Open in browser:

```powershell
start target\site\allure-maven-plugin\index.html
```

### Step 3 — Interactive report server (recommended for demos)

```powershell
mvn allure:serve -B
```

Opens a local server (usually http://localhost:random-port) with live report — great for interviews and team demos.

---

## What You'll See in the Report

| Widget | Content |
|--------|---------|
| **Overview** | Pass/fail/skip counts, duration, success rate |
| **Suites** | Tests grouped by TestNG suite/class |
| **Graphs** | Status, severity, duration charts |
| **Timeline** | Parallel execution timeline |
| **Behaviors** | Epic → Feature → Story tree |
| **Packages** | Tests by Java package |
| **Categories** | Auto-grouped failures (auth, API, UI) |
| **Environment** | Browser, Base URL, API URL, OS, Java version |
| **Attachments** | Screenshots on failure (from `ScreenshotUtil`) |

---

## Failure Screenshots

On test failure, `ScreenshotUtil` automatically:
1. Saves PNG to `automation-tests/screenshots/`
2. Attaches image to the Allure test result

Visible under each failed test → **Attachments** section.

---

## Jenkins Integration (Stage 11)

`Jenkinsfile` already includes:

```groovy
stage('11. Generate Allure Report') {
    steps {
        dir('automation-tests') {
            sh 'mvn allure:report -B || echo "Allure report generation skipped"'
        }
    }
    post {
        always {
            allure([
                results: [[path: 'automation-tests/target/allure-results']]
            ])
        }
    }
}
```

### Jenkins plugin setup

1. Install **Allure Jenkins Plugin** (Manage Jenkins → Plugins)
2. Configure tool: **Manage Jenkins → Global Tool Configuration → Allure Commandline**
   - Name: `Allure-2.29`
   - Install automatically (or point to local install)
3. After pipeline run → build page → **Allure Report** link in left sidebar

### Pipeline flow

```
Stage 10: mvn test          → writes allure-results/
Stage 11: mvn allure:report → generates HTML
Post:     allure() step     → publishes to Jenkins UI
```

---

## categories.json Explained

Auto-groups failures for faster triage:

| Category | Matches |
|----------|---------|
| Authentication failures | Login-related errors |
| API / backend failures | `ApiSmokeTest` stack traces |
| UI navigation failures | Dashboard/Employee/nav errors |
| Element interaction issues | Selenium timeout/click errors |

Edit `src/test/resources/categories.json` to add custom rules.

---

## Environment Tab

`AllureEnvironmentListener` writes at suite start:

```
Project=Employee Management System
Browser=chrome
Base.URL=http://localhost
API.URL=http://localhost
Headless=true
OS=Windows 11
Java=21.0.6
```

Useful when comparing runs across environments (local vs Jenkins vs staging).

---

## Troubleshooting

| Problem | Fix |
|---------|-----|
| Empty Allure report | Run `mvn test` first — results must exist |
| `allure:serve` fails | Run from `automation-tests/` directory |
| No Environment tab | Check `target/allure-results/environment.properties` exists |
| No screenshots | Failure must occur in UI test; check `screenshots/` folder |
| Jenkins Allure link missing | Install Allure plugin + configure commandline tool |
| Categories not showing | Ensure `categories.json` is in `src/test/resources` |

---

## Quick Reference

```powershell
# Full flow (local)
cd c:\Devops\automation-tests
mvn clean test "-Dbase.url=http://localhost" "-Dapi.url=http://localhost" "-Dheadless=true"
mvn allure:serve

# Report paths
# Raw results:  target/allure-results/
# HTML report:  target/site/allure-maven-plugin/index.html
# Screenshots:  screenshots/
```

---

## Next Step

**Step 12 — Microsoft Teams + Email Notifications** — configure webhook and SMTP (Slack not used).

Say **"proceed to Step 13"** when ready.
