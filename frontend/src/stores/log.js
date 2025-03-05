import { defineStore } from 'pinia'
import { logApi } from '../api'

export const useLogStore = defineStore('log', {
  state: () => ({
    logs: [],
    loading: false,
    total: 0,
    currentLog: null
  }),

  actions: {
    async fetchLogs(params) {
      this.loading = true
      try {
        const { data } = await logApi.getLogs(params)
        this.logs = data.items
        this.total = data.total
      } catch (error) {
        console.error('获取日志列表失败:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async fetchLogDetail(id) {
      try {
        const { data } = await logApi.getLogDetail(id)
        this.currentLog = data
      } catch (error) {
        console.error('获取日志详情失败:', error)
        throw error
      }
    }
  }
}) 