import { Routes, Route, Navigate } from 'react-router-dom'
import PrivateRoute from '../components/PrivateRoute'
import LoginPage from '../pages/LoginPage'
import DashboardPage from '../pages/DashboardPage'
import EmployeesPage from '../pages/EmployeesPage'

/**
 * AppRoutes — central routing configuration.
 *
 * /login          → public
 * /dashboard      → protected
 * /employees      → protected
 * /               → redirect to dashboard
 */
function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        path="/dashboard"
        element={
          <PrivateRoute>
            <DashboardPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/employees"
        element={
          <PrivateRoute>
            <EmployeesPage />
          </PrivateRoute>
        }
      />
      <Route path="/" element={<Navigate to="/dashboard" replace />} />
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  )
}

export default AppRoutes
