# 数据库设计文档

## 1. 数据库概述

### 1.1 数据库选型
- 主数据库：MySQL 5.7+
- 字符集：utf8mb4
- 排序规则：utf8mb4_general_ci

### 1.2 数据库命名规范
- 数据库名：scheduler
- 表名：小写字母，下划线分隔
- 字段名：小写字母，下划线分隔
- 索引名：idx_字段名
- 主键名：pk_表名

## 2. 表结构设计

### 2.1 用户表（user）
```sql
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

### 2.2 任务表（task）
```sql
CREATE TABLE `task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '任务名称',
  `type` varchar(20) NOT NULL COMMENT '任务类型：HTTP/SHELL/DATABASE',
  `cron` varchar(50) NOT NULL COMMENT 'Cron表达式',
  `timeout` int(11) DEFAULT '300' COMMENT '超时时间(秒)',
  `content` text NOT NULL COMMENT '任务内容',
  `params` text COMMENT '任务参数',
  `alert_enabled` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否启用告警：0-禁用，1-启用',
  `alert_type` varchar(20) DEFAULT NULL COMMENT '告警类型：EMAIL/DINGTALK/WECHAT_WORK',
  `alert_receiver` varchar(500) DEFAULT NULL COMMENT '告警接收人',
  `alert_condition` varchar(100) DEFAULT NULL COMMENT '告警条件',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务表';
```

### 2.3 任务依赖表（task_dependency）
```sql
CREATE TABLE `task_dependency` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_id` bigint(20) NOT NULL COMMENT '任务ID',
  `dependency_task_id` bigint(20) NOT NULL COMMENT '依赖任务ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_dependency` (`task_id`,`dependency_task_id`),
  KEY `idx_dependency_task_id` (`dependency_task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务依赖表';
```

### 2.4 执行记录表（execution_record）
```sql
CREATE TABLE `execution_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_id` bigint(20) NOT NULL COMMENT '任务ID',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `duration` bigint(20) DEFAULT NULL COMMENT '执行时长(毫秒)',
  `status` tinyint(4) NOT NULL COMMENT '状态：0-失败，1-成功',
  `result` text COMMENT '执行结果',
  `error_message` text COMMENT '错误信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='执行记录表';
```

### 2.5 执行日志表（execution_log）
```sql
CREATE TABLE `execution_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `execution_id` bigint(20) NOT NULL COMMENT '执行记录ID',
  `level` varchar(20) NOT NULL COMMENT '日志级别：INFO/WARN/ERROR',
  `content` text NOT NULL COMMENT '日志内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_execution_id` (`execution_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='执行日志表';
```

### 2.6 告警记录表（alert_record）
```sql
CREATE TABLE `alert_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_id` bigint(20) NOT NULL COMMENT '任务ID',
  `type` varchar(20) NOT NULL COMMENT '告警类型：EMAIL/DINGTALK/WECHAT_WORK',
  `content` text NOT NULL COMMENT '告警内容',
  `receiver` varchar(500) NOT NULL COMMENT '接收人',
  `status` tinyint(4) NOT NULL COMMENT '状态：0-失败，1-成功',
  `send_result` text COMMENT '发送结果',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警记录表';
```

### 2.7 系统设置表（system_setting）
```sql
CREATE TABLE `system_setting` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `category` varchar(50) NOT NULL COMMENT '设置类别',
  `key` varchar(50) NOT NULL COMMENT '设置键',
  `value` text NOT NULL COMMENT '设置值',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_category_key` (`category`,`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统设置表';
```

## 3. 表关系说明

### 3.1 任务与执行记录
- 一对多关系
- 一个任务可以有多个执行记录
- 通过task_id关联

### 3.2 任务与任务依赖
- 多对多关系
- 通过task_dependency表关联
- 一个任务可以依赖多个任务
- 一个任务可以被多个任务依赖

### 3.3 执行记录与执行日志
- 一对多关系
- 一个执行记录可以有多个日志
- 通过execution_id关联

### 3.4 任务与告警记录
- 一对多关系
- 一个任务可以有多个告警记录
- 通过task_id关联

## 4. 索引设计

### 4.1 主键索引
- 所有表都使用自增ID作为主键
- 主键索引名格式：pk_表名

### 4.2 唯一索引
- user表：username
- task_dependency表：task_id + dependency_task_id
- system_setting表：category + key

### 4.3 普通索引
- task表：status
- execution_record表：task_id, start_time
- execution_log表：execution_id
- alert_record表：task_id, create_time

## 5. 分表策略

### 5.1 执行记录表分表
- 按时间范围分表
- 表名格式：execution_record_YYYYMM
- 每月一张表
- 保留最近12个月数据

### 5.2 执行日志表分表
- 按时间范围分表
- 表名格式：execution_log_YYYYMM
- 每月一张表
- 保留最近12个月数据

### 5.3 告警记录表分表
- 按时间范围分表
- 表名格式：alert_record_YYYYMM
- 每月一张表
- 保留最近12个月数据

## 6. 数据备份策略

### 6.1 全量备份
- 每天凌晨2点执行
- 使用mysqldump工具
- 保留最近30天备份

### 6.2 增量备份
- 每小时执行一次
- 使用binlog
- 保留最近7天binlog

### 6.3 备份验证
- 每周进行一次备份恢复测试
- 验证数据完整性
- 记录验证结果 