
-- 用户表
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    last_login_time DATETIME,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    roles VARCHAR(255) DEFAULT 'USER',
    INDEX idx_username (username)
);


-- 角色表
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200)
);

-- 用户角色关联表
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- 任务表
CREATE TABLE tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,  -- SHELL, JAR, SPARK, PYTHON, HTTP
    command TEXT NOT NULL,
    cron_expression VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'CREATED',  -- PENDING, RUNNING, SUCCESS, FAILED
    last_execute_time TIMESTAMP,
    next_execute_time TIMESTAMP,
    alert_email VARCHAR(100),
    alert_on_failure BOOLEAN DEFAULT FALSE,
    execute_machine VARCHAR(100),
    timeout BIGINT,  -- 超时时间（秒）
    retry_count INT DEFAULT 0,
    description TEXT,
    -- Spark任务特定属性
    spark_master VARCHAR(100),
    spark_app_name VARCHAR(100),
    spark_main_class VARCHAR(200),
    spark_args TEXT,
    -- Python任务特定属性
    python_version VARCHAR(20),
    python_path VARCHAR(200),
    requirements TEXT,
    -- HTTP任务特定属性
    http_method VARCHAR(10),
    http_url VARCHAR(500),
    http_headers TEXT,
    http_body TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- DAG表
CREATE TABLE dags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    cron_expression VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CREATED',
    nodes TEXT,
    edges TEXT,
    last_execute_time DATETIME,
    next_execute_time DATETIME,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_status (status)
);

-- DAG任务表
CREATE TABLE dag_tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dag_id BIGINT NOT NULL,
    task_id BIGINT NOT NULL,
    MODIFY COLUMN sequence INT NOT NULL DEFAULT 0;
    status VARCHAR(20),
    is_retry BOOLEAN DEFAULT FALSE,
    retry_count INT DEFAULT 0,
    max_retry_count INT DEFAULT 3,
    execute_time TIMESTAMP,
    execute_machine VARCHAR(100),
    FOREIGN KEY (dag_id) REFERENCES dags(id),
    FOREIGN KEY (task_id) REFERENCES tasks(id)
);

-- DAG任务依赖表
CREATE TABLE dag_task_dependencies (
    task_id BIGINT NOT NULL,
    dependency_id BIGINT NOT NULL,
    PRIMARY KEY (task_id, dependency_id),
    FOREIGN KEY (task_id) REFERENCES dag_tasks(id) ON DELETE CASCADE,
    FOREIGN KEY (dependency_id) REFERENCES dag_tasks(id) ON DELETE CASCADE
);
CREATE TABLE dag_task_downstream (
    source_task_id BIGINT NOT NULL,
    target_task_id BIGINT NOT NULL,
    PRIMARY KEY (source_task_id, target_task_id),
    FOREIGN KEY (source_task_id) REFERENCES dag_tasks(id) ON DELETE CASCADE,
    FOREIGN KEY (target_task_id) REFERENCES dag_tasks(id) ON DELETE CASCADE
);


-- 告警记录表
CREATE TABLE alert_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT,
    dag_id BIGINT,
    alert_type VARCHAR(20),  -- EMAIL, SMS, WEBHOOK
    alert_content TEXT,
    status VARCHAR(20),  -- SUCCESS, FAILED
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES tasks(id),
    FOREIGN KEY (dag_id) REFERENCES dags(id)
);


CREATE TABLE `task_executions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_id` bigint NOT NULL,
  `task_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime DEFAULT NULL,
  `duration` bigint DEFAULT NULL,
  `error` text COLLATE utf8mb4_unicode_ci,
  `output` text COLLATE utf8mb4_unicode_ci,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `error_message` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `execute_machine` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dag_execution_id` bigint DEFAULT NULL,
  `dag_task_id` bigint DEFAULT NULL,
  `node_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_task_executions_task_id` (`task_id`),
  KEY `idx_task_executions_dag_execution_id` (`dag_execution_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ;

CREATE TABLE dag_executions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    dag_id BIGINT NOT NULL,
    dag_name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME,
    duration BIGINT,
    error TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_task_executions_task_id ON task_executions(task_id);
CREATE INDEX idx_dag_executions_dag_id ON dag_executions(dag_id);

-- 初始化角色数据
INSERT INTO roles (name, description) VALUES 
('ADMIN', '管理员'),
('USER', '普通用户');

-- 初始化管理员用户 (密码为 admin123 的BCrypt散列)
INSERT INTO users (username, password, roles, create_time, update_time)
VALUES ('admin', '$2a$10$X/uHa1i9IGG9rQqS3PzZp.FRpX0p7FyEA8GxPtM8Ip884MNgnboZO', 'ADMIN', NOW(), NOW());

-- 给管理员用户赋予管理员角色
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r 
WHERE u.username = 'admin' AND r.name = 'ADMIN';
