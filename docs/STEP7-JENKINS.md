# Step 7: Jenkins CI/CD Pipeline Setup Guide

## Pipeline Overview

```
GitHub Push
    │
    ▼
┌─────────────────────────────────────────────────────────┐
│                  JENKINS PIPELINE                        │
│                                                          │
│  1. Checkout          ← Pull code from GitHub            │
│  2. Build Backend     ← mvn clean package                │
│  3. Build Frontend    ← npm ci && npm run build          │
│  4. Unit Tests        ← mvn test (32 tests)              │
│  5. SonarQube         ← Static code analysis             │
│  6. Docker Build      ← docker build (Step 8)            │
│  7. Docker Push       ← push to registry                 │
│  8. K8s Deploy        ← kubectl apply (Step 9)           │
│  9. Verify Rollout    ← kubectl rollout status           │
│  10. Smoke Tests      ← Selenium TestNG (Step 10)        │
│  11. Allure Report    ← Generate & publish (Step 11)     │
│  12. Notify           ← Teams + Email (Step 12)          │
└─────────────────────────────────────────────────────────┘
```

---

## Prerequisites

| Tool | Version | Install |
|------|---------|---------|
| Jenkins | 2.4xx+ | https://www.jenkins.io/download/ |
| Java JDK | 17 | Jenkins Global Tool Configuration |
| Maven | 3.9+ | Jenkins Global Tool Configuration |
| Node.js | 18+ | Jenkins Global Tool Configuration |
| Docker | Latest | On Jenkins agent |
| kubectl | Latest | On Jenkins agent |
| Git | Latest | On Jenkins agent |

### Required Jenkins Plugins

Install via **Manage Jenkins → Plugins**:

| Plugin | Purpose |
|--------|---------|
| Pipeline | Jenkinsfile support |
| Git | GitHub integration |
| GitHub | Webhook triggers |
| Docker Pipeline | Build & push images |
| Kubernetes CLI | kubectl deploy |
| JUnit | Test result publishing |
| JaCoCo | Coverage reports |
| Allure | Test reports (Step 11) |
| Email Extension | Email notifications |
| HTML Publisher | Coverage HTML reports |
| Credentials Binding | Secure secrets |
| Workspace Cleanup | Clean workspace post-build |

---

## Jenkins Credentials Setup

Go to **Manage Jenkins → Credentials → System → Global credentials → Add Credentials**

| ID | Type | Value | Used for |
|----|------|-------|----------|
| `github-credentials-id` | Username/Password | GitHub PAT | Clone private repo |
| `docker-registry-credentials` | Username/Password | Docker Hub creds | Push images |
| `kubeconfig-credentials` | Secret file | `~/.kube/config` | K8s deploy |
| `sonar-token` | Secret text | SonarQube token | Static analysis |
| `teams-webhook-url` | Secret text | Teams Incoming Webhook URL | Teams notify |

> Email recipients are set via Jenkins env var `EMAIL_RECIPIENTS` (not a credential).

### GitHub Personal Access Token

1. GitHub → Settings → Developer settings → Personal access tokens
2. Generate token with scopes: `repo`, `read:org`
3. Add as `github-credentials-id` in Jenkins

### Microsoft Teams Webhook

See full guide: `docs/STEP12-NOTIFICATIONS.md`

1. Teams channel → **⋯** → **Workflows** → template **Send webhook alerts to a channel**
2. Copy HTTP POST URL → Jenkins credential `teams-webhook-url` (Secret text)

> Old **Connectors** menu is retired by Microsoft — use **Workflows** instead.

### Email (SMTP)

1. **Manage Jenkins → System** → configure **E-mail Notification** (SMTP server, TLS, credentials)
2. Set global env var `EMAIL_RECIPIENTS=you@email.com`
3. Install **Email Extension Plugin**

---

## Jenkins Global Tool Configuration

**Manage Jenkins → Tools**

| Tool | Name in Jenkinsfile | Path/Version |
|------|---------------------|--------------|
| JDK | `JDK-17` | Java 17 home |
| Maven | `Maven-3.9` | Maven 3.9 home |
| NodeJS | `NodeJS-18` | Node 18.x |

---

## Create Jenkins Pipeline Job

1. **New Item** → Name: `employee-management-pipeline` → **Pipeline** → OK
2. **Pipeline** section:
   - Definition: **Pipeline script from SCM**
   - SCM: **Git**
   - Repository URL: `https://github.com/Prathanu/employee-management-system.git`
   - Credentials: `github-credentials-id`
   - Branch: `*/develop`
   - Script Path: `Jenkinsfile`
3. **Build Triggers**:
   - ☑ GitHub hook trigger for GITScm polling
4. **Save**

### GitHub Webhook (auto-trigger on push)

1. GitHub repo → Settings → Webhooks → Add webhook
2. Payload URL: `http://YOUR_JENKINS_URL:8080/github-webhook/`
3. Content type: `application/json`
4. Events: **Just the push event**
5. Add webhook

