import axios from 'axios'
import router from '@/router'
import auth from '@/utils/auth'
import { Message } from 'element-ui'

const http = axios.create({
  timeout: 5000
})

// 请求拦截器
http.interceptors.request.use(
  config => {
    const token = auth.getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器
http.interceptors.response.use(
  response => {
    console.log('Response:', response)  // 调试日志
    const res = response.data

    // 处理业务状态码
    if (res.code === 401) {
      console.log('Business code 401 detected')  // 调试日志
      handleUnauthorized()
      return Promise.reject(new Error(res.message || '未登录或登录已过期'))
    }

    return res
  },
  error => {
    console.log('Response error:', error)  // 调试日志
    console.log('Response error status:', error.response?.status)  // 调试日志

    // 处理 HTTP 错误状态码
    if (error.response) {
      if (error.response.status === 401) {
        console.log('HTTP status 401 detected')  // 调试日志
        handleUnauthorized()
        return Promise.reject(new Error('未登录或登录已过期'))
      }
      Message.error(error.response.data?.message || '请求失败')
    } else {
      Message.error('网络错误，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

function handleUnauthorized() {
  const currentPath = router.currentRoute.fullPath
  console.log('Handling unauthorized access, current path:', currentPath)
  
  if (currentPath !== '/login') {
    // 清除认证信息
    auth.clearAuth()
    
    // 保存当前路径
    auth.setLastPath(currentPath)
    
    // 提示用户
    Message.warning('登录已过期，请重新登录')
    
    // 跳转到登录页
    router.replace({
      path: '/login',
      query: { redirect: currentPath }
    })
  }
}

export default http
