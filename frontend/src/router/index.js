import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/tasks',
    name: 'tasks',
    component: () => import('../views/Tasks.vue')
  },
  {
    path: '/logs',
    name: 'logs',
    component: () => import('../views/Logs.vue')
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/LoginView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router 