/**
 * EmployeeForm — add/edit employee form with validation.
 */
function EmployeeForm({ formData, onChange, onSubmit, onCancel, isEditing, loading }) {
  const handleChange = (e) => {
    const { name, value } = e.target
    onChange({ ...formData, [name]: name === 'salary' ? Number(value) : value })
  }

  return (
    <form onSubmit={onSubmit}>
      <div className="row g-3">
        <div className="col-md-6">
          <label className="form-label">First Name *</label>
          <input
            type="text"
            name="firstName"
            className="form-control"
            value={formData.firstName}
            onChange={handleChange}
            required
          />
        </div>
        <div className="col-md-6">
          <label className="form-label">Last Name *</label>
          <input
            type="text"
            name="lastName"
            className="form-control"
            value={formData.lastName}
            onChange={handleChange}
            required
          />
        </div>
        <div className="col-md-6">
          <label className="form-label">Email *</label>
          <input
            type="email"
            name="email"
            className="form-control"
            value={formData.email}
            onChange={handleChange}
            required
          />
        </div>
        <div className="col-md-6">
          <label className="form-label">Department *</label>
          <input
            type="text"
            name="department"
            className="form-control"
            value={formData.department}
            onChange={handleChange}
            required
          />
        </div>
        <div className="col-md-6">
          <label className="form-label">Job Title *</label>
          <input
            type="text"
            name="jobTitle"
            className="form-control"
            value={formData.jobTitle}
            onChange={handleChange}
            required
          />
        </div>
        <div className="col-md-6">
          <label className="form-label">Hire Date *</label>
          <input
            type="date"
            name="hireDate"
            className="form-control"
            value={formData.hireDate}
            onChange={handleChange}
            required
          />
        </div>
        <div className="col-md-6">
          <label className="form-label">Salary *</label>
          <input
            type="number"
            name="salary"
            className="form-control"
            value={formData.salary}
            onChange={handleChange}
            min="1"
            required
          />
        </div>
      </div>

      <div className="d-flex gap-2 mt-4">
        <button type="submit" className="btn btn-primary" disabled={loading}>
          {loading ? 'Saving...' : isEditing ? 'Update Employee' : 'Add Employee'}
        </button>
        <button type="button" className="btn btn-outline-secondary" onClick={onCancel}>
          Cancel
        </button>
      </div>
    </form>
  )
}

export default EmployeeForm
