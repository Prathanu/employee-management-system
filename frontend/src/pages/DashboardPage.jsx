import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import Layout from '../components/Layout'
import { employeeService } from '../services/employeeService'

/**
 * DashboardPage — overview with key statistics after login.
 */
function DashboardPage() {
  const [stats, setStats] = useState({ totalEmployees: 0, departments: 0, avgSalary: 0 })
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const loadStats = async () => {
      try {
        const data = await employeeService.getStats()
        setStats(data)
      } catch (err) {
        console.error('Failed to load stats', err)
      } finally {
        setLoading(false)
      }
    }
    loadStats()
  }, [])

  return (
    <Layout title="Dashboard">
      <p className="text-muted mb-4">Welcome to the Employee Management System</p>

      <div className="row g-4 mb-4">
        <div className="col-md-4">
          <div className="card stat-card h-100">
            <div className="card-body">
              <div className="d-flex justify-content-between align-items-center">
                <div>
                  <p className="text-muted mb-1">Total Employees</p>
                  <h2 className="fw-bold mb-0">{loading ? '...' : stats.totalEmployees}</h2>
                </div>
                <i className="bi bi-people fs-1 text-primary opacity-50"></i>
              </div>
            </div>
          </div>
        </div>
        <div className="col-md-4">
          <div className="card stat-card h-100">
            <div className="card-body">
              <div className="d-flex justify-content-between align-items-center">
                <div>
                  <p className="text-muted mb-1">Departments</p>
                  <h2 className="fw-bold mb-0">{loading ? '...' : stats.departments}</h2>
                </div>
                <i className="bi bi-building fs-1 text-success opacity-50"></i>
              </div>
            </div>
          </div>
        </div>
        <div className="col-md-4">
          <div className="card stat-card h-100">
            <div className="card-body">
              <div className="d-flex justify-content-between align-items-center">
                <div>
                  <p className="text-muted mb-1">Avg. Salary</p>
                  <h2 className="fw-bold mb-0">
                    {loading ? '...' : `$${stats.avgSalary.toLocaleString()}`}
                  </h2>
                </div>
                <i className="bi bi-currency-dollar fs-1 text-warning opacity-50"></i>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="card table-card">
        <div className="card-body">
          <h5 className="card-title">Quick Actions</h5>
          <div className="d-flex flex-wrap gap-2">
            <Link to="/employees" className="btn btn-primary">
              <i className="bi bi-person-plus me-1"></i>
              Manage Employees
            </Link>
            <Link to="/employees" className="btn btn-outline-primary">
              <i className="bi bi-search me-1"></i>
              Search Employees
            </Link>
          </div>
        </div>
      </div>
    </Layout>
  )
}

export default DashboardPage
