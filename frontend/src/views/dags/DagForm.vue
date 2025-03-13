<template>
  <!-- ...existing code... -->
</template>

<script>
export default {
  data() {
    return {
      id: null,
      form: {
        name: '',
        description: '',
        cronExpression: ''
      },
      nodes: [],
      edges: [],
      loading: false
    }
  },
  created() {
    // 从路由参数获取ID
    this.id = this.$route.params.id
    if (this.id) {
      this.loadDagData()
    }
  },
  methods: {
    async loadDagData() {
      this.loading = true
      try {
        console.log('Loading DAG with ID:', this.id); // 调试日志
        const response = await this.$http.get(`/api/dags/${this.id}`);
        console.log('DAG API Response:', response); // 调试日志

        if (response.code === 200 && response.data) {
          const dagData = response.data;
          
          // 更新表单数据
          this.form = {
            name: dagData.name,
            description: dagData.description,
            cronExpression: dagData.cronExpression
          };
          
          // 直接从数据库中获取的JSON字符串解析为对象
          if (typeof dagData.nodes === 'string') {
            this.nodes = JSON.parse(dagData.nodes);
          } else {
            this.nodes = dagData.nodes || [];
          }

          if (typeof dagData.edges === 'string') {
            this.edges = JSON.parse(dagData.edges);
          } else {
            this.edges = dagData.edges || [];
          }

          console.log('Parsed Data:', { // 调试日志
            form: this.form,
            nodes: this.nodes,
            edges: this.edges
          });

          // 等待DOM更新后再加载画布
          await this.$nextTick();
          if (this.$refs.workflow) {
            this.$refs.workflow.initGraph(); // 初始化画布
            this.$refs.workflow.loadData(this.nodes, this.edges); // 加载数据
          } else {
            console.error('Workflow component not found');
          }
        }
      } catch (error) {
        console.error('Failed to load DAG:', error);
        this.$message.error('加载DAG数据失败');
      } finally {
        this.loading = false;
      }
    },
    // ...existing code...
  }
}
</script>

<style scoped>
/* ...existing code... */
</style>
