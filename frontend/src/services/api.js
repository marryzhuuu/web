import axios from 'axios'

const API_BASE_URL = '/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Перехватчик для добавления username в заголовки
api.interceptors.request.use((config) => {
  const username = localStorage.getItem('username')
  if (username) {
    config.headers['X-Username'] = username
  }
  return config
})

export const authAPI = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (credentials) => api.post('/auth/register', credentials)
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