#!/bin/bash

echo "开始初始化测试数据..."

# 等待数据库就绪
echo "等待数据库就绪..."
sleep 10

# 执行数据库初始化脚本
echo "执行数据库初始化脚本..."
docker-compose -f docker-compose.test.yml exec mysql mysql -u root -p123456 scheduler < test/data/init.sql

# 初始化Redis数据
echo "初始化Redis数据..."
docker-compose -f docker-compose.test.yml exec redis redis-cli FLUSHALL

# 初始化测试用户
echo "初始化测试用户..."
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"testpass","email":"test@example.com"}'

# 初始化测试任务
echo "初始化测试任务..."
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $(curl -s -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d '{"username":"testuser","password":"testpass"}' | jq -r .token)" \
  -d '{"name":"测试任务","type":"HTTP","cron":"0 0 * * * ?","url":"http://example.com"}'

echo "测试数据初始化完成" 