# 分布式任务调度系统使用文档

## 1. 系统概述

本系统是一个基于Spring Boot和Vue 3开发的分布式任务调度系统，支持多种任务类型和告警方式，具有高可用性和可扩展性。

### 1.1 主要功能

- 任务管理：支持HTTP、Shell、数据库、JAR、Python和消息队列等多种任务类型
- 任务调度：基于Cron表达式的灵活调度配置
- 执行记录：详细记录任务执行历史和日志
- 告警管理：支持邮件、钉钉、企业微信、短信和Webhook等多种告警方式
- 系统设置：可配置的系统参数和告警渠道设置

### 1.2 技术架构

- 后端：Spring Boot + MyBatis + ZooKeeper
- 前端：Vue 3 + Element Plus + Pinia
- 数据库：MySQL
- 缓存：Redis

## 2. 快速开始

### 2.1 环境要求

- JDK 1.8+
- Node.js 16+
- MySQL 5.7+
- Redis 6.0+
- ZooKeeper 3.6+

### 2.2 安装步骤

1. 克隆代码仓库
```bash
git clone [repository_url]
```

2. 配置数据库
```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE scheduler DEFAULT CHARACTER SET utf8mb4;
```

3. 修改配置文件
```yaml
# backend/common/src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/scheduler
    username: your_username
    password: your_password
```

4. 启动后端服务
```bash
cd backend
mvn clean package
java -jar common/target/scheduler-common.jar
```

5. 启动前端服务
```bash
cd frontend
npm install
npm run dev
```

## 3. 功能使用说明

### 3.1 任务管理

#### 3.1.1 创建任务

1. 点击"任务管理" -> "新建任务"
2. 填写任务信息：
   - 任务名称：任务的唯一标识
   - 任务类型：选择任务执行方式
   - 执行计划：配置Cron表达式
   - 超时时间：任务执行超时时间
   - 任务内容：根据任务类型填写具体内容
   - 任务参数：可选的任务参数

#### 3.1.2 任务类型说明

1. HTTP任务
   - 内容格式：完整的URL地址
   - 参数：URL参数（可选）

2. Shell任务
   - 内容格式：Shell命令
   - 参数：命令参数（可选）

3. 数据库任务
   - 内容格式：SQL语句
   - 参数：SQL参数（可选）

4. JAR任务
   - 内容格式：JAR文件路径
   - 参数：JVM参数（可选）

5. Python任务
   - 内容格式：Python脚本路径
   - 参数：脚本参数（可选）

6. 消息队列任务
   - 内容格式：消息内容
   - 参数：队列参数（可选）

### 3.2 告警配置

#### 3.2.1 告警类型

1. 邮件告警
   - 配置SMTP服务器信息
   - 设置发件人和收件人

2. 钉钉告警
   - 配置Webhook地址
   - 设置安全配置（签名/IP白名单）

3. 企业微信告警
   - 配置Webhook地址

4. 短信告警
   - 配置短信服务商信息
   - 设置签名和模板

5. Webhook告警
   - 配置回调地址
   - 设置请求方式

#### 3.2.2 告警条件

- 执行失败：任务执行失败时触发
- 执行超时：任务执行超时时触发
- 执行成功：任务执行成功时触发

### 3.3 系统设置

#### 3.3.1 基本设置

- 系统名称：显示在页面标题
- 系统Logo：显示在页面左上角
- 系统描述：系统功能说明

#### 3.3.2 告警渠道设置

1. 邮件设置
   - SMTP服务器地址
   - SMTP端口
   - 发件人邮箱
   - 邮箱密码
   - SSL/TLS配置

2. 钉钉设置
   - Webhook地址
   - 安全设置（签名/IP白名单）

3. 企业微信设置
   - Webhook地址

4. 短信设置
   - 服务商选择
   - AccessKey
   - SecretKey
   - 签名名称
   - 模板代码

## 4. 常见问题

### 4.1 任务执行失败

1. 检查任务配置是否正确
2. 查看执行日志定位问题
3. 确认执行环境是否正常

### 4.2 告警发送失败

1. 检查告警配置是否正确
2. 验证告警渠道是否可用
3. 查看告警日志定位问题

### 4.3 系统性能问题

1. 检查数据库连接池配置
2. 监控系统资源使用情况
3. 优化任务执行策略

## 5. 安全建议

1. 定期修改密码
2. 使用强密码策略
3. 及时更新系统
4. 配置访问控制
5. 启用操作日志

## 6. 联系支持

如遇到问题，请通过以下方式获取支持：

- 提交Issue：[repository_url]/issues
- 发送邮件：support@example.com
- 查看文档：[documentation_url]

## 7. 版本历史

### v1.0.0 (2024-03-05)
- 初始版本发布
- 支持基本任务管理功能
- 支持多种告警方式
- 提供完整的Web界面 