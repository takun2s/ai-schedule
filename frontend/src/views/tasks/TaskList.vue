<template>
  <div class="task-list">
    <div class="toolbar">
      <el-button type="primary" @click="$router.push('/tasks/edit')">创建任务</el-button>
    </div>

    <el-table :data="tasks" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="name" label="名称"></el-table-column>
      <el-table-column prop="type" label="类型" width="100"></el-table-column>
      <el-table-column prop="command" label="命令" show-overflow-tooltip></el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180">
        <template slot-scope="scope">
          {{ formatDateTime(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="300" fixed="right">
        <template slot-scope="scope">
          <div class="operation-btns">
            <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="primary" @click="handleExecute(scope.row)">执行</el-button>
            <el-button size="mini" type="info" @click="showExecutions(scope.row)">执行记录</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import moment from 'moment'

export default {
  name: 'TaskList',
  components: {
  },
  data() {
    return {
      tasks: [],
      loading: false,
      executionDialogVisible: false,
      currentTaskId: null
    }
  },
  created() {
    this.loadTasks()
  },
  methods: {
    async loadTasks() {
      this.loading = true
      try {
        const response = await this.$http.get('/api/tasks')
        if (response.data && response.code === 200) {
          this.tasks = response.data
        } else {
          throw new Error('Invalid response format')
        }
      } catch (error) {
        console.error('Load tasks error:', error) // 调试日志
        this.$message.error('加载任务列表失败')
      } finally {
        this.loading = false
      }
    },
    formatDateTime(date) {
      return date ? moment(date).format('YYYY-MM-DD HH:mm:ss') : '-'
    },
    getStatusType(status) {
      const statusMap = {
        'CREATED': 'info',
        'RUNNING': 'primary',
        'COMPLETED': 'success',
        'FAILED': 'danger',
        'STOPPED': 'warning'
      }
      return statusMap[status] || 'info'
    },
    handleEdit(row) {
      this.$router.push(`/tasks/edit/${row.id}`)
    },
    async handleExecute(row) {
      try {
        await this.$http.post(`/api/tasks/${row.id}/execute`)
        this.$message.success('任务已开始执行')
        this.loadTasks()
      } catch (error) {
        this.$message.error('执行任务失败')
      }
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确认删除该任务?', '提示', { type: 'warning' })
        await this.$http.delete(`/api/tasks/${row.id}`)
        this.$message.success('删除成功')
        this.loadTasks()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
        }
      }
    },
    showExecutions(task) {
      this.$router.push({
        path: '/executions',
        query: { taskId: task.id }
      })
    }
  }
}
</script>

<style scoped>
.task-list {
  padding: 20px;
}
.toolbar {
  margin-bottom: 20px;
}
.el-button + .el-button {
  margin-left: 5px; /* 减小按钮间距 */
}
.operation-btns {
  white-space: nowrap;
  display: flex;
  gap: 4px;
}
.el-button--mini {
  padding: 5px 8px;
  font-size: 12px;
}
</style>
