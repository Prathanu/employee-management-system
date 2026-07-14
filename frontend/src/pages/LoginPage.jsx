import { useEffect, useState } from 'react'
import { useNavigate, Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { isMockMode } from '../services/api'
import { checkBackendHealth } from '../services/healthService'

/**
 * LoginPage — authenticates against Spring Boot API (Step 4).
 */
function LoginPage() {
  const [username, setUsername] = useState('admin')
  const [password, setPassword] = useState('admin123')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [backendStatus, setBackendStatus] = useState(null)
  const { login, isAuthenticated } = useAuth()
  const navigate = useNavigate()

  useEffect(() => {
    checkBackendHealth().then(setBackendStatus)
  }, [])

  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await login(username, password)
      navigate('/dashboard')
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Login failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-page d-flex align-items-center justify-content-center p-3">
      <div className="card login-card" style={{ maxWidth: '420px', width: '100%' }}>
        <div className="card-body p-4 p-md-5">
          <div className="text-center mb-4">
            <i className="bi bi-people-fill text-primary" style={{ fontSize: '3rem' }}></i>
            <h3 className="mt-2 fw-bold">Employee Management</h3>
            <p className="text-muted">Sign in to your account</p>
          </div>

          {isMockMode() ? (
            <div className="alert alert-info small">
              <strong>Mock mode:</strong> Use <code>admin</code> / <code>admin123</code>
            </div>
          ) : backendStatus && (
            <div className={`alert small py-2 ${backendStatus.connected ? 'alert-success' : 'alert-danger'}`}>
              {backendStatus.connected ? (
                <>
                  <i className="bi bi-check-circle me-1"></i>
                  Backend connected (Spring Boot :8080)
                </>
              ) : (
                <>
                  <i className="bi bi-x-circle me-1"></i>
                  Backend not reachable. Start: <code>mvn spring-boot:run</code>
                </>
              )}
            </div>
          )}

          {error && (
            <div className="alert alert-danger py-2" role="alert">
              <i className="bi bi-exclamation-triangle me-1"></i>
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className="form-label">Username</label>
              <input
                type="text"
                className="form-control form-control-lg"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Enter username"
                required
                autoFocus
              />
            </div>
            <div className="mb-4">
              <label className="form-label">Password</label>
              <input
                type="password"
                className="form-control form-control-lg"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter password"
                required
              />
            </div>
            <button
              type="submit"
              className="btn btn-primary btn-lg w-100"
              disabled={loading || (!isMockMode() && backendStatus && !backendStatus.connected)}
            >
              {loading ? 'Signing in...' : 'Sign In'}
            </button>
          </form>
        </div>
      </div>
    </div>
  )
}

export default LoginPage
