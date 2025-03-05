# 生产环境部署指南

## 1. 环境规划

### 1.1 服务器规划
- 应用服务器：4台（2主2备）
- 数据库服务器：2台（主从）
- 缓存服务器：3台（Redis集群）
- 配置中心：3台（ZooKeeper集群）
- 负载均衡器：2台（Nginx主备）
- 监控服务器：2台（Prometheus + Grafana）

### 1.2 网络规划
- 内网网段：192.168.1.0/24
- 外网网段：10.0.0.0/24
- 负载均衡IP：10.0.0.100
- 应用服务器IP：192.168.1.101-104
- 数据库服务器IP：192.168.1.201-202
- 缓存服务器IP：192.168.1.301-303
- 配置中心IP：192.168.1.401-403
- 监控服务器IP：192.168.1.501-502

### 1.3 硬件配置
- 应用服务器：
  - CPU：8核+
  - 内存：32GB+
  - 磁盘：500GB+
  - 网卡：双千兆

- 数据库服务器：
  - CPU：16核+
  - 内存：64GB+
  - 磁盘：2TB+（SSD）
  - 网卡：双万兆

- 缓存服务器：
  - CPU：8核+
  - 内存：32GB+
  - 磁盘：500GB+
  - 网卡：双千兆

## 2. 系统配置

### 2.1 操作系统配置
```bash
# 系统优化
cat > /etc/sysctl.conf << EOF
net.ipv4.tcp_fin_timeout = 30
net.ipv4.tcp_keepalive_time = 1200
net.ipv4.tcp_max_syn_backlog = 8192
net.ipv4.tcp_tw_reuse = 1
net.ipv4.tcp_tw_recycle = 0
net.ipv4.ip_local_port_range = 1024 65000
net.ipv4.tcp_max_tw_buckets = 5000
EOF

# 应用系统参数
cat > /etc/security/limits.conf << EOF
* soft nofile 65535
* hard nofile 65535
* soft nproc 65535
* hard nproc 65535
EOF

# 时区配置
timedatectl set-timezone Asia/Shanghai
```

### 2.2 防火墙配置
```bash
# 开放必要端口
firewall-cmd --permanent --add-port=80/tcp
firewall-cmd --permanent --add-port=443/tcp
firewall-cmd --permanent --add-port=8080/tcp
firewall-cmd --permanent --add-port=3306/tcp
firewall-cmd --permanent --add-port=6379/tcp
firewall-cmd --permanent --add-port=2181/tcp
firewall-cmd --reload
```

### 2.3 监控配置
```bash
# 安装监控工具
yum install -y prometheus node_exporter alertmanager

# 配置Prometheus
cat > /etc/prometheus/prometheus.yml << EOF
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'scheduler'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['192.168.1.101:8080', '192.168.1.102:8080']
EOF

# 配置告警规则
cat > /etc/prometheus/rules/scheduler.rules << EOF
groups:
- name: scheduler
  rules:
  - alert: HighErrorRate
    expr: rate(http_requests_total{status=~"5.."}[5m]) > 0.1
    for: 5m
    labels:
      severity: critical
    annotations:
      summary: High error rate detected
      description: Error rate is above 10% for 5 minutes
EOF
```

## 3. 数据库部署

### 3.1 MySQL主从配置
```bash
# 主库配置
cat > /etc/my.cnf << EOF
[mysqld]
server-id=1
log-bin=mysql-bin
binlog_format=ROW
sync_binlog=1
innodb_flush_log_at_trx_commit=1
EOF

# 从库配置
cat > /etc/my.cnf << EOF
[mysqld]
server-id=2
read_only=1
EOF

# 主从同步
mysql> CHANGE MASTER TO
    MASTER_HOST='192.168.1.201',
    MASTER_USER='repl',
    MASTER_PASSWORD='password',
    MASTER_LOG_FILE='mysql-bin.000001',
    MASTER_LOG_POS=0;
mysql> START SLAVE;
```

### 3.2 Redis集群配置
```bash
# Redis配置
cat > /etc/redis/redis.conf << EOF
port 6379
cluster-enabled yes
cluster-config-file nodes.conf
cluster-node-timeout 5000
appendonly yes
appendfsync everysec
maxmemory 16gb
maxmemory-policy allkeys-lru
EOF

# 创建集群
redis-cli --cluster create \
  192.168.1.301:6379 \
  192.168.1.302:6379 \
  192.168.1.303:6379 \
  --cluster-replicas 1
```

