# DevOps Workflow — Presentation Guide

---

## Opening (30 seconds)

> "This diagram shows how we take code from a developer's laptop and deliver it to users — automatically, reliably, and with quality checks at every stage. Think of it as an assembly line for software: each station does one job, and nothing moves forward until the previous step passes."

---

## Step-by-Step Walkthrough

### SECTION 1 — Source Control

#### Step 1: Developer Writes Code

| | |
|---|---|
| **What is happening** | A developer writes or updates application code on their local machine and commits the changes. |
| **Why it matters** | All software delivery starts here. Controlled, versioned code is the foundation of a repeatable pipeline. |
| **Tools** | IDE (VS Code, IntelliJ), Git |
| **Input** | Business requirements, feature requests, bug fixes |
| **Output** | Code changes ready to be pushed |
| **Connects to next step** | The developer pushes code to the shared repository on GitHub. |

**Analogy:** A chef prepares a new recipe. Before it can be served to customers, it must be written down and shared with the kitchen.

---

#### Step 2: GitHub Repository

| | |
|---|---|
| **What is happening** | Code is stored in a central GitHub repository. Every change is tracked with version history, branches, and commit messages. |
| **Why it matters** | Provides a single source of truth. Teams can collaborate, review changes, and roll back if needed. |
| **Tools** | Git, GitHub |
| **Input** | Developer commits and push |
| **Output** | Updated code in the remote repository |
| **Connects to next step** | A push to GitHub automatically notifies Jenkins via a webhook. |

**Analogy:** GitHub is like a shared document library — everyone works from the same master copy, and every edit is recorded.

---

#### Step 3: Jenkins Webhook Trigger

| | |
|---|---|
| **What is happening** | When code is pushed to GitHub, a webhook sends an automatic signal to Jenkins to start the pipeline. No manual intervention is needed. |
| **Why it matters** | Removes human delay and inconsistency. Every code change gets the same automated treatment. |
| **Tools** | GitHub Webhooks, Jenkins |
| **Input** | Git push event from GitHub |
| **Output** | Jenkins pipeline execution starts |
| **Connects to next step** | Jenkins begins the CI/CD Pipeline — starting with Checkout. |

**Analogy:** Like a doorbell — when someone arrives (code is pushed), the system knows immediately and starts the process.

---

### SECTION 2 — CI/CD Pipeline (Jenkins)

*DevOps lifecycle stages: **Development → Build → Test***

#### Step 4: Checkout (Development)

| | |
|---|---|
| **What is happening** | Jenkins pulls the latest code from GitHub onto the build server. |
| **Why it matters** | Ensures the pipeline always builds the exact code that was just committed — not an older version. |
| **Tools** | Jenkins, Git |
| **Input** | Repository URL + branch/commit from webhook trigger |
| **Output** | Fresh copy of source code on the Jenkins agent |
| **Connects to next step** | Source code is ready to be compiled and built. |

**Analogy:** A factory worker picks up the latest blueprint before starting production.

---

#### Step 5: Build — Maven (Backend)

| | |
|---|---|
| **What is happening** | The backend application (Java/Spring Boot) is compiled and packaged into a deployable JAR file using Maven. |
| **Why it matters** | Converts human-readable source code into a runnable application artifact. Catches compile-time errors early. |
| **Tools** | Maven, JDK 17, Spring Boot |
| **Input** | Backend source code |
| **Output** | Compiled JAR file (`backend/target/*.jar`) |
| **Connects to next step** | Backend artifact is ready; pipeline moves to frontend build. |

**Analogy:** Baking the main dish — turning raw ingredients into something ready to serve.

---

#### Step 6: Build — npm (Frontend)

| | |
|---|---|
| **What is happening** | The frontend application (React) is compiled into optimized static files using npm. |
| **Why it matters** | Produces a production-ready UI bundle that loads fast in the browser. |
| **Tools** | npm, Node.js, React, Vite |
| **Input** | Frontend source code |
| **Output** | Production build files (`frontend/dist/`) |
| **Connects to next step** | Both backend and frontend artifacts are ready for testing. |

**Analogy:** Preparing the packaging and presentation — making the product look polished and ready for the customer.

---

#### Step 7: Unit Tests — JUnit

| | |
|---|---|
| **What is happening** | Automated unit and integration tests run against the backend code. Each function and API endpoint is verified automatically. |
| **Why it matters** | Catches bugs early — before deployment. Cheaper to fix a bug here than in production. |
| **Tools** | JUnit, Mockito, JaCoCo (coverage reports) |
| **Input** | Compiled backend code + test code |
| **Output** | Test pass/fail results, code coverage report |
| **Connects to next step** | If tests pass, pipeline continues to code quality scan. If tests fail, pipeline stops. |

