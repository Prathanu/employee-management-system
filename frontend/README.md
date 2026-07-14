# Employee Management — React Frontend

React single-page application for Employee Management System.

## Tech Stack

- React 19 + Vite
- React Router v7
- Axios
- Bootstrap 5 + Bootstrap Icons

## Quick Start

```powershell
cd frontend
npm install
npm run dev
```

Open: **http://localhost:3000**

## Mock Mode (Step 2 — no backend required)

`.env` has `VITE_USE_MOCK=true` by default.

**Login credentials:**
- Username: `admin`
- Password: `admin123`

## Pages

| Route | Page | Auth Required |
|-------|------|---------------|
| `/login` | Login | No |
| `/dashboard` | Dashboard stats | Yes |
| `/employees` | CRUD + Search | Yes |

## Switch to Real Backend (Step 4) — CURRENT MODE

`.env` is configured for live backend:
```
VITE_USE_MOCK=false
VITE_API_BASE_URL=http://localhost:8080/api
```

**Run both apps:**
```powershell
# Terminal 1
cd c:\Devops\backend
mvn spring-boot:run

# Terminal 2
cd c:\Devops\frontend
npm run dev
```

See `docs/STEP4-INTEGRATION.md` for full verification checklist.

## Build for Production

```powershell
npm run build
npm run preview
```

Output: `dist/` folder (used in Docker/K8s later)
