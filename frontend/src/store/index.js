import { createStore } from 'vuex'
import { authService } from '@/services/auth'
import {inject} from "vue";

export const useStore = () => inject('store')

export default createStore({
  state: {
    token: localStorage.getItem('token'),
    user: authService.getStoredUser(),
    isAuthenticated: authService.isAuthenticated(),
    pointChecks: [],
    drawPointChecks: []
  },
  mutations: {
    SET_AUTH(state, { token, user }) {
      state.token = token
      state.user = user
      state.isAuthenticated = true
      localStorage.setItem('token', token)
      localStorage.setItem('user', JSON.stringify(user))
    },
    CLEAR_AUTH(state) {
      state.token = null
      state.user = null
      state.isAuthenticated = false
      authService.logout()
    },
    SET_POINT_CHECKS(state, checks) {
      state.pointChecks = checks
    },
    ADD_POINT_CHECK(state, check) {
      state.pointChecks.unshift(check)
    },
    SET_DRAW_POINT_CHECKS(state, checks) {
      state.drawPointChecks = checks
    },
    ADD_DRAW_POINT_CHECK(state, check) {
      state.drawPointChecks.unshift(check)
    }
  },
  actions: {
    async login({ commit }, { username, password }) {
      const result = await authService.login(username, password)
      if (result.success) {
        commit('SET_AUTH', {
          token: result.data.token,
          user: result.data.user
        })
        return { success: true }
      }
      return { success: false, error: result.error }
    },

    async register({ commit }, { username, password }) {
      const result = await authService.register(username, password)
      if (result.success) {
        commit('SET_AUTH', {
          token: result.data.token,
          user: result.data.user
        })
        return { success: true }
      }
      return { success: false, error: result.error }
    },

    async initializeAuth({ commit, dispatch }) {
      const token = localStorage.getItem('token')
      if (token) {
        // Проверяем токен на сервере
        const result = await authService.validateToken()
        if (result.valid) {
          commit('SET_AUTH', {
            token,
            user: result.user
          })
          return true
        } else {
          // Токен невалиден - очищаем
          dispatch('logout')
          return false
        }
      }
      return false
    },

    logout({ commit }) {
      commit('CLEAR_AUTH')
      commit('SET_POINT_CHECKS', [])
      commit('SET_DRAW_POINT_CHECKS', [])
    },
    setPointChecks({ commit }, checks) {
      commit('SET_POINT_CHECKS', checks)
    },
    addPointCheck({ commit }, check) {
      commit('ADD_POINT_CHECK', check)
    },
    setDrawPointChecks({ commit }, checks) {
      commit('SET_DRAW_POINT_CHECKS', checks)
    },
    addDrawPointCheck({ commit }, check) {
      commit('ADD_DRAW_POINT_CHECK', check)
    }
  },
  getters: {
    isAuthenticated: state => state.isAuthenticated,
    user: state => state.user,
    pointChecks: state => state.pointChecks,
    drawPointChecks: state => state.drawPointChecks
  }
})

// Для использования в компонентах
// import { inject } from 'vue'
// export const useStore = () => inject('store')
// export const useAuthStore = () => {
//   const store = useStore()
//   return {
//     isAuthenticated: store.state.isAuthenticated,
//     user: store.state.user,
//     login: (user) => store.dispatch('login', user),
//     logout: () => store.dispatch('logout')
//   }
// }