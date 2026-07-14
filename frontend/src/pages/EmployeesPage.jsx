import { useCallback, useEffect, useState } from 'react'
import Layout from '../components/Layout'
import SearchBar from '../components/SearchBar'
import EmployeeTable from '../components/EmployeeTable'
import EmployeeForm from '../components/EmployeeForm'
import { employeeService } from '../services/employeeService'

const emptyForm = {
  firstName: '',
  lastName: '',
  email: '',
  department: '',
  jobTitle: '',
  hireDate: '',
  salary: '',
}

/**
 * EmployeesPage — full CRUD: list, search, add, update, delete.
 */
function EmployeesPage() {
  const [employees, setEmployees] = useState([])
  const [searchKeyword, setSearchKeyword] = useState('')
  const [loading, setLoading] = useState(true)
  const [formLoading, setFormLoading] = useState(false)
  const [showForm, setShowForm] = useState(false)
  const [isEditing, setIsEditing] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const [formData, setFormData] = useState(emptyForm)
  const [message, setMessage] = useState({ type: '', text: '' })
  const [deleteTarget, setDeleteTarget] = useState(null)

  const loadEmployees = useCallback(async (keyword = '') => {
    setLoading(true)
    try {
      const data = await employeeService.getAll(keyword)
      setEmployees(data)
    } catch (err) {
      setMessage({ type: 'danger', text: err.message || 'Failed to load employees' })
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    loadEmployees()
  }, [loadEmployees])

  const handleSearch = (keyword) => {
    loadEmployees(keyword)
  }

  const openAddForm = () => {
    setFormData(emptyForm)
    setIsEditing(false)
    setEditingId(null)
    setShowForm(true)
    setMessage({ type: '', text: '' })
  }

  const openEditForm = (employee) => {
    setFormData({
      firstName: employee.firstName,
      lastName: employee.lastName,
      email: employee.email,
      department: employee.department,
      jobTitle: employee.jobTitle,
      hireDate: employee.hireDate,
      salary: employee.salary,
    })
    setIsEditing(true)
    setEditingId(employee.id)
    setShowForm(true)
    setMessage({ type: '', text: '' })
  }

  const closeForm = () => {
    setShowForm(false)
    setFormData(emptyForm)
    setIsEditing(false)
    setEditingId(null)
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setFormLoading(true)
    setMessage({ type: '', text: '' })
    try {
      if (isEditing) {
        await employeeService.update(editingId, formData)
        setMessage({ type: 'success', text: 'Employee updated successfully!' })
      } else {
        await employeeService.create(formData)
        setMessage({ type: 'success', text: 'Employee added successfully!' })
      }
      closeForm()
      loadEmployees(searchKeyword)
    } catch (err) {
      setMessage({
        type: 'danger',
        text: err.response?.data?.message || err.message || 'Operation failed',
      })
    } finally {
      setFormLoading(false)
    }
  }

  const handleDelete = async () => {
    if (!deleteTarget) return
    try {
      await employeeService.delete(deleteTarget.id)
      setMessage({ type: 'success', text: 'Employee deleted successfully!' })
      setDeleteTarget(null)
      loadEmployees(searchKeyword)
    } catch (err) {
      setMessage({
        type: 'danger',
        text: err.response?.data?.message || err.message || 'Delete failed',
      })
    }
  }

  return (
    <Layout title="Employees">
      {message.text && (
        <div className={`alert alert-${message.type} alert-dismissible`} role="alert">
          {message.text}
          <button type="button" className="btn-close" onClick={() => setMessage({ type: '', text: '' })}></button>
        </div>
      )}

      <div className="card table-card mb-4">
        <div className="card-body">
          <div className="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
            <h5 className="card-title mb-0">Employee List</h5>
            <button className="btn btn-primary" onClick={openAddForm}>
              <i className="bi bi-person-plus me-1"></i>
              Add Employee
            </button>
          </div>
          <SearchBar
            value={searchKeyword}
            onChange={setSearchKeyword}
            onSearch={handleSearch}
          />
        </div>
      </div>

      <div className="card table-card mb-4">
        <div className="card-body p-0">
          <EmployeeTable
            employees={employees}
            onEdit={openEditForm}
            onDelete={setDeleteTarget}
            loading={loading}
          />
        </div>
      </div>

      {/* Add/Edit Modal */}
      {showForm && (
        <div className="modal show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog modal-lg modal-dialog-centered">
            <div className="modal-content">
              <div className="modal-header">
                <h5 className="modal-title">
                  {isEditing ? 'Update Employee' : 'Add New Employee'}
                </h5>
                <button type="button" className="btn-close" onClick={closeForm}></button>
              </div>
              <div className="modal-body">
                <EmployeeForm
                  formData={formData}
                  onChange={setFormData}
                  onSubmit={handleSubmit}
                  onCancel={closeForm}
                  isEditing={isEditing}
                  loading={formLoading}
                />
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Delete Confirmation Modal */}
      {deleteTarget && (
        <div className="modal show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content">
              <div className="modal-header">
                <h5 className="modal-title">Confirm Delete</h5>
                <button type="button" className="btn-close" onClick={() => setDeleteTarget(null)}></button>
              </div>
              <div className="modal-body">
                Are you sure you want to delete{' '}
                <strong>
                  {deleteTarget.firstName} {deleteTarget.lastName}
                </strong>
                ?
              </div>
              <div className="modal-footer">
                <button className="btn btn-secondary" onClick={() => setDeleteTarget(null)}>
                  Cancel
                </button>
                <button className="btn btn-danger" onClick={handleDelete}>
                  Delete
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </Layout>
  )
}

export default EmployeesPage
