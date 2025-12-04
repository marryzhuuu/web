import { useStore } from '@/store'


export const useAuthStore = () => {
  const store = useStore()
  return {
    isAuthenticated: store.state.isAuthenticated,
    user: store.state.user,
    token: store.state.token,
    login: (credentials) => store.dispatch('login', credentials),
    register: (credentials) => store.dispatch('register', credentials),
    logout: () => store.dispatch('logout'),
    initializeAuth: () => store.dispatch('initializeAuth')
  }
}