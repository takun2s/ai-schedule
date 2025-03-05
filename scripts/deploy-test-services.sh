#!/bin/bash

echo "开始部署测试服务..."

# 构建后端服务
echo "构建后端服务..."
cd backend
mvn clean package -DskipTests
docker build -t scheduler-backend-test .

# 构建前端服务
echo "构建前端服务..."
cd ../frontend
npm install
npm run build
docker build -t scheduler-frontend-test .

# 部署服务
echo "部署服务..."
cd ..
docker-compose -f docker-compose.test.yml up -d

# 等待服务启动
echo "等待服务启动..."
sleep 15

# 检查服务状态
echo "检查服务状态..."
docker-compose -f docker-compose.test.yml ps

echo "测试服务部署完成" 