-- 系统用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    sex TINYINT DEFAULT 2 COMMENT '性别（0男 1女 2未知）',
    avatar VARCHAR(200) COMMENT '头像',
    status TINYINT DEFAULT 0 COMMENT '状态（0正常 1停用）',
    del_flag TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建者',
    update_by VARCHAR(50) COMMENT '更新者',
    remark VARCHAR(500) COMMENT '备注',
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 任务表
CREATE TABLE IF NOT EXISTS task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '任务名称',
    type INT NOT NULL COMMENT '任务类型：1-Shell命令，2-JAR文件',
    content TEXT NOT NULL COMMENT '任务内容',
    params VARCHAR(500) COMMENT '任务参数',
    working_dir VARCHAR(200) COMMENT '工作目录',
    env TEXT COMMENT '环境变量',
    timeout INT DEFAULT 3600 COMMENT '超时时间(秒)',
    retry_count INT DEFAULT 0 COMMENT '重试次数',
    retry_interval INT DEFAULT 0 COMMENT '重试间隔(秒)',
    alert_type VARCHAR(20) COMMENT '告警类型',
    alert_receiver VARCHAR(200) COMMENT '告警接收者',
    status INT DEFAULT 0 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '任务表';

-- 任务执行记录表
CREATE TABLE IF NOT EXISTS task_execution (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT NOT NULL COMMENT '任务ID',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '执行状态：0-等待执行，1-执行中，2-执行成功，3-执行失败，4-执行超时',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    duration BIGINT COMMENT '执行时长(毫秒)',
    result TEXT COMMENT '执行结果',
    error_message TEXT COMMENT '错误信息',
    retry_count INT NOT NULL DEFAULT 0 COMMENT '重试次数',
    node_id VARCHAR(64) COMMENT '执行节点ID',
    ip VARCHAR(64) COMMENT '执行节点IP',
    del_flag TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(64) COMMENT '创建者',
    update_by VARCHAR(64) COMMENT '更新者',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_task_id (task_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务执行记录表';

-- 任务告警表
CREATE TABLE IF NOT EXISTS task_alert (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT NOT NULL COMMENT '任务ID',
    execution_id BIGINT NOT NULL COMMENT '执行记录ID',
    alert_type TINYINT NOT NULL COMMENT '告警类型：1-成功告警，2-失败告警，3-超时告警',
    receiver VARCHAR(500) NOT NULL COMMENT '接收者',
    content TEXT NOT NULL COMMENT '告警内容',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '发送状态：0-未发送，1-发送成功，2-发送失败',
    send_time DATETIME COMMENT '发送时间',
    send_result TEXT COMMENT '发送结果',
    del_flag TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(64) COMMENT '创建者',
    update_by VARCHAR(64) COMMENT '更新者',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_task_id (task_id),
    INDEX idx_execution_id (execution_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务告警记录表'; 