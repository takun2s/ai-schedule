<template>
  <el-form ref="form" :model="formData" :rules="rules" label-width="120px">
    <el-form-item label="任务名称" prop="name">
      <el-input v-model="formData.name" placeholder="请输入任务名称"></el-input>
    </el-form-item>
    
    <el-form-item label="任务类型" prop="type">
      <el-select v-model="formData.type" placeholder="请选择任务类型">
        <el-option label="HTTP请求" value="HTTP"></el-option>
        <el-option label="命令行" value="COMMAND"></el-option>
        <el-option label="Python脚本" value="PYTHON"></el-option>
        <el-option label="JAR包" value="JAR"></el-option>
        <el-option label="Spark任务" value="SPARK"></el-option>
      </el-select>
    </el-form-item>

    <el-form-item 
      :label="getCommandLabel" 
      :prop="getCommandProp"
      required>
      <el-input 
        v-if="formData.type === 'HTTP'"
        v-model="formData.httpUrl" 
        type="text"
        placeholder="请输入HTTP请求URL">
      </el-input>
      <template v-else-if="formData.type === 'JAR'">
        <el-upload
          class="jar-uploader"
          :action="uploadUrl"
          :on-success="handleUploadSuccess"
          :on-error="handleUploadError"
          :before-upload="beforeJarUpload"
          accept=".jar"
          :limit="1">
          <el-button size="small" type="primary">上传JAR文件</el-button>
          <div slot="tip" class="el-upload__tip">只能上传jar文件</div>
        </el-upload>
        <el-input 
          v-model="formData.jarPath" 
          placeholder="JAR包路径"
          :readonly="true"
          style="margin-top: 10px">
        </el-input>
        <el-input 
          v-model="formData.mainClass" 
          style="margin-top: 10px"
          placeholder="请输入主类名(可选)">
        </el-input>
        <el-input 
          v-model="formData.command" 
          style="margin-top: 10px"
          placeholder="请输入程序参数(可选)">
        </el-input>
      </template>
      <template v-else-if="formData.type === 'SPARK'">
        <el-input 
          v-model="formData.sparkMaster" 
          placeholder="Spark Master地址"
          style="margin-bottom: 10px">
        </el-input>
        <el-input 
          v-model="formData.sparkAppName" 
          placeholder="应用名称"
          style="margin-bottom: 10px">
        </el-input>
        <el-input 
          v-model="formData.sparkMainClass" 
          placeholder="主类名"
          style="margin-bottom: 10px">
        </el-input>
        <el-input 
          v-model="formData.sparkArgs" 
          type="textarea"
          :rows="3"
          placeholder="程序参数">
        </el-input>
      </template>
      <el-input 
        v-else
        v-model="formData.command" 
        type="textarea"
        :rows="3"
        placeholder="请输入执行命令">
      </el-input>
    </el-form-item>

    <template v-if="formData.type === 'HTTP'">
      <el-form-item label="请求方法" prop="httpMethod">
        <el-select v-model="formData.httpMethod" placeholder="请选择请求方法">
          <el-option label="GET" value="GET"></el-option>
          <el-option label="POST" value="POST"></el-option>
          <el-option label="PUT" value="PUT"></el-option>
          <el-option label="DELETE" value="DELETE"></el-option>
        </el-select>
      </el-form-item>
    </template>

    <el-form-item label="任务描述" prop="description">
      <el-input 
        type="textarea"
        v-model="formData.description" 
        :rows="2"
        placeholder="请输入任务描述">
      </el-input>
    </el-form-item>
    
    <!-- 执行计划 移到高级配置前面 -->
    <el-form-item label="执行计划" prop="cronExpression">
      <el-input v-model="formData.cronExpression" placeholder="输入Cron表达式">
        <el-button slot="append" @click="showCronHelper">
          <i class="el-icon-edit-outline"></i>
          生成
        </el-button>
      </el-input>
    </el-form-item>

    <el-dialog title="Cron表达式生成" :visible.sync="showCronDialog" width="660px">
      <cron-helper v-model="formData.cronExpression"></cron-helper>
    </el-dialog>

    <!-- 高级配置折叠面板 放到最后 -->
    <el-collapse>
      <el-collapse-item title="高级配置">
        <el-form-item label="告警邮箱" prop="alertEmail">
          <el-input 
            v-model="formData.alertEmail"
            placeholder="执行失败时通知的邮箱地址">
          </el-input>
        </el-form-item>

        <el-form-item label="失败告警">
          <el-switch v-model="formData.alertOnFailure"></el-switch>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="超时时间" prop="timeout">
              <el-input-number 
                v-model="formData.timeout"
                :min="0"
                :controls-position="'right'"
                placeholder="单位: 秒">
              </el-input-number>
            </el-form-item>
          </el-col>
          
          <el-col :span="8">
            <el-form-item label="重试次数" prop="retryCount">
              <el-input-number 
                v-model="formData.retryCount"
                :min="0"
                :controls-position="'right'">
              </el-input-number>
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="优先级" prop="priority">
              <el-input-number 
                v-model="formData.priority"
                :min="0"
                :max="100"
                :controls-position="'right'">
              </el-input-number>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="执行机器" prop="executeMachine">
          <el-input 
            v-model="formData.executeMachine"
            placeholder="指定执行机器">
          </el-input>
        </el-form-item>

        <el-form-item label="工作目录" prop="workDir">
          <el-input 
            v-model="formData.workDir"
            placeholder="任务执行的工作目录">
          </el-input>
        </el-form-item>

        <!-- HTTP任务特有配置 -->
        <template v-if="formData.type === 'HTTP'">
          <el-form-item label="HTTP Headers">
            <key-value-editor v-model="formData.httpHeaders"></key-value-editor>
          </el-form-item>
          <el-form-item label="请求体" prop="httpBody">
            <el-input 
              type="textarea"
              v-model="formData.httpBody"
              :rows="4"
              placeholder="HTTP请求体内容">
            </el-input>
          </el-form-item>
        </template>

        <!-- Python任务特有配置 -->
        <template v-if="formData.type === 'PYTHON'">
          <el-form-item label="Python版本">
            <el-select v-model="formData.pythonVersion">
              <el-option label="Python 2" value="python2"></el-option>
              <el-option label="Python 3" value="python3"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="依赖包">
            <el-input 
              type="textarea"
              v-model="formData.requirements"
              :rows="3"
              placeholder="每行一个依赖包，格式：包名==版本号">
            </el-input>
          </el-form-item>
        </template>

        <!-- JAR任务特有配置 -->
        <template v-if="formData.type === 'JAR'">
          <el-form-item label="JVM参数" prop="javaOpts">
            <el-input 
              v-model="formData.javaOpts"
              placeholder="如: -Xmx2g -Xms1g">
            </el-input>
          </el-form-item>
        </template>

        <!-- Spark任务特有配置 -->
        <template v-if="formData.type === 'SPARK'">
          <el-form-item label="Spark配置" prop="sparkConfig">
            <key-value-editor v-model="formData.sparkConfig"></key-value-editor>
          </el-form-item>
        </template>
      </el-collapse-item>
    </el-collapse>

    <el-form-item>
      <el-button type="primary" @click="submitForm">保存</el-button>
      <el-button @click="$router.back()">取消</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import CronHelper from '@/components/CronHelper.vue'

