import Vue from 'vue';
import Vuex from 'vuex';
import axios from 'axios';

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    user: null,
    tasks: [],
    dags: [],
    currentDag: null
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
    },
    SET_CURRENT_DAG(state, dag) {
      state.currentDag = dag;
    }
  },
  actions: {
    async login({ commit }, credentials) {
      const { data } = await axios.post('/api/auth/login', credentials);
      localStorage.setItem('token', data.token);
      commit('SET_USER', data.user);
    },
    async fetchTasks({ commit }) {
      const { data } = await axios.get('/api/tasks');
      commit('SET_TASKS', data);
    },
    async fetchDags({ commit }) {
      const { data } = await axios.get('/api/dags');
      commit('SET_DAGS', data);
    }
  }
});
