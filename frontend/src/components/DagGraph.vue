<template>
  <div class="dag-graph">
    <div class="toolbar">
      <el-button-group>
        <el-button size="small" icon="el-icon-plus" @click="addNode">添加节点</el-button>
        <el-button size="small" icon="el-icon-delete" @click="deleteSelected">删除选中</el-button>
        <el-button size="small" icon="el-icon-refresh" @click="autoLayout">自动布局</el-button>
      </el-button-group>
    </div>
    <div ref="container" class="graph-container"></div>
    <el-dialog title="节点配置" :visible.sync="dialogVisible" width="500px">
      <el-form :model="currentNode" label-width="100px">
        <el-form-item label="选择任务">
          <el-select v-model="currentNode.taskId" filterable placeholder="请选择任务">
            <el-option
              v-for="task in tasks"
              :key="task.id"
              :label="task.name"
              :value="task.id">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveNode">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import G6 from '@antv/g6';
import { mix } from '@antv/util';

export default {
  name: 'DagGraph',
  props: {
    value: {
      type: Object,
      default: () => ({ nodes: [], edges: [] })
    },
    tasks: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      graph: null,
      dialogVisible: false,
      currentNode: {},
      selectedItem: null
    }
  },
  mounted() {
    this.initGraph();
  },
  methods: {
    initGraph() {
      const container = this.$refs.container;
      
      this.graph = new G6.Graph({
        container,
        width: container.scrollWidth,
        height: container.scrollHeight || 500,
        modes: {
          default: ['drag-canvas', 'zoom-canvas', 'drag-node', 'click-select'],
          addEdge: ['click-add-edge']
        },
        defaultNode: {
          type: 'circle',
          size: [60],
          style: {
            fill: '#91d5ff',
            stroke: '#40a9ff'
          },
          labelCfg: {
            style: {
              fill: '#000000A6',
              fontSize: 10
            }
          }
        },
        defaultEdge: {
          type: 'cubic-horizontal',
          style: {
            stroke: '#91d5ff',
            endArrow: true
          }
        }
      });

      // 注册事件
      this.graph.on('node:click', this.handleNodeClick);
      this.graph.on('edge:click', this.handleEdgeClick);
      
      // 渲染数据
      this.graph.data(this.value);
      this.graph.render();
    },
    
    addNode() {
      const id = `node-${Date.now()}`;
      const node = {
        id,
        label: '新节点',
        x: 100,
        y: 100,
        taskId: null
      };
      
      this.currentNode = node;
      this.dialogVisible = true;
    },
    
    saveNode() {
      if (!this.currentNode.taskId) {
        this.$message.warning('请选择任务');
        return;
      }
      
      const task = this.tasks.find(t => t.id === this.currentNode.taskId);
      this.currentNode.label = task.name;
      
      if (!this.currentNode._created) {
        this.graph.addItem('node', this.currentNode);
        this.currentNode._created = true;
      } else {
        this.graph.updateItem(this.currentNode.id, {
          label: this.currentNode.label,
          taskId: this.currentNode.taskId
        });
      }
      
      this.dialogVisible = false;
      this.emitChange();
    },
    
    deleteSelected() {
      if (this.selectedItem) {
        this.graph.removeItem(this.selectedItem);
        this.selectedItem = null;
        this.emitChange();
      }
    },
    
    autoLayout() {
      const layout = new G6.Layout.Dagre({
        rankdir: 'LR',
        align: 'UL',
        nodesep: 20,
        ranksep: 50
      });
      
      layout.execute(this.graph);
    },
    
    handleNodeClick(e) {
      this.selectedItem = e.item;
      const node = e.item.getModel();
      this.currentNode = mix({}, node);
      this.dialogVisible = true;
    },
    
    handleEdgeClick(e) {
      this.selectedItem = e.item;
    },
    
    emitChange() {
      const data = this.graph.save();
      this.$emit('input', data);
      this.$emit('change', data);
    }
  },
  beforeDestroy() {
    if (this.graph) {
      this.graph.destroy();
    }
  }
}
</script>

<style scoped>
.dag-graph {
  border: 1px solid #eee;
  border-radius: 4px;
  padding: 20px;
}
.toolbar {
  margin-bottom: 20px;
}
.graph-container {
  height: 600px;
  background: #fafafa;
}
</style>
