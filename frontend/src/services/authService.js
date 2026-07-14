import api, { isMockMode } from './api'
import { mockAuth } from './mockData'

/**
 * Authentication service — login and logout.
 * Uses mock data in Step 2; switches to real API in Step 4.
 */
export const authService = {
  async login(username, password) {
    if (isMockMode()) {
      return mockAuth.login(username, password)
    }
    const response = await api.post('/auth/login', { username, password })
    return response.data
  },

  logout() {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  },
}
