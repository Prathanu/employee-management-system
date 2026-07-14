/**
 * EmployeeTable — displays employee list with Edit/Delete actions.
 */
function EmployeeTable({ employees, onEdit, onDelete, loading }) {
  if (loading) {
    return (
      <div className="text-center py-5">
        <div className="spinner-border text-primary" role="status"></div>
      </div>
    )
  }

  if (!employees.length) {
    return (
      <div className="alert alert-info mb-0">
        <i className="bi bi-info-circle me-2"></i>
        No employees found. Add a new employee to get started.
      </div>
    )
  }

  return (
    <div className="table-responsive">
      <table className="table table-hover align-middle mb-0">
        <thead className="table-light">
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Department</th>
            <th>Job Title</th>
            <th>Hire Date</th>
            <th>Salary</th>
            <th className="text-end">Actions</th>
          </tr>
        </thead>
        <tbody>
          {employees.map((emp) => (
            <tr key={emp.id}>
              <td>{emp.id}</td>
              <td>
                <strong>
                  {emp.firstName} {emp.lastName}
                </strong>
              </td>
              <td>{emp.email}</td>
              <td>
                <span className="badge bg-primary-subtle text-primary">{emp.department}</span>
              </td>
              <td>{emp.jobTitle}</td>
              <td>{emp.hireDate}</td>
              <td>${emp.salary?.toLocaleString()}</td>
              <td className="text-end">
                <button
                  className="btn btn-sm btn-outline-primary me-1"
                  onClick={() => onEdit(emp)}
                  title="Edit"
                >
                  <i className="bi bi-pencil"></i>
                </button>
                <button
                  className="btn btn-sm btn-outline-danger"
                  onClick={() => onDelete(emp)}
                  title="Delete"
                >
                  <i className="bi bi-trash"></i>
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default EmployeeTable
