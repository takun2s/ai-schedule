<template>
  <el-container class="app-wrapper">
    <el-aside width="200px">
      <div class="logo">调度系统</div>
      <el-menu
        :router="true"
        :default-active="$route.path"
        :collapse="isCollapse"
        class="el-menu-vertical"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF">
        
        <el-menu-item index="/">
          <i class="el-icon-s-home"></i>
          <span>首页</span>
        </el-menu-item>

        <el-submenu index="task">
          <template slot="title">
            <i class="el-icon-s-order"></i>
            <span>任务管理</span>
          </template>
          <el-menu-item index="/tasks">任务列表</el-menu-item>
          <el-menu-item index="/tasks/edit">创建任务</el-menu-item>
        </el-submenu>

        <el-submenu index="dag">
          <template slot="title">
            <i class="el-icon-share"></i>
            <span>DAG管理</span>
          </template>
          <el-menu-item index="/dags">DAG列表</el-menu-item>
          <el-menu-item index="/dags/edit">创建DAG</el-menu-item>
        </el-submenu>

        <el-menu-item index="/executions">
          <i class="el-icon-time"></i>
          <span slot="title">执行记录</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header height="50px">
        <div class="header-left">
          <i 
            :class="['el-icon-s-fold', 'collapse-btn']"
            @click="toggleCollapse"
          ></i>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              {{ username }}<i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  name: 'Layout',
  data() {
    return {
      username: localStorage.getItem('username') || '用户',
      isCollapse: false
    }
  },
  methods: {
    toggleCollapse() {
      this.isCollapse = !this.isCollapse
    },
    handleCommand(command) {
      if (command === 'logout') {
        localStorage.removeItem('token')
        localStorage.removeItem('username')
        this.$router.push('/login')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.app-wrapper {
  height: 100vh;
}

.logo {
  height: 50px;
  line-height: 50px;
  text-align: center;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  background: #2b2f3a;
}

.el-menu-vertical {
  border-right: none;

  &:not(.el-menu--collapse) {
    width: 200px;
  }
}

.el-header {
  background-color: white;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left {
  .collapse-btn {
    font-size: 20px;
    cursor: pointer;
    transition: color 0.3s;
    
    &:hover {
      color: #409EFF;
    }
  }
}

.header-right {
  .el-dropdown-link {
    cursor: pointer;
    color: #409EFF;
  }
}

.el-main {
  background-color: #f0f2f5;
  padding: 0;
}

.el-aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}
</style>
