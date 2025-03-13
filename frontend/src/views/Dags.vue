<template>
  <div class="dags">
    <div class="page-header">
      <h2>DAG列表</h2>
      <el-button type="primary" @click="createDag">新建DAG</el-button>
    </div>

    <el-table :data="dags" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="cronExpression" label="调度表达式" width="150" />
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="启用" width="80">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.enabled" @change="toggleDag(scope.row)" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250">
        <template slot-scope="scope">
          <el-button size="small" @click="executeDag(scope.row)">执行</el-button>
          <el-button size="small" type="primary" @click="editDag(scope.row)">编辑</el-button>
          <el-button size="small" type="info" @click="viewDag(scope.row)">查看</el-button>
          <el-button size="small" type="danger" @click="deleteDag(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { getErrorMessage } from '@/utils/request'

export default {
  name: 'Dags',
  data() {
    return {
      dags: [],
      loading: false
    }
  },
  methods: {
    getStatusType(status) {
      const types = {
        'RUNNING': 'success',
        'FAILED': 'danger',
        'PENDING': 'warning',
        'COMPLETED': 'info'
      }
      return types[status] || 'info'
    },
    createDag() {
      this.$router.push('/dags/create');
    },
    editDag(dag) {
      this.$router.push(`/dags/edit/${dag.id}`);
    },
    viewDag(dag) {
      this.$router.push(`/dags/view/${dag.id}`);
    },
    executeDag(dag) {
      this.loading = true;
      this.$http.post(`/api/dags/${dag.id}/execute`)
        .then(() => {
          this.$message.success('DAG已开始执行');
          this.loadDags();
        })
        .catch(error => {
          const message = error.response && error.response.data && error.response.data.message;
          this.$message.error(message || '执行失败');
        })
        .finally(() => {
          this.loading = false;
        });
    },
    deleteDag(dag) {
      this.$confirm('确认删除该DAG?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.delete(`/api/dags/${dag.id}`)
          .then(() => {
            this.$message.success('删除成功');
            this.loadDags();
          })
          .catch(error => {
            const message = error.response && error.response.data && error.response.data.message;
            this.$message.error(message || '删除失败');
          });
      });
    },
    toggleDag(dag) {
      this.$http.put(`/api/dags/${dag.id}/toggle`, {
        enabled: dag.enabled
      })
        .then(() => {
          this.$message.success(dag.enabled ? 'DAG已启用' : 'DAG已禁用');
        })
        .catch(error => {
          dag.enabled = !dag.enabled; // 还原状态
          const message = error.response && error.response.data && error.response.data.message;
          this.$message.error(message || '操作失败');
        });
    },
    loadDags() {
      this.loading = true;
      this.$http.get('/api/dags')
        .then(response => {
          this.dags = response.data;
        })
        .catch(error => {
          this.$message.error(getErrorMessage(error));
        })
        .finally(() => {
          this.loading = false;
        });
    }
  },
  created() {
    this.loadDags();
  }
}
</script>

<style scoped>
.dags {
  padding: 20px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>
