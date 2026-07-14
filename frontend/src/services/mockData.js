/**
 * In-memory mock data used when VITE_USE_MOCK=true.
 * Allows full UI testing before Spring Boot backend is available (Steps 2–3).
 */
let mockEmployees = [
  {
    id: 1,
    firstName: 'John',
    lastName: 'Doe',
    email: 'john.doe@company.com',
    department: 'Engineering',
    jobTitle: 'Software Engineer',
    hireDate: '2022-01-15',
    salary: 75000,
  },
  {
    id: 2,
    firstName: 'Sarah',
    lastName: 'Wilson',
    email: 'sarah.wilson@company.com',
    department: 'HR',
    jobTitle: 'HR Manager',
    hireDate: '2021-06-01',
    salary: 68000,
  },
]

let nextId = 3

export const mockAuth = {
  login(username, password) {
    const validUser = import.meta.env.VITE_MOCK_USERNAME || 'admin'
    const validPass = import.meta.env.VITE_MOCK_PASSWORD || 'admin123'

    if (username === validUser && password === validPass) {
      return {
        token: 'mock-jwt-token-' + Date.now(),
        username,
        role: 'ADMIN',
        message: 'Login successful (mock mode)',
      }
    }
    throw new Error('Invalid username or password')
  },
}

export const mockEmployeeApi = {
  getAll(keyword = '') {
    if (!keyword.trim()) return [...mockEmployees]
    const k = keyword.toLowerCase()
    return mockEmployees.filter(
      (e) =>
        e.firstName.toLowerCase().includes(k) ||
        e.lastName.toLowerCase().includes(k) ||
        e.email.toLowerCase().includes(k) ||
        e.department.toLowerCase().includes(k) ||
        e.jobTitle.toLowerCase().includes(k),
    )
  },

  getById(id) {
    const emp = mockEmployees.find((e) => e.id === Number(id))
    if (!emp) throw new Error('Employee not found')
    return emp
  },

  create(data) {
    if (mockEmployees.some((e) => e.email === data.email)) {
      throw new Error('Employee with this email already exists')
    }
    const employee = { id: nextId++, ...data }
    mockEmployees.push(employee)
    return employee
  },

  update(id, data) {
    const index = mockEmployees.findIndex((e) => e.id === Number(id))
    if (index === -1) throw new Error('Employee not found')
    if (mockEmployees.some((e) => e.email === data.email && e.id !== Number(id))) {
      throw new Error('Employee with this email already exists')
    }
    mockEmployees[index] = { ...mockEmployees[index], ...data, id: Number(id) }
    return mockEmployees[index]
  },

  delete(id) {
    const index = mockEmployees.findIndex((e) => e.id === Number(id))
    if (index === -1) throw new Error('Employee not found')
    mockEmployees.splice(index, 1)
  },

  count() {
    return mockEmployees.length
  },

  departments() {
    return [...new Set(mockEmployees.map((e) => e.department))]
  },
}
