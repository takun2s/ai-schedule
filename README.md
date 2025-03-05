# 说明
**这是由 AI 生成的项目，目前还在调试中**

# 分布式任务调度系统

## 项目简介
这是一个基于Spring Boot + Vue的分布式任务调度系统，支持多种任务类型、分布式部署、任务监控和告警等功能。

## 系统架构
### 技术栈
- 后端：Spring Boot 2.7.x + MyBatis + Spring Security
- 前端：Vue 3 + Element Plus
- 数据库：MySQL 8.0
- 分布式协调：ZooKeeper
- 消息队列：RabbitMQ
- 监控：Prometheus + Grafana

### 核心功能
1. 用户认证与权限控制
   - 基于JWT的认证机制
   - RBAC权限模型
   - 多角色支持

2. 任务管理
   - 支持Shell命令任务
   - 支持JAR包任务
   - 支持手动触发
   - 支持定时调度
   - 支持API触发
   - 支持任务流程编排

3. 分布式特性
   - 基于ZooKeeper的集群管理
   - 任务分片执行
   - 故障转移
   - 负载均衡

4. 监控告警
   - 任务执行状态监控
   - 系统资源监控
   - 自定义告警规则
   - 多渠道告警（邮件、短信、Webhook）

## 项目结构
```
task-scheduler/
├── backend/                # 后端服务
│   ├── common/            # 公共模块
│   ├── scheduler-core/    # 调度核心模块
│   ├── scheduler-api/     # API接口模块
│   └── scheduler-web/     # Web应用模块
├── frontend/              # 前端应用
│   ├── src/
│   └── public/
└── docker/               # Docker配置文件
```

## 快速开始
1. 环境要求
   - JDK 11+
   - Node.js 16+
   - MySQL 8.0+
   - ZooKeeper 3.7+
   - RabbitMQ 3.9+
   - Prometheus + Grafana

2. 配置说明
   - 数据库配置：application.yml
   - ZooKeeper配置：application.yml
   - RabbitMQ配置：application.yml
   - 邮件配置：application.yml

3. 启动步骤
   ```bash
   # 后端启动
   cd backend
   mvn clean package
   java -jar scheduler-web/target/scheduler-web.jar

   # 前端启动
   cd frontend
   npm install
   npm run serve
   ```

## 开发计划
- [x] 基础框架搭建
- [x] 用户认证与权限
- [x] 任务管理
- [x] 分布式调度
- [x] 监控告警
- [ ] 任务流程编排
- [ ] 更多任务类型支持 