import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res.data
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // 未授权，清除token并跳转到登录页
          localStorage.removeItem('token')
          window.location.href = '/login'
          break
        case 403:
          ElMessage.error('没有权限访问')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器错误')
          break
        default:
          ElMessage.error(error.response.data.message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

// 用户相关接口
export const userApi = {
  login: (data) => api.post('/user/login', data),
  logout: () => api.post('/user/logout'),
  getInfo: () => api.get('/user/info'),
  updatePassword: (data) => api.put('/user/password', data)
}

// 任务相关接口
export const taskApi = {
  // 获取任务列表
  getTasks: (params) => api.get('/tasks', { params }),
  
  // 创建任务
  createTask: (data) => api.post('/tasks', data),
  
  // 更新任务
  updateTask: (id, data) => api.put(`/tasks/${id}`, data),
  
  // 删除任务
  deleteTask: (id) => api.delete(`/tasks/${id}`),
  
  // 启动任务
  startTask: (id) => api.post(`/tasks/${id}/start`),
  
  // 停止任务
  stopTask: (id) => api.post(`/tasks/${id}/stop`)
}

// 执行记录相关接口
export const executionApi = {
  getList: (params) => api.get('/executions', { params }),
  getById: (id) => api.get(`/executions/${id}`),
  getLogs: (id) => api.get(`/executions/${id}/logs`)
}

// 告警相关接口
export const alertApi = {
  getList: (params) => api.get('/alerts', { params }),
  getById: (id) => api.get(`/alerts/${id}`),
  retry: (id) => api.post(`/alerts/${id}/retry`)
}

// 系统设置相关接口
export const settingApi = {
  getBasic: () => api.get('/settings/basic'),
  updateBasic: (data) => api.put('/settings/basic', data),
  getEmail: () => api.get('/settings/email'),
  updateEmail: (data) => api.put('/settings/email', data),
  testEmail: (data) => api.post('/settings/email/test', data),
  getDingtalk: () => api.get('/settings/dingtalk'),
  updateDingtalk: (data) => api.put('/settings/dingtalk', data),
  testDingtalk: (data) => api.post('/settings/dingtalk/test', data),
  getWechatWork: () => api.get('/settings/wechat-work'),
  updateWechatWork: (data) => api.put('/settings/wechat-work', data),
  testWechatWork: (data) => api.post('/settings/wechat-work/test', data),
  getSms: () => api.get('/settings/sms'),
  updateSms: (data) => api.put('/settings/sms', data),
  testSms: (data) => api.post('/settings/sms/test', data)
}

// 文件上传接口
export const uploadApi = {
  upload: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}

// 日志相关接口
export const logApi = {
  // 获取日志列表
  getLogs: (params) => api.get('/logs', { params }),
  
  // 获取日志详情
  getLogDetail: (id) => api.get(`/logs/${id}`)
}

// 统计相关接口
export const statsApi = {
  // 获取统计数据
  getStats: () => api.get('/stats')
}

export default api 