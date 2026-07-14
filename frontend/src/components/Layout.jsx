import { NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { isMockMode } from '../services/api'

/**
 * Layout — sidebar navigation + main content area.
 * Used by Dashboard and Employees pages after login.
 */
function Layout({ children, title }) {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <div className="app-layout">
      <aside className="sidebar p-3">
        <div className="mb-4">
          <h5 className="fw-bold mb-0">
            <i className="bi bi-people-fill me-2"></i>
            EMS
          </h5>
          <small className="text-secondary">Employee Management</small>
        </div>

        {isMockMode() ? (
          <div className="alert alert-warning py-1 px-2 small mb-3">
            <i className="bi bi-info-circle me-1"></i>
            Mock mode ON
          </div>
        ) : (
          <div className="alert alert-success py-1 px-2 small mb-3">
            <i className="bi bi-plug me-1"></i>
            Live API (:8080)
          </div>
        )}

        <nav className="nav flex-column">
          <NavLink to="/dashboard" className="nav-link">
            <i className="bi bi-speedometer2 me-2"></i>
            Dashboard
          </NavLink>
          <NavLink to="/employees" className="nav-link">
            <i className="bi bi-person-lines-fill me-2"></i>
            Employees
          </NavLink>
        </nav>

        <div className="mt-auto pt-4 border-top border-secondary">
          <small className="text-secondary d-block mb-2">
            <i className="bi bi-person-circle me-1"></i>
            {user?.username} ({user?.role})
          </small>
          <button className="btn btn-outline-light btn-sm w-100" onClick={handleLogout}>
            <i className="bi bi-box-arrow-right me-1"></i>
            Logout
          </button>
        </div>
      </aside>

      <main className="main-content">
        {title && <h2 className="mb-4 fw-bold">{title}</h2>}
        {children}
      </main>
    </div>
  )
}

export default Layout
