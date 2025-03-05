import { defineStore } from 'pinia'
import { statsApi } from '../api'

export const useStatsStore = defineStore('stats', {
  state: () => ({
    stats: {
      totalTasks: 0,
      runningTasks: 0,
      todayExecutions: 0
    },
    loading: false
  }),

  actions: {
    async fetchStats() {
      this.loading = true
      try {
        const { data } = await statsApi.getStats()
        this.stats = data
      } catch (error) {
        console.error('获取统计数据失败:', error)
        throw error
      } finally {
        this.loading = false
      }
    }
  }
}) 