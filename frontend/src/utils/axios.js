import axios from 'axios'
import { Message } from 'element-ui'
import router from '@/router'
import auth from './auth'

// 创建axios实例
const instance = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 15000,
  withCredentials: true
})

// 请求拦截器
instance.interceptors.request.use(
  config => {
    const token = auth.getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
instance.interceptors.response.use(
  response => {
    const res = response.data
    console.log('Response:', res)  // 调试日志

    // 处理业务状态码
    if (res.code === 401) {
      handleUnauthorized()
      return Promise.reject(new Error(res.message || '未登录或登录已过期'))
    }
    
    if (res.code !== 200) {
      Message.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }

    return res
  },
  error => {
    console.log('Response error:', error.response)  // 调试日志

    if (error.response && error.response.status === 401) {
      handleUnauthorized()
      return Promise.reject(new Error('未登录或登录已过期'))
    }

    Message.error(error.message || '请求失败')
    return Promise.reject(error)
  }
)

// 统一处理未授权情况
function handleUnauthorized() {
  const currentPath = router.currentRoute.fullPath
  console.log('Handling unauthorized, current path:', currentPath)

  if (currentPath !== '/login') {
    auth.clearAuth()
    auth.setLastPath(currentPath)
    
    Message.warning('请重新登录')
    
    router.replace({
      path: '/login',
      query: { redirect: currentPath }
    })
  }
}

export default instance