**Analogy:** Quality inspection on the assembly line — checking each part before it moves forward.

---

#### Step 8: Code Quality — SonarQube

| | |
|---|---|
| **What is happening** | SonarQube scans the code for bugs, security vulnerabilities, code smells, and maintainability issues. |
| **Why it matters** | Goes beyond "does it work?" to ask "is it well-written and secure?" Prevents technical debt from accumulating. |
| **Tools** | SonarQube, Sonar Maven plugin |
| **Input** | Source code + test coverage data |
| **Output** | Quality report with issues, ratings, and security findings |
| **Connects to next step** | If quality gates pass, pipeline moves to containerization. |

**Analogy:** A building inspector checking not just that the house stands, but that the wiring is safe and the structure meets standards.

---

### SECTION 3 — Container & Deployment

*DevOps lifecycle stages: **Release → Deploy***

#### Step 9: Docker Images (Backend + Frontend)

| | |
|---|---|
| **What is happening** | The built backend JAR and frontend static files are packaged into Docker container images — self-contained units that include everything needed to run. |
| **Why it matters** | "It works on my machine" becomes "it works everywhere." Containers ensure consistency across dev, staging, and production. |
| **Tools** | Docker, Dockerfile (multi-stage builds) |
| **Input** | Backend JAR + frontend dist files |
| **Output** | Two Docker images: backend image + frontend image |
| **Connects to next step** | Images are pushed to the Docker Registry. |

**Analogy:** Packing a complete meal kit — everything needed (ingredients, instructions, containers) in one box that works anywhere.

---

#### Step 10: Docker Registry (Release)

| | |
|---|---|
| **What is happening** | Docker images are pushed to a central container registry (like Docker Hub) where they are stored, versioned, and ready for deployment. |
| **Why it matters** | Acts as a secure warehouse for application versions. Any environment can pull the exact same image. |
| **Tools** | Docker Registry / Docker Hub |
| **Input** | Docker images tagged with build number |
| **Output** | Versioned images stored in registry |
| **Connects to next step** | Kubernetes pulls images from registry to deploy. |

**Analogy:** A warehouse that stores finished products with version labels — pick any version, ship it anywhere.

---

#### Step 11: Kubernetes Cluster — Deploy Application

| | |
|---|---|
| **What is happening** | Kubernetes pulls the container images and deploys them to the cluster. It manages running containers, networking, scaling, and health checks. |
| **Why it matters** | Automates deployment, ensures high availability, and makes rollbacks easy if something goes wrong. |
| **Tools** | Kubernetes (kubectl), K8s manifests (Deployments, Services, ConfigMaps, Secrets) |
| **Input** | Docker images from registry + K8s configuration |
| **Output** | Running application accessible to users |
| **Connects to next step** | Once deployed and healthy, automated tests run against the live application. |

**Analogy:** An airport traffic controller — manages which planes (containers) land, where they park, and replaces any that have problems.

---

### SECTION 4 — Testing & Reporting

*DevOps lifecycle stages: **Test → Monitor → Feedback***

#### Step 12: Automated Tests (UI + API — TestNG)

| | |
|---|---|
| **What is happening** | Selenium-based smoke tests run against the deployed application — testing real user flows (login, dashboard, CRUD) and API endpoints. |
| **Why it matters** | Validates the application works end-to-end in a real environment — not just in isolation. Catches integration issues that unit tests miss. |
| **Tools** | Selenium, TestNG, Page Object Model, WebDriverManager |
| **Input** | Deployed application URL + test scripts |
| **Output** | Pass/fail results for each test scenario, screenshots on failure |
| **Connects to next step** | Results feed into Allure for reporting. |

**Analogy:** A mystery shopper testing the full customer experience — not just checking if the store is open, but actually buying something.

---

#### Step 13: Test Reports — Allure

| | |
|---|---|
| **What is happening** | Allure generates rich, visual test reports with step-by-step details, screenshots, trends, and failure categories. |
| **Why it matters** | Makes test results easy to understand for everyone — not just engineers. Speeds up debugging. |
| **Tools** | Allure Framework, Allure Jenkins plugin |
| **Input** | Raw test execution results |
| **Output** | Interactive HTML test dashboard |
| **Connects to next step** | Reports are analyzed by AI for root cause identification. |

**Analogy:** A detailed medical report after a health checkup — not just "pass/fail" but exactly what was checked and what failed.

---

#### Step 14: AI Analysis

