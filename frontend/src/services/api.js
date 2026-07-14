import axios from 'axios'

/**
 * Axios instance — single HTTP client for all API calls.
 * Base URL comes from .env (VITE_API_BASE_URL).
 * Request interceptor attaches JWT token from localStorage.
 */
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 15000,
})

// Attach Bearer token to every request if user is logged in
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error),
)

// Handle 401/403 globally — redirect to login (token expired or missing)
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status
    const isLoginRequest = error.config?.url?.includes('/auth/login')

    if ((status === 401 || status === 403) && !isLoginRequest) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  },
)

export const isMockMode = () => import.meta.env.VITE_USE_MOCK === 'true'

export default api
