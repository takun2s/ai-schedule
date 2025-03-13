<template>
  <div class="cron-helper">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="快速选择" name="quick">
        <el-radio-group v-model="quickSelect" @change="handleQuickSelect">
          <el-radio label="every-minute">每分钟</el-radio>
          <el-radio label="every-hour">每小时</el-radio>
          <el-radio label="every-day">每天0点</el-radio>
          <el-radio label="every-week">每周一0点</el-radio>
          <el-radio label="every-month">每月1日0点</el-radio>
        </el-radio-group>
      </el-tab-pane>
      <el-tab-pane label="自定义" name="custom">
        <el-form label-width="80px" size="small">
          <el-form-item label="分钟">
            <el-select v-model="minute" multiple placeholder="选择分钟" @change="updateExpression">
              <el-option v-for="i in 60" :key="i-1" :label="i-1" :value="i-1"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="小时">
            <el-select v-model="hour" multiple placeholder="选择小时" @change="updateExpression">
              <el-option v-for="i in 24" :key="i-1" :label="i-1" :value="i-1"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="日">
            <el-select v-model="day" multiple placeholder="选择日期" @change="updateExpression">
              <el-option v-for="i in 31" :key="i" :label="i" :value="i"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="月">
            <el-select v-model="month" multiple placeholder="选择月份" @change="updateExpression">
              <el-option v-for="i in 12" :key="i" :label="i" :value="i"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="周">
            <el-select v-model="week" multiple placeholder="选择星期" @change="updateExpression">
              <el-option v-for="(day, i) in weekDays" :key="i" :label="day" :value="i"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
    <div class="preview">
      <span>表达式预览：</span>
      <code>{{ expression }}</code>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CronHelper',
  props: {
    value: String
  },
  data() {
    return {
      activeTab: 'quick',
      quickSelect: '',
      minute: [],
      hour: [],
      day: [],
      month: [],
      week: [],
      weekDays: ['周日', '周一', '周二', '周三', '周四', '周五', '周六'],
      expression: ''
    }
  },
  methods: {
    handleQuickSelect(val) {
      const cronMap = {
        'every-minute': '* * * * *',
        'every-hour': '0 * * * *',
        'every-day': '0 0 * * *',
        'every-week': '0 0 * * 1',
        'every-month': '0 0 1 * *'
      }
      this.expression = cronMap[val] || '* * * * *'
      this.$emit('input', this.expression)
    },
    updateExpression() {
      const parts = []
      parts[0] = this.minute.length ? this.minute.join(',') : '*'
      parts[1] = this.hour.length ? this.hour.join(',') : '*'
      parts[2] = this.day.length ? this.day.join(',') : '*'
      parts[3] = this.month.length ? this.month.join(',') : '*'
      parts[4] = this.week.length ? this.week.join(',') : '*'
      
      this.expression = parts.join(' ')
      this.$emit('input', this.expression)
    }
  }
}
</script>

<style scoped>
.cron-helper {
  padding: 15px;
}
.preview {
  margin-top: 15px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
}
code {
  color: #409EFF;
  font-family: monospace;
}
</style>
