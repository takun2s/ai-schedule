<template>
  <div class="task-edit">
    <el-form :model="task" :rules="rules" ref="taskForm" label-width="120px">
      <el-card>
        <div slot="header">
          <span>{{ isEdit ? '编辑任务' : '创建任务' }}</span>
        </div>
        
        <!-- 使用TaskForm组件处理所有表单输入 -->
        <task-form 
          ref="taskForm"
          :initial-data="taskData"
          @submit="handleSubmit"
        />
      </el-card>
    </el-form>
  </div>
</template>

<script>
import TaskForm from './components/TaskForm.vue'

export default {
  name: 'TaskEdit',
  components: {
    TaskForm
  },
  data() {
    return {
      taskData: {},
      isEdit: false
    }
  },
  created() {
    const { id } = this.$route.params
    if (id) {
      this.isEdit = true
      this.loadTask(id)
    }
  },
  methods: {
    async loadTask(id) {
      try {
        const { data } = await this.$http.get(`/api/tasks/${id}`)
        this.taskData = data.data
      } catch (error) {
        this.$message.error('加载任务失败')
        this.$router.push('/tasks')
      }
    },
    async handleSubmit(formData) {
      try {
        if (this.isEdit) {
          await this.$http.put(`/api/tasks/${this.$route.params.id}`, formData)
          this.$message.success('更新成功')
        } else {
          await this.$http.post('/api/tasks', formData)
          this.$message.success('创建成功')
        }
        this.$router.push('/tasks')
      } catch (error) {
        this.$message.error(this.isEdit ? '更新失败' : '创建失败')
      }
    },
    async saveTask() {
      try {
        await this.$refs.taskForm.validate()
        
        // 根据任务类型处理保存数据
        const taskData = { ...this.task }
        
        // 清理不相关的字段
        if (taskData.type === 'HTTP') {
          taskData.command = undefined
        } else {
          taskData.httpUrl = undefined
          taskData.httpMethod = undefined
        }
        
        // 发送请求保存任务
        const url = this.isEdit ? `/api/tasks/${this.taskId}` : '/api/tasks'
        const method = this.isEdit ? 'put' : 'post'
        await this.$http[method](url, taskData)
        
        this.$message.success(this.isEdit ? '更新成功' : '创建成功')
        this.$router.push('/tasks')
      } catch (err) {
        console.error('Save task error:', err)
        this.$message.error(this.isEdit ? '更新失败' : '创建失败')
      }
    }
  }
}
</script>

<style scoped>
.task-edit {
  padding: 20px;
}
</style>
