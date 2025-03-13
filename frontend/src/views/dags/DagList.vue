<template>
  <div class="dag-list-container">
    <div class="dag-list">
      <div class="toolbar">
        <el-button type="primary" @click="$router.push('/dags/edit')">创建DAG</el-button>
      </div>

      <el-table :data="dags" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="name" label="名称"></el-table-column>
        <el-table-column label="任务列表" show-overflow-tooltip>
          <template slot-scope="scope">
            <div v-if="scope.row.nodes">
              {{ getTaskNames(scope.row.nodes) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="cronExpression" label="调度表达式" width="150"></el-table-column>
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
  </div>
</template>

<script>
import moment from 'moment'

export default {
  name: 'DagList',
  components: {
  },
  data() {
    return {
      dags: [],
      loading: false,
      executionDialogVisible: false,
      currentDagId: null
    }
  },
  created() {
    this.loadDags()
  },
  methods: {
    async loadDags() {
      this.loading = true
      try {
        const response = await this.$http.get('/api/dags')
        if (response.code === 200) {  // 直接使用 response 而不是 response.data
          this.dags = response.data || []
          console.log('Loaded DAGs:', this.dags)  // 添加调试日志
        } else {
          throw new Error(response.message || '加载失败')
        }
      } catch (error) {
        console.error('Load DAGs error:', error)
        this.$message.error('加载DAG列表失败')
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
    getTaskNames(nodesJson) {
      try {
        // 如果已经是数组，直接使用
        const nodes = Array.isArray(nodesJson) ? nodesJson : JSON.parse(nodesJson);
        if (Array.isArray(nodes)) {
          return nodes
            .map(node => node.taskName || node.name || '未命名任务')
            .join('、');
        }
      } catch (e) {
        console.error('解析任务节点失败:', e);
      }
      return '无任务';
    },
    handleEdit(row) {
      this.$router.push(`/dags/edit/${row.id}`)
    },
    async handleExecute(row) {
      try {
        await this.$http.post(`/api/dags/${row.id}/execute`)
        this.$message.success('DAG已开始执行')
        this.loadDags()
      } catch (error) {
        this.$message.error('执行DAG失败')
      }
    },
    async handleStop(row) {
      try {
        await this.$http.post(`/api/dags/${row.id}/stop`)
        this.$message.success('DAG已停止')
        this.loadDags()
      } catch (error) {
        this.$message.error('停止DAG失败')
      }
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确认删除该DAG?', '提示', { type: 'warning' })
        await this.$http.delete(`/api/dags/${row.id}`)
        this.$message.success('删除成功')
        this.loadDags()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
        }
      }
    },
    showExecutions(row) {
      this.currentDagId = row.id
      this.executionDialogVisible = true
    }
  }
}
</script>

<style scoped>
.dag-list-container {
  width: 100%;
  height: 100%;
}

.dag-list {
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
