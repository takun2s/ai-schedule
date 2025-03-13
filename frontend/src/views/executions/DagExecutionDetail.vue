<template>
  <div class="execution-detail" v-loading="loading">
    <!-- 基本信息卡片 - 集成进度统计 -->
    <el-card class="info-card">
      <el-row :gutter="20">
        <el-col :span="18">
          <el-descriptions :column="3" border>
            <el-descriptions-item label="DAG名称">{{ execution.dagName }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(execution.status)">{{ execution.status }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ execution.createdAt }}</el-descriptions-item>
            <el-descriptions-item label="执行进度">
              <el-progress 
                :percentage="Math.round(execution.progress || 0)"
                :status="computeProgressStatus"
                :text-inside="true"
                :stroke-width="18">
              </el-progress>
            </el-descriptions-item>
            <el-descriptions-item label="开始时间">{{ execution.startTime }}</el-descriptions-item>
            <el-descriptions-item label="结束时间">{{ execution.endTime || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-col>
        <el-col :span="6">
          <div class="stats-compact">
            <div class="stat-item">
              <span class="label">总任务</span>
              <span class="value">{{ execution.totalTasks }}</span>
            </div>
            <div class="stat-item">
              <span class="label">已完成</span>
              <span class="value success">{{ execution.completedTasks }}</span>
            </div>
            <div class="stat-item">
              <span class="label">运行中</span>
              <span class="value warning">{{ execution.runningTasks }}</span>
            </div>
            <div class="stat-item">
              <span class="label">等待中</span>
              <span class="value info">{{ execution.pendingTasks }}</span>
            </div>
            <div class="stat-item">
              <span class="label">失败</span>
              <span class="value danger">{{ execution.failedTasks }}</span>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- DAG图展示 -->
    <el-card class="dag-graph-card">
      <div slot="header">
        <span>DAG 执行状态图</span>
      </div>
      <div ref="container" class="graph-container"></div>
    </el-card>

    <!-- 任务执行列表 -->
    <el-table :data="execution.taskSummaries" border>
      // ...existing table columns...
    </el-table>

    <!-- 错误信息 -->
    <div v-if="execution.error" class="error-info">
      <pre>{{ execution.error }}</pre>
    </div>

    <!-- 任务详情对话框 -->
    <el-dialog title="任务执行详情" :visible.sync="dialogVisible" width="60%">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="任务名称">{{ currentTask.taskName }}</el-descriptions-item>
        <el-descriptions-item label="节点ID">{{ currentTask.nodeId }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentTask.status)">{{ currentTask.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="耗时">{{ currentTask.duration }}ms</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ formatDateTime(currentTask.startTime) }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ formatDateTime(currentTask.endTime) }}</el-descriptions-item>
      </el-descriptions>
      
      <!-- 输出和错误信息 -->
      <template v-if="currentTask.output || currentTask.error">
        <el-tabs>
          <el-tab-pane v-if="currentTask.output" label="输出">
            <pre>{{ currentTask.output }}</pre>
          </el-tab-pane>
          <el-tab-pane v-if="currentTask.error" label="错误">
            <pre class="error-text">{{ currentTask.error }}</pre>
          </el-tab-pane>
        </el-tabs>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { Graph } from '@antv/x6'
import '@antv/x6-vue-shape'
import { formatDateTime } from '@/utils/date'

export default {
  name: 'DagExecutionDetail',
  props: {
    id: {
      type: [String, Number],
      required: true
    }
  },
  data() {
    return {
      loading: false,
      execution: {
        dagName: '',
        status: '',
        startTime: null,
        endTime: null,
        duration: 0,
        error: null,
        createdAt: null,
        totalTasks: 0,
        completedTasks: 0,
        pendingTasks: 0,
        runningTasks: 0,
        failedTasks: 0,
        progress: 0,
        taskSummaries: []
      },
      dialogVisible: false,
      currentTask: {},
      graph: null,
      graphData: {
        nodes: [],
        edges: []
      }
    }
  },
  created() {
    const executionId = this.id || this.$route.params.id
    if (executionId) {
      this.loadExecutionDetail(executionId)
    }
  },
  computed: {
    computeProgressStatus() {
      const status = this.execution.status;
      // Element UI progress 组件只接受: success/exception/warning 三个值
      if (!status) return undefined;
      
      if (status === 'COMPLETED') return 'success';
      if (status === 'FAILED' || status === 'TIMEOUT') return 'exception';
      if (status === 'RUNNING') return 'warning';
      return undefined;  // 其他状态不设置
    }
  },
  methods: {
    formatDateTime,
    async loadExecutionDetail(id) {
      this.loading = true;
      try {
        const response = await this.$http.get(`/api/executions/dags/${id}`);
        console.log('DAG execution detail response:', response);

        if (response.code === 200 && response.data) {
          // 处理响应数据
          const data = response.data;
          this.execution = {
            ...this.execution,
            id: data.id,
            dagId: data.dagId,
            dagName: data.dagName,
            status: data.status,
            startTime: data.startTime,
            endTime: data.endTime,
            duration: data.duration,
            error: data.error,
            createdAt: data.createdAt,
            totalTasks: data.totalTasks || 0,
            completedTasks: data.completedTasks || 0,
            pendingTasks: data.pendingTasks || 0,
            runningTasks: data.runningTasks || 0,
            failedTasks: data.failedTasks || 0,
            progress: data.progress || 0,
            taskSummaries: Array.isArray(data.taskSummaries) ? data.taskSummaries : []
          };
          console.log('Processed execution data:', this.execution);
        } else {
          throw new Error(response.message || '获取详情失败');
        }
      } catch (error) {
        console.error('Failed to load execution detail:', error);
        this.$message.error('加载执行详情失败：' + error.message);
      } finally {
        this.loading = false;
      }
    },
    getStatusType(status) {
      const types = {
        'PENDING': 'info',
        'RUNNING': 'warning',
        'COMPLETED': 'success',
        'FAILED': 'danger',
        'TIMEOUT': 'danger',
        'STOPPED': 'info'
      }
      return types[status] || 'info'
    },
    showTaskDetail(task) {
      this.currentTask = task
      this.dialogVisible = true
    },
    initGraph() {
      this.graph = new Graph({
        container: this.$refs.container,
        grid: true,
        connecting: {
          snap: true,
          allowBlank: false,
          allowLoop: false,
          highlight: true,
          router: 'manhattan'
        },
        interacting: false,
        scaling: {
          min: 0.2,
          max: 2
        }
      })
    },

    renderGraph() {
      if (!this.graph) {
        this.initGraph()
      }

      // 清空画布
      this.graph.clearCells()

      if (!this.execution.dagId) {
        return
      }

      // 获取DAG配置
      this.$http.get(`/api/dags/${this.execution.dagId}`).then(response => {
        if (response.code === 200 && response.data) {
          const dagConfig = response.data
          let nodes = []
          let edges = []
          
          try {
            nodes = JSON.parse(dagConfig.nodes || '[]')
            edges = JSON.parse(dagConfig.edges || '[]')
            console.log('DAG nodes:', nodes)  // 调试日志
            console.log('DAG edges:', edges)  // 调试日志
          } catch (e) {
            console.error('Failed to parse DAG configuration:', e)
            return
          }

          // 计算节点的初始位置
          const startX = 100
          const startY = 100
          const xGap = 200  // 节点之间的水平间距
          const yGap = 100  // 节点之间的垂直间距
          
          // 创建节点位置映射
          const nodePositions = new Map()
          let currentX = startX
          let currentY = startY

          // 渲染节点
          nodes.forEach((node, index) => {
            const taskStatus = this.getTaskStatus(node.id)
            const taskSummary = this.execution.taskSummaries.find(t => t.nodeId === node.id)
            const taskName = taskSummary ? taskSummary.taskName : ''
            
            // 计算新位置
            const row = Math.floor(index / 3)  // 每行最多3个节点
            const col = index % 3
            const x = startX + col * xGap
            const y = startY + row * yGap
            nodePositions.set(node.id, { x, y })
            
            const nodeConfig = {
              id: node.id,
              shape: 'rect',
              x: x,
              y: y,
              width: 150,
              height: 40,
              attrs: {
                body: {
                  fill: this.getNodeColor(taskStatus),
                  stroke: '#5F95FF',
                  strokeWidth: 1,
                  rx: 4,
                  ry: 4
                },
                label: {
                  text: taskName || '未命名任务',
                  fill: '#ffffff',
                  fontSize: 12,
                  refX: 0.5,
                  refY: 0.5,
                  textAnchor: 'middle',
                  textVerticalAnchor: 'middle'
                }
              }
            }
            
            try {
              this.graph.addNode(nodeConfig)
            } catch (e) {
              console.error('Failed to add node:', node, e)
            }
          })

          // 渲染边
          edges.forEach(edge => {
            try {
              this.graph.addEdge({
                source: edge.source,
                target: edge.target,
                router: {
                  name: 'manhattan',
                  args: {
                    padding: 20
                  }
                },
                connector: {
                  name: 'rounded',
                  args: {
                    radius: 20,
                  },
                },
                attrs: {
                  line: {
                    stroke: '#5F95FF',
                    strokeWidth: 1,
                    targetMarker: {
                      name: 'classic',
                      size: 8
                    }
                  }
                }
              })
            } catch (e) {
              console.error('Failed to add edge:', edge, e)
            }
          })

          // 渲染完所有节点和边后计算合适的缩放比例
          setTimeout(() => {
            // 获取画布尺寸
            const containerWidth = this.$refs.container.clientWidth
            const containerHeight = this.$refs.container.clientHeight
            
            // 获取图形内容尺寸
            const contentBox = this.graph.getContentBBox()
            const contentWidth = contentBox.width
            const contentHeight = contentBox.height
            
            // 计算合适的缩放比例
            const scaleX = (containerWidth - 100) / contentWidth  // 留出50px边距
            const scaleY = (containerHeight - 100) / contentHeight
            const scale = Math.min(scaleX, scaleY, 1)  // 不超过1倍
            const finalScale = Math.max(scale, 0.4)     // 不小于0.4倍
            
            console.log('Container size:', containerWidth, containerHeight)
            console.log('Content size:', contentWidth, contentHeight)
            console.log('Calculated scale:', scale)
            console.log('Final scale:', finalScale)
            
            // 应用缩放
            this.graph.scale(finalScale)
            this.graph.centerContent()
            
            // 最后微调以确保完全可见
            this.graph.zoomToFit({ 
              padding: 50,
              maxScale: 0.8,
              minScale: 0.4
            })
          }, 100)
        }
      }).catch(error => {
        console.error('Failed to load DAG configuration:', error)
        this.$message.error('加载DAG配置失败')
      })
    },

    getTaskStatus(nodeId) {
      const task = this.execution.taskSummaries.find(t => t.nodeId === nodeId)
      return task ? task.status : 'PENDING'
    },

    getNodeColor(status) {
      const colors = {
        'PENDING': '#909399',   // 灰色
        'RUNNING': '#E6A23C',   // 黄色
        'COMPLETED': '#67C23A', // 绿色
        'FAILED': '#F56C6C',    // 红色
        'TIMEOUT': '#F56C6C',   // 红色
        'STOPPED': '#909399'    // 灰色
      }
      return colors[status] || colors.PENDING
    }
  },
  watch: {
    '$route.params.id': {
      handler(newId) {
        if (newId) {
          this.loadExecutionDetail(newId)
        }
      },
      immediate: true
    },
    execution: {
      handler() {
        this.$nextTick(() => {
          this.renderGraph()
        })
      },
      deep: true
    }
  },
  beforeDestroy() {
    if (this.graph) {
      this.graph.dispose()
    }
  }
}
</script>

<style scoped>
.execution-detail {
  padding: 20px;
}
.info-card, .progress-card, .tasks-card {
  margin-bottom: 20px;
}
.stats {
  padding: 10px;
  background: #f8f9fa;
  border-radius: 4px;
}
.stats div {
  margin: 5px 0;
}
.error-info {
  margin-top: 15px;
  padding: 10px;
  background: #fff1f0;
  border: 1px solid #ffa39e;
  border-radius: 4px;
}
.error-info pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}
.error-text {
  color: #f56c6c;
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}
.tasks-card .el-table {
  margin-top: 10px;
}
.empty-tip {
  text-align: center;
  color: #909399;
  padding: 20px 0;
}
.graph-container {
  width: 100%;
  height: 500px;  /* 增加容器高度，给更多显示空间 */
  background-color: #fff;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
  position: relative;  /* 添加相对定位 */
}

.dag-graph-card {
  margin-bottom: 20px;
}

.stats-compact {
  padding: 10px;
  background: #f8f9fa;
  border-radius: 4px;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-around;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 0;
}

.stat-item .label {
  color: #606266;
  font-size: 13px;
}

.stat-item .value {
  font-weight: bold;
  font-size: 14px;
}

.success { color: #67C23A; }
.warning { color: #E6A23C; }
.danger { color: #F56C6C; }
.info { color: #909399; }

.el-progress {
  margin-top: 3px;
}
</style>
