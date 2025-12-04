import { authAPI } from './api'

export const authService = {
  async login(username, password) {
    try {
      const response = await authAPI.login({ username, password })

      if (response.data.success && response.data.token) {
        localStorage.setItem('token', response.data.token)
        localStorage.setItem('user', JSON.stringify(response.data.user))
        return { success: true, data: response.data }
      } else {
        return { success: false, error: response.data.error || 'Login failed' }
      }
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.error || 'Ошибка соединения с сервером'
      }
    }
  },

  async register(username, password) {
    try {
      const response = await authAPI.register({ username, password })

      if (response.data.success && response.data.token) {
        localStorage.setItem('token', response.data.token)
        localStorage.setItem('user', JSON.stringify(response.data.user))
        return { success: true, data: response.data }
      } else {
        return { success: false, error: response.data.error || 'Registration failed' }
      }
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.error || 'Ошибка соединения с сервером'
      }
    }
  },

  async validateToken() {
    try {
      const token = localStorage.getItem('token')
      if (!token) {
        return { valid: false }
      }

      const response = await authAPI.validateToken()
      return { valid: true, user: response.data.user }
    } catch (error) {
      return { valid: false }
    }
  },

  logout() {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    localStorage.removeItem('pointForm')
  },

  getStoredUser() {
    const userStr = localStorage.getItem('user')
    return userStr ? JSON.parse(userStr) : null
  },

  getToken() {
    return localStorage.getItem('token')
  },

  isAuthenticated() {
    return !!localStorage.getItem('token')
  }
}