# Troubleshooting — Docker Not Installed

## Error you saw

```
docker : The term 'docker' is not recognized...
```

**Meaning:** Docker Desktop is not installed (or not added to your system PATH).

---

## Option 1: Run backend WITHOUT Docker (use now)

We added an **H2 in-memory database** profile. No Docker or MySQL needed.

```powershell
cd c:\Devops\backend
mvn spring-boot:run
```

Default profile is `h2`. Data resets when you stop the app (fine for learning).

**Test login:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST `
  -ContentType "application/json" `
  -Body '{"username":"admin","password":"admin123"}'
```

**Optional — H2 web console:** http://localhost:8080/h2-console  
- JDBC URL: `jdbc:h2:mem:employee_db`  
- Username: `sa`  
- Password: *(leave empty)*

---

## Option 2: Install Docker Desktop (needed for Steps 7–9)

Docker is required later for Jenkins pipeline, container images, and Kubernetes.

### Install steps (Windows)

1. Check virtualization is enabled in BIOS (Intel VT-x / AMD-V)
2. Download: https://www.docker.com/products/docker-desktop/
3. Run installer → enable **WSL 2** when prompted
4. Restart your computer
5. Open **Docker Desktop** and wait until it shows **Running**
6. Open a **new** PowerShell window and verify:

```powershell
docker --version
docker compose version
```

**Expected:**
```
Docker version 24.x.x, build ...
Docker Compose version v2.x.x
```

### Then run MySQL

```powershell
cd c:\Devops\docker
docker compose up -d
docker ps
```

### Run backend with MySQL

```powershell
cd c:\Devops\backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## Profile summary

| Profile | Database | Docker needed? | Command |
|---------|----------|----------------|---------|
| `h2` (default) | H2 in-memory | No | `mvn spring-boot:run` |
| `dev` | MySQL in Docker | Yes | `mvn spring-boot:run -Dspring-boot.run.profiles=dev` |
| `prod` | External MySQL | K8s/Cloud | Set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` |

---

## Option 3: Local MySQL without Docker

If you have MySQL installed directly on Windows:

1. Create database `employee_db`
2. Create user `ems_user` / password `ems_pass`
3. Run: `mvn spring-boot:run -Dspring-boot.run.profiles=dev`
