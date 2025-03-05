<template>
  <div class="logs">
    <div class="toolbar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="任务名称">
          <el-input v-model="searchForm.taskName" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="执行状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态">
            <el-option label="全部" value="" />
            <el-option label="成功" value="SUCCESS" />
            <el-option label="失败" value="FAILED" />
          </el-select>
        </el-form-item>
        <el-form-item label="执行时间">
          <el-date-picker
            v-model="searchForm.timeRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchLogs">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table :data="logs" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="taskName" label="任务名称" />
      <el-table-column prop="startTime" label="开始时间" width="180" />
      <el-table-column prop="endTime" label="结束时间" width="180" />
      <el-table-column prop="duration" label="执行时长" width="100">
        <template #default="{ row }">
          {{ row.duration }}ms
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'SUCCESS' ? 'success' : 'danger'">
            {{ row.status === 'SUCCESS' ? '成功' : '失败' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button 
            type="primary" 
            size="small"
            @click="viewLogDetail(row)"
          >
            详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <el-dialog
      v-model="detailVisible"
      title="执行详情"
      width="800px"
    >
      <div class="log-detail">
        <div class="detail-item">
          <span class="label">任务名称：</span>
          <span>{{ currentLog.taskName }}</span>
        </div>
        <div class="detail-item">
          <span class="label">执行时间：</span>
          <span>{{ currentLog.startTime }}</span>
        </div>
        <div class="detail-item">
          <span class="label">执行状态：</span>
          <el-tag :type="currentLog.status === 'SUCCESS' ? 'success' : 'danger'">
            {{ currentLog.status === 'SUCCESS' ? '成功' : '失败' }}
          </el-tag>
        </div>
        <div class="detail-item">
          <span class="label">执行结果：</span>
          <pre class="result">{{ currentLog.result }}</pre>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const searchForm = ref({
  taskName: '',
  status: '',
  timeRange: []
})

const logs = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailVisible = ref(false)
const currentLog = ref({})

const searchLogs = () => {
  // TODO: 调用API查询日志
}

const resetSearch = () => {
  searchForm.value = {
    taskName: '',
    status: '',
    timeRange: []
  }
  searchLogs()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  searchLogs()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  searchLogs()
}

const viewLogDetail = (log) => {
  currentLog.value = log
  detailVisible.value = true
}
</script>

<style scoped>
.logs {
  padding: 20px;
}

.toolbar {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.log-detail {
  padding: 20px;
}

.detail-item {
  margin-bottom: 15px;
}

.label {
  font-weight: bold;
  margin-right: 10px;
}

.result {
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  margin-top: 10px;
  white-space: pre-wrap;
  word-break: break-all;
}
</style> 