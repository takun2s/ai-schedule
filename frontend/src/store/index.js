import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    user: null,
    tasks: [],
    dags: []
  },
  mutations: {
    SET_USER(state, user) {
      state.user = user;
    },
    SET_TASKS(state, tasks) {
      state.tasks = tasks;
    },
    SET_DAGS(state, dags) {
      state.dags = dags;
    }
  },
  actions: {
    login({ commit }, user) {
      commit('SET_USER', user);
    },
    logout({ commit }) {
      commit('SET_USER', null);
    }
  },
  modules: {
  }
})
