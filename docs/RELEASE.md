# 版本发布说明

## 1. 版本历史

### v1.0.0 (2024-01-01)
- 初始版本发布
- 支持基本的任务调度功能
- 支持多种任务类型（HTTP、Shell、数据库等）
- 支持多种告警方式（邮件、钉钉、企业微信等）

### v1.1.0 (2024-02-01)
- 新增任务执行记录功能
- 新增任务执行日志查看功能
- 新增任务执行统计功能
- 优化任务执行性能

### v1.2.0 (2024-03-01)
- 新增任务依赖管理功能
- 新增任务执行超时控制
- 新增任务执行重试机制
- 优化告警通知功能

## 2. 更新内容

### v1.0.0 更新内容

#### 2.1.1 功能特性
1. 任务管理
   - 支持创建、编辑、删除任务
   - 支持启用/禁用任务
   - 支持手动执行任务
   - 支持查看任务详情

2. 任务类型
   - HTTP任务：支持GET、POST等请求方式
   - Shell任务：支持执行Shell脚本
   - 数据库任务：支持执行SQL语句
   - JAR任务：支持执行JAR包
   - Python任务：支持执行Python脚本
   - 消息队列任务：支持发送消息

3. 告警通知
   - 邮件告警：支持SMTP发送邮件
   - 钉钉告警：支持Webhook发送消息
   - 企业微信告警：支持Webhook发送消息
   - 短信告警：支持发送短信通知

#### 2.1.2 系统要求
- JDK 1.8+
- MySQL 5.7+
- Redis 6.0+
- Node.js 16+

### v1.1.0 更新内容

#### 2.2.1 功能特性
1. 执行记录
   - 记录任务执行历史
   - 记录执行开始时间
   - 记录执行结束时间
   - 记录执行状态
   - 记录执行结果

2. 执行日志
   - 记录详细执行日志
   - 支持日志级别控制
   - 支持日志文件轮转
   - 支持日志实时查看

3. 执行统计
   - 统计执行成功次数
   - 统计执行失败次数
   - 统计执行总时长
   - 统计平均执行时长

#### 2.2.2 数据库变更
```sql
-- 创建执行记录表
CREATE TABLE task_execution (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME,
    duration BIGINT,
    status TINYINT NOT NULL,
    result TEXT,
    error_message TEXT,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES task(id)
);

-- 创建执行日志表
CREATE TABLE task_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    execution_id BIGINT NOT NULL,
    level VARCHAR(10) NOT NULL,
    content TEXT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (execution_id) REFERENCES task_execution(id)
);
```

### v1.2.0 更新内容

#### 2.3.1 功能特性
1. 任务依赖
   - 支持设置任务依赖关系
   - 支持多级任务依赖
   - 支持依赖任务执行状态检查
   - 支持依赖任务执行结果传递

2. 超时控制
   - 支持设置任务超时时间
   - 支持超时自动终止
   - 支持超时告警通知
   - 支持超时重试机制

3. 重试机制
   - 支持设置重试次数
   - 支持设置重试间隔
   - 支持设置重试条件
   - 支持重试结果通知

#### 2.3.2 数据库变更
```sql
-- 创建任务依赖表
CREATE TABLE task_dependency (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT NOT NULL,
    dependency_task_id BIGINT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES task(id),
    FOREIGN KEY (dependency_task_id) REFERENCES task(id)
);

-- 修改任务表，添加超时和重试字段
ALTER TABLE task
ADD COLUMN timeout INT NOT NULL DEFAULT 300,
ADD COLUMN retry_count INT NOT NULL DEFAULT 0,
ADD COLUMN retry_interval INT NOT NULL DEFAULT 60;
```

## 3. 升级指南

### 3.1 升级步骤

1. 备份数据
```bash
# 备份数据库
mysqldump -u root -p'password' scheduler > scheduler_backup.sql

# 备份配置文件
tar -czf config_backup.tar.gz /etc/scheduler/
```

2. 停止服务
```bash
# 停止后端服务
systemctl stop scheduler

# 停止前端服务
pm2 stop scheduler-frontend
```

3. 更新代码
```bash
# 更新后端代码
cd /opt/scheduler/backend
git pull origin v1.2.0

# 更新前端代码
cd /opt/scheduler/frontend
git pull origin v1.2.0
```

4. 更新依赖
```bash
# 更新后端依赖
cd /opt/scheduler/backend
mvn clean install -DskipTests

# 更新前端依赖
cd /opt/scheduler/frontend
npm install
```

5. 执行数据库变更
```bash
# 执行数据库变更脚本
mysql -u root -p'password' scheduler < upgrade_v1.2.0.sql
```

6. 启动服务
```bash
# 启动后端服务
systemctl start scheduler

# 启动前端服务
pm2 start scheduler-frontend
```

### 3.2 升级注意事项

1. 数据兼容性
   - 确保数据库版本兼容
   - 确保数据格式兼容
   - 确保配置格式兼容

2. 服务兼容性
   - 确保JDK版本兼容
   - 确保Node.js版本兼容
   - 确保中间件版本兼容

3. 功能兼容性
   - 确保API接口兼容
   - 确保任务类型兼容
   - 确保告警方式兼容

## 4. 已知问题

### 4.1 已修复问题

1. v1.0.0
   - 修复任务执行超时问题
   - 修复告警发送失败问题
   - 修复日志记录不完整问题

2. v1.1.0
   - 修复执行记录丢失问题
   - 修复日志文件过大问题
   - 修复统计数据不准确问题

3. v1.2.0
   - 修复任务依赖死锁问题
   - 修复重试机制失效问题
   - 修复超时控制不准确问题

### 4.2 待修复问题

1. 性能问题
   - 大量任务并发执行时的性能问题
   - 大量日志记录时的性能问题
   - 大量告警发送时的性能问题

2. 功能问题
   - 任务依赖循环检测问题
   - 任务执行状态同步问题
   - 告警通知重复问题

## 5. 后续计划

### 5.1 功能规划

1. v1.3.0
   - 支持任务执行优先级
   - 支持任务执行分组
   - 支持任务执行标签
   - 支持任务执行参数

2. v1.4.0
   - 支持任务执行监控
   - 支持任务执行分析
   - 支持任务执行报告
   - 支持任务执行优化

3. v1.5.0
   - 支持分布式部署
   - 支持集群管理
   - 支持负载均衡
   - 支持故障转移

### 5.2 技术规划

1. 性能优化
   - 优化数据库访问
   - 优化缓存使用
   - 优化并发处理
   - 优化资源利用

2. 安全增强
   - 增强认证机制
   - 增强授权控制
   - 增强数据加密
   - 增强审计日志

3. 可用性提升
   - 提升系统稳定性
   - 提升系统可靠性
   - 提升系统可维护性
   - 提升系统可扩展性 