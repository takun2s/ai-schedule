<template>
  <div class="alert-list-container">
    <div class="page-header">
      <h2>告警管理</h2>
    </div>
    
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="任务名称">
          <el-input v-model="searchForm.taskName" placeholder="请输入任务名称" clearable />
        </el-form-item>
        <el-form-item label="告警类型">
          <el-select v-model="searchForm.type" placeholder="请选择告警类型" clearable>
            <el-option label="邮件" value="email" />
            <el-option label="钉钉" value="dingtalk" />
            <el-option label="企业微信" value="wechat-work" />
            <el-option label="短信" value="sms" />
            <el-option label="Webhook" value="webhook" />
          </el-select>
        </el-form-item>
        <el-form-item label="告警状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="未发送" :value="0" />
            <el-option label="发送成功" :value="1" />
            <el-option label="发送失败" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="告警时间">
          <el-date-picker
            v-model="searchForm.timeRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="alertList"
        style="width: 100%"
      >
        <el-table-column prop="taskName" label="任务名称" min-width="150" />
        <el-table-column prop="type" label="告警类型" width="120">
          <template #default="{ row }">
            {{ getAlertTypeText(row.type) }}
          </template>
        </el-table-column>
        <el-table-column prop="content" label="告警内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="receiver" label="接收人" width="150" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-button
                type="primary"
                link
                @click="handleDetail(row)"
              >
                详情
              </el-button>
              <el-button
                v-if="row.status === 2"
                type="primary"
                link
                @click="handleRetry(row)"
              >
                重试
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 告警详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="告警详情"
      width="800px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="任务名称">
          {{ currentAlert?.taskName }}
        </el-descriptions-item>
        <el-descriptions-item label="告警ID">
          {{ currentAlert?.id }}
        </el-descriptions-item>
        <el-descriptions-item label="告警类型">
          {{ getAlertTypeText(currentAlert?.type) }}
        </el-descriptions-item>
        <el-descriptions-item label="接收人">
          {{ currentAlert?.receiver }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ currentAlert?.createTime }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentAlert?.status)">
            {{ getStatusText(currentAlert?.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="告警内容" :span="2">
          <pre class="alert-content">{{ currentAlert?.content }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="发送结果" :span="2" v-if="currentAlert?.sendResult">
          <pre class="send-result">{{ currentAlert?.sendResult }}</pre>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh
} from '@element-plus/icons-vue'

const loading = ref(false)
const detailDialogVisible = ref(false)
const currentAlert = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  taskName: '',
  type: '',
  status: '',
  timeRange: []
})

const alertList = ref([
  {
    id: 1,
    taskName: '数据同步任务',
    type: 'email',
    content: '任务执行失败：数据库连接超时',
    receiver: 'admin@example.com',
    status: 1,
    createTime: '2024-03-05 10:00:00',
    sendResult: '邮件发送成功'
  },
  {
    id: 2,
    taskName: '报表生成任务',
    type: 'dingtalk',
    content: '任务执行超时：耗时超过600秒',
    receiver: 'https://oapi.dingtalk.com/robot/send?access_token=xxx',
    status: 1,
    createTime: '2024-03-05 09:30:00',
    sendResult: '钉钉消息发送成功'
  },
  {
    id: 3,
    taskName: '数据清理任务',
    type: 'wechat-work',
    content: '任务执行失败：SQL语法错误',
    receiver: 'https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxx',
    status: 2,
    createTime: '2024-03-05 09:00:00',
    sendResult: '企业微信消息发送失败：网络超时'
  }
])

const getAlertTypeText = (type) => {
  const types = {
    email: '邮件',
    dingtalk: '钉钉',
    'wechat-work': '企业微信',
    sms: '短信',
    webhook: 'Webhook'
  }
  return types[type] || '未知类型'
}

const getStatusType = (status) => {
  const types = {
    0: 'info',
    1: 'success',
    2: 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    0: '未发送',
    1: '发送成功',
    2: '发送失败'
  }
  return texts[status] || '未知'
}

const handleSearch = () => {
  currentPage.value = 1
  // TODO: 调用搜索API
}

const handleReset = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  handleSearch()
}

const handleDetail = (row) => {
  currentAlert.value = row
  detailDialogVisible.value = true
}

const handleRetry = async (row) => {
  try {
    await ElMessageBox.confirm(
      '确定要重试发送该告警吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // TODO: 调用重试API
    ElMessage.success('重试发送成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('重试发送失败')
    }
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  handleSearch()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  handleSearch()
}
</script>

<style scoped>
.alert-list-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.alert-content,
.send-result {
  margin: 0;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  white-space: pre-wrap;
  word-break: break-all;
}

.send-result {
  color: #909399;
}
</style> 