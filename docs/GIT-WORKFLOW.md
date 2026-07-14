# Git Workflow & Branch Strategy

## Branch Strategy (GitFlow-lite)

```
main          ─── production-ready code (protected)
  │
develop       ─── integration branch (default for features)
  │
feature/*     ─── new features (e.g. feature/jenkins-pipeline)
  │
hotfix/*      ─── urgent production fixes
```

| Branch | Purpose | Merges into |
|--------|---------|-------------|
| `main` | Stable, deployable code | — |
| `develop` | Integration of completed features | `main` (releases) |
| `feature/*` | New feature development | `develop` |
| `hotfix/*` | Critical production fixes | `main` + `develop` |

---

## Commit Message Convention

Use [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <short description>

[optional body]
```

### Types

| Type | When to use | Example |
|------|-------------|---------|
| `feat` | New feature | `feat(backend): add employee search API` |
| `fix` | Bug fix | `fix(frontend): resolve login redirect issue` |
| `test` | Add/update tests | `test(backend): add EmployeeService unit tests` |
| `docs` | Documentation only | `docs: add Git workflow guide` |
| `chore` | Build, config, tooling | `chore: configure root gitignore` |
| `ci` | CI/CD changes | `ci: add Jenkinsfile` |
| `refactor` | Code refactor | `refactor(backend): extract validation logic` |

### Scopes

`frontend`, `backend`, `docker`, `k8s`, `jenkins`, `selenium`, `docs`

### Examples

```bash
git commit -m "feat(frontend): add employee CRUD pages with Bootstrap UI"
git commit -m "feat(backend): implement Spring Boot REST APIs with MySQL"
git commit -m "test(backend): add 32 unit and integration tests"
git commit -m "docs: add architecture and Git workflow documentation"
git commit -m "chore: configure GitHub repository structure"
```

---

## Recommended Initial Commits (Step 6)

When setting up the repository for the first time:

```bash
# Commit 1 — Project foundation
git add docs/ pom.xml README.md .gitignore
git commit -m "docs: add project architecture and README"

# Commit 2 — Frontend
git add frontend/
git commit -m "feat(frontend): add React employee management UI"

# Commit 3 — Backend
git add backend/ docker/
git commit -m "feat(backend): add Spring Boot REST API with auth and CRUD"

# Commit 4 — Tests
git commit -m "test(backend): add unit, controller, and integration tests"
# (already staged if included in backend commit — or amend strategy)

# Commit 5 — GitHub config
git add .github/ docs/GIT-WORKFLOW.md
git commit -m "chore: configure GitHub repository structure and workflow"
```

> For learning, a single initial commit is also acceptable:
> `git commit -m "feat: initial employee management system with React, Spring Boot, and tests"`

---

## Daily Workflow

```bash
# 1. Start from latest develop
git checkout develop
git pull origin develop

# 2. Create feature branch
git checkout -b feature/my-feature

# 3. Make changes, stage, commit
git add .
git commit -m "feat(scope): description"

# 4. Push to GitHub
git push -u origin feature/my-feature

# 5. Create Pull Request on GitHub: feature/my-feature → develop
```

---

## Files That Must NEVER Be Committed

| File/Pattern | Reason |
|--------------|--------|
| `.env` | Contains secrets |
| `node_modules/` | Dependencies (use package.json) |
| `target/` | Maven build output |
| `*.pem`, `*.key` | Private keys |
| `credentials.json` | API credentials |
| `k8s/**/secret-local.yaml` | Real K8s secrets |

---

## GitHub Repository Setup

### 1. Create repository on GitHub

1. Go to https://github.com/new
2. Repository name: `employee-management-system`
3. Description: `Full-stack Employee Management with DevOps CI/CD pipeline`
4. Visibility: Public (for portfolio) or Private
5. **Do NOT** initialize with README (we already have one)
6. Click **Create repository**

### 2. Connect local repo to GitHub

```powershell
cd c:\Devops
git init
git branch -M main
git add .
git status
# Review staged files — ensure no secrets or target/ folders

git commit -m "feat: initial employee management system with React, Spring Boot, and tests"
git remote add origin https://github.com/YOUR_USERNAME/employee-management-system.git
git push -u origin main
```

### 3. Create develop branch

```powershell
git checkout -b develop
git push -u origin develop
```

### 4. Protect branches (GitHub Settings)

- **Settings → Branches → Add rule**
- Branch: `main` → Require pull request before merging
- Branch: `develop` → Require pull request before merging

---

## .gitignore Coverage

Root `.gitignore` excludes:

- `target/` (Maven build)
- `node_modules/` (npm packages)
- `frontend/dist/` (production build)
- `.env` (secrets)
- IDE files (`.idea/`, `.vscode/`)
- Test reports (`allure-results/`, `surefire-reports/`)
- Coverage reports (`backend/target/site/`)

---

## Verification Checklist

| # | Check | Command |
|---|-------|---------|
| 1 | Git initialized | `git status` |
| 2 | No secrets staged | `git status` — no `.env` files |
| 3 | No build artifacts | `git status` — no `target/`, `node_modules/` |
| 4 | Remote configured | `git remote -v` |
| 5 | Pushed to GitHub | View repo on github.com |
