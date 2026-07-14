import api, { isMockMode } from './api'

/**
 * Checks if the Spring Boot backend is reachable.
 * Used on the login page to show connection status.
 */
export async function checkBackendHealth() {
  if (isMockMode()) {
    return { connected: false, mode: 'mock' }
  }

  try {
    const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
    const healthUrl = baseUrl.replace('/api', '/actuator/health')
    const response = await fetch(healthUrl)
    const data = await response.json()
    return { connected: data.status === 'UP', mode: 'live', status: data.status }
  } catch {
    return { connected: false, mode: 'live', status: 'DOWN' }
  }
}
