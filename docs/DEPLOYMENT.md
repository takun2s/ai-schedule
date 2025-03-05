# 部署文档

## 1. 系统架构

### 1.1 整体架构
```
                                    +----------------+
                                    |     Nginx      |
                                    |  (负载均衡)    |
                                    +----------------+
                                           |
                    +----------------------+----------------------+
                    |                      |                      |
            +----------------+    +----------------+    +----------------+
            |  Spring Boot  |    |  Spring Boot  |    |  Spring Boot  |
            |  Application  |    |  Application  |    |  Application  |
            +----------------+    +----------------+    +----------------+
                    |                      |                      |
                    |                      |                      |
            +----------------+    +----------------+    +----------------+
            |    Redis      |    |    Redis      |    |    Redis      |
            |   Cluster     |    |   Cluster     |    |   Cluster     |
            +----------------+    +----------------+    +----------------+
                    |                      |                      |
                    |                      |                      |
            +----------------+    +----------------+    +----------------+
            |    MySQL      |    |    MySQL      |    |    MySQL      |
            |  Master-Slave |    |  Master-Slave |    |  Master-Slave |
            +----------------+    +----------------+    +----------------+
                    |                      |                      |
                    |                      |                      |
            +----------------+    +----------------+    +----------------+
            |   ZooKeeper   |    |   ZooKeeper   |    |   ZooKeeper   |
            |   Cluster     |    |   Cluster     |    |   Cluster     |
            +----------------+    +----------------+    +----------------+
```

### 1.2 组件说明

1. 负载均衡层
   - Nginx：反向代理、负载均衡
   - 配置SSL证书
   - 配置访问控制

2. 应用服务层
   - Spring Boot应用
   - 多节点部署
   - 服务注册与发现

3. 数据存储层
   - Redis集群：缓存
   - MySQL主从：数据存储
   - ZooKeeper集群：配置管理

4. 监控层
   - Prometheus：监控系统
   - Grafana：监控面板
   - ELK：日志收集

## 2. 部署要求

### 2.1 硬件要求

1. 应用服务器
   - CPU：8核+
   - 内存：16GB+
   - 磁盘：100GB+
   - 网络：千兆网卡

2. 数据库服务器
   - CPU：16核+
   - 内存：32GB+
   - 磁盘：500GB+
   - 网络：千兆网卡

3. 缓存服务器
   - CPU：8核+
   - 内存：16GB+
   - 磁盘：100GB+
   - 网络：千兆网卡

### 2.2 软件要求

1. 操作系统
   - CentOS 7.x
   - Ubuntu 20.04
   - 内核版本：4.4+

2. 中间件
   - JDK 1.8+
   - MySQL 5.7+
   - Redis 6.0+
   - ZooKeeper 3.6+
   - Nginx 1.18+

3. 监控工具
   - Prometheus 2.x
   - Grafana 8.x
   - ELK 7.x

## 3. 部署步骤

### 3.1 环境准备

1. 系统配置
```bash
# 修改系统参数
cat >> /etc/sysctl.conf << EOF
net.ipv4.tcp_fin_timeout = 30
net.ipv4.tcp_keepalive_time = 1200
net.ipv4.tcp_max_syn_backlog = 8192
net.ipv4.tcp_tw_reuse = 1
net.ipv4.tcp_tw_recycle = 0
net.ipv4.ip_local_port_range = 1024 65000
net.ipv4.tcp_max_tw_buckets = 5000
EOF

# 应用系统参数
sysctl -p

# 修改文件描述符限制
cat >> /etc/security/limits.conf << EOF
* soft nofile 65535
* hard nofile 65535
EOF
```

2. 安装基础软件
```bash
# 安装JDK
yum install -y java-1.8.0-openjdk-devel

# 安装MySQL
yum install -y mysql-server

# 安装Redis
yum install -y redis

# 安装ZooKeeper
yum install -y zookeeper

# 安装Nginx
yum install -y nginx
```

### 3.2 数据库部署

1. MySQL主从配置
```bash
# 主库配置
cat > /etc/my.cnf << EOF
[mysqld]
server-id=1
log-bin=mysql-bin
binlog-format=ROW
sync_binlog=1
innodb_flush_log_at_trx_commit=2
EOF

# 从库配置
cat > /etc/my.cnf << EOF
[mysqld]
server-id=2
read_only=1
EOF

# 创建主从用户
mysql -uroot -p
CREATE USER 'repl'@'%' IDENTIFIED BY 'password';
GRANT REPLICATION SLAVE ON *.* TO 'repl'@'%';
FLUSH PRIVILEGES;
```

2. 创建数据库
```sql
CREATE DATABASE scheduler DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

### 3.3 Redis集群部署

1. Redis配置
```bash
# 主节点配置
cat > /etc/redis/redis.conf << EOF
port 6379
bind 0.0.0.0
daemonize yes
pidfile /var/run/redis_6379.pid
logfile /var/log/redis/redis.log
dir /var/lib/redis
appendonly yes
appendfilename "appendonly.aof"
appendfsync everysec
EOF

# 从节点配置
cat > /etc/redis/redis.conf << EOF
port 6379
bind 0.0.0.0
daemonize yes
pidfile /var/run/redis_6379.pid
logfile /var/log/redis/redis.log
dir /var/lib/redis
appendonly yes
appendfilename "appendonly.aof"
appendfsync everysec
slaveof 192.168.1.100 6379
EOF
```

2. 创建集群
```bash
# 安装redis-trib
yum install -y redis-trib

