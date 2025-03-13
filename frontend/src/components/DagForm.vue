<template>
  <el-form ref="form" :model="dagForm" :rules="rules" label-width="120px">
    <el-form-item label="DAG名称" prop="name">
      <el-input v-model="dagForm.name"></el-input>
    </el-form-item>

    <el-form-item label="调度表达式" prop="cronExpression">
      <cron-expression 
        v-model="dagForm.cronExpression"
        placeholder="请输入Cron表达式，例如：0 0 * * * ?"
      />
    </el-form-item>

    <el-form-item label="任务节点">
      <el-button type="primary" @click="addNode">添加节点</el-button>
      <div class="nodes-container">
        <el-card v-for="(node, index) in dagForm.nodes" :key="index" class="node-card">
          <div slot="header">
            <span>节点 {{index + 1}}</span>
            <el-button type="text" @click="removeNode(index)" style="float: right; color: #F56C6C">删除</el-button>
          </div>
          <el-form-item label="选择任务">
            <el-select v-model="node.taskId" filterable placeholder="请选择任务">
              <el-option
                v-for="task in tasks"
                :key="task.id"
                :label="task.name"
                :value="task.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="依赖节点">
            <el-select v-model="node.dependencies" multiple placeholder="选择依赖节点">
              <el-option
                v-for="(n, i) in dagForm.nodes"
                :key="i"
                :label="`节点 ${i + 1}`"
                :value="i"
                :disabled="i === index">
              </el-option>
            </el-select>
          </el-form-item>
        </el-card>
      </div>
    </el-form-item>

    <el-form-item label="DAG图">
      <dag-graph 
        v-model="dagForm.graph"
        :tasks="tasks"
        @change="handleGraphChange">
      </dag-graph>
    </el-form-item>

    <el-form-item>
      <el-button type="primary" @click="submitForm">保存</el-button>
      <el-button @click="$router.back()">取消</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import DagGraph from './DagGraph.vue'
import CronExpression from './CronExpression.vue'

export default {
  name: 'DagForm',
  components: {
    DagGraph,
    CronExpression
  },
  props: {
    initData: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      tasks: [],
      dagForm: {
        name: '',
        cronExpression: '',
        nodes: [],
        graph: {
          nodes: [],
          edges: []
        },
        ...this.initData
      },
      rules: {
        name: [{ required: true, message: '请输入DAG名称', trigger: 'blur' }],
        cronExpression: [{ required: true, message: '请输入调度表达式', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.loadTasks();
  },
  methods: {
    loadTasks() {
      this.$http.get('/api/tasks')
        .then(response => {
          this.tasks = response.data;
        });
    },
    addNode() {
      this.dagForm.nodes.push({
        taskId: '',
        dependencies: []
      });
    },
    removeNode(index) {
      this.dagForm.nodes.splice(index, 1);
      // 更新其他节点的依赖
      this.dagForm.nodes.forEach(node => {
        node.dependencies = node.dependencies.filter(dep => dep !== index)
          .map(dep => dep > index ? dep - 1 : dep);
      });
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.$emit('submit', this.dagForm);
        }
      });
    },
    testCron() {
      if (this.dagForm.cronExpression) {
        this.$http.post('/api/utils/cron/next', {
          expression: this.dagForm.cronExpression
        }).then(response => {
          this.$message.info(`下次执行时间: ${response.data}`);
        });
      }
    },
    handleGraphChange(graph) {
      // 将图形数据转换为节点依赖关系
      const nodes = graph.nodes.map(node => ({
        taskId: node.taskId,
        dependencies: graph.edges
          .filter(edge => edge.target === node.id)
          .map(edge => graph.nodes.findIndex(n => n.id === edge.source))
          .filter(index => index !== -1)
      }));
      
      this.dagForm.nodes = nodes;
    }
  }
}
</script>

<style scoped>
.nodes-container {
  margin-top: 20px;
}
.node-card {
  margin-bottom: 20px;
}
</style>
