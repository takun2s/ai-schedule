<template>
  <div class="dag-editor">
    <div class="toolbar">
      <el-button size="small" @click="showTaskSelector">添加任务</el-button>
      <el-button size="small" @click="handleClear">清空</el-button>
      <div class="zoom-controls">
        <el-button-group>
          <el-button size="small" @click="zoomOut">
            <i class="el-icon-zoom-out"></i>
          </el-button>
          <el-button size="small" @click="resetZoom">
            {{ Math.round(zoomLevel * 100) }}%
          </el-button>
          <el-button size="small" @click="zoomIn">
            <i class="el-icon-zoom-in"></i>
          </el-button>
        </el-button-group>
      </div>
    </div>
    <div class="graph-container" ref="container"></div>

    <!-- 任务选择对话框 -->
    <el-dialog title="选择任务" :visible.sync="dialogVisible" width="50%">
      <task-selector ref="taskSelector" @select="handleTaskSelect"/>
    </el-dialog>
  </div>
</template>

<script>
import G6 from '@antv/g6'
import TaskSelector from './TaskSelector.vue'

export default {
  name: 'DagEditor',
  components: { TaskSelector },
  props: {
    initialNodes: {
      type: Array,
      default: () => []
    },
    initialEdges: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      graph: null,
      dialogVisible: false,
      addNodePosition: null,
      zoomLevel: 1
    }
  },
  mounted() {
    // 等待 DOM 更新后再初始化图形
    this.$nextTick(() => {
      this.initGraph()
    })
  },
  methods: {
    initGraph() {
      if (!this.$refs.container) {
        console.error('Container element not found')
        return
      }

      if (this.graph) {
        this.graph.destroy()
      }

      const container = this.$refs.container
      const width = container.offsetWidth || 800
      const height = container.offsetHeight || 600

      const self = this // 保存当前实例的引用

      // 注册连线行为
      G6.registerBehavior('edge-connection', {
        getEvents() {
          return {
            'node:mouseover': 'onNodeOver',
            'node:mouseleave': 'onNodeLeave',
            'node:click': 'onNodeClick',
            'edge:click': 'onEdgeClick'
          }
        },
        onNodeOver(e) {
          const node = e.item
          node.setState('hover', true)
        },
        onNodeLeave(e) {
          const node = e.item
          node.setState('hover', false)
        },
        onNodeClick(e) {
          if (!this.sourceNode) {
            this.sourceNode = e.item
            this.sourceNode.setState('selected', true)
          } else {
            const targetNode = e.item
            if (targetNode !== this.sourceNode) {
              // 添加边时使用新的配置
              this.graph.addItem('edge', {
                source: this.sourceNode.get('id'),
                target: targetNode.get('id'),
                type: 'custom-edge',
                style: {
                  stroke: '#1890ff',
                  lineWidth: 2,
                  endArrow: {
                    path: G6.Arrow.triangle(10, 12, 15),
                    fill: '#1890ff'
                  }
                }
              })
              // 调用外部的更新方法
              self.handleGraphUpdate()
            }
            this.sourceNode.setState('selected', false)
            this.sourceNode = null
          }
        },
        onEdgeClick(e) {
          const edge = e.item
          if (edge && window.confirm('确认删除此连线？')) {
            const graph = this.graph
            try {
              graph.removeItem(edge)
              self.handleGraphUpdate()
            } catch (error) {
              console.error('Error removing edge:', error)
            }
          }
        }
      })

      // 注册自定义边
      G6.registerEdge('custom-edge', {
        draw(cfg, group) {
          const startPoint = cfg.startPoint
          const endPoint = cfg.endPoint
          const shape = group.addShape('path', {
            attrs: {
              stroke: '#1890ff',
              lineWidth: 2,
              path: this.getPath(cfg),
              endArrow: {
                path: G6.Arrow.triangle(10, 12, 15),
                fill: '#1890ff',
              },
              radius: 20,
              cursor: 'pointer'
            },
          })
          return shape
        },
        getPath(cfg) {
          const startPoint = cfg.startPoint
          const endPoint = cfg.endPoint
          const offset = 40

          // 计算两点之间的垂直和水平距离
          const deltaY = Math.abs(endPoint.y - startPoint.y)
          const deltaX = Math.abs(endPoint.x - startPoint.x)

          // 如果垂直距离大于水平距离，使用上下连接点
          if (deltaY > deltaX) {
            const controlPoints = [
              [startPoint.x, startPoint.y + offset],
              [endPoint.x, endPoint.y - offset]
            ]
            return [
              ['M', startPoint.x, startPoint.y],
              ['L', startPoint.x, startPoint.y + offset],
              ['L', endPoint.x, startPoint.y + offset],
              ['L', endPoint.x, endPoint.y],
            ]
          }
          // 否则使用左右连接点
          else {
            return [
              ['M', startPoint.x, startPoint.y],
              ['L', startPoint.x + offset, startPoint.y],
              ['L', endPoint.x - offset, endPoint.y],
              ['L', endPoint.x, endPoint.y],
            ]
          }
        },
      })

      this.graph = new G6.Graph({
        container,
        width,
        height,
        modes: {
          default: ['drag-canvas', 'drag-node', 'edge-connection'] // 移除 zoom-canvas
        },
        defaultNode: {
          type: 'rect',
          size: [160, 50],  // 增加节点尺寸
          style: {
            fill: '#fff',
            stroke: '#1890ff',
            radius: 6,
            cursor: 'pointer',
            shadowColor: '#ccc',
            shadowBlur: 4,
            shadowOffsetX: 2,
            shadowOffsetY: 2
          },
          labelCfg: {
            style: {
              fill: '#333',
              fontSize: 13,  // 增大字体
              fontWeight: 500
            }
          },
          // 添加状态样式
          stateStyles: {
            hover: {
              stroke: '#40a9ff',
              lineWidth: 2
            },
            selected: {
              stroke: '#1890ff',
              lineWidth: 3
            }
          }
        },
        defaultEdge: {
          type: 'custom-edge',
          style: {
            stroke: '#1890ff',
            lineWidth: 2,
            radius: 20,
            cursor: 'pointer',
            opacity: 0.8  // 默认透明度
          },
          stateStyles: {
            hover: {
              stroke: '#40a9ff',
              lineWidth: 3,
              opacity: 1,
              shadowColor: '#1890ff',
              shadowBlur: 10,
              animation: 'ease-in-out'
            }
          }
        },
        // 添加画布配置
        layout: {
          type: 'dagre',
          rankdir: 'LR',        // 从左到右布局
          align: 'UL',          // 上左对齐
          nodesep: 80,          // 同级节点间距
          ranksep: 120,         // 层级间距
          controlPoints: true,   // 边的控制点
          nodesepFunc: () => 100, // 动态节点间距
          ranksepFunc: () => 80,  // 动态层级间距
          preventOverlap: true,   // 防止重叠
          sortByCombo: false,     // 禁用组排序
        },
        fitView: true,
        fitViewPadding: [50, 120, 50, 120], // [top, right, bottom, left]
        animate: true,           // 启用动画
        animateCfg: {
          duration: 500,        // 动画持续时间
          easing: 'easeCubic'   // 动画曲线
        }
      })

      // 监听缩放变化
      this.graph.on('viewportchange', () => {
        this.zoomLevel = this.graph.getZoom()
      })

      // 添加边的鼠标交互事件
      this.graph.on('edge:mouseenter', (e) => {
        try {
          const edge = e.item
          if (!edge) return
          
          edge.setState('hover', true)
          const sourceId = edge.getSource().getModel().id
          const targetId = edge.getTarget().getModel().id
          
          const sourceNode = this.graph.findById(sourceId)
          const targetNode = this.graph.findById(targetId)
          
          if (sourceNode) sourceNode.setState('hover', true)
          if (targetNode) targetNode.setState('hover', true)
        } catch (error) {
          console.error('Error in edge:mouseenter:', error)
        }
      })

      this.graph.on('edge:mouseleave', (e) => {
        try {
          const edge = e.item
          if (!edge) return
          
          edge.setState('hover', false)
          const sourceId = edge.getSource().getModel().id
          const targetId = edge.getTarget().getModel().id
          
          const sourceNode = this.graph.findById(sourceId)
          const targetNode = this.graph.findById(targetId)
          
          if (sourceNode) sourceNode.setState('hover', false)
          if (targetNode) targetNode.setState('hover', false)
        } catch (error) {
          console.error('Error in edge:mouseleave:', error)
        }
      })

      // 监听边的点击
      this.graph.on('edge:click', e => {
        try {
          const edge = e.item
          if (edge && confirm('确认删除此连线？')) {
            this.graph.removeItem(edge)
            this.handleGraphUpdate()
          }
        } catch (error) {
          console.error('Error removing edge:', error)
        }
      })

      // 移除之前的拖拽相关事件
      // 保留其他事件监听...

      // 监听窗口大小变化
      const resizeHandler = () => {
        if (this.graph && this.$refs.container) {
          const { offsetWidth, offsetHeight } = this.$refs.container
          this.graph.changeSize(offsetWidth, offsetHeight)
        }
      }
      window.addEventListener('resize', resizeHandler)

      // 组件销毁时移除事件监听
      this.$once('hook:beforeDestroy', () => {
        window.removeEventListener('resize', resizeHandler)
        if (this.graph) {
          this.graph.destroy()
        }
      })

      // 加载初始数据
      if (this.initialNodes.length > 0 || this.initialEdges.length > 0) {
        this.loadData(this.initialNodes, this.initialEdges)
      }
    },

    loadData(nodes, edges) {
      if (!this.graph) return

      // 清空画布
      this.graph.clear()
      
      // 处理节点数据
      const graphNodes = nodes.map(node => ({
        ...node,
        id: node.id,
        taskId: node.taskId,
        taskName: node.taskName,
        taskType: node.taskType,
        x: node.x || 0,
        y: node.y || 0,
        label: `${node.taskName}(${node.taskType})`,
        style: {
          fill: this.getNodeColor(node.taskType)
        }
      }))

      // 处理边数据
      const graphEdges = edges.map(edge => ({
        id: `edge-${Date.now()}`,
        source: edge.source,
        target: edge.target,
        type: 'cubic-vertical',
        style: {
          stroke: '#1890ff',
          lineWidth: 2,
          opacity: 0.8,
          endArrow: true
        }
      }))

      // 先添加所有节点
      graphNodes.forEach(node => {
        this.graph.addItem('node', node)
      })

      // 等待节点渲染完成后添加边并布局
      this.$nextTick(() => {
        // 添加边
        edges.forEach(edge => {
          if (this.graph.findById(edge.source) && this.graph.findById(edge.target)) {
            this.graph.addItem('edge', {
              source: edge.source,
              target: edge.target,
              type: 'custom-edge'
            })
          }
        })

        // 优化布局和视图
        this.graph.layout()
        setTimeout(() => {
          this.graph.fitView([40, 120, 40, 120])
          this.graph.zoomTo(0.85, {
            x: this.graph.getWidth() / 2,
            y: this.graph.getHeight() / 2
          })
        }, 100)
      })
    },

    showTaskSelector() {
      const container = this.$refs.container
      if (!container) return

      this.addNodePosition = {
        x: (container.offsetWidth || 800) * 0.15,  // 默认宽度
        y: (container.offsetHeight || 600) * 0.4   // 默认高度
      }
      this.dialogVisible = true
    },

    handleClear() {
      this.graph.clear()
      this.handleGraphUpdate()
    },

    handleTaskSelect(task) {
      const container = this.$refs.container
      if (!container) return
      
      // 计算新节点的位置
      const nodes = this.graph.getNodes()
      const x = nodes.length === 0 
        ? container.offsetWidth * 0.2  // 第一个节点放在左侧 20% 处
        : Math.max(...nodes.map(n => n.getModel().x)) + 200  // 后续节点往右排列
      
      const y = nodes.length === 0
        ? container.offsetHeight * 0.4  // 第一个节点放在上方 40% 处
        : container.offsetHeight * 0.4 + (nodes.length % 2) * 100  // 后续节点上下交错

      const node = {
        id: `node-${Date.now()}`,
        taskId: task.id,
        taskName: task.name,
        taskType: task.type,
        x,
        y,
        label: `${task.name}(${task.type})`,
        style: {
          fill: this.getNodeColor(task.type)
        }
      }
      
      this.graph.addItem('node', node)
      this.dialogVisible = false
      
      // 平滑过渡到新布局
      this.graph.layout()
      this.handleGraphUpdate()
    },

    getNodeColor(type) {
      const colors = {
        'COMMAND': '#e6f7ff',
        'HTTP': '#f6ffed',
        'PYTHON': '#fff7e6',
        'JAR': '#fff1f0',
        'SPARK': '#f9f0ff'
      }
      return colors[type] || '#fff'
    },

    handleGraphUpdate() {
      try {
        if (!this.graph) return

        const data = {
          nodes: this.graph.getNodes().map(node => {
            const model = node.getModel()
            return {
              id: model.id,
              taskId: model.taskId,
              taskName: model.taskName,
              taskType: model.taskType,
              x: model.x,
              y: model.y
            }
          }),
          edges: this.graph.getEdges().map(edge => {
            const model = edge.getModel()
            return {
              source: model.source,
              target: model.target
            }
          })
        }
        console.log('Graph update:', data)
        this.$emit('update', data)
      } catch (error) {
        console.error('Error in handleGraphUpdate:', error)
      }
    },

    emitUpdate() {
      try {
        const data = {
          nodes: this.graph.getNodes().map(node => {
            const model = node.getModel()
            return {
              id: model.id,
              taskId: model.taskId,
              taskName: model.taskName,
              taskType: model.taskType,
              x: model.x,
              y: model.y
            }
          }),
          edges: this.graph.getEdges().map(edge => {
            const model = edge.getModel()
            console.log('Edge model:', model) // 调试日志
            return {
              source: model.source,
              target: model.target
            }
          })
        }

        console.log('Emitting update with data:', data) // 调试日志
        this.$emit('update', data)
      } catch (error) {
        console.error('Error in emitUpdate:', error)
      }
    },

    refreshGraph(data) {
      console.log('Refreshing graph with data:', data)
      if (!this.graph) {
        this.initGraph()
      }

      this.graph.clear()
      
      // 先添加所有节点
      const nodes = data.nodes.map(node => ({
        ...node,
        id: node.id,
        taskId: node.taskId,
        taskName: node.taskName,
        taskType: node.taskType,
        label: `${node.taskName}(${node.taskType})`,
        style: {
          fill: this.getNodeColor(node.taskType)
        }
      }))
      
      nodes.forEach(node => {
        this.graph.addItem('node', node)
      })

      // 等节点添加完后再添加边
      this.$nextTick(() => {
        // 添加边
        data.edges.forEach(edge => {
          if (this.graph.findById(edge.source) && this.graph.findById(edge.target)) {
            this.graph.addItem('edge', {
              source: edge.source,
              target: edge.target,
              type: 'cubic-horizontal',
              style: {
                stroke: '#1890ff',
                lineWidth: 2,
                endArrow: true
              }
            })
          }
        })

        // 调整布局和缩放
        this.graph.layout()
        this.graph.fitView()
        this.graph.zoomTo(0.6)
        
        console.log('Graph rendered with:', { 
          nodes: this.graph.getNodes().length,
          edges: this.graph.getEdges().length 
        })
      })
    },

    // 添加缩放控制方法
    zoomIn() {
      const zoom = this.graph.getZoom()
      const nextZoom = Math.min(zoom * 1.2, 2)
      this.graph.zoomTo(nextZoom, {
        x: this.graph.getWidth() / 2,
        y: this.graph.getHeight() / 2
      })
    },

    zoomOut() {
      const zoom = this.graph.getZoom()
      const nextZoom = Math.max(zoom / 1.2, 0.2)
      this.graph.zoomTo(nextZoom, {
        x: this.graph.getWidth() / 2,
        y: this.graph.getHeight() / 2
      })
    },

    resetZoom() {
      this.graph.fitView([40, 120, 40, 120])
      this.graph.zoomTo(0.85, {
        x: this.graph.getWidth() / 2,
        y: this.graph.getHeight() / 2
      })
    }
  },
  beforeDestroy() {
    if (this.graph) {
      this.graph.destroy()
    }
  }
}
</script>

<style scoped>
.dag-editor {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.toolbar {
  padding: 8px;
  border-bottom: 1px solid #eee;
  background: #fff;
  display: flex;
  align-items: center;
}

.graph-container {
  flex: 1;
  overflow: hidden;
  background: #fafafa;
}

.el-button + .el-button {
  margin-left: 10px;
}

.zoom-controls {
  margin-left: auto;
  padding-right: 16px;
}

.el-button-group .el-button {
  padding: 7px 12px;
}
</style>
