<template>
  <div class="dashboard">
    <!-- 任务统计 -->
    <h3>任务统计</h3>
    <el-row :gutter="20">
      <el-col :span="4" v-for="(value, key) in taskStats" :key="key">
        <el-card class="box-card">
          <div class="card-header">
            <span>{{ getStatTitle(key) }}</span>
          </div>
          <div class="card-content" :class="getStatusClass(key)">{{ value }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- DAG统计 -->
    <h3>DAG统计</h3>
    <el-row :gutter="20">
      <el-col :span="4" v-for="(value, key) in dagStats" :key="key">
        <el-card class="box-card">
          <div class="card-header">
            <span>{{ getStatTitle(key) }}</span>
          </div>
          <div class="card-content" :class="getStatusClass(key)">{{ value }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 告警统计 -->
    <h3>告警统计</h3>
    <el-row :gutter="20">
      <el-col :span="4">
        <el-card class="box-card">
          <div class="card-header">
            <span>24小时告警数</span>
          </div>
          <div class="card-content warning">{{ stats.alertCount24h || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'Dashboard',
  data() {
    return {
      taskStats: {},
      dagStats: {},
      stats: {},
      loading: false
    }
  },
  created() {
    this.loadStats()
  },
  methods: {
    async loadStats() {
      this.loading = true
      try {
        // 加载任务统计
        const taskResponse = await this.$http.get('/api/dashboard/task-stats')
        if (taskResponse.code === 200) {
          this.taskStats = taskResponse.data
        }

        // 加载DAG统计
        const dagResponse = await this.$http.get('/api/dashboard/dag-stats')
        if (dagResponse.code === 200) {
          this.dagStats = dagResponse.data
        }

        // 加载总体统计
        const statsResponse = await this.$http.get('/api/dashboard/stats')
        if (statsResponse.code === 200) {
          this.stats = statsResponse.data
        }
      } catch (error) {
        console.error('Failed to load stats:', error)
        this.$message.error('加载统计数据失败')
      } finally {
        this.loading = false
      }
    },
    getStatTitle(key) {
      const titles = {
        'total': '总数',
        'running': '运行中',
        'completed': '已完成',
        'failed': '失败',
        'pending': '等待中'
      }
      return titles[key] || key
    },
    getStatusClass(key) {
      const classes = {
        'running': 'warning',
        'completed': 'success',
        'failed': 'danger',
        'pending': 'info'
      }
      return classes[key] || ''
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard {
  padding: 20px;
  
  h3 {
    margin: 20px 0;
    color: #606266;
  }
  
  .box-card {
    margin-bottom: 20px;
    
    .card-header {
      font-size: 14px;
      color: #606266;
    }
    
    .card-content {
      font-size: 24px;
      font-weight: bold;
      margin-top: 10px;
      text-align: center;
      
      &.success { color: #67C23A; }
      &.warning { color: #E6A23C; }
      &.danger { color: #F56C6C; }
      &.info { color: #909399; }
    }
  }
}
</style>