### 3.3 ZooKeeper集群配置
```bash
# ZooKeeper配置
cat > /etc/zookeeper/zoo.cfg << EOF
tickTime=2000
initLimit=10
syncLimit=5
dataDir=/var/lib/zookeeper
clientPort=2181

server.1=192.168.1.401:2888:3888
server.2=192.168.1.402:2888:3888
server.3=192.168.1.403:2888:3888
EOF
```

## 4. 应用部署

### 4.1 Docker配置
```bash
# 安装Docker
curl -fsSL https://get.docker.com | sh

# 配置镜像加速
cat > /etc/docker/daemon.json << EOF
{
  "registry-mirrors": [
    "https://mirror.ccs.tencentyun.com",
    "https://registry.docker-cn.com"
  ]
}
EOF

# 配置日志轮转
cat > /etc/docker/daemon.json << EOF
{
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "100m",
    "max-file": "3"
  }
}
EOF
```

### 4.2 应用配置
```yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:mysql://192.168.1.201:3306/scheduler?useSSL=false&serverTimezone=UTC
    username: scheduler
    password: ${MYSQL_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  redis:
    cluster:
      nodes:
        - 192.168.1.301:6379
        - 192.168.1.302:6379
        - 192.168.1.303:6379
    
  zookeeper:
    connect-string: 192.168.1.401:2181,192.168.1.402:2181,192.168.1.403:2181

server:
  port: 8080

logging:
  file:
    name: /var/log/scheduler/application.log
  logback:
    rollingpolicy:
      max-file-size: 100MB
      max-history: 30
```

### 4.3 Nginx配置
```nginx
# nginx.conf
user  nginx;
worker_processes  auto;

events {
    worker_connections  1024;
}

http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;

    upstream scheduler {
        server 192.168.1.101:8080;
        server 192.168.1.102:8080;
        server 192.168.1.103:8080;
        server 192.168.1.104:8080;
    }

    server {
        listen       80;
        server_name  scheduler.com;

        ssl_certificate      /etc/nginx/ssl/scheduler.crt;
        ssl_certificate_key  /etc/nginx/ssl/scheduler.key;
        ssl_session_timeout  5m;
        ssl_protocols TLSv1.2 TLSv1.3;
        ssl_ciphers HIGH:!aNULL:!MD5;

        location / {
            proxy_pass http://scheduler;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
    }
}
```

## 5. 部署流程

### 5.1 准备工作
- 检查服务器状态
- 备份数据
- 准备部署包
- 通知相关人员

### 5.2 部署步骤
```bash
# 1. 停止服务
docker stop scheduler

# 2. 备份数据
mysqldump -h 192.168.1.201 -u root -p scheduler > backup.sql

# 3. 更新代码
git pull origin master

# 4. 构建镜像
docker build -t scheduler:latest .

# 5. 启动服务
docker run -d \
  --name scheduler \
  -p 8080:8080 \
  -v /var/log/scheduler:/var/log/scheduler \
  -e SPRING_PROFILES_ACTIVE=prod \
  scheduler:latest

# 6. 检查服务
curl http://localhost:8080/actuator/health
```

### 5.3 回滚步骤
```bash
# 1. 停止服务
docker stop scheduler

# 2. 恢复数据
mysql -h 192.168.1.201 -u root -p scheduler < backup.sql

# 3. 回滚代码
git reset --hard HEAD^

# 4. 重新部署
docker run -d \
  --name scheduler \
  -p 8080:8080 \
  -v /var/log/scheduler:/var/log/scheduler \
  -e SPRING_PROFILES_ACTIVE=prod \
  scheduler:previous
```

## 6. 监控告警

### 6.1 系统监控
- CPU使用率
- 内存使用率
- 磁盘使用率
- 网络流量

### 6.2 应用监控
- 接口响应时间
- 错误率
- 并发数
- JVM状态

### 6.3 数据库监控
- 连接数
- 慢查询
- 主从延迟
- 缓存命中率

## 7. 运维管理

### 7.1 日常运维
- 系统巡检
- 日志分析
- 性能优化
- 安全加固

### 7.2 故障处理
- 故障发现
- 故障定位
- 故障处理
- 故障复盘

### 7.3 变更管理
- 变更申请
- 变更评估
- 变更实施
- 变更验证

## 8. 应急预案

### 8.1 系统故障
- 服务不可用
- 数据库故障
- 缓存故障
- 网络故障

### 8.2 数据故障
- 数据丢失
- 数据不一致
- 数据泄露
- 数据损坏

### 8.3 安全事件
- 系统入侵
- DDoS攻击
- 数据泄露
- 病毒攻击 