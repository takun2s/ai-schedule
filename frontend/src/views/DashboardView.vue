<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>总任务数</span>
              <el-icon><List /></el-icon>
            </div>
          </template>
          <div class="card-content">
            <div class="number">{{ statistics.totalTasks }}</div>
            <div class="trend">
              <span :class="statistics.taskTrend >= 0 ? 'up' : 'down'">
                {{ Math.abs(statistics.taskTrend) }}%
                <el-icon><ArrowUp v-if="statistics.taskTrend >= 0" /><ArrowDown v-else /></el-icon>
              </span>
              较上月
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>运行中任务</span>
              <el-icon><Loading /></el-icon>
            </div>
          </template>
          <div class="card-content">
            <div class="number">{{ statistics.runningTasks }}</div>
            <div class="trend">
              <span :class="statistics.runningTrend >= 0 ? 'up' : 'down'">
                {{ Math.abs(statistics.runningTrend) }}%
                <el-icon><ArrowUp v-if="statistics.runningTrend >= 0" /><ArrowDown v-else /></el-icon>
              </span>
              较上月
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>今日执行次数</span>
              <el-icon><Operation /></el-icon>
            </div>
          </template>
          <div class="card-content">
            <div class="number">{{ statistics.todayExecutions }}</div>
            <div class="trend">
              <span :class="statistics.executionTrend >= 0 ? 'up' : 'down'">
                {{ Math.abs(statistics.executionTrend) }}%
                <el-icon><ArrowUp v-if="statistics.executionTrend >= 0" /><ArrowDown v-else /></el-icon>
              </span>
              较昨日
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>告警数量</span>
              <el-icon><Bell /></el-icon>
            </div>
          </template>
          <div class="card-content">
            <div class="number">{{ statistics.totalAlerts }}</div>
            <div class="trend">
              <span :class="statistics.alertTrend >= 0 ? 'up' : 'down'">
                {{ Math.abs(statistics.alertTrend) }}%
                <el-icon><ArrowUp v-if="statistics.alertTrend >= 0" /><ArrowDown v-else /></el-icon>
              </span>
              较上月
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="chart-row">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>任务执行趋势</span>
              <el-radio-group v-model="executionChartType" size="small">
                <el-radio-button label="day">日</el-radio-button>
                <el-radio-button label="week">周</el-radio-button>
                <el-radio-button label="month">月</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="chart-container">
            <!-- TODO: 集成图表库 -->
            <div class="chart-placeholder">图表区域</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>任务类型分布</span>
            </div>
          </template>
          <div class="chart-container">
            <!-- TODO: 集成图表库 -->
            <div class="chart-placeholder">图表区域</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="table-row">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最近执行记录</span>
              <el-button type="primary" link>查看更多</el-button>
            </div>
          </template>
          <el-table :data="recentExecutions" style="width: 100%">
            <el-table-column prop="taskName" label="任务名称" />
            <el-table-column prop="startTime" label="开始时间" />
            <el-table-column prop="endTime" label="结束时间" />
            <el-table-column prop="duration" label="耗时" />
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import {
  List,
  Loading,
  Operation,
  Bell,
  ArrowUp,
  ArrowDown
} from '@element-plus/icons-vue'

const executionChartType = ref('day')

const statistics = reactive({
  totalTasks: 128,
  taskTrend: 12.5,
  runningTasks: 32,
  runningTrend: -5.2,
  todayExecutions: 256,
  executionTrend: 8.3,
  totalAlerts: 16,
  alertTrend: -2.1
})

const recentExecutions = [
  {
    taskName: '数据同步任务',
    startTime: '2024-03-05 10:00:00',
    endTime: '2024-03-05 10:05:00',
    duration: '5分钟',
    status: 2
  },
  {
    taskName: '报表生成任务',
    startTime: '2024-03-05 09:30:00',
    endTime: '2024-03-05 09:35:00',
    duration: '5分钟',
    status: 2
  },
  {
    taskName: '数据清理任务',
    startTime: '2024-03-05 09:00:00',
    endTime: '2024-03-05 09:10:00',
    duration: '10分钟',
    status: 3
  }
]

const getStatusType = (status) => {
  const types = {
    1: 'info',
    2: 'success',
    3: 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    1: '运行中',
    2: '成功',
    3: '失败'
  }
  return texts[status] || '未知'
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-content {
  text-align: center;
}

.number {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.trend {
  font-size: 14px;
  color: #909399;
}

.up {
  color: #67c23a;
}

.down {
  color: #f56c6c;
}

.chart-row {
  margin-top: 20px;
}

.chart-container {
  height: 300px;
}

.chart-placeholder {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  color: #909399;
}

.table-row {
  margin-top: 20px;
}
</style> 