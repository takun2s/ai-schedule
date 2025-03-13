<template>
  <div class="dag-edit">
    <el-card class="form-card">
      <div slot="header">
        <span>{{ isEdit ? '编辑DAG' : '创建DAG' }}</span>
      </div>
      
      <el-form ref="form" :model="dagForm" :rules="rules" label-width="120px">
        <el-form-item label="DAG名称" prop="name">
          <el-input v-model="dagForm.name"></el-input>
        </el-form-item>
        
        <el-form-item label="Cron表达式">
          <el-input v-model="dagForm.cronExpression" placeholder="示例: 0 0 * * * (每天0点执行) 或留空为手动执行">
            <template slot="append">
              <el-popover placement="bottom" width="400" trigger="click">
                <cron-helper v-model="dagForm.cronExpression"/>
                <el-button slot="reference">辅助生成</el-button>
              </el-popover>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="描述">
          <el-input type="textarea" v-model="dagForm.description" rows="3"></el-input>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="editor-card">
      <div slot="header">
        <span>任务节点编排</span>
      </div>
      
      <dag-editor 
        ref="dagEditor"
        :initial-nodes="nodes"
        :initial-edges="edges"
        @update="handleGraphUpdate"
      />
    </el-card>

    <div class="actions">
      <el-button type="primary" @click="handleSave">保存</el-button>
      <el-button @click="$router.push('/dags')">取消</el-button>
    </div>
  </div>
</template>

<script>
import CronHelper from '@/components/CronHelper.vue'
import DagEditor from './components/DagEditor.vue'

export default {
  name: 'DagEdit',
  components: {
    CronHelper,
    DagEditor
  },
  data() {
    return {
      isEdit: false,
      dagForm: {
        name: '',
        cronExpression: '',
        description: ''
      },
      nodes: [],
      edges: [],
      rules: {
        name: [{ required: true, message: '请输入DAG名称', trigger: 'blur' }]
      }
    }
  },
  created() {
    const { id } = this.$route.params
    if (id) {
      this.isEdit = true
      this.loadDag(id)
    }
  },
  methods: {
    async loadDag(id) {
      try {
        const response = await this.$http.get(`/api/dags/${id}`)
        console.log('DAG load response:', response)

        if (response.code === 200 && response.data) {
          const dag = response.data
          // 更新表单数据
          this.dagForm = {
            name: dag.name || '',
            cronExpression: dag.cronExpression || '',
            description: dag.description || ''
          }
          
          // 转换数据格式
          try {
            this.nodes = JSON.parse(dag.nodes || '[]')
            this.edges = JSON.parse(dag.edges || '[]')
            
            // 打印转换后的数据
            console.log('Converted graph data:', {
              nodes: this.nodes,
              edges: this.edges
            })

            // 等待视图更新
            await this.$nextTick()
            if (this.$refs.dagEditor) {
              this.$refs.dagEditor.refreshGraph({
                nodes: this.nodes,
                edges: this.edges
              })
            }
          } catch (e) {
            console.error('Failed to parse graph data:', e)
            this.$message.error('加载图形数据失败')
          }
        }
      } catch (error) {
        console.error('Load DAG error:', error)
        this.$message.error('加载DAG失败')
        this.$router.push('/dags')
      }
    },
    handleGraphUpdate({ nodes, edges }) {
      this.nodes = nodes
      this.edges = edges
    },
    async handleSave() {
      try {
        const dagData = {
          ...this.dagForm,
          nodes: JSON.stringify(this.nodes),
          edges: JSON.stringify(this.edges)
        }

        if (this.isEdit) {
          await this.$http.put(`/api/dags/${this.$route.params.id}`, dagData)
          this.$message.success('更新成功')
        } else {
          await this.$http.post('/api/dags', dagData)
          this.$message.success('创建成功')
        }
        
        this.$router.push('/dags')
      } catch (error) {
        console.error('Save DAG error:', error)
        this.$message.error(this.isEdit ? '更新失败' : '创建失败')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.dag-edit {
  padding: 20px;
  height: calc(100vh - 84px);  // 调整整体高度
  display: flex;
  flex-direction: column;
  gap: 20px;
  background: #f0f2f5;
}

.form-card {
  flex: none;
}

.editor-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 600px;  // 增加画布最小高度

  :deep(.el-card__body) {
    flex: 1;
    padding: 0;
    height: 100%;
    overflow: hidden;
    min-height: 550px;  // 设置内容区最小高度
  }
}

.actions {
  text-align: center;
  padding: 20px 0;
  background: white;
  margin: 0 -20px -20px;
  border-top: 1px solid #eee;
}
</style>
