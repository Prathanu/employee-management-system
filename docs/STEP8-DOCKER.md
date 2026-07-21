# Step 8: Docker Containerization Guide

## Files Created

| File | Purpose |
|------|---------|
| `docker/Dockerfile.backend` | Multi-stage build for Spring Boot |
| `docker/Dockerfile.frontend` | Multi-stage build for React + Nginx |
| `docker/nginx.conf` | Nginx config — SPA routing + API proxy |
| `docker/docker-compose.full.yml` | Run full stack locally |
| `docker/docker-compose.yml` | MySQL only (from Step 3) |
| `.dockerignore` | Exclude unnecessary files from build |
| `backend/.../application-docker.yml` | Spring profile for Docker |

---

## Multi-Stage Build Explained

### Backend Dockerfile

```
Stage 1 (build):  maven:3.9 + JDK 17  →  compile & package JAR
Stage 2 (runtime): eclipse-temurin JRE  →  run JAR only (smaller image)
```

**Why multi-stage?** Build tools (Maven, JDK compiler) are not needed at runtime. Final image is ~200MB instead of ~600MB.

### Frontend Dockerfile

```
Stage 1 (build):  node:20-alpine  →  npm ci && npm run build (Vite 8 requires Node 20+)
Stage 2 (runtime): nginx:alpine   →  serve static files
```

---

## Dockerfile Instructions Explained

### Backend Key Instructions

| Instruction | Meaning |
|-------------|---------|
| `FROM maven:3.9... AS build` | Name this build stage "build" |
| `WORKDIR /app` | Set working directory inside container |
| `COPY pom.xml .` | Copy files into container |
| `RUN mvn dependency:go-offline` | Download Maven deps (cached layer) |
| `RUN mvn package -DskipTests` | Compile and create JAR |
| `FROM eclipse-temurin:17-jre-alpine` | Start fresh slim runtime image |
| `COPY --from=build ...` | Copy JAR from build stage only |
| `USER ems` | Run as non-root (security) |
| `EXPOSE 8080` | Document the port |
| `HEALTHCHECK` | Docker monitors app health |
| `ENTRYPOINT` | Command to start the app |

### Frontend Key Instructions

| Instruction | Meaning |
|-------------|---------|
| `RUN npm ci` | Install exact deps from lock file |
| `ARG VITE_API_BASE_URL=/api` | Build-time variable for API URL |
| `RUN npm run build` | Create production React bundle |
| `FROM nginx:1.25-alpine` | Use Nginx to serve static files |
| `COPY docker/nginx.conf` | Custom Nginx config |
| `try_files $uri /index.html` | React Router support |

---

## Commands

### Prerequisites
Install Docker Desktop: https://www.docker.com/products/docker-desktop/

### Build images individually

```powershell
cd c:\Devops

# Build backend image
docker build -f docker/Dockerfile.backend -t ems-backend:latest .

# Build frontend image
docker build -f docker/Dockerfile.frontend -t ems-frontend:latest .
```

### Run full stack (MySQL + Backend + Frontend)

```powershell
cd c:\Devops\docker
docker compose -f docker-compose.full.yml up -d --build
```

**Access:**
- Frontend: http://localhost
- Backend API: http://localhost:8080
- Login: `admin` / `admin123`

### Check running containers

```powershell
docker ps
docker compose -f docker-compose.full.yml ps
```

### View logs

```powershell
docker logs ems-backend -f
docker logs ems-frontend -f
docker logs ems-mysql -f
```

### Stop all containers

```powershell
cd c:\Devops\docker
docker compose -f docker-compose.full.yml down
```

### Stop and remove volumes (reset database)

```powershell
docker compose -f docker-compose.full.yml down -v
```

---

## Verification Checklist

| # | Test | Expected |
|---|------|----------|
| 1 | `docker build` backend | Image `ems-backend:latest` created |
| 2 | `docker build` frontend | Image `ems-frontend:latest` created |
| 3 | `docker compose up` | 3 containers running |
| 4 | http://localhost | Login page loads |
| 5 | Login admin/admin123 | Dashboard appears |
| 6 | CRUD employees | Works end-to-end |
| 7 | `docker inspect` health | All containers healthy |
| 8 | Jenkins Stage 6 | Docker build stage runs |

---

## Image Sizes (approximate)

| Image | Size |
|-------|------|
| ems-backend | ~220 MB |
| ems-frontend | ~45 MB |
| mysql:8.0 | ~500 MB |

---

## Troubleshooting

| Issue | Fix |
|-------|-----|
| `docker: command not found` | Install Docker Desktop |
| Backend won't start | Wait for MySQL healthy; check `docker logs ems-backend` |
| Frontend shows API error | Ensure backend is healthy first |
| Port 80 in use | Change frontend port to `"3000:80"` in compose file |
| Port 8080 in use | Stop local `mvn spring-boot:run` first |
| Build fails at mvn step | Ensure internet access for Maven deps |
