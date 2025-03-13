<template>
  <div class="execution-list-container">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="任务执行记录" name="tasks">
        <!-- 添加任务过滤功能 -->
        <div class="filter-section" v-if="$route.query.taskId">
          <el-tag type="info">当前仅显示任务ID: {{ $route.query.taskId }} 的记录</el-tag>
          <el-button size="small" type="text" @click="clearFilter">显示全部</el-button>
        </div>
        
        <el-table :data="taskRecords" v-loading="taskLoading">
          <el-table-column prop="id" label="ID" width="80"></el-table-column>
          <el-table-column label="任务名称">
            <template slot-scope="scope">
              <el-link type="primary" @click="goToTask(scope.row.taskId)">
                {{ scope.row.taskName }}
              </el-link>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="startTime" label="开始时间" width="180">
            <template slot-scope="scope">
              {{ formatDateTime(scope.row.startTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="endTime" label="结束时间" width="180">
            <template slot-scope="scope">
              {{ formatDateTime(scope.row.endTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="duration" label="耗时" width="100">
            <template slot-scope="scope">
              {{ getDuration(scope.row.startTime, scope.row.endTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" fixed="right">
            <template slot-scope="scope">
              <el-button type="text" @click="showTaskDetail(scope.row)">
                查看详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="DAG执行记录" name="dags">
        <el-table :data="dagRecords" v-loading="dagLoading">
          <el-table-column prop="dagName" label="DAG名称"></el-table-column>
          <el-table-column prop="status" label="状态">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="startTime" label="开始时间">
            <template slot-scope="scope">
              {{ formatDateTime(scope.row.startTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="endTime" label="结束时间">
            <template slot-scope="scope">
              {{ formatDateTime(scope.row.endTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="duration" label="执行时长">
            <template slot-scope="scope">
              {{ formatDuration(scope.row.duration) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template slot-scope="scope">
              <el-button type="text" @click="showDetail(scope.row)">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 执行详情对话框 -->
    <el-dialog title="执行详情" :visible.sync="detailVisible" width="650px">
      <div v-if="currentRecord" class="detail-content">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="任务名称">{{ currentRecord.taskName }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentRecord.status)">{{ currentRecord.status }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="开始时间">
            {{ formatDateTime(currentRecord.startTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="结束时间">
            {{ formatDateTime(currentRecord.endTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="执行耗时">
            {{ getDuration(currentRecord.startTime, currentRecord.endTime) }}
          </el-descriptions-item>
        </el-descriptions>

        <div v-if="currentRecord.output" class="output-section">
          <h4>执行输出</h4>
          <pre class="output-content">{{ currentRecord.output }}</pre>
        </div>

        <div v-if="currentRecord.errorMessage || currentRecord.error" class="error-section">
          <h4>错误信息</h4>
          <pre class="error-content">{{ currentRecord.errorMessage || currentRecord.error }}</pre>
        </div>
      </div>
    </el-dialog>

    <!-- DAG执行详情对话框 -->
    <el-dialog 
      title="DAG执行详情" 
      :visible.sync="dialogVisible" 
      width="80%" 
      class="dag-detail-dialog"
    >
      <dag-execution-detail
        :id="currentDagId"
        v-if="dialogVisible"
      />
    </el-dialog>
  </div>
</template>

<script>
import moment from 'moment'
import DagExecutionDetail from './DagExecutionDetail.vue'

export default {
  name: 'ExecutionList',
  components: {
    DagExecutionDetail
  },
  data() {
    return {
      activeTab: 'tasks',
      taskRecords: [],
      dagRecords: [],
      taskLoading: false,
      dagLoading: false,
      detailVisible: false,
      taskDetailVisible: false,
      dagDetailVisible: false,
      currentRecord: null,
      currentTask: null,
      currentDag: null,
      dialogVisible: false,
      currentDagId: null,
      detail: null
    }
  },
  created() {
    this.loadRecords()
    this.loadDagRecords()
  },
  watch: {
    '$route.query.taskId'() {
      this.loadRecords()
    }
  },
  methods: {
    async loadRecords() {
      this.taskLoading = true
      try {
        let url = '/api/executions/tasks'
        if (this.$route.query.taskId) {
          url = `/api/tasks/${this.$route.query.taskId}/executions`
        }
        
        const response = await this.$http.get(url)
        console.log('API Response:', response) // 调试日志
        
        if (response.code === 200) {  // 移除 .data
          this.taskRecords = response.data || []
          console.log('Processed records:', this.taskRecords)
        } else {
          throw new Error(response.message || '加载失败')  // 移除 .data
        }
      } catch (err) {
        console.error('Load records error:', err)
        this.$message.error('加载执行记录失败')
      } finally {
        this.taskLoading = false
      }
    },

    async loadDagRecords() {
      this.dagLoading = true
      try {
        const response = await this.$http.get('/api/executions/dags')
        if (response.code === 200) {  // 移除 .data
          this.dagRecords = response.data || []
        } else {
          throw new Error(response.message || '加载失败')  // 移除 .data
        }
      } catch (error) {
        console.error('Load DAG records error:', error)
        this.$message.error('加载DAG执行记录失败')
      } finally {
        this.dagLoading = false
      }
    },
    formatDateTime(time) {
      return time ? moment(time).format('YYYY-MM-DD HH:mm:ss') : '-'
    },
    getDuration(startTime, endTime) {
      if (!startTime || !endTime) return '-'
      const duration = moment(endTime).diff(moment(startTime), 'seconds')
      return `${duration}秒`
    },
    formatDuration(duration) {
      return duration ? `${duration}秒` : '-'
    },
    getStatusType(status) {
      const typeMap = {
        'RUNNING': 'warning',
        'COMPLETED': 'success',  // 改为 success
        'FAILED': 'danger',
        'PENDING': 'info',
        'STOPPED': 'info'
      }
      return typeMap[status] || 'info'
    },
    showTaskDetail(record) {
      this.currentRecord = record
      this.detailVisible = true
    },
    clearFilter() {
      this.$router.push('/executions')
    },
    goToTask(taskId) {
      this.$router.push(`/tasks/${taskId}`);
    },
    showDetail(row) {
      this.currentDagId = row.id;
      this.dialogVisible = true;
    }
  }
}
</script>

<style lang="scss" scoped>
.execution-list-container {
  padding: 20px;
  
  .filter-section {
    margin-bottom: 15px;
    display: flex;
    align-items: center;
    gap: 10px;
  }
  
  .output-section,
  .error-section {
    margin-top: 20px;
    
    h4 {
      margin-bottom: 10px;
      font-weight: 500;
    }
    
    pre {
      background: #f8f8f8;
      padding: 12px;
      border-radius: 4px;
      max-height: 300px;
      overflow-y: auto;
      white-space: pre-wrap;
      word-break: break-all;
    }
  }
  
  .error-section pre {
    background: #fff5f5;
    color: #f56c6c;
  }
  
  :deep(.el-descriptions) {
    margin: 20px 0;
  }
}

.detail-content {
  .info-item {
    margin: 10px 0;
    
    label {
      display: inline-block;
      width: 80px;
      color: #666;
    }
  }
}

.error-message, .output-message {
  margin-top: 20px;
  
  pre {
    background: #f5f5f5;
    padding: 10px;
    border-radius: 4px;
    margin: 0;
    white-space: pre-wrap;
  }
}

.error-message pre {
  color: #f56c6c;
}

.node-executions {
  margin-top: 20px;
  
  h4 {
    margin-bottom: 10px;
  }
}

.error-section {
  margin-top: 20px;
  
  h4 {
    margin-bottom: 10px;
    color: #f56c6c;
  }
  
  pre {
    background: #fff5f5;
    padding: 12px;
    border-radius: 4px;
    color: #f56c6c;
    white-space: pre-wrap;
    word-break: break-all;
  }
}

.dag-detail-dialog .el-dialog__body {
  padding: 20px;
}
.info-card, .progress-card {
  margin-bottom: 20px;
}
.stats {
  padding: 10px;
  background: #f8f9fa;
  border-radius: 4px;
}
.error-info {
  margin-top: 15px;
  padding: 10px;
  background: #fff1f0;
  border: 1px solid #ffa39e;
  border-radius: 4px;
}
</style>
