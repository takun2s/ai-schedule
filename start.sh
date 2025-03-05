#!/bin/bash

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java环境，请先安装Java"
    exit 1
fi

# 检查ZooKeeper是否运行
if ! nc -z localhost 2181; then
    echo "错误: ZooKeeper未运行，请先启动ZooKeeper"
    exit 1
fi

# 检查MySQL是否运行
if ! nc -z localhost 3306; then
    echo "错误: MySQL未运行，请先启动MySQL"
    exit 1
fi

# 检查Redis是否运行
if ! nc -z localhost 6379; then
    echo "错误: Redis未运行，请先启动Redis"
    exit 1
fi

# 检查RabbitMQ是否运行
if ! nc -z localhost 5672; then
    echo "错误: RabbitMQ未运行，请先启动RabbitMQ"
    exit 1
fi

# 设置JVM参数
JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError"

# 启动应用
echo "正在启动任务调度系统..."
java $JAVA_OPTS -jar backend/common/target/common-1.0.0-SNAPSHOT.jar

# 检查启动状态
if [ $? -eq 0 ]; then
    echo "任务调度系统启动成功"
else
    echo "任务调度系统启动失败"
    exit 1
fi 
