#!/bin/bash

echo "开始准备测试环境..."

# 检查必要的工具
command -v docker >/dev/null 2>&1 || { echo "需要安装 Docker"; exit 1; }
command -v docker-compose >/dev/null 2>&1 || { echo "需要安装 Docker Compose"; exit 1; }
command -v mvn >/dev/null 2>&1 || { echo "需要安装 Maven"; exit 1; }
command -v node >/dev/null 2>&1 || { echo "需要安装 Node.js"; exit 1; }

# 创建测试目录
mkdir -p test/data
mkdir -p test/logs
mkdir -p test/reports

# 启动测试服务
docker-compose -f docker-compose.test.yml up -d

# 等待服务启动
echo "等待服务启动..."
sleep 10

# 检查服务状态
docker-compose -f docker-compose.test.yml ps

echo "测试环境准备完成" 