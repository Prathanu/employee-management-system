# Step 9: Kubernetes Deployment Guide

## Files Created

| File | Kubernetes Resource | Purpose |
|------|---------------------|---------|
| `k8s/00-namespace.yaml` | Namespace | Isolates all EMS resources in `employee-mgmt` (prefixed `00-` for apply order) |
| `k8s/configmap.yaml` | ConfigMap | Non-sensitive config (DB URL, Spring profile, JVM opts) |
| `k8s/secret.yaml` | Secret | DB passwords (base64 — dev only) |
| `k8s/mysql-deployment.yaml` | Deployment + Service | MySQL 8 database |
| `k8s/backend-deployment.yaml` | Deployment | Spring Boot API pods |
| `k8s/backend-service.yaml` | Service | Internal API (`backend:8080`) |
| `k8s/frontend-deployment.yaml` | Deployment | React + Nginx pods |
| `k8s/frontend-service.yaml` | Service | Exposes UI via LoadBalancer |
| `k8s/ingress.yaml` | Ingress | Optional host-based routing |

---

## Architecture in Kubernetes

```
                    ┌─────────────────────────────────────┐
                    │  Namespace: employee-mgmt            │
                    │                                      │
  Browser ─────────►│  Service: ems-frontend (LoadBalancer)│
                    │       │                              │
                    │       ▼                              │
                    │  Pod: ems-frontend (Nginx + React)   │
                    │       │  proxy /api                    │
                    │       ▼                              │
                    │  Service: backend (ClusterIP)        │
                    │       │                              │
                    │       ▼                              │
                    │  Pod: ems-backend (Spring Boot)      │
                    │       │                              │
                    │       ▼                              │
                    │  Service: mysql (ClusterIP)          │
                    │       │                              │
                    │       ▼                              │
                    │  Pod: ems-mysql (MySQL 8)            │
                    └─────────────────────────────────────┘
```

**Key naming (required for app to work):**

| Name | Why |
|------|-----|
| Service `mysql` | JDBC URL uses `mysql:3306` |
| Service `backend` | Nginx in frontend image proxies to `http://backend:8080` |
| Container `backend` | Jenkins `kubectl set image` target |
| Container `frontend` | Jenkins `kubectl set image` target |
| Deployment `ems-backend` | Jenkins rollout status |
| Deployment `ems-frontend` | Jenkins rollout status |

---

## Kubernetes Concepts (Quick Reference)

| Concept | What it does | EMS example |
|---------|--------------|---------------|
| **Namespace** | Logical isolation | `employee-mgmt` |
| **Pod** | Smallest deployable unit (1+ containers) | One backend instance |
| **Deployment** | Manages pod replicas, rolling updates | `ems-backend` |
| **Service** | Stable network endpoint for pods | `backend` → port 8080 |
| **ConfigMap** | Non-sensitive configuration | `DB_URL`, `JAVA_OPTS` |
| **Secret** | Sensitive data (encoded) | Passwords |
| **Ingress** | HTTP routing from outside cluster | `employee-mgmt.local` |
| **Probe** | Health check (liveness/readiness) | `/actuator/health` |

---

## Prerequisites

1. **Docker Desktop** with **Kubernetes enabled**
   - Docker Desktop → Settings → Kubernetes → Enable Kubernetes → Apply
2. **Docker images built** (from Step 8):

```powershell
cd c:\Devops
docker build -f docker/Dockerfile.backend -t ems-backend:latest .
docker build -f docker/Dockerfile.frontend -t ems-frontend:latest .
```

3. **kubectl** available:

```powershell
kubectl version --client
kubectl cluster-info
```

4. **Stop Docker Compose** if still running (avoids port 80/8080 conflicts):

```powershell
cd c:\Devops\docker
docker compose -f docker-compose.full.yml down
```

---

## Deploy to Kubernetes (Local)

### Apply all manifests

**Option A — one command (recommended):**

```powershell
cd c:\Devops
kubectl apply -f k8s/
```

`00-namespace.yaml` is named so the namespace is created before other resources.

**Option B — step by step:**

```powershell
cd c:\Devops

kubectl apply -f k8s/00-namespace.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/mysql-deployment.yaml
kubectl apply -f k8s/backend-deployment.yaml
kubectl apply -f k8s/frontend-deployment.yaml
kubectl apply -f k8s/backend-service.yaml
kubectl apply -f k8s/frontend-service.yaml
```

> **Note:** `ingress.yaml` is optional — skip until you install an Ingress controller.

**If you see `namespaces "employee-mgmt" not found`:** the namespace was still propagating. Wait 5 seconds and run `kubectl apply -f k8s/` again.

### Watch rollout

```powershell
kubectl get pods -n employee-mgmt -w
```

Wait until all pods show `Running` and `READY 1/1` (backend may take ~60–90s on first start).

```powershell
kubectl rollout status deployment/ems-backend -n employee-mgmt --timeout=300s
kubectl rollout status deployment/ems-frontend -n employee-mgmt --timeout=120s
```

### Verify services

```powershell
kubectl get all -n employee-mgmt
```

Expected:

