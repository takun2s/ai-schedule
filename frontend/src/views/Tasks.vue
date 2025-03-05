<template>
  <div class="tasks">
    <div class="toolbar">
      <el-button type="primary" @click="showCreateDialog">
        创建任务
      </el-button>
    </div>
    
    <el-table :data="tasks" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="任务名称" />
      <el-table-column prop="type" label="任务类型" width="120" />
      <el-table-column prop="cron" label="Cron表达式" width="150" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'RUNNING' ? 'success' : 'info'">
            {{ row.status === 'RUNNING' ? '运行中' : '已停止' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button-group>
            <el-button 
              :type="row.status === 'RUNNING' ? 'warning' : 'success'"
              size="small"
              @click="toggleTask(row)"
            >
              {{ row.status === 'RUNNING' ? '停止' : '启动' }}
            </el-button>
            <el-button 
              type="primary" 
              size="small"
              @click="editTask(row)"
            >
              编辑
            </el-button>
            <el-button 
              type="danger" 
              size="small"
              @click="deleteTask(row)"
            >
              删除
            </el-button>
          </el-button-group>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'create' ? '创建任务' : '编辑任务'"
      width="500px"
    >
      <el-form :model="taskForm" label-width="100px">
        <el-form-item label="任务名称">
          <el-input v-model="taskForm.name" />
        </el-form-item>
        <el-form-item label="任务类型">
          <el-select v-model="taskForm.type">
            <el-option label="HTTP" value="HTTP" />
            <el-option label="Shell" value="SHELL" />
            <el-option label="Email" value="EMAIL" />
          </el-select>
        </el-form-item>
        <el-form-item label="Cron表达式">
          <el-input v-model="taskForm.cron" />
        </el-form-item>
        <el-form-item label="任务配置">
          <el-input
            v-model="taskForm.config"
            type="textarea"
            :rows="4"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveTask">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useTaskStore } from '../stores/task'
import { storeToRefs } from 'pinia'

const taskStore = useTaskStore()
const { tasks, loading } = storeToRefs(taskStore)

const dialogVisible = ref(false)
const dialogType = ref('create')
const taskForm = ref({
  name: '',
  type: 'HTTP',
  cron: '',
  config: ''
})

onMounted(async () => {
  await taskStore.fetchTasks()
})

const showCreateDialog = () => {
  dialogType.value = 'create'
  taskForm.value = {
    name: '',
    type: 'HTTP',
    cron: '',
    config: ''
  }
  dialogVisible.value = true
}

const editTask = (task) => {
  dialogType.value = 'edit'
  taskForm.value = { ...task }
  dialogVisible.value = true
}

const saveTask = async () => {
  try {
    if (dialogType.value === 'create') {
      await taskStore.createTask(taskForm.value)
      ElMessage.success('创建成功')
    } else {
      await taskStore.updateTask(taskForm.value.id, taskForm.value)
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    await taskStore.fetchTasks()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const toggleTask = async (task) => {
  try {
    if (task.status === 'RUNNING') {
      await taskStore.stopTask(task.id)
      ElMessage.success('已停止')
    } else {
      await taskStore.startTask(task.id)
      ElMessage.success('已启动')
    }
    await taskStore.fetchTasks()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const deleteTask = (task) => {
  ElMessageBox.confirm(
    '确定要删除该任务吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await taskStore.deleteTask(task.id)
      ElMessage.success('删除成功')
      await taskStore.fetchTasks()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}
</script>

<style scoped>
.tasks {
  padding: 20px;
}

.toolbar {
  margin-bottom: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 