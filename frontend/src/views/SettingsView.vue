<template>
  <div class="settings-container">
    <div class="page-header">
      <h2>系统设置</h2>
    </div>
    
    <el-tabs v-model="activeTab">
      <!-- 基本设置 -->
      <el-tab-pane label="基本设置" name="basic">
        <el-card>
          <el-form
            ref="basicForm"
            :model="basicSettings"
            :rules="basicRules"
            label-width="120px"
          >
            <el-form-item label="系统名称" prop="systemName">
              <el-input v-model="basicSettings.systemName" />
            </el-form-item>
            
            <el-form-item label="系统Logo">
              <el-upload
                class="logo-uploader"
                action="/api/upload"
                :show-file-list="false"
                :on-success="handleLogoSuccess"
                :before-upload="beforeLogoUpload"
              >
                <img v-if="basicSettings.logo" :src="basicSettings.logo" class="logo" />
                <el-icon v-else class="logo-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>
            
            <el-form-item label="系统描述" prop="description">
              <el-input
                v-model="basicSettings.description"
                type="textarea"
                :rows="3"
              />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleBasicSubmit">保存</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>
      
      <!-- 邮件设置 -->
      <el-tab-pane label="邮件设置" name="email">
        <el-card>
          <el-form
            ref="emailForm"
            :model="emailSettings"
            :rules="emailRules"
            label-width="120px"
          >
            <el-form-item label="SMTP服务器" prop="host">
              <el-input v-model="emailSettings.host" />
            </el-form-item>
            
            <el-form-item label="SMTP端口" prop="port">
              <el-input-number v-model="emailSettings.port" :min="1" :max="65535" />
            </el-form-item>
            
            <el-form-item label="发件人邮箱" prop="username">
              <el-input v-model="emailSettings.username" />
            </el-form-item>
            
            <el-form-item label="邮箱密码" prop="password">
              <el-input
                v-model="emailSettings.password"
                type="password"
                show-password
              />
            </el-form-item>
            
            <el-form-item label="SSL/TLS">
              <el-switch v-model="emailSettings.ssl" />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleEmailSubmit">保存</el-button>
              <el-button @click="handleTestEmail">测试发送</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>
      
      <!-- 钉钉设置 -->
      <el-tab-pane label="钉钉设置" name="dingtalk">
        <el-card>
          <el-form
            ref="dingtalkForm"
            :model="dingtalkSettings"
            :rules="dingtalkRules"
            label-width="120px"
          >
            <el-form-item label="Webhook地址" prop="webhook">
              <el-input v-model="dingtalkSettings.webhook" />
            </el-form-item>
            
            <el-form-item label="安全设置">
              <el-switch
                v-model="dingtalkSettings.security"
                active-text="加签"
                inactive-text="IP白名单"
              />
            </el-form-item>
            
            <el-form-item
              v-if="dingtalkSettings.security"
              label="加签密钥"
              prop="secret"
            >
              <el-input v-model="dingtalkSettings.secret" />
            </el-form-item>
            
            <el-form-item
              v-else
              label="IP白名单"
              prop="ipWhitelist"
            >
              <el-input
                v-model="dingtalkSettings.ipWhitelist"
                type="textarea"
                :rows="3"
                placeholder="多个IP用逗号分隔"
              />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleDingtalkSubmit">保存</el-button>
              <el-button @click="handleTestDingtalk">测试发送</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>
      
      <!-- 企业微信设置 -->
      <el-tab-pane label="企业微信设置" name="wechat-work">
        <el-card>
          <el-form
            ref="wechatWorkForm"
            :model="wechatWorkSettings"
            :rules="wechatWorkRules"
            label-width="120px"
          >
            <el-form-item label="Webhook地址" prop="webhook">
              <el-input v-model="wechatWorkSettings.webhook" />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleWechatWorkSubmit">保存</el-button>
              <el-button @click="handleTestWechatWork">测试发送</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>
      
      <!-- 短信设置 -->
      <el-tab-pane label="短信设置" name="sms">
        <el-card>
          <el-form
            ref="smsForm"
            :model="smsSettings"
            :rules="smsRules"
            label-width="120px"
          >
            <el-form-item label="服务商">
              <el-select v-model="smsSettings.provider" placeholder="请选择短信服务商">
                <el-option label="阿里云" value="aliyun" />
                <el-option label="腾讯云" value="tencent" />
                <el-option label="华为云" value="huawei" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="AccessKey" prop="accessKey">
              <el-input v-model="smsSettings.accessKey" />
            </el-form-item>
            
            <el-form-item label="SecretKey" prop="secretKey">
              <el-input
                v-model="smsSettings.secretKey"
                type="password"
                show-password
              />
            </el-form-item>
            
            <el-form-item label="签名名称" prop="signName">
              <el-input v-model="smsSettings.signName" />
            </el-form-item>
            
            <el-form-item label="模板代码" prop="templateCode">
              <el-input v-model="smsSettings.templateCode" />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleSmsSubmit">保存</el-button>
              <el-button @click="handleTestSms">测试发送</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const activeTab = ref('basic')