---

## Pipeline Parameters

| Parameter | Default | Description |
|-----------|---------|-------------|
| `DEPLOY_ENV` | dev | Target environment |
| `SKIP_DEPLOY` | false | Skip Docker + K8s stages |
| `SKIP_SMOKE_TESTS` | false | Skip Selenium tests |
| `RUN_SONAR` | true | Run SonarQube analysis |

---

## Running the Pipeline (First Time)

For first run without Docker/K8s (Steps 8-10 not done yet):

1. Open Jenkins job → **Build with Parameters**
2. Set:
   - `SKIP_DEPLOY` = ☑ true
   - `SKIP_SMOKE_TESTS` = ☑ true
   - `RUN_SONAR` = ☐ false (until SonarQube installed)
3. Click **Build**

**Stages that will run:** 1-5 (Checkout, Build, Test)
**Stages skipped:** 6-11 (until Steps 8-11 are complete)

---

## Stage Details

### Stage 1 — Checkout
Pulls latest code from GitHub. Records branch name and commit SHA.

### Stage 2 — Build Backend
`mvn clean package -DskipTests` compiles and packages the Spring Boot JAR.
Archives `backend/target/*.jar` as Jenkins artifact.

### Stage 3 — Build Frontend
`npm ci && npm run build` creates production React bundle in `frontend/dist/`.

### Stage 4 — Unit Tests
`mvn test` runs all 32 JUnit/Mockito tests.
Publishes JUnit results and JaCoCo HTML coverage report.

### Stage 5 — Static Code Analysis
Runs SonarQube scanner. Fails build if quality gate not met (configurable).
Requires SonarQube server running and `sonar-token` credential.

### Stage 6-7 — Docker Build & Push
Builds backend and frontend Docker images (Step 8 Dockerfiles required).
Pushes tagged images to Docker registry.

### Stage 8-9 — Kubernetes Deploy
Applies K8s manifests (Step 9 required).
Updates deployment image tags and waits for rollout success.

### Stage 10 — Selenium Smoke Tests
Runs TestNG smoke suite against deployed application (Step 10 required).

### Stage 11 — Allure Report
Generates and publishes Allure HTML test report (Step 11 required).

### Post — Notifications
On success/failure/unstable sends alerts to **Microsoft Teams** and **Email** (see `docs/STEP12-NOTIFICATIONS.md`).

---

## Verification Checklist

| # | Check | How |
|---|-------|-----|
| 1 | Jenkinsfile in repo root | Visible on GitHub |
| 2 | Jenkins job created | Pipeline visible in Jenkins |
| 3 | First build runs stages 1-4 | Blue build with test results |
| 4 | JUnit report shows 32 tests | Jenkins Test Result |
| 5 | JaCoCo report published | Coverage link in build |
| 6 | GitHub webhook triggers build | Push to develop triggers build |

---

## Troubleshooting

| Issue | Fix |
|-------|-----|
| `JDK-17 not found` | Configure JDK in Global Tool Configuration |
| `mvn: command not found` | Configure Maven in Global Tools |
| `npm: command not found` | Configure NodeJS in Global Tools |
| SonarQube fails | Set `RUN_SONAR=false` until SonarQube is installed |
| `Could not find credentials entry with ID 'sonar-token'` | Set `RUN_SONAR=false` on build, or add `sonar-token` credential in Jenkins |
| Docker stages skipped | Complete Step 8 (Dockerfiles) first |
| **Stage 6 fails in ~5s** | **Docker Hub 401** — run `docker login` on Jenkins machine; verify `docker-registry-credentials` credential |
| Stage 6 `docker is not recognized` | Start **Docker Desktop**; add `C:\Program Files\Docker\Docker\resources\bin` to **System** PATH; restart Jenkins service |
| `Cannot connect to Docker daemon` | Start Docker Desktop; ensure Jenkins can access Docker (run Jenkins as your user, not Local System) |
| `docker-registry-credentials not found` | Add Docker Hub username + password/token in Jenkins Credentials |
| K8s stages skipped | Complete Step 9 (K8s manifests) first |
| Teams notification fails | Verify `teams-webhook-url` credential; test webhook with PowerShell (Step 12 doc) |
| Email not received | Configure SMTP in Jenkins System; set `EMAIL_RECIPIENTS` env var |
| Stage 9 backend rollout timeout | Backend JVM can take 3–5 min on Docker Desktop; startupProbe added; Jenkins timeout raised to 600s |
| Stage 10 skipped | Ensure `SKIP_SMOKE_TESTS` is **unchecked** when building |
| Stage 10 `UnknownHostException: msedgedriver.azureedge.net` | Corporate network blocks driver download — Jenkins uses `testng-jenkins.xml` (API only) |
| Git clone fails | Check `github-credentials-id` PAT permissions |
