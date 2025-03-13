<template>
  <div class="login-container">
    <div ref="container" class="graph-container"></div>
    
    <el-card class="login-card">
      <div slot="header">
        <span>系统登录</span>
      </div>
      
      <el-form ref="loginForm" :model="loginForm" :rules="rules">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="用户名">
            <i slot="prefix" class="el-icon-user"></i>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="密码">
            <i slot="prefix" class="el-icon-lock"></i>
          </el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" style="width: 100%" @click="handleLogin" :loading="loading">
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import auth from '@/utils/auth'
import G6 from '@antv/g6'

export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      loading: false
    }
  },
  mounted() {
    this.initBackground()
  },
  beforeDestroy() {
    if (this.animationFrame) {
      cancelAnimationFrame(this.animationFrame)
    }
  },
  methods: {
    async handleLogin() {
      try {
        await this.$refs.loginForm.validate()
        this.loading = true
        
        const response = await this.$http.post('/api/auth/login', this.loginForm)
        
        if (response.code === 200) {
          auth.setToken(response.data.token)
          
          // 优先使用 query 参数中的 redirect，其次使用存储的路径
          const redirect = this.$route.query.redirect || auth.getLastPath() || '/'
          auth.clearLastPath()
          
          this.$message.success('登录成功')
          this.$router.push(redirect)
        } else {
          throw new Error(response.message || '登录失败')
        }
      } catch (error) {
        console.error('Login error:', error)
        this.$message.error(error.message || '登录失败')
      } finally {
        this.loading = false
      }
    },
    initBackground() {
      this.$nextTick(() => {
        const container = this.$refs.container
        
        // 自定义节点
        G6.registerNode('task-node', {
          draw(cfg, group) {
            const { status = 'PENDING' } = cfg

            // 重新设计状态颜色
            const colors = {
              PENDING: { 
                fill: '#1a1a2e', 
                stroke: '#30475e',
                textColor: '#7a8c98'
              },
              RUNNING: { 
                fill: '#1a2c42', 
                stroke: '#2463a5',
                textColor: '#3e9cff'
              },
              COMPLETED: { 
                fill: '#162b22', 
                stroke: '#046d4f',
                textColor: '#00cf8a'
              },
              FAILED: { 
                fill: '#2d1f1f', 
                stroke: '#a83240',
                textColor: '#ff4d6f'
              }
            }

            const color = colors[status] || colors.PENDING

            const keyShape = group.addShape('rect', {
              attrs: {
                x: -40,
                y: -15,
                width: 80,
                height: 30,
                radius: 4,
                fill: color.fill,
                stroke: color.stroke,
                lineWidth: 1.5,
                cursor: 'default',
                shadowColor: color.stroke,
                shadowBlur: 5,
                shadowOffsetX: 0,
                shadowOffsetY: 0
              },
              name: 'rect-shape'
            })

            // 更新文本样式
            group.addShape('text', {
              attrs: {
                text: cfg.label,
                x: 0,
                y: -4,
                fontSize: 10,
                fill: color.textColor,
                textAlign: 'center',
                textBaseline: 'middle',
                fontFamily: '"Menlo", "Monaco", monospace'
              },
              name: 'label-shape'
            })

            group.addShape('text', {
              attrs: {
                text: status,
                x: 0,
                y: 8,
                fontSize: 9,
                fill: color.textColor,
                textAlign: 'center',
                textBaseline: 'middle',
                fontFamily: '"Menlo", "Monaco", monospace',
                opacity: 0.8
              },
              name: 'status-shape'
            })

            return keyShape
          },
          
          afterUpdate(cfg, node) {
            // ...复用上面定义的颜色配置...
            const colors = {
              PENDING: { fill: '#1a1a2e', stroke: '#30475e', textColor: '#7a8c98' },
              RUNNING: { fill: '#1a2c42', stroke: '#2463a5', textColor: '#3e9cff' },
              COMPLETED: { fill: '#162b22', stroke: '#046d4f', textColor: '#00cf8a' },
              FAILED: { fill: '#2d1f1f', stroke: '#a83240', textColor: '#ff4d6f' }
            }
            
            const status = cfg.status || 'PENDING'
            const color = colors[status]
            const group = node.getContainer()
            const [shape, label, statusText] = group.get('children')
            
            shape.attr('fill', color.fill)
            shape.attr('stroke', color.stroke)
            shape.attr('shadowColor', color.stroke)
            label.attr('fill', color.textColor)
            statusText.attr('fill', color.textColor)
          }
        })

        // 创建 G6 图实例
        const graph = new G6.Graph({
          container,
          width: window.innerWidth,
          height: window.innerHeight,
          fitView: true,  // 重新启用自动适配
          fitViewPadding: [60, 80, 60, 80],  // 恢复原来的边距
          defaultNode: {
            type: 'task-node',
            size: [70, 28]  // 进一步减小节点大小
          },
          defaultEdge: {
            type: 'cubic-horizontal',
            style: {
              stroke: '#304c75',  // 更深的线条颜色
              lineWidth: 1.2,
              opacity: 0.6,
              shadowColor: '#1b3b6a',
              shadowBlur: 10,
              endArrow: {
                path: G6.Arrow.triangle(4, 6, 0),
                fill: '#304c75',
                stroke: '#304c75'
              }
            }
          },
          layout: {
            type: 'dagre',
            rankdir: 'LR',
            align: 'DL',
            nodesep: 35,    // 恢复原来的节点间距
            ranksep: 50,    // 恢复原来的层级间距
            controlPoints: true,
            // 增加布局参数
            margin: [40, 60],  // 恢复原来的边距
            maxIteration: 1000,
            nodesepFunc: () => 30,
            ranksepFunc: () => 40
          },
          minZoom: 0.6,    // 允许更小的缩放
          maxZoom: 0.1,    // 限制最大缩放
          zoom: 0.8        // 设置初始缩放比例为 0.6
        })

        // 生成随机 DAG 数据
        function generateDagData() {
          // 调整节点生成参数
          const nodeWidth = 70   // 减小节点宽度
          const nodeHeight = 28  // 减小节点高度
          const nodeMargin = 10  // 恢复原来的节点间距
          const levelMargin = 30 // 恢复原来的层级间距
          const loginCardWidth = 500 // 增加登录框安全区域
          const loginCardHeight = 400 // 登录框高度加边距

          // 计算可用空间时考虑边距和缩放
          const availableWidth = (window.innerWidth - 240) * 0.5  // 考虑缩放因子
          const availableHeight = (window.innerHeight - 200) * 0.5

          // 调整节点数量
          const levels = Math.floor(availableWidth / (nodeWidth + levelMargin)) // 恢复原来的节点数量计算
          const nodesPerLevel = Math.floor(availableHeight / (nodeHeight + nodeMargin)) // 恢复原来的节点数量计算
          const firstLevelNodes = Math.ceil(nodesPerLevel / 2.5)  // 第一层节点数为总数的1/4
          console.log('nodesPerLevel, firstLevelNodes', nodesPerLevel, firstLevelNodes)
          
          const nodes = []
          const edges = []

          // 生成节点，为第一层特殊处理
          for (let i = 0; i < levels; i++) {
            const currentLevelNodes = i === 0 ? firstLevelNodes : Math.ceil((Math.random() + 0.1) * levels)
            const xPos = i * (nodeWidth + levelMargin)
            const centerX = window.innerWidth / 2
            const centerY = window.innerHeight / 2

            // 优化节点垂直分布
            for (let j = 0; j < currentLevelNodes; j++) {
              const yPos = j * (nodeHeight + nodeMargin)
              // 向上偏移以保持垂直居中
              const actualY = yPos - (currentLevelNodes * (nodeHeight + nodeMargin)) / 2 + centerY

              // 跳过登录框区域时保持连续性
              if (Math.abs(xPos - centerX) < loginCardWidth/2 && 
                  Math.abs(actualY - centerY) < loginCardHeight/2) {
                continue
              }

              nodes.push({
                id: `${i}-${j}`,
                label: `Task ${i}-${j}`,
                status: 'PENDING',
                level: i
              })
            }
          }

          // 优化边的生成逻辑，确保合理的连接
          for (let i = 1; i < levels; i++) {
            const currentNodes = nodes.filter(n => n.level === i)
            const prevNodes = nodes.filter(n => n.level === (i - 1))
            
            if (prevNodes.length === 0) continue
            
            currentNodes.forEach(node => {
              // 为每个节点选择1-2个邻近的父节点
              const nearestParents = findNearestParents(node, prevNodes, 2)
              nearestParents.forEach(parent => {
                edges.push({
                  source: parent.id,
                  target: node.id
                })
              })
            })
          }

          return { nodes, edges }
        }

        // 添加查找最近父节点的辅助函数
        function findNearestParents(node, parentNodes, maxCount) {
          const nodeIndex = parseInt(node.id.split('-')[1])
          
          // 按照与当前节点的距离排序父节点
          const sortedParents = [...parentNodes].sort((a, b) => {
            const aIndex = parseInt(a.id.split('-')[1])
            const bIndex = parseInt(b.id.split('-')[1])
            return Math.abs(aIndex - nodeIndex) - Math.abs(bIndex - nodeIndex)
          })
          
          // 随机选择1-2个最近的父节点
          const count = 1 + Math.floor(Math.random() * maxCount)
          return sortedParents.slice(0, count)
        }

        // 修改状态更新函数
        function updateNodesStatus() {
          if (!graph) return
          
          const nodes = graph.getNodes()
          if (!nodes || nodes.length === 0) return
          
          const edges = graph.getEdges() || []
          
          // 按层级排序节点
          const sortedNodes = [...nodes].sort((a, b) => {
            const levelA = parseInt(a.getModel().id.split('-')[0])
            const levelB = parseInt(b.getModel().id.split('-')[0])
            return levelA - levelB
          })

          // 配置参数
          const SUCCESS_RATE = 0.9   // 成功概率 90%
          const COMPLETE_RATE = 0.5  // 完成概率 50%

          // 获取节点的直接父节点状态
          function getParentStates(nodeId) {
            return edges
              .filter(edge => edge.getModel().target === nodeId)
              .map(edge => {
                const parentNode = nodes.find(n => n.getModel().id === edge.getModel().source)
                return parentNode ? parentNode.getModel().status : null
              })
              .filter(Boolean)
          }

          // 更新每个节点的状态
          sortedNodes.forEach(node => {
            const model = node.getModel()
            const level = parseInt(model.id.split('-')[0])
            const parentStates = getParentStates(model.id)
            
            let newStatus = model.status
            if (level === 0) {
              // 第一层节点逻辑
              if (model.status === 'PENDING') {
                newStatus = 'RUNNING'  // 第一层节点直接开始运行
              } else if (model.status === 'RUNNING' && Math.random() < COMPLETE_RATE) {
                newStatus = Math.random() < SUCCESS_RATE ? 'COMPLETED' : 'FAILED'
              }
            } else if (parentStates.length > 0) {
              // 检查所有父节点状态
              const allParentsCompleted = parentStates.every(s => s === 'COMPLETED')
              const anyParentFailed = parentStates.some(s => s === 'FAILED')

              if (anyParentFailed) {
                newStatus = 'PENDING'
              } else if (allParentsCompleted) { // 所有父节点都完成才能执行
                if (model.status === 'PENDING') {
                  newStatus = 'RUNNING'
                } else if (model.status === 'RUNNING' && Math.random() < COMPLETE_RATE) {
                  newStatus = Math.random() < SUCCESS_RATE ? 'COMPLETED' : 'FAILED'
                }
              }
            }

            // 更新节点状态
            if (newStatus !== model.status) {
              console.log(`节点 ${model.id} (Level ${level}): ${model.status} -> ${newStatus}, 父节点状态: ${JSON.stringify(parentStates)}`)
              graph.updateItem(node, {
                ...model,
                status: newStatus
              })
            }
          })
        }

        // 初始化数据
        const data = generateDagData()
        graph.data(data)
        graph.render()
        setTimeout(() => {
          graph.zoom(0.8)     // 设置缩放比例为 0.6
          graph.fitCenter()   // 居中显示
          graph.moveCanvas(0, 0)  // 重置位置
        }, 100)

        // 自动更新状态
        setInterval(() => {
          updateNodesStatus()
        }, 1000)  // 从 2000 改为 1000

        // 自动调整视图
        graph.fitView()

        // 监听窗口大小变化
        window.addEventListener('resize', () => {
          if (!graph || graph.get('destroyed')) return
          graph.changeSize(window.innerWidth, window.innerHeight)
          graph.fitView()
        })

        // 组件销毁时清理
        this.$once('hook:beforeDestroy', () => {
          graph.destroy()
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
  background: #f0f2f5;
}

.graph-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  background: linear-gradient(135deg, #0f1726 0%, #162238 100%);  // 深色渐变背景
  opacity: 0.95;  // 提高不透明度
  overflow: hidden;  // 添加这行确保不会出现滚动条
  transform-origin: center center;  // 添加这行以确保正确的缩放中心点
}

.login-card {
  width: 350px;
  position: relative;
  z-index: 2;  // 确保卡片在图形上层
  margin: 0 auto;  // 居中显示
  background: rgba(255, 255, 255, 0.98);  // 增加登录框不透明度
  backdrop-filter: blur(8px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);  // 加强阴影效果
  
  :deep(.el-card__header) {
    text-align: center;
    font-size: 18px;
    border-bottom: none;
    padding-bottom: 0;
  }
}

.el-input {
  :deep(.el-input__prefix) {
    left: 5px;
    top: 0;
    height: 100%;
    display: flex;
    align-items: center;
  }
  
  :deep(input) {
    padding-left: 35px;
    background: rgba(255, 255, 255, 0.9);
  }
}
</style>
