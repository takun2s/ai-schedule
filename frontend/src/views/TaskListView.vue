<template>
  <div class="task-list-container">
    <div class="page-header">
      <h2>任务管理</h2>
      <el-button type="primary" @click="handleCreate">
        <el-icon><Plus /></el-icon>新建任务
      </el-button>
    </div>
    
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="任务名称">
          <el-input v-model="searchForm.name" placeholder="请输入任务名称" clearable />
        </el-form-item>
        <el-form-item label="任务类型">
          <el-select v-model="searchForm.type" placeholder="请选择任务类型" clearable>
            <el-option label="HTTP任务" :value="1" />
            <el-option label="Shell任务" :value="2" />
            <el-option label="数据库任务" :value="3" />
            <el-option label="JAR任务" :value="4" />
            <el-option label="Python任务" :value="5" />
            <el-option label="消息队列任务" :value="6" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
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
        :data="taskList"
        style="width: 100%"
      >
        <el-table-column prop="name" label="任务名称" min-width="150" />
        <el-table-column prop="type" label="任务类型" width="120">
          <template #default="{ row }">
            {{ getTaskTypeText(row.type) }}
          </template>
        </el-table-column>
        <el-table-column prop="cron" label="执行计划" width="150" />
        <el-table-column prop="timeout" label="超时时间(秒)" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-button
                type="primary"
                link
                @click="handleEdit(row)"
              >
                编辑
              </el-button>
              <el-button
                type="primary"
                link
                @click="handleExecute(row)"
              >
                执行
              </el-button>
              <el-button
                type="primary"
                link
                @click="handleLog(row)"
              >
                日志
              </el-button>
              <el-button
                :type="row.status === 1 ? 'danger' : 'success'"
                link
                @click="handleToggleStatus(row)"
              >
                {{ row.status === 1 ? '禁用' : '启用' }}
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
    
    <!-- 执行确认对话框 -->
    <el-dialog
      v-model="executeDialogVisible"
      title="执行确认"
      width="400px"
    >
      <p>确定要立即执行任务"{{ currentTask?.name }}"吗？</p>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="executeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmExecute">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Search,
  Refresh
} from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const executeDialogVisible = ref(false)
const currentTask = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  name: '',
  type: '',
  status: ''
})

const taskList = ref([
  {
    id: 1,
    name: '数据同步任务',
    type: 1,
    cron: '0 0 * * * ?',
    timeout: 300,
    status: 1,
    createTime: '2024-03-05 10:00:00'
  },
  {
    id: 2,
    name: '报表生成任务',
    type: 2,
    cron: '0 0 1 * * ?',
    timeout: 600,
    status: 1,
    createTime: '2024-03-05 10:00:00'
  },
  {
    id: 3,
    name: '数据清理任务',
    type: 3,
    cron: '0 0 2 * * ?',
    timeout: 300,
    status: 0,
    createTime: '2024-03-05 10:00:00'
  }
])

const getTaskTypeText = (type) => {
  const types = {
    1: 'HTTP任务',
    2: 'Shell任务',
    3: '数据库任务',
    4: 'JAR任务',
    5: 'Python任务',
    6: '消息队列任务'
  }
  return types[type] || '未知类型'
}

const handleCreate = () => {
  router.push('/tasks/create')
}

const handleEdit = (row) => {
  router.push(`/tasks/${row.id}/edit`)
}

const handleExecute = (row) => {
  currentTask.value = row
  executeDialogVisible.value = true
}

const confirmExecute = async () => {
  try {
    // TODO: 调用执行API
    ElMessage.success('任务执行成功')
    executeDialogVisible.value = false
  } catch (error) {
    ElMessage.error('任务执行失败')
  }
}

const handleLog = (row) => {
  // TODO: 实现日志查看功能
}

const handleToggleStatus = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要${row.status === 1 ? '禁用' : '启用'}任务"${row.name}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // TODO: 调用状态更新API
    row.status = row.status === 1 ? 0 : 1
    ElMessage.success('状态更新成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('状态更新失败')
    }
  }
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
.task-list-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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
</style> 