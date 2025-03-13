
-- 告警策略表
CREATE TABLE alert_strategies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    alert_type VARCHAR(20) NOT NULL,
    receivers TEXT,
    webhook_url VARCHAR(500),
    alert_on_failure BOOLEAN DEFAULT TRUE,
    alert_on_timeout BOOLEAN DEFAULT FALSE,
    alert_on_retry BOOLEAN DEFAULT FALSE,
    timeout_threshold INT,
    retry_threshold INT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 任务实例表
CREATE TABLE task_instances (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    task_name VARCHAR(100),
    status VARCHAR(20),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    execute_machine VARCHAR(100),
    parameters TEXT,
    env_variables TEXT,
    log_content TEXT,
    retry_count INT DEFAULT 0,
    error_message TEXT,
    FOREIGN KEY (task_id) REFERENCES tasks(id)
);

-- 用户组表
CREATE TABLE user_groups (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 用户组成员关系表
CREATE TABLE user_group_members (
    group_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (group_id, user_id),
    FOREIGN KEY (group_id) REFERENCES user_groups(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 权限表
CREATE TABLE permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    resource_type VARCHAR(20) NOT NULL,
    resource_id BIGINT,
    action VARCHAR(20) NOT NULL,
    group_id BIGINT NOT NULL,
    FOREIGN KEY (group_id) REFERENCES user_groups(id)
);

-- 审计日志表
CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    operation VARCHAR(20) NOT NULL,
    resource_type VARCHAR(20) NOT NULL,
    resource_id BIGINT,
    resource_name VARCHAR(100),
    detail TEXT,
    client_ip VARCHAR(50),
    operate_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20)
);

-- 初始化权限数据
INSERT INTO user_groups (name, description) VALUES 
('administrators', '系统管理员组'),
('operators', '运维人员组'),
('developers', '开发人员组');

-- 初始化管理员组权限
INSERT INTO permissions (resource_type, action, group_id)
SELECT 'ALL', 'ALL', id FROM user_groups WHERE name = 'administrators';

-- 初始化运维组权限
INSERT INTO permissions (resource_type, action, group_id)
SELECT 'TASK', 'READ', id FROM user_groups WHERE name = 'operators'
UNION ALL
SELECT 'TASK', 'EXECUTE', id FROM user_groups WHERE name = 'operators'
UNION ALL
SELECT 'DAG', 'READ', id FROM user_groups WHERE name = 'operators'
UNION ALL
SELECT 'DAG', 'EXECUTE', id FROM user_groups WHERE name = 'operators';

-- 初始化开发组权限
INSERT INTO permissions (resource_type, action, group_id)
SELECT 'TASK', 'READ', id FROM user_groups WHERE name = 'developers'
UNION ALL
SELECT 'TASK', 'WRITE', id FROM user_groups WHERE name = 'developers'
UNION ALL
SELECT 'DAG', 'READ', id FROM user_groups WHERE name = 'developers'
UNION ALL
SELECT 'DAG', 'WRITE', id FROM user_groups WHERE name = 'developers';

-- 创建索引
CREATE INDEX idx_task_instances_task_id ON task_instances(task_id);
CREATE INDEX idx_task_instances_status ON task_instances(status);
CREATE INDEX idx_audit_logs_username ON audit_logs(username);
CREATE INDEX idx_audit_logs_operate_time ON audit_logs(operate_time);
CREATE INDEX idx_permissions_group ON permissions(group_id, resource_type, action);
