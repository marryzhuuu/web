import axios from 'axios'

const API_BASE_URL = '/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Перехватчик для добавления токена
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`
  }
  return config
})

// Перехватчик для обработки ответов
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response) {
      if (error.response.status === 401) {
        // Токен невалиден или истек
        console.warn('Token expired or invalid')
        localStorage.removeItem('token')
        localStorage.removeItem('user')

        // Редирект на логин только если не на странице логина
        if (!window.location.pathname.includes('/')) {
          window.location.href = '/'
        }
      }
    }
    return Promise.reject(error)
  }
)

export const authAPI = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (credentials) => api.post('/auth/register', credentials),
  validateToken: () => api.get('/auth/validate')
}

export const pointsAPI = {
  checkPoint: (pointData) => api.post('/points/check', pointData),
  getHistory: (radius) => {
    if(radius) {
      return api.get(`/points/history?r=${radius}`)
    }
    return api.get('/points/history')
  }
}

export default api