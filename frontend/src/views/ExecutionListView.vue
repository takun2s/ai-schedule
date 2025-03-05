<template>
  <div class="execution-list-container">
    <div class="page-header">
      <h2>执行记录</h2>
    </div>
    
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="任务名称">
          <el-input v-model="searchForm.taskName" placeholder="请输入任务名称" clearable />
        </el-form-item>
        <el-form-item label="执行状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="运行中" :value="1" />
            <el-option label="成功" :value="2" />
            <el-option label="失败" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="执行时间">
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
        :data="executionList"
        style="width: 100%"
      >
        <el-table-column prop="taskName" label="任务名称" min-width="150" />
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column prop="duration" label="耗时" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
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
                type="primary"
                link
                @click="handleLog(row)"
              >
                日志
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
    
    <!-- 执行详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="执行详情"
      width="800px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="任务名称">
          {{ currentExecution?.taskName }}
        </el-descriptions-item>
        <el-descriptions-item label="执行ID">
          {{ currentExecution?.id }}
        </el-descriptions-item>
        <el-descriptions-item label="开始时间">
          {{ currentExecution?.startTime }}
        </el-descriptions-item>
        <el-descriptions-item label="结束时间">
          {{ currentExecution?.endTime }}
        </el-descriptions-item>
        <el-descriptions-item label="耗时">
          {{ currentExecution?.duration }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentExecution?.status)">
            {{ getStatusText(currentExecution?.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="执行结果" :span="2">
          <pre class="execution-result">{{ currentExecution?.result }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="错误信息" :span="2" v-if="currentExecution?.errorMessage">
          <pre class="error-message">{{ currentExecution?.errorMessage }}</pre>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Search,
  Refresh
} from '@element-plus/icons-vue'

const loading = ref(false)
const detailDialogVisible = ref(false)
const currentExecution = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  taskName: '',
  status: '',
  timeRange: []
})

const executionList = ref([
  {
    id: 1,
    taskName: '数据同步任务',
    startTime: '2024-03-05 10:00:00',
    endTime: '2024-03-05 10:05:00',
    duration: '5分钟',
    status: 2,
    result: '数据同步完成',
    errorMessage: null
  },
  {
    id: 2,
    taskName: '报表生成任务',
    startTime: '2024-03-05 09:30:00',
    endTime: '2024-03-05 09:35:00',
    duration: '5分钟',
    status: 2,
    result: '报表生成完成',
    errorMessage: null
  },
  {
    id: 3,
    taskName: '数据清理任务',
    startTime: '2024-03-05 09:00:00',
    endTime: '2024-03-05 09:10:00',
    duration: '10分钟',
    status: 3,
    result: null,
    errorMessage: '数据库连接失败'
  }
])

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
  currentExecution.value = row
  detailDialogVisible.value = true
}

const handleLog = (row) => {
  // TODO: 实现日志查看功能
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
.execution-list-container {
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

.execution-result,
.error-message {
  margin: 0;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  white-space: pre-wrap;
  word-break: break-all;
}

.error-message {
  color: #f56c6c;
}
</style> 