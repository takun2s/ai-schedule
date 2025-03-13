<template>
  <div class="log-viewer">
    <div class="log-header">
      <span>执行日志</span>
      <span class="log-info">
        开始时间：{{ formatTime(log.startTime) }}
        状态：
        <el-tag :type="getStatusType(log.status)">{{ log.status }}</el-tag>
      </span>
    </div>
    <div class="log-content" v-loading="loading">
      <pre>{{ log.logContent || '暂无日志' }}</pre>
    </div>
  </div>
</template>

<script>
import moment from 'moment';

export default {
  props: {
    logId: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      loading: false,
      log: {}
    };
  },
  methods: {
    async fetchLog() {
      this.loading = true;
      try {
        const { data } = await this.$http.get(`/api/logs/${this.logId}`);
        this.log = data;
      } finally {
        this.loading = false;
      }
    },
    formatTime(time) {
      return moment(time).format('YYYY-MM-DD HH:mm:ss');
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
    this.fetchLog();
  }
};
</script>

<style scoped>
.log-viewer {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.log-header {
  padding: 10px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.log-content {
  flex: 1;
  padding: 10px;
  background: #1e1e1e;
  color: #fff;
  overflow: auto;
}

.log-content pre {
  margin: 0;
  white-space: pre-wrap;
  font-family: Consolas, Monaco, monospace;
}
</style>
