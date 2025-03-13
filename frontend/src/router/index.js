import Vue from 'vue';
import VueRouter from 'vue-router';
import auth from '@/utils/auth';

Vue.use(VueRouter);

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', public: true, requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layout/Layout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: '/tasks',
        name: 'TaskList',
        component: () => import('@/views/tasks/TaskList.vue'),
        meta: { title: '任务列表' }
      },
      {
        path: '/tasks/edit/:id?',
        name: 'TaskEdit',
        component: () => import('@/views/tasks/TaskEdit.vue'),
        meta: { title: '任务编辑' }
      },
      {
        path: '/dags',
        name: 'DagList',
        component: () => import('@/views/dags/DagList.vue'),
        meta: { title: 'DAG列表' }
      },
      {
        path: '/dags/edit/:id?',
        name: 'DagEdit',
        component: () => import('@/views/dags/DagEdit.vue'),
        meta: { title: 'DAG编辑' }
      },
      {
        path: '/executions',
        name: 'ExecutionList',
        component: () => import('@/views/executions/ExecutionList.vue'),
        meta: { title: '执行记录' }
      },
      {
        path: '/executions/dags/:id',
        name: 'DagExecutionDetail',
        component: () => import('@/views/executions/DagExecutionDetail.vue'),
        props: true
      }
    ]
  }
];

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
});

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = to.meta.title || '任务调度系统'
  
  // 检查路由是否需要认证
  if (to.matched.some(record => record.meta.requiresAuth !== false)) {
    const token = auth.getToken()
    
    if (!token) {
      console.log('No token found, redirecting to login')
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }
  }
  
  next()
})

export default router;
