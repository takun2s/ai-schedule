<template>
  <el-menu
    :default-active="activeIndex"
    class="nav-menu"
    router
    :collapse="isCollapse"
    background-color="#304156"
    text-color="#bfcbd9"
    active-text-color="#409EFF">
    
    <el-menu-item index="/">
      <i class="el-icon-s-home"></i>
      <span slot="title">首页</span>
    </el-menu-item>

    <el-submenu index="2">
      <template slot="title">
        <i class="el-icon-s-order"></i>
        <span>任务管理</span>
      </template>
      <el-menu-item index="/tasks">任务列表</el-menu-item>
      <el-menu-item index="/tasks/create">创建任务</el-menu-item>
    </el-submenu>

    <el-submenu index="3">
      <template slot="title">
        <i class="el-icon-share"></i>
        <span>DAG管理</span>
      </template>
      <el-menu-item index="/dags">DAG列表</el-menu-item>
      <el-menu-item index="/dags/create">创建DAG</el-menu-item>
    </el-submenu>
  </el-menu>
</template>

<script>
export default {
  name: 'NavMenu',
  data() {
    return {
      isCollapse: false
    }
  },
  computed: {
    activeIndex() {
      return this.$route.path;
    }
  }
}
</script>

<style scoped>
.nav-menu {
  height: 100%;
  border-right: none;
}
.nav-menu:not(.el-menu--collapse) {
  width: 200px;
}
</style>