export default {
  name: 'TaskForm',
  components: {
    CronHelper
  },
  props: {
    initialData: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      formData: {
        id: null,
        name: '',
        description: '',
        type: 'HTTP',
        command: '',
        cronExpression: '',
        
        // HTTP任务字段
        httpUrl: '',
        httpMethod: 'GET',
        httpHeaders: {},
        httpBody: '',
        
        // Python任务字段
        pythonPath: '',
        pythonVersion: 'python3',
        requirements: '',
        
        // JAR任务字段
        jarPath: '',
        mainClass: '',
        javaOpts: '',  // 新增
        
        // Spark任务字段
        sparkMaster: '',
        sparkAppName: '',
        sparkMainClass: '',
        sparkConfig: '',  // 新增
        sparkArgs: '',
        
        // 任务配置字段
        alertEmail: '',
        alertOnFailure: false,
        retryCount: 0,
        timeout: 3600,
        executeMachine: '',
        priority: 0,    // 新增
        workDir: '',    // 新增
        
        // 状态字段
        status: 'CREATED',
        output: '',     // 新增
        error: '',      // 新增
        lastExecuteTime: null,
        nextExecuteTime: null,
        createTime: null,
        updateTime: null,
        
        ...this.initialData
      },
      uploadUrl: '/api/files/upload',  // 文件上传接口
      rules: {
        name: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
        type: [{ required: true, message: '请选择任务类型', trigger: 'change' }],
        description: [{ required: false, message: '请输入任务描述', trigger: 'blur' }],
        command: [{ required: true, message: '请输入执行命令', trigger: 'blur' }],
        httpUrl: [
          { required: true, message: '请输入HTTP请求URL', trigger: 'blur' },
          { type: 'url', message: '请输入有效的URL地址', trigger: 'blur' }
        ],
        pythonPath: [{ required: true, message: '请输入Python脚本路径', trigger: 'blur' }],
        jarPath: [{ required: true, message: '请输入JAR包路径', trigger: 'blur' }],
        sparkMaster: [{ required: true, message: '请输入Spark Master地址', trigger: 'blur' }],
        sparkMainClass: [{ required: true, message: '请输入Spark主类名', trigger: 'blur' }],
        timeout: [{ type: 'number', message: '超时时间必须为数字', trigger: 'blur' }],
        retryCount: [{ type: 'number', message: '重试次数必须为数字', trigger: 'blur' }],
        cronExpression: [{ required: false, message: '请输入Cron表达式', trigger: 'blur' }],
        javaOpts: [{ required: false }],
        sparkConfig: [{ required: false }],
        priority: [{ type: 'number', message: '优先级必须为数字', trigger: 'blur' }],
        workDir: [{ required: false }]
      },
      showCronDialog: false
    }
  },
  created() {
    if (this.initialData.cronExpression) {
      this.scheduleType = 'cron'
    }
    this.formData = { ...this.formData, ...this.initialData }
  },
  computed: {
    getCommandLabel() {
      const labels = {
        'HTTP': 'URL',
        'JAR': 'JAR配置',
        'SPARK': 'Spark配置',
        'COMMAND': '命令',
        'PYTHON': '命令'
      }
      return labels[this.formData.type] || '命令'
    },
    getCommandProp() {
      const props = {
        'HTTP': 'httpUrl',
        'JAR': 'jarPath',
        'SPARK': 'sparkMainClass',
        'COMMAND': 'command',
        'PYTHON': 'command'
      }
      return props[this.formData.type] || 'command'
    }
  },
  methods: {
    async submitForm() {
      try {
        await this.$refs.form.validate()
        const submitData = { ...this.formData }
        
        // 根据任务类型处理特定字段
        switch (submitData.type) {
          case 'HTTP': {  // 添加块级作用域
            submitData.command = submitData.httpUrl
            submitData.jarPath = null
            submitData.mainClass = null
            submitData.javaOpts = null
            submitData.pythonPath = null
            submitData.pythonVersion = null
            submitData.requirements = null
            submitData.sparkMaster = null
            submitData.sparkAppName = null
            submitData.sparkMainClass = null
            submitData.sparkConfig = null
            submitData.sparkArgs = null
            break;
          }
            
          case 'JAR': {  // 添加块级作用域
            const jarCommand = [`java`]
            if (submitData.javaOpts) {
              jarCommand.push(submitData.javaOpts)
            }
            jarCommand.push('-jar', submitData.jarPath)
            if (submitData.mainClass) {
              jarCommand.push(submitData.mainClass)
            }
            if (submitData.command) {
              jarCommand.push(submitData.command)
            }
            submitData.command = jarCommand.join(' ')

            submitData.httpUrl = null
            submitData.httpMethod = null
            submitData.httpHeaders = null
            submitData.httpBody = null
            submitData.pythonPath = null
            submitData.pythonVersion = null
            submitData.requirements = null
            submitData.sparkMaster = null
            submitData.sparkAppName = null
            submitData.sparkMainClass = null
            submitData.sparkConfig = null
            submitData.sparkArgs = null
            break;
          }
            
          case 'SPARK': {  // 添加块级作用域
            const sparkCommand = [`spark-submit --master ${submitData.sparkMaster}`]
            if (submitData.sparkAppName) {
              sparkCommand.push(`--name "${submitData.sparkAppName}"`)
            }
            if (submitData.sparkMainClass) {
              sparkCommand.push(`--class ${submitData.sparkMainClass}`)
            }
            if (submitData.sparkConfig) {
              Object.entries(submitData.sparkConfig).forEach(([key, value]) => {
                sparkCommand.push(`--conf "${key}=${value}"`)
              })
            }
            if (submitData.sparkArgs) {
              sparkCommand.push(submitData.sparkArgs)
            }
            submitData.command = sparkCommand.join(' ')

            submitData.httpUrl = null
            submitData.httpMethod = null
            submitData.httpHeaders = null
            submitData.httpBody = null
            submitData.jarPath = null
            submitData.mainClass = null
            submitData.javaOpts = null
            submitData.pythonPath = null
            submitData.pythonVersion = null
            submitData.requirements = null
            break;
          }
            
          case 'PYTHON': {  // 添加块级作用域
            const pythonCommand = [
              submitData.pythonVersion || 'python3',
              submitData.pythonPath
            ]
            if (submitData.command) {
              pythonCommand.push(submitData.command)
            }
            submitData.command = pythonCommand.join(' ')
            
            // 清理其他类型字段
            submitData.httpUrl = null
            submitData.httpMethod = null
            submitData.httpHeaders = null
            submitData.httpBody = null
            submitData.jarPath = null
            submitData.mainClass = null
            submitData.javaOpts = null
            submitData.sparkMaster = null
            submitData.sparkAppName = null
            submitData.sparkMainClass = null
            submitData.sparkConfig = null
            submitData.sparkArgs = null
            break;
          }
        }

        // 确保必要字段不为空
        if (!submitData.command) {
          throw new Error('命令不能为空')
        }

        // 设置默认值
        if (submitData.timeout === '') submitData.timeout = 3600;
        if (submitData.retryCount === '') submitData.retryCount = 0;
        if (submitData.priority === '') submitData.priority = 0;
        
        this.$emit('submit', submitData)
      } catch (err) {
        console.error('Form validation failed:', err)
        this.$message.error(err.message || '表单验证失败')
      }
    },
    handleUploadSuccess(response) {  // 移除未使用的file参数
      if (response.code === 200) {
        this.formData.jarPath = response.data
        this.$message.success('JAR包上传成功')
      } else {
        this.$message.error('JAR包上传失败')
      }
    },
    handleUploadError() {
      this.$message.error('JAR包上传失败')
    },
    beforeJarUpload(file) {
      const isJar = file.name.endsWith('.jar')
      if (!isJar) {
        this.$message.error('只能上传JAR文件!')
      }
      return isJar
    },
    showCronHelper() {
      this.showCronDialog = true
    }
  }
}
</script>

<style lang="scss" scoped>
.jar-uploader {
  margin-bottom: 10px;
}

.el-upload__tip {
  line-height: 1.2;
  padding-top: 5px;
  color: #909399;
}

.cron-help {
  font-size: 13px;
  line-height: 1.5;
  
  pre {
    background: #f5f5f5;
    padding: 8px;
    border-radius: 4px;
    margin: 8px 0;
  }
  
  ul {
    margin: 8px 0;
    padding-left: 20px;
    
    li {
      margin: 4px 0;
    }
  }
}

.schedule-type {
  margin-bottom: 15px;
}

.cron-input {
  margin-top: 10px;
}

.no-margin {
  margin-bottom: 0;
}

.cron-help {
  h4 {
    margin-top: 0;
    margin-bottom: 15px;
  }
  
  .el-table {
    margin-bottom: 0;
  }
}

.cron-tips {
  color: #909399;
  font-size: 12px;
  line-height: 1.4;
  padding-top: 4px;

  .el-icon-info {
    margin-right: 4px;
  }
}

.cron-tips {
  margin-top: 5px;
  color: #909399;
  font-size: 12px;
  
  .el-icon-time {
    margin-right: 4px;
  }
}

.el-collapse {
  margin-bottom: 20px;
}
</style>