| | |
|---|---|
| **What is happening** | AI reviews test failures, error messages, stack traces, and screenshots to identify root causes and classify issues (application bug vs. environment issue vs. test script issue). |
| **Why it matters** | Reduces manual triage time. Helps teams focus on real bugs instead of investigating false alarms. |
| **Tools** | AI-powered failure analysis |
| **Input** | Allure test reports, logs, screenshots |
| **Output** | Root cause analysis with categorized findings |
| **Connects to next step** | Findings route to Jira (for bugs) and Teams (for team notification). |

**Analogy:** A smart assistant that reads all the error reports overnight and gives you a morning briefing: "Here are the 2 real issues and here's why they happened."

---

#### Step 15: Defect Tracking — Jira (on test failures)

| | |
|---|---|
| **What is happening** | When AI analysis confirms a real defect, a Jira ticket is automatically created with failure details, steps to reproduce, and severity. |
| **Why it matters** | Ensures nothing falls through the cracks. Creates accountability and traceability from test failure to fix. |
| **Tools** | Jira |
| **Input** | AI analysis findings for confirmed defects |
| **Output** | Jira bug ticket assigned to the team |
| **Connects to next step** | Team is notified via Teams alert. |

**Analogy:** An automatic incident report filed at the police station — complete with evidence, so the right team can investigate.

---

#### Step 16: Teams Alert Summary

| | |
|---|---|
| **What is happening** | A summary notification is sent to the Microsoft Teams channel with build status, branch, commit, environment, and a link to the Jenkins build. |
| **Why it matters** | Keeps everyone informed without checking Jenkins manually. Immediate visibility for managers and team members. |
| **Tools** | Microsoft Teams (Incoming Webhook / Workflows) |
| **Input** | Pipeline result (success/failure/unstable) + build metadata |
| **Output** | Teams message card in the project channel |
| **Connects to next step** | Closes the feedback loop — team acts on failures or celebrates success. |

**Analogy:** A group text message after a team meeting — "Here's what happened and what we need to do next."

---

## Full Presentation Script (5–7 minutes)

> **Slide: Workflow Diagram**
>
> "Thank you for your time. I'd like to walk you through our DevOps workflow — how we take code from development all the way to production, with quality checks and automated feedback at every step."
>
> "The workflow has four main phases, moving left to right."
>
> ---
>
> **"Phase 1 — Source Control."**
>
> "It starts with a developer writing code and pushing it to our GitHub repository. GitHub stores every version of our code — who changed what, and when. When code is pushed, a webhook automatically tells Jenkins: 'New code is ready — start the pipeline.' No one has to manually kick off a build."
>
> ---
>
> **"Phase 2 — CI/CD Pipeline."**
>
> "Jenkins takes over from here. First, it checks out the latest code. Then it builds our backend using Maven and our frontend using npm — turning source code into deployable artifacts."
>
> "Next, it runs unit tests with JUnit to make sure individual components work correctly. Then SonarQube scans the code for bugs, security issues, and quality problems."
>
> "If anything fails at this stage, the pipeline stops. We don't deploy broken code."
>
> ---
>
> **"Phase 3 — Container and Deployment."**
>
> "Once the code passes all checks, we package it into Docker containers — self-contained units that run the same way everywhere. These images are stored in our Docker Registry, versioned and ready to deploy."
>
> "Kubernetes then pulls those images and deploys them to our cluster. Kubernetes manages the running application — if a container crashes, it automatically restarts it."
>
> ---
>
> **"Phase 4 — Testing and Reporting."**
>
> "After deployment, we don't just hope it works — we verify it. Automated Selenium tests run real user scenarios against the live application: logging in, viewing dashboards, creating and editing records."
>
> "Results go into Allure, which gives us rich visual reports. AI then analyzes any failures to identify root causes — was it an application bug, an environment issue, or a test problem?"
>
> "If a real defect is found, a Jira ticket is created automatically. And regardless of pass or fail, the team gets a Teams notification with the build summary."
>
> ---
>
> **"In summary..."**
>
> "This workflow gives us three key benefits: **speed** — deployments happen automatically in minutes, not days; **quality** — we test at multiple levels before and after deployment; and **visibility** — everyone knows the status through Teams alerts and detailed reports."
>
> "Happy to take any questions."

---

## Beginner-Friendly Explanation

If someone has never heard of DevOps, use this:

> "Imagine you're publishing a book. The **developer** writes the manuscript (code). **GitHub** is the publisher's archive. **Jenkins** is the printing press that automatically prints every time a chapter is submitted. **Maven and npm** format the book for print. **JUnit and SonarQube** are the editors who check for errors and quality. **Docker** packages the book into a box that opens the same way everywhere. **Kubernetes** is the bookstore that displays and manages copies. **Selenium tests** are readers who check if the book makes sense cover to cover. **Allure** is the review summary. **AI** is the analyst who explains what went wrong. **Jira** files a complaint if there's a problem. **Teams** sends everyone a notification: 'The new edition is out — here's how it went.'"

