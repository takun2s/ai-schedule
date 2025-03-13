import axios from 'axios'
import { Message } from 'element-ui'
import router from '@/router'

const service = axios.create({
  baseURL: process.env.VUE_APP_API_URL || '',
  timeout: 5000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
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
service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code && res.code !== 200) {
      Message.error(res.message || '请求失败')
      if (res.code === 401) {
        // 清除token并跳转登录页
        localStorage.removeItem('token')
        router.push('/login')
      }
      return Promise.reject(res)
    }
    return res
  },
  error => {
    console.error('Response error:', error)
    let errorMessage = '请求失败'
    
    if (error.response) {
      switch (error.response.status) {
        case 401:
          localStorage.removeItem('token')
          router.push('/login')
          break
        case 403:
          Message.error('没有权限访问')
          break
        default:
          if (error.response.data && error.response.data.message) {
            errorMessage = error.response.data.message
          }
          Message.error(errorMessage)
      }
    } else {
      Message.error('网络错误')
    }
    return Promise.reject(error)
  }
)

export default service
