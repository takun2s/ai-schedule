<template>
  <div class="task-selector">
    <!-- 搜索栏 -->
    <el-form :inline="true" class="search-form">
      <el-form-item label="任务名称">
        <el-input
          v-model="search"
          placeholder="搜索任务名称"
          prefix-icon="el-icon-search"
          clearable
          @clear="loadTasks">
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadTasks">搜索</el-button>
      </el-form-item>
    </el-form>

    <!-- 任务列表表格 -->
    <el-table
      :data="filteredTasks"
      v-loading="loading"
      height="400px"
      @row-click="handleSelect"
      highlight-current-row>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="name" label="名称" min-width="150"></el-table-column>
      <el-table-column prop="type" label="类型" width="100">
        <template slot-scope="scope">
          <el-tag size="small">{{ scope.row.type }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" show-overflow-tooltip></el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  name: 'TaskSelector',
  data() {
    return {
      tasks: [],
      loading: false,
      search: ''
    }
  },
  computed: {
    filteredTasks() {
      if (!this.search) return this.tasks
      const searchLower = this.search.toLowerCase()
      return this.tasks.filter(task => 
        task.name.toLowerCase().includes(searchLower) ||
        (task.description && task.description.toLowerCase().includes(searchLower))
      )
    }
  },
  created() {
    this.loadTasks()
  },
  methods: {
    async loadTasks() {
      this.loading = true
      try {
        const response = await this.$http.get('/api/tasks')
        if (response.code === 200) {
          this.tasks = response.data
        }
      } catch (error) {
        console.error('Failed to load tasks:', error)
        this.$message.error('加载任务列表失败')
      } finally {
        this.loading = false
      }
    },
    handleSelect(row) {
      this.$emit('select', row)
    }
  }
}
</script>

<style scoped>
.task-selector {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.search-form {
  margin-bottom: 10px;
}

.el-table {
  cursor: pointer;
}

.el-table .current-row {
  background-color: #f0f9ff;
}
</style>
