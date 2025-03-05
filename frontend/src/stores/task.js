import { defineStore } from 'pinia'
import { taskApi } from '../api'

export const useTaskStore = defineStore('task', {
  state: () => ({
    tasks: [],
    loading: false,
    total: 0
  }),

  actions: {
    async fetchTasks(params) {
      this.loading = true
      try {
        const { data } = await taskApi.getTasks(params)
        this.tasks = data.items
        this.total = data.total
      } catch (error) {
        console.error('获取任务列表失败:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async createTask(taskData) {
      try {
        await taskApi.createTask(taskData)
      } catch (error) {
        console.error('创建任务失败:', error)
        throw error
      }
    },

    async updateTask(id, taskData) {
      try {
        await taskApi.updateTask(id, taskData)
      } catch (error) {
        console.error('更新任务失败:', error)
        throw error
      }
    },

    async deleteTask(id) {
      try {
        await taskApi.deleteTask(id)
      } catch (error) {
        console.error('删除任务失败:', error)
        throw error
      }
    },

    async startTask(id) {
      try {
        await taskApi.startTask(id)
      } catch (error) {
        console.error('启动任务失败:', error)
        throw error
      }
    },

    async stopTask(id) {
      try {
        await taskApi.stopTask(id)
      } catch (error) {
        console.error('停止任务失败:', error)
        throw error
      }
    }
  }
}) 