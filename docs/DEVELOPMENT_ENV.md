# 开发环境搭建指南

## 1. 基础环境要求

### 1.1 操作系统
- 推荐：macOS 10.15+ / Ubuntu 20.04+ / Windows 10+
- 内存：8GB+
- 硬盘：256GB+
- 网络：稳定的互联网连接

### 1.2 开发工具
- IDE：IntelliJ IDEA 2023.1+
- 版本控制：Git 2.30+
- 终端：iTerm2 (macOS) / Windows Terminal (Windows)
- 数据库工具：Navicat Premium 16+ / MySQL Workbench 8.0+

### 1.3 基础软件
- JDK：OpenJDK 17+
- Maven：3.8+
- Node.js：16+
- npm：8+
- Docker：20.10+
- Docker Compose：2.0+

## 2. 后端环境配置

### 2.1 JDK配置
```bash
# 下载并安装JDK
brew install openjdk@17

# 配置环境变量
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.zshrc
source ~/.zshrc

# 验证安装
java -version
```

### 2.2 Maven配置
```bash
# 下载并安装Maven
brew install maven

# 配置settings.xml
cp ~/.m2/settings.xml ~/.m2/settings.xml.bak
vim ~/.m2/settings.xml

# 添加阿里云镜像
<mirrors>
    <mirror>
        <id>aliyun</id>
        <mirrorOf>central</mirrorOf>
        <name>Aliyun Maven Central</name>
        <url>https://maven.aliyun.com/repository/central</url>
    </mirror>
</mirrors>

# 验证安装
mvn -version
```

### 2.3 IDE配置
- 安装插件：
  - Lombok
  - Spring Boot Assistant
  - MyBatisX
  - GitToolBox
  - Maven Helper
  - SonarLint

- 配置代码风格：
  - 导入项目代码风格配置
  - 配置自动导入
  - 配置代码格式化

## 3. 前端环境配置

### 3.1 Node.js配置
```bash
# 安装nvm
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash

# 安装Node.js
nvm install 16
nvm use 16

# 验证安装
node -v
npm -v
```

### 3.2 全局依赖安装
```bash
# 安装Vue CLI
npm install -g @vue/cli

# 安装TypeScript
npm install -g typescript

# 安装Vite
npm install -g vite
```

### 3.3 项目依赖安装
```bash
# 进入前端项目目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

## 4. 数据库环境配置

### 4.1 MySQL配置
```bash
# 使用Docker启动MySQL
docker run -d \
  --name mysql \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=scheduler \
  mysql:5.7

# 验证连接
mysql -h localhost -P 3306 -u root -p
```

### 4.2 Redis配置
```bash
# 使用Docker启动Redis
docker run -d \
  --name redis \
  -p 6379:6379 \
  redis:6.2

# 验证连接
redis-cli ping
```

### 4.3 ZooKeeper配置
```bash
# 使用Docker启动ZooKeeper
docker run -d \
  --name zookeeper \
  -p 2181:2181 \
  zookeeper:3.7

# 验证连接
echo stat | nc localhost 2181
```

## 5. 项目配置

### 5.1 后端配置
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/scheduler?useSSL=false&serverTimezone=UTC
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  redis:
    host: localhost
    port: 6379
    database: 0
    
  zookeeper:
    connect-string: localhost:2181
```

### 5.2 前端配置
```typescript
// .env.development
VITE_API_BASE_URL=http://localhost:8080
VITE_APP_TITLE=Scheduler Dev
```

## 6. 开发规范

### 6.1 代码规范
- 遵循阿里巴巴Java开发手册
- 使用ESLint + Prettier进行代码格式化
- 使用Husky进行提交前检查
- 使用CommitLint规范提交信息

### 6.2 Git规范
- 遵循Git Flow工作流
- 使用feature分支开发新功能
- 使用hotfix分支修复紧急bug
- 提交前进行代码审查

### 6.3 文档规范
- 及时更新API文档
- 编写单元测试
- 更新README文档
- 记录重要决策

## 7. 调试技巧

### 7.1 后端调试
- 使用IDEA的调试功能
- 配置远程调试
- 使用日志调试
- 使用断点调试

### 7.2 前端调试
- 使用Vue DevTools
- 使用Chrome开发者工具
- 使用Vite的热更新
- 使用console调试

### 7.3 数据库调试
- 使用数据库工具
- 分析慢查询
- 优化SQL语句
- 监控数据库性能

## 8. 常见问题

### 8.1 环境问题
- 端口占用
- 依赖冲突
- 版本不兼容
- 网络问题

### 8.2 开发问题
- 代码编译错误
- 运行时异常
- 性能问题
- 安全问题

### 8.3 解决方案
- 查看错误日志
- 使用搜索引擎
- 查阅官方文档
- 寻求团队帮助

## 9. 开发资源

### 9.1 文档资源
- [Spring Boot文档](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Vue 3文档](https://v3.vuejs.org/)
- [Element Plus文档](https://element-plus.org/)
- [MySQL文档](https://dev.mysql.com/doc/)

### 9.2 工具资源
- [Maven中央仓库](https://mvnrepository.com/)
- [npm包管理](https://www.npmjs.com/)
- [GitHub](https://github.com/)
- [Stack Overflow](https://stackoverflow.com/) 