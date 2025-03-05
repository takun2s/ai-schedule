import { defineStore } from 'pinia'
import { userApi } from '../api'

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null,
    token: localStorage.getItem('token') || ''
  }),
  
  getters: {
    isLoggedIn: (state) => !!state.token,
    username: (state) => state.userInfo?.username || '',
    roles: (state) => state.userInfo?.roles || []
  },
  
  actions: {
    async login(loginData) {
      try {
        const data = await userApi.login(loginData)
        this.token = data.token
        localStorage.setItem('token', data.token)
        await this.getUserInfo()
        return data
      } catch (error) {
        throw error
      }
    },
    
    async logout() {
      try {
        await userApi.logout()
        this.token = ''
        this.userInfo = null
        localStorage.removeItem('token')
      } catch (error) {
        throw error
      }
    },
    
    async getUserInfo() {
      try {
        const data = await userApi.getInfo()
        this.userInfo = data
        return data
      } catch (error) {
        throw error
      }
    },
    
    async updatePassword(passwordData) {
      try {
        await userApi.updatePassword(passwordData)
      } catch (error) {
        throw error
      }
    }
  }
})

export const useTaskStore = defineStore('task', {
  state: () => ({
    taskList: [],
    currentTask: null,
    loading: false,
    total: 0
  }),
  
  actions: {
    async getTaskList(params) {
      this.loading = true
      try {
        const data = await taskApi.getList(params)
        this.taskList = data.list
        this.total = data.total
        return data
      } catch (error) {
        throw error
      } finally {
        this.loading = false
      }
    },
    
    async getTaskById(id) {
      try {
        const data = await taskApi.getById(id)
        this.currentTask = data
        return data
      } catch (error) {
        throw error
      }
    },
    
    async createTask(taskData) {
      try {
        const data = await taskApi.create(taskData)
        return data
      } catch (error) {
        throw error
      }
    },
    
    async updateTask(id, taskData) {
      try {
        const data = await taskApi.update(id, taskData)
        return data
      } catch (error) {
        throw error
      }
    },
    
    async deleteTask(id) {
      try {
        await taskApi.delete(id)
      } catch (error) {
        throw error
      }
    },
    
    async executeTask(id) {
      try {
        const data = await taskApi.execute(id)
        return data
      } catch (error) {
        throw error
      }
    },
    
    async toggleTaskStatus(id, status) {
      try {
        await taskApi.toggleStatus(id, status)
      } catch (error) {
        throw error
      }
    },
    
    async getTaskLogs(id, params) {
      try {
        const data = await taskApi.getLogs(id, params)
        return data
      } catch (error) {
        throw error
      }
    }
  }
})

export const useExecutionStore = defineStore('execution', {
  state: () => ({
    executionList: [],
    currentExecution: null,
    loading: false,
    total: 0
  }),
  
  actions: {
    async getExecutionList(params) {
      this.loading = true
      try {
        const data = await executionApi.getList(params)
        this.executionList = data.list
        this.total = data.total
        return data
      } catch (error) {
        throw error
      } finally {
        this.loading = false
      }
    },
    
    async getExecutionById(id) {
      try {
        const data = await executionApi.getById(id)
        this.currentExecution = data
        return data
      } catch (error) {
        throw error
      }
    },
    
    async getExecutionLogs(id) {
      try {
        const data = await executionApi.getLogs(id)
        return data
      } catch (error) {
        throw error
      }
    }
  }
})

export const useAlertStore = defineStore('alert', {
  state: () => ({
    alertList: [],
    currentAlert: null,
    loading: false,
    total: 0
  }),
  
  actions: {
    async getAlertList(params) {
      this.loading = true
      try {
        const data = await alertApi.getList(params)
        this.alertList = data.list
        this.total = data.total
        return data
      } catch (error) {
        throw error
      } finally {
        this.loading = false
      }
    },
    
    async getAlertById(id) {
      try {
        const data = await alertApi.getById(id)
        this.currentAlert = data
        return data
      } catch (error) {
        throw error
      }
    },
    
    async retryAlert(id) {
      try {
        const data = await alertApi.retry(id)
        return data
      } catch (error) {
        throw error
      }
    }
  }
})

export const useSettingStore = defineStore('setting', {
  state: () => ({
    basicSettings: null,
    emailSettings: null,
    dingtalkSettings: null,
    wechatWorkSettings: null,
    smsSettings: null
  }),
  
  actions: {
    async getBasicSettings() {
      try {
        const data = await settingApi.getBasic()
        this.basicSettings = data
        return data
      } catch (error) {
        throw error
      }
    },
    
    async updateBasicSettings(settings) {
      try {
        const data = await settingApi.updateBasic(settings)
        this.basicSettings = data
        return data
      } catch (error) {
        throw error
      }
    },
    
    async getEmailSettings() {
      try {
        const data = await settingApi.getEmail()
        this.emailSettings = data
        return data
      } catch (error) {
        throw error
      }
    },
    
    async updateEmailSettings(settings) {
      try {
        const data = await settingApi.updateEmail(settings)
        this.emailSettings = data
        return data
      } catch (error) {
        throw error
      }
    },
    
    async testEmailSettings(settings) {
      try {
        const data = await settingApi.testEmail(settings)
        return data
      } catch (error) {
        throw error
      }
    },
    
    async getDingtalkSettings() {
      try {
        const data = await settingApi.getDingtalk()
        this.dingtalkSettings = data
        return data
      } catch (error) {
        throw error
      }
    },
    
    async updateDingtalkSettings(settings) {
      try {
        const data = await settingApi.updateDingtalk(settings)
        this.dingtalkSettings = data
        return data
      } catch (error) {
        throw error
      }
    },
    
    async testDingtalkSettings(settings) {
      try {
        const data = await settingApi.testDingtalk(settings)
        return data
      } catch (error) {
        throw error
      }
    },
    
    async getWechatWorkSettings() {
      try {
        const data = await settingApi.getWechatWork()
        this.wechatWorkSettings = data
        return data
      } catch (error) {
        throw error
      }
    },
    
    async updateWechatWorkSettings(settings) {
      try {
        const data = await settingApi.updateWechatWork(settings)
        this.wechatWorkSettings = data
        return data
      } catch (error) {
        throw error
      }
    },
    
    async testWechatWorkSettings(settings) {
      try {
        const data = await settingApi.testWechatWork(settings)
        return data
      } catch (error) {
        throw error
      }
    },
    
    async getSmsSettings() {
      try {
        const data = await settingApi.getSms()
        this.smsSettings = data
        return data
      } catch (error) {
        throw error
      }
    },
    
    async updateSmsSettings(settings) {
      try {
        const data = await settingApi.updateSms(settings)
        this.smsSettings = data
        return data
      } catch (error) {
        throw error
      }
    },
    
    async testSmsSettings(settings) {
      try {
        const data = await settingApi.testSms(settings)
        return data
      } catch (error) {
        throw error
      }
    }
  }
}) 