```
NAME                                READY   STATUS    RESTARTS   AGE
pod/ems-backend-xxxxx               1/1     Running   0          2m
pod/ems-frontend-xxxxx              1/1     Running   0          2m
pod/ems-mysql-xxxxx                 1/1     Running   0          2m

NAME                   TYPE           CLUSTER-IP      EXTERNAL-IP   PORT(S)
service/backend        ClusterIP      10.x.x.x        <none>        8080/TCP
service/ems-frontend   LoadBalancer   10.x.x.x        localhost     80:xxxxx/TCP
service/mysql          ClusterIP      10.x.x.x        <none>        3306/TCP
```

---

## Access the Application

| What | URL |
|------|-----|
| **Frontend UI** | http://localhost (LoadBalancer on Docker Desktop) |
| **Login** | `admin` / `admin123` |
| **Backend API (direct)** | Port-forward only (ClusterIP is internal) |

If LoadBalancer shows `<pending>`, use port-forward:

```powershell
kubectl port-forward svc/ems-frontend 8081:80 -n employee-mgmt
```

Then open: http://localhost:8081

Test API through frontend proxy:

```powershell
Invoke-RestMethod -Uri "http://localhost/api/auth/login" -Method POST `
  -ContentType "application/json" `
  -Body '{"username":"admin","password":"admin123"}'
```

---

## Useful kubectl Commands

```powershell
# Pod status
kubectl get pods -n employee-mgmt

# Logs
kubectl logs deployment/ems-backend -n employee-mgmt
kubectl logs deployment/ems-frontend -n employee-mgmt
kubectl logs deployment/ems-mysql -n employee-mgmt

# Describe pod (events, probe failures)
kubectl describe pod -l app=ems-backend -n employee-mgmt

# Shell into container
kubectl exec -it deployment/ems-backend -n employee-mgmt -- sh

# Scale replicas (demo)
kubectl scale deployment/ems-backend --replicas=2 -n employee-mgmt

# Delete everything
kubectl delete namespace employee-mgmt
```

---

## Jenkins Pipeline Integration

Stage 8 in `Jenkinsfile` applies these manifests and updates images:

```groovy
kubectl apply -f k8s/00-namespace.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/mysql-deployment.yaml
kubectl apply -f k8s/backend-deployment.yaml
kubectl apply -f k8s/frontend-deployment.yaml
kubectl apply -f k8s/backend-service.yaml
kubectl apply -f k8s/frontend-service.yaml

kubectl set image deployment/ems-backend backend=${BACKEND_IMAGE}:${IMAGE_TAG} -n employee-mgmt
kubectl set image deployment/ems-frontend frontend=${FRONTEND_IMAGE}:${IMAGE_TAG} -n employee-mgmt
```

**Jenkins credentials needed:**

| Credential ID | Type | Purpose |
|---------------|------|---------|
| `kubeconfig-credentials` | Secret file | `~/.kube/config` for cluster access |
| `docker-registry-credentials` | Username/password | Push images before K8s deploy |

**First Jenkins run tip:** Keep `SKIP_DEPLOY=true` until Jenkins has kubeconfig + Docker registry configured.

---

## Optional: Ingress (Production-style routing)

1. Install ingress-nginx (one-time):

```powershell
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.11.3/deploy/static/provider/cloud/deploy.yaml
```

2. Add to `C:\Windows\System32\drivers\etc\hosts`:

```
127.0.0.1 employee-mgmt.local
```

3. Apply ingress:

```powershell
kubectl apply -f k8s/ingress.yaml
```

4. Open: http://employee-mgmt.local

---

## Troubleshooting

| Problem | Cause | Fix |
|---------|-------|-----|
| `ImagePullBackOff` | Image not found in cluster | Rebuild: `docker build ... -t ems-backend:latest .` |
| Backend `CrashLoopBackOff` | MySQL not ready | `kubectl logs deployment/ems-mysql -n employee-mgmt` |
| Backend probe failures | Slow JVM startup | Wait 90s; check `kubectl describe pod` |
| `localhost` refused | Compose still on port 80 | `docker compose -f docker-compose.full.yml down` |
| LoadBalancer `<pending>` | No cloud LB | Use `kubectl port-forward` (see above) |
| `namespaces "employee-mgmt" not found` | Namespace race on first bulk apply | Wait 5s, run `kubectl apply -f k8s/` again |
| Login fails | API proxy broken | Confirm Service is named `backend` (not `ems-backend`) |

---

## Docker Compose vs Kubernetes

| | Docker Compose (Step 8) | Kubernetes (Step 9) |
|--|-------------------------|---------------------|
| **Use case** | Local dev, quick demo | Production-like orchestration |
| **Scaling** | Manual | `kubectl scale` / HPA |
| **Self-healing** | restart policy | Deployment recreates failed pods |
| **Secrets** | env in compose file | Kubernetes Secrets |
| **URL** | http://localhost | http://localhost (LoadBalancer) |

---

## Next Step

**Step 10 — Selenium TestNG framework** with Page Object Model and smoke tests against the deployed app.

Say **"proceed to Step 10"** when ready.
