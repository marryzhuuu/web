import { authAPI } from './api'

export const authService = {
  async login(username, password) {
    try {
      const response = await authAPI.login({ username, password })

      if (response.data.success) {
        localStorage.setItem('username', username)
        return { success: true, data: response.data }
      } else {
        return { success: false, error: response.data.error }
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

      if (response.data.success) {
        localStorage.setItem('username', username)
        return { success: true, data: response.data }
      } else {
        return { success: false, error: response.data.error }
      }
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.error || 'Ошибка соединения с сервером'
      }
    }
  },

  logout() {
    localStorage.removeItem('username')
  },

  getStoredUsername() {
    return localStorage.getItem('username')
  }
}