<template>
  <div class="home">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>任务总数</span>
            </div>
          </template>
          <div class="card-content">
            <h2>{{ stats.totalTasks }}</h2>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>运行中任务</span>
            </div>
          </template>
          <div class="card-content">
            <h2>{{ stats.runningTasks }}</h2>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>今日执行次数</span>
            </div>
          </template>
          <div class="card-content">
            <h2>{{ stats.todayExecutions }}</h2>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useStatsStore } from '../stores/stats'
import { storeToRefs } from 'pinia'

const statsStore = useStatsStore()
const { stats } = storeToRefs(statsStore)

onMounted(async () => {
  await statsStore.fetchStats()
})
</script>

<style scoped>
.home {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-content {
  text-align: center;
  padding: 20px 0;
}

h2 {
  margin: 0;
  font-size: 36px;
  color: #409EFF;
}
</style> 