# 创建集群
redis-trib.rb create --replicas 1 \
192.168.1.100:6379 \
192.168.1.101:6379 \
192.168.1.102:6379 \
192.168.1.103:6379 \
192.168.1.104:6379 \
192.168.1.105:6379
```

### 3.4 ZooKeeper集群部署

1. ZooKeeper配置
```bash
# 配置zoo.cfg
cat > /etc/zookeeper/conf/zoo.cfg << EOF
tickTime=2000
initLimit=10
syncLimit=5
dataDir=/var/lib/zookeeper
clientPort=2181
server.1=192.168.1.100:2888:3888
server.2=192.168.1.101:2888:3888
server.3=192.168.1.102:2888:3888
EOF

# 创建myid文件
echo 1 > /var/lib/zookeeper/myid
```

2. 启动服务
```bash
systemctl start zookeeper
systemctl enable zookeeper
```

### 3.5 应用服务部署

1. 打包应用
```bash
# 后端打包
cd backend
mvn clean package -DskipTests

# 前端打包
cd frontend
npm install
npm run build
```

2. 创建服务脚本
```bash
# 创建systemd服务
cat > /etc/systemd/system/scheduler.service << EOF
[Unit]
Description=Scheduler Service
After=network.target

[Service]
Type=simple
User=scheduler
WorkingDirectory=/opt/scheduler
ExecStart=/usr/bin/java -jar scheduler.jar
Restart=always

[Install]
WantedBy=multi-user.target
EOF
```

3. 部署应用
```bash
# 创建部署目录
mkdir -p /opt/scheduler

# 复制文件
cp target/scheduler.jar /opt/scheduler/
cp -r frontend/dist/* /usr/share/nginx/html/

# 启动服务
systemctl daemon-reload
systemctl start scheduler
systemctl enable scheduler
```

### 3.6 Nginx配置

1. 配置Nginx
```nginx
# 配置nginx.conf
cat > /etc/nginx/conf.d/scheduler.conf << EOF
server {
    listen 80;
    server_name scheduler.example.com;

    # SSL配置
    listen 443 ssl;
    ssl_certificate /etc/nginx/ssl/scheduler.crt;
    ssl_certificate_key /etc/nginx/ssl/scheduler.key;

    # 前端配置
    location / {
        root /usr/share/nginx/html;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    # API配置
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # 上传文件配置
    location /upload {
        proxy_pass http://localhost:8080;
        client_max_body_size 50m;
    }
}
EOF
```

2. 启动Nginx
```bash
# 检查配置
nginx -t

# 启动服务
systemctl start nginx
systemctl enable nginx
```

### 3.7 监控部署

1. Prometheus配置
```yaml
# 配置prometheus.yml
cat > /etc/prometheus/prometheus.yml << EOF
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'scheduler'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
EOF
```

2. Grafana配置
```bash
# 安装Grafana
yum install -y grafana

# 启动服务
systemctl start grafana-server
systemctl enable grafana-server
```

## 4. 运维指南

### 4.1 日常维护

1. 日志管理
```bash
# 配置日志轮转
cat > /etc/logrotate.d/scheduler << EOF
/var/log/scheduler/*.log {
    daily
    rotate 7
    compress
    delaycompress
    missingok
    notifempty
    create 0640 scheduler scheduler
}
EOF
```

2. 备份策略
```bash
# 数据库备份
#!/bin/bash
BACKUP_DIR="/backup/mysql"
DATE=$(date +%Y%m%d)
mysqldump -uroot -p'password' scheduler > $BACKUP_DIR/scheduler_$DATE.sql

# 配置文件备份
tar -czf /backup/config/config_$DATE.tar.gz /etc/scheduler/
```

3. 监控告警
```bash
# 配置告警规则
cat > /etc/prometheus/rules/scheduler.yml << EOF
groups:
  - name: scheduler
    rules:
      - alert: HighCPUUsage
        expr: cpu_usage > 80
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: High CPU usage
          description: CPU usage is above 80%
EOF
```

### 4.2 故障处理

1. 服务故障
```bash
# 检查服务状态
systemctl status scheduler

# 查看服务日志
journalctl -u scheduler -f

# 重启服务
systemctl restart scheduler
```

2. 数据库故障
```bash
# 检查数据库状态
mysql -uroot -p -e "SHOW SLAVE STATUS\G"

# 修复主从同步
mysql -uroot -p -e "STOP SLAVE; START SLAVE;"
```

3. 缓存故障
```bash
# 检查Redis状态
redis-cli ping

# 修复Redis集群
redis-cli cluster nodes
redis-cli cluster failover
```

### 4.3 扩容方案

1. 应用扩容
```bash
# 添加新节点
scp scheduler.jar user@new-node:/opt/scheduler/
ssh user@new-node "systemctl start scheduler"
```

2. 数据库扩容
```bash
# 添加从库
mysql -uroot -p -e "CHANGE MASTER TO MASTER_HOST='192.168.1.100', MASTER_USER='repl', MASTER_PASSWORD='password';"
mysql -uroot -p -e "START SLAVE;"
```

3. 缓存扩容
```bash
# 添加Redis节点
redis-cli cluster meet 192.168.1.106 6379
redis-cli cluster rebalance
``` 