---

## Q&A — VP, Manager, and Team

### For the VP (Business / Strategy)

**Q: What business value does this workflow provide?**
> Faster time-to-market, fewer production incidents, and lower cost of fixing bugs. We catch issues before customers see them, and deployments that used to take hours now run in minutes.

**Q: How does this reduce risk?**
> Every change goes through automated testing and quality gates. If something breaks, we know within minutes — not days. Kubernetes can roll back to the previous version instantly.

**Q: What happens if the pipeline fails?**
> The pipeline stops at the failed step. Nothing gets deployed. The team is notified via Teams, and if it's a test failure, AI creates a Jira ticket for tracking.

**Q: Can we deploy to production with this?**
> Yes. The pipeline supports dev, staging, and production environments. Production deployments can require additional approval gates as we mature.

**Q: How much manual effort is involved?**
> After initial setup, day-to-day work is zero-touch. Developers push code; everything else is automated. Manual intervention is only needed when something fails.

---

### For the Manager (Operations / Delivery)

**Q: How long does a full pipeline run take?**
> Typically 10–20 minutes depending on test suite size and deployment target. Build and unit tests are the fastest stages; deployment and smoke tests take longer.

**Q: Can we skip certain stages?**
> Yes. The pipeline has parameters: skip deploy, skip smoke tests, skip SonarQube. Useful for quick validation builds during development.

**Q: How do we know what's deployed in each environment?**
> Every Docker image is tagged with the Jenkins build number. Kubernetes deployments reference that tag, so we always know exactly which build is running where.

**Q: What if a smoke test fails after deployment?**
> The pipeline is marked as failed or unstable. AI analyzes the failure, creates a Jira ticket, and the team is alerted on Teams. We can roll back the Kubernetes deployment to the previous version.

**Q: Who gets notified and when?**
> The Teams channel receives a notification on every build — success, failure, or unstable. The card includes branch, commit, environment, duration, and a link to the Jenkins build.

---

### For the Team (Technical)

**Q: What triggers the pipeline?**
> GitHub webhook on push to the tracked branch, or manual "Build with Parameters" in Jenkins.

**Q: What testing happens and when?**
> Unit tests (JUnit) run during the build phase — before deployment. Smoke tests (Selenium) run after deployment — against the live environment. Two layers of defense.

**Q: How does AI analysis work?**
> It reviews Allure test results, error messages, stack traces, and failure screenshots. It classifies failures (script, environment, application, data) and generates a root cause summary. Confirmed defects are routed to Jira.

**Q: What's the difference between unit tests and smoke tests?**
> Unit tests check individual functions in isolation — fast, run before deploy. Smoke tests check the full application end-to-end in a real browser — slower, run after deploy. Both are needed.

**Q: Can I run the pipeline locally without Jenkins?**
> Yes. You can run `mvn test` for unit tests and `mvn test` in automation-tests for smoke tests. Docker Compose runs the full stack locally. Jenkins orchestrates all of this in one automated flow.

**Q: What credentials and integrations are needed?**
> Jenkins credentials for: GitHub, Docker registry, Kubernetes kubeconfig, SonarQube token, and Teams webhook URL.

---

## End-to-End Summary

```
Developer pushes code
        ↓
GitHub stores it → Jenkins starts automatically
        ↓
Build backend (Maven) + frontend (npm)
        ↓
Unit tests (JUnit) + Code quality (SonarQube)
        ↓
Package into Docker containers → Push to Registry
        ↓
Deploy to Kubernetes cluster
        ↓
Run automated smoke tests (Selenium)
        ↓
Generate reports (Allure) → AI analyzes failures
        ↓
Create Jira tickets (if bugs found) + Notify team (Teams)
```

| Phase | Purpose | Key Tools |
|-------|---------|-----------|
| Source Control | Version and trigger | Git, GitHub, Jenkins |
| CI/CD Pipeline | Build and validate | Maven, npm, JUnit, SonarQube |
| Container & Deployment | Package and release | Docker, Kubernetes |
| Testing & Reporting | Verify and feedback | Selenium, Allure, AI, Jira, Teams |

**Three things to remember:**
1. **Automated** — from code push to deployment, no manual steps.
2. **Quality gates** — tests and scans run before and after deployment.
3. **Closed feedback loop** — failures are analyzed, tracked in Jira, and communicated via Teams.

