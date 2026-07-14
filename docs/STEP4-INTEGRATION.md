# Step 4: Frontend ↔ Backend Integration

## How They Connect

```
React (localhost:3000)  ──Axios──►  Spring Boot (localhost:8080)
     │                                      │
     │  POST /api/auth/login                │
     │  GET/POST/PUT/DELETE /api/employees  │
     │  Header: Authorization: Bearer <token> │
     └──────────────────────────────────────┘
```

## Configuration Changed

| File | Change |
|------|--------|
| `frontend/.env` | `VITE_USE_MOCK=false` |
| `frontend/.env` | `VITE_API_BASE_URL=http://localhost:8080/api` |
| `backend/application.yml` | CORS allows `http://localhost:3000` |

## Run Both Applications

**Terminal 1 — Backend:**
```powershell
cd c:\Devops\backend
mvn spring-boot:run
```

**Terminal 2 — Frontend:**
```powershell
cd c:\Devops\frontend
npm run dev
```

## Verification Checklist

| # | Action | Expected |
|---|--------|----------|
| 1 | Open http://localhost:3000 | Login page shows "Backend connected" |
| 2 | Login admin/admin123 | Redirect to Dashboard |
| 3 | Dashboard stats | Shows real data from backend (2 employees) |
| 4 | Go to Employees | John Doe, Sarah Wilson listed |
| 5 | Search "john" | Filters to John Doe |
| 6 | Add employee | New row appears (saved in H2 DB) |
| 7 | Update employee | Changes persist |
| 8 | Delete employee | Row removed |
| 9 | Logout | Returns to login |
| 10 | Browser DevTools → Network | API calls go to localhost:8080 |

## API Contract

| Frontend (Axios) | Backend (Spring Boot) |
|------------------|----------------------|
| `POST /api/auth/login` | `AuthController.login()` |
| `GET /api/employees?keyword=` | `EmployeeController.searchEmployees()` |
| `POST /api/employees` | `EmployeeController.addEmployee()` |
| `PUT /api/employees/{id}` | `EmployeeController.updateEmployee()` |
| `DELETE /api/employees/{id}` | `EmployeeController.deleteEmployee()` |

## Switch Back to Mock Mode

Edit `frontend/.env`:
```
VITE_USE_MOCK=true
```
Restart: `npm run dev`
