<template>
  <div class="home">
    <h1>调度系统</h1>
    <Dashboard />
  </div>
</template>

<script>
import Dashboard from '@/views/dashboard/Dashboard.vue'

export default {
  name: 'Home',
  components: {
    Dashboard
  }
}
</script>

<style scoped>
.home {
  padding: 20px;
}
</style>
