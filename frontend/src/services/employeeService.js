import api, { isMockMode } from './api'
import { mockEmployeeApi } from './mockData'

/**
 * Employee CRUD service.
 * All employee API calls go through this file — easy to swap mock vs real backend.
 */
export const employeeService = {
  async getAll(keyword = '') {
    if (isMockMode()) {
      return mockEmployeeApi.getAll(keyword)
    }
    const response = await api.get('/employees', { params: { keyword: keyword || undefined } })
    return response.data
  },

  async getById(id) {
    if (isMockMode()) {
      return mockEmployeeApi.getById(id)
    }
    const response = await api.get(`/employees/${id}`)
    return response.data
  },

  async create(employee) {
    if (isMockMode()) {
      return mockEmployeeApi.create(employee)
    }
    const response = await api.post('/employees', employee)
    return response.data
  },

  async update(id, employee) {
    if (isMockMode()) {
      return mockEmployeeApi.update(id, employee)
    }
    const response = await api.put(`/employees/${id}`, employee)
    return response.data
  },

  async delete(id) {
    if (isMockMode()) {
      return mockEmployeeApi.delete(id)
    }
    await api.delete(`/employees/${id}`)
  },

  async getStats() {
    if (isMockMode()) {
      const employees = mockEmployeeApi.getAll()
      return {
        totalEmployees: employees.length,
        departments: mockEmployeeApi.departments().length,
        avgSalary: employees.length
          ? Math.round(employees.reduce((s, e) => s + e.salary, 0) / employees.length)
          : 0,
      }
    }
    const employees = await this.getAll()
    return {
      totalEmployees: employees.length,
      departments: new Set(employees.map((e) => e.department)).size,
      avgSalary: employees.length
        ? Math.round(employees.reduce((s, e) => s + e.salary, 0) / employees.length)
        : 0,
    }
  },
}
