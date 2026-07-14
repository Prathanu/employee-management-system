import { createContext, useContext, useState, useEffect } from 'react'
import { authService } from '../services/authService'

/**
 * AuthContext — global authentication state shared across all components.
 * Stores token + user info in localStorage so login persists on page refresh.
 */
const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  // Restore session from localStorage on app load
  useEffect(() => {
    const token = localStorage.getItem('token')
    const savedUser = localStorage.getItem('user')
    if (token && savedUser) {
      setUser(JSON.parse(savedUser))
    }
    setLoading(false)
  }, [])

  const login = async (username, password) => {
    const data = await authService.login(username, password)
    localStorage.setItem('token', data.token)
    const userInfo = { username: data.username, role: data.role }
    localStorage.setItem('user', JSON.stringify(userInfo))
    setUser(userInfo)
    return data
  }

  const logout = () => {
    authService.logout()
    setUser(null)
  }

  const value = {
    user,
    isAuthenticated: !!user,
    loading,
    login,
    logout,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider')
  }
  return context
}
