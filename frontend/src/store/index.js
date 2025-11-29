import { createStore } from 'vuex'

export default createStore({
  state: {
    user: null,
    isAuthenticated: false,
    pointChecks: []
  },
  mutations: {
    SET_USER(state, user) {
      state.user = user
      state.isAuthenticated = true
    },
    CLEAR_USER(state) {
      state.user = null
      state.isAuthenticated = false
    },
    SET_POINT_CHECKS(state, checks) {
      state.pointChecks = checks
    },
    ADD_POINT_CHECK(state, check) {
      state.pointChecks.unshift(check)
    }
  },
  actions: {
    login({ commit }, user) {
      commit('SET_USER', user)
    },
    logout({ commit }) {
      commit('CLEAR_USER')
      commit('SET_POINT_CHECKS', [])
    },
    setPointChecks({ commit }, checks) {
      commit('SET_POINT_CHECKS', checks)
    },
    addPointCheck({ commit }, check) {
      commit('ADD_POINT_CHECK', check)
    }
  },
  getters: {
    isAuthenticated: state => state.isAuthenticated,
    user: state => state.user,
    pointChecks: state => state.pointChecks
  }
})

// Для использования в компонентах
import { inject } from 'vue'
export const useStore = () => inject('store')
export const useAuthStore = () => {
  const store = useStore()
  return {
    isAuthenticated: store.state.isAuthenticated,
    user: store.state.user,
    login: (user) => store.dispatch('login', user),
    logout: () => store.dispatch('logout')
  }
}