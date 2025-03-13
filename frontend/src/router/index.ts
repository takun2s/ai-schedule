import Vue from 'vue';
import VueRouter from 'vue-router';
import Login from '../views/Login.vue';
import Dashboard from '../views/Dashboard.vue';
import TaskList from '../views/TaskList.vue';
import DagList from '../views/DagList.vue';
import DagEditor from '../views/DagEditor.vue';

Vue.use(VueRouter);

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { public: true }
  },
  {
    path: '/',
    name: 'Dashboard',
    component: Dashboard
  },
  {
    path: '/tasks',
    name: 'TaskList',
    component: TaskList
  },
  {
    path: '/dags',
    name: 'DagList',
    component: DagList
  },
  {
    path: '/dags/editor/:id?',
    name: 'DagEditor',
    component: DagEditor
  }
];

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
});

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  if (!to.meta.public && !token) {
    next('/login');
  } else {
    next();
  }
});

export default router;
