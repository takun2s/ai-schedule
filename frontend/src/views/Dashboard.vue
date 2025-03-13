<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6" v-for="(value, key) in stats" :key="key">
        <el-card shadow="hover" :class="'stat-card ' + key">
          <div class="stat-item">
            <div class="stat-title">{{ getStatTitle(key) }}</div>
            <div class="stat-value" :class="{ error: key === 'failedTasks' }">
              {{ value }}
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="16">
        <el-card>
          <div slot="header">执行趋势</div>
          <div class="chart-container">
            <ve-line 
              :data="chartData"
              :settings="chartSettings"
              height="300px">
            </ve-line>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <div slot="header">任务状态分布</div>
          <div class="chart-container">
            <ve-pie 
              :data="pieData"
              :settings="pieSettings"
              height="300px">
            </ve-pie>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <div slot="header">最近执行记录</div>
          <el-table :data="recentExecutions" stripe>
            <el-table-column prop="taskName" label="任务名称" />
            <el-table-column prop="startTime" label="开始时间" width="180">
              <template slot-scope="scope">
                {{ formatTime(scope.row.startTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="duration" label="执行时长" width="120">
              <template slot-scope="scope">
                {{ formatDuration(scope.row.duration) }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import VeLine from 'v-charts/lib/line.common';
import VePie from 'v-charts/lib/pie.common';
import moment from 'moment';

export default {
  components: { VeLine, VePie },
  data() {
    return {
      stats: {},
      recentExecutions: [],
      executionTrend: [],
      taskDistribution: [],
      chartSettings: {
        area: true,
        axisSite: { right: ['failRate'] },
      },
      pieSettings: {
        radius: '65%',
      }
    };
  },
  computed: {
    chartData() {
      return {
        columns: ['日期', '执行次数'],
        rows: this.executionTrend.map(item => ({
          '日期': item.date,
          '执行次数': item.count
        }))
      };
    },
    pieData() {
      return {
        columns: ['状态', '数量'],
        rows: this.taskDistribution.map(item => ({
          '状态': item.status,
          '数量': item.count
        }))
      };
    }
  },
  methods: {
    async fetchDashboardData() {
      try {
        const { data } = await this.$http.get('/api/dashboard');
        this.stats = data.stats;
        this.recentExecutions = data.recentExecutions;
        this.executionTrend = data.executionTrend;
        this.taskDistribution = data.taskDistribution;
      } catch (error) {
        this.$message.error('获取数据失败');
      }
    },
    getStatTitle(key) {
      const titles = {
        totalTasks: '总任务数',
        runningTasks: '运行中任务',
        todayExecutions: '今日执行次数',
        failedTasks: '失败任务数'
      };
      return titles[key] || key;
    },
    formatTime(time) {
      return moment(time).format('YYYY-MM-DD HH:mm:ss');
    },
    formatDuration(seconds) {
      return `${seconds}秒`;
    },
    getStatusType(status) {
      return {
        'RUNNING': 'primary',
        'SUCCESS': 'success',
        'FAILED': 'danger'
      }[status] || 'info';
    }
  },
  created() {
    this.fetchDashboardData();
  }
};
</script>

<style scoped>
.stat-card {
  .stat-item {
    text-align: center;
  }
  .stat-title {
    font-size: 14px;
    color: #666;
  }
  .stat-value {
    font-size: 24px;
    font-weight: bold;
    margin-top: 10px;
    &.error {
      color: #f56c6c;
    }
  }
}

.chart-container {
  height: 300px;
}
</style>
