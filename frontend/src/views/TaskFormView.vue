<template>
  <div class="task-form-container">
    <div class="page-header">
      <h2>{{ isEdit ? '编辑任务' : '新建任务' }}</h2>
    </div>
    
    <el-card>
      <el-form
        ref="taskForm"
        :model="taskForm"
        :rules="taskRules"
        label-width="120px"
      >
        <el-form-item label="任务名称" prop="name">
          <el-input v-model="taskForm.name" placeholder="请输入任务名称" />
        </el-form-item>
        
        <el-form-item label="任务类型" prop="type">
          <el-select v-model="taskForm.type" placeholder="请选择任务类型">
            <el-option label="HTTP任务" :value="1" />
            <el-option label="Shell任务" :value="2" />
            <el-option label="数据库任务" :value="3" />
            <el-option label="JAR任务" :value="4" />
            <el-option label="Python任务" :value="5" />
            <el-option label="消息队列任务" :value="6" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="执行计划" prop="cron">
          <el-input v-model="taskForm.cron" placeholder="请输入Cron表达式">
            <template #append>
              <el-button @click="showCronHelp">帮助</el-button>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="超时时间" prop="timeout">
          <el-input-number
            v-model="taskForm.timeout"
            :min="1"
            :max="3600"
            placeholder="请输入超时时间(秒)"
          />
        </el-form-item>
        
        <el-form-item label="任务内容" prop="content">
          <el-input
            v-if="taskForm.type === 1"
            v-model="taskForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入HTTP请求URL"
          />
          <el-input
            v-else-if="taskForm.type === 2"
            v-model="taskForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入Shell命令"
          />
          <el-input
            v-else-if="taskForm.type === 3"
            v-model="taskForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入SQL语句"
          />
          <el-input
            v-else-if="taskForm.type === 4"
            v-model="taskForm.content"
            placeholder="请输入JAR文件路径"
          />
          <el-input
            v-else-if="taskForm.type === 5"
            v-model="taskForm.content"
            placeholder="请输入Python脚本路径"
          />
          <el-input
            v-else-if="taskForm.type === 6"
            v-model="taskForm.content"
            placeholder="请输入消息内容"
          />
        </el-form-item>
        
        <el-form-item label="任务参数" prop="params">
          <el-input
            v-model="taskForm.params"
            type="textarea"
            :rows="2"
            placeholder="请输入任务参数(可选)"
          />
        </el-form-item>
        
        <el-form-item label="告警设置">
          <el-switch v-model="taskForm.alertEnabled" />
          <template v-if="taskForm.alertEnabled">
            <el-form-item label="告警类型" prop="alertType">
              <el-select v-model="taskForm.alertType" placeholder="请选择告警类型">
                <el-option label="邮件" value="email" />
                <el-option label="钉钉" value="dingtalk" />
                <el-option label="企业微信" value="wechat-work" />
                <el-option label="短信" value="sms" />
                <el-option label="Webhook" value="webhook" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="告警接收人" prop="alertReceiver">
              <el-input
                v-model="taskForm.alertReceiver"
                placeholder="请输入告警接收人"
              />
            </el-form-item>
            
            <el-form-item label="告警条件" prop="alertCondition">
              <el-select v-model="taskForm.alertCondition" placeholder="请选择告警条件">
                <el-option label="执行失败" value="failure" />
                <el-option label="执行超时" value="timeout" />
                <el-option label="执行成功" value="success" />
              </el-select>
            </el-form-item>
          </template>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">保存</el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- Cron表达式帮助对话框 -->
    <el-dialog
      v-model="cronHelpVisible"
      title="Cron表达式帮助"
      width="600px"
    >
      <div class="cron-help">
        <h3>常用Cron表达式示例：</h3>
        <el-table :data="cronExamples" style="width: 100%">
          <el-table-column prop="expression" label="表达式" width="180" />
          <el-table-column prop="description" label="说明" />
        </el-table>
        
        <h3>Cron表达式格式：</h3>
        <p>秒 分 时 日 月 周</p>
        <p>每个字段的取值范围：</p>
        <ul>
          <li>秒：0-59</li>
          <li>分：0-59</li>
          <li>时：0-23</li>
          <li>日：1-31</li>
          <li>月：1-12</li>
          <li>周：0-6（0表示周日）</li>
        </ul>
        
        <h3>特殊字符：</h3>
        <ul>
          <li>*：表示任意值</li>
          <li>?：表示不指定值</li>
          <li>-：表示范围</li>
          <li>,：表示多个值</li>
          <li>/：表示间隔</li>
          <li>L：表示最后</li>
          <li>W：表示工作日</li>
          <li>#：表示第几个</li>
        </ul>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const taskForm = ref(null)
const cronHelpVisible = ref(false)

const isEdit = computed(() => route.params.id !== undefined)

const taskForm = reactive({
  name: '',
  type: 1,
  cron: '',
  timeout: 300,
  content: '',
  params: '',
  alertEnabled: false,
  alertType: '',
  alertReceiver: '',
  alertCondition: 'failure'
})

const taskRules = {
  name: [
    { required: true, message: '请输入任务名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择任务类型', trigger: 'change' }
  ],
  cron: [
    { required: true, message: '请输入Cron表达式', trigger: 'blur' }
  ],
  timeout: [
    { required: true, message: '请输入超时时间', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入任务内容', trigger: 'blur' }
  ],
  alertType: [
    { required: true, message: '请选择告警类型', trigger: 'change' }
  ],
  alertReceiver: [
    { required: true, message: '请输入告警接收人', trigger: 'blur' }
  ],
  alertCondition: [
    { required: true, message: '请选择告警条件', trigger: 'change' }
  ]
}

const cronExamples = [
  { expression: '0 0 * * * ?', description: '每小时执行一次' },
  { expression: '0 0 0 * * ?', description: '每天0点执行一次' },
  { expression: '0 0 12 * * ?', description: '每天12点执行一次' },
  { expression: '0 0 0 1 * ?', description: '每月1号0点执行一次' },
  { expression: '0 0 0 ? * MON', description: '每周一0点执行一次' },
  { expression: '0 0/30 * * * ?', description: '每30分钟执行一次' }
]

const showCronHelp = () => {
  cronHelpVisible.value = true
}

const handleSubmit = async () => {
  if (!taskForm.value) return
  
  try {
    await taskForm.value.validate()
    
    // TODO: 调用保存API
    ElMessage.success('保存成功')
    router.push('/tasks')
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

const handleCancel = () => {
  router.back()
}

// 如果是编辑模式，获取任务详情
if (isEdit.value) {
  // TODO: 调用获取详情API
  const taskId = route.params.id
  console.log('获取任务详情:', taskId)
}
</script>

<style scoped>
.task-form-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
}

.cron-help {
  padding: 0 20px;
}

.cron-help h3 {
  margin: 20px 0 10px;
}

.cron-help p {
  margin: 10px 0;
}

.cron-help ul {
  margin: 10px 0;
  padding-left: 20px;
}

.cron-help li {
  margin: 5px 0;
}
</style> 