// 基本设置
const basicSettings = reactive({
  systemName: '任务调度系统',
  logo: '',
  description: '分布式任务调度系统'
})

const basicRules = {
  systemName: [
    { required: true, message: '请输入系统名称', trigger: 'blur' }
  ]
}

// 邮件设置
const emailSettings = reactive({
  host: 'smtp.example.com',
  port: 587,
  username: 'noreply@example.com',
  password: '',
  ssl: true
})

const emailRules = {
  host: [
    { required: true, message: '请输入SMTP服务器地址', trigger: 'blur' }
  ],
  port: [
    { required: true, message: '请输入SMTP端口', trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请输入发件人邮箱', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入邮箱密码', trigger: 'blur' }
  ]
}

// 钉钉设置
const dingtalkSettings = reactive({
  webhook: '',
  security: true,
  secret: '',
  ipWhitelist: ''
})

const dingtalkRules = {
  webhook: [
    { required: true, message: '请输入Webhook地址', trigger: 'blur' }
  ],
  secret: [
    { required: true, message: '请输入加签密钥', trigger: 'blur' }
  ],
  ipWhitelist: [
    { required: true, message: '请输入IP白名单', trigger: 'blur' }
  ]
}

// 企业微信设置
const wechatWorkSettings = reactive({
  webhook: ''
})

const wechatWorkRules = {
  webhook: [
    { required: true, message: '请输入Webhook地址', trigger: 'blur' }
  ]
}

// 短信设置
const smsSettings = reactive({
  provider: 'aliyun',
  accessKey: '',
  secretKey: '',
  signName: '',
  templateCode: ''
})

const smsRules = {
  provider: [
    { required: true, message: '请选择短信服务商', trigger: 'change' }
  ],
  accessKey: [
    { required: true, message: '请输入AccessKey', trigger: 'blur' }
  ],
  secretKey: [
    { required: true, message: '请输入SecretKey', trigger: 'blur' }
  ],
  signName: [
    { required: true, message: '请输入签名名称', trigger: 'blur' }
  ],
  templateCode: [
    { required: true, message: '请输入模板代码', trigger: 'blur' }
  ]
}

// Logo上传
const handleLogoSuccess = (response) => {
  basicSettings.logo = response.url
  ElMessage.success('Logo上传成功')
}

const beforeLogoUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 基本设置提交
const handleBasicSubmit = async () => {
  try {
    // TODO: 调用保存API
    ElMessage.success('基本设置保存成功')
  } catch (error) {
    ElMessage.error('基本设置保存失败')
  }
}

// 邮件设置提交
const handleEmailSubmit = async () => {
  try {
    // TODO: 调用保存API
    ElMessage.success('邮件设置保存成功')
  } catch (error) {
    ElMessage.error('邮件设置保存失败')
  }
}

// 测试邮件发送
const handleTestEmail = async () => {
  try {
    // TODO: 调用测试API
    ElMessage.success('测试邮件发送成功')
  } catch (error) {
    ElMessage.error('测试邮件发送失败')
  }
}

// 钉钉设置提交
const handleDingtalkSubmit = async () => {
  try {
    // TODO: 调用保存API
    ElMessage.success('钉钉设置保存成功')
  } catch (error) {
    ElMessage.error('钉钉设置保存失败')
  }
}

// 测试钉钉发送
const handleTestDingtalk = async () => {
  try {
    // TODO: 调用测试API
    ElMessage.success('测试钉钉消息发送成功')
  } catch (error) {
    ElMessage.error('测试钉钉消息发送失败')
  }
}

// 企业微信设置提交
const handleWechatWorkSubmit = async () => {
  try {
    // TODO: 调用保存API
    ElMessage.success('企业微信设置保存成功')
  } catch (error) {
    ElMessage.error('企业微信设置保存失败')
  }
}

// 测试企业微信发送
const handleTestWechatWork = async () => {
  try {
    // TODO: 调用测试API
    ElMessage.success('测试企业微信消息发送成功')
  } catch (error) {
    ElMessage.error('测试企业微信消息发送失败')
  }
}

// 短信设置提交
const handleSmsSubmit = async () => {
  try {
    // TODO: 调用保存API
    ElMessage.success('短信设置保存成功')
  } catch (error) {
    ElMessage.error('短信设置保存失败')
  }
}

// 测试短信发送
const handleTestSms = async () => {
  try {
    // TODO: 调用测试API
    ElMessage.success('测试短信发送成功')
  } catch (error) {
    ElMessage.error('测试短信发送失败')
  }
}
</script>

<style scoped>
.settings-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
}

.logo-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 178px;
  height: 178px;
}

.logo-uploader:hover {
  border-color: #409eff;
}

.logo-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px;
}

.logo {
  width: 178px;
  height: 178px;
  display: block;
}
</style> 