# 贡献指南

## 1. 项目介绍

分布式任务调度系统是一个开源的、功能强大的任务调度平台，支持多种任务类型和告警方式。我们欢迎所有形式的贡献，包括但不限于：

- 提交问题和建议
- 改进文档
- 提交代码修复
- 贡献新功能
- 改进测试用例

## 2. 开发环境

### 2.1 环境要求
- JDK 1.8+
- MySQL 5.7+
- Redis 6.0+
- Node.js 16+
- Maven 3.6+
- Git 2.x

### 2.2 开发工具
- IntelliJ IDEA 2023.x
- Navicat Premium 16.x
- Redis Desktop Manager
- Postman
- Git

## 3. 贡献流程

### 3.1 Fork 项目
1. 访问项目主页
2. 点击 Fork 按钮
3. 选择目标仓库
4. 克隆到本地

```bash
git clone https://github.com/your-username/scheduler.git
cd scheduler
```

### 3.2 创建分支
```bash
# 创建功能分支
git checkout -b feature/your-feature-name

# 创建修复分支
git checkout -b fix/your-fix-name

# 创建文档分支
git checkout -b docs/your-docs-name
```

### 3.3 提交代码
```bash
# 添加修改
git add .

# 提交修改
git commit -m "feat: add new feature"

# 推送到远程
git push origin feature/your-feature-name
```

### 3.4 提交 Pull Request
1. 访问你的 Fork 仓库
2. 点击 Pull Request 按钮
3. 选择目标分支
4. 填写描述信息
5. 提交 PR

## 4. 代码规范

### 4.1 Java 代码规范
1. 命名规范
   - 类名：大驼峰命名
   - 方法名：小驼峰命名
   - 变量名：小驼峰命名
   - 常量名：全大写下划线分隔
   - 包名：全小写点分隔

2. 注释规范
   - 类注释：说明类的功能
   - 方法注释：说明方法的功能、参数和返回值
   - 关键代码注释：说明代码的作用

3. 代码格式
   - 使用 4 个空格缩进
   - 每行不超过 120 个字符
   - 大括号使用 K&R 风格

### 4.2 Vue 代码规范
1. 命名规范
   - 组件名：大驼峰命名
   - 方法名：小驼峰命名
   - 变量名：小驼峰命名
   - 常量名：全大写下划线分隔

2. 文件组织
   - 组件文件：大驼峰命名
   - 工具文件：小驼峰命名
   - 样式文件：小写中划线命名

3. 代码格式
   - 使用 2 个空格缩进
   - 每行不超过 100 个字符
   - 使用单引号

## 5. 提交规范

### 5.1 提交信息格式
```
<type>(<scope>): <subject>

<body>

<footer>
```

### 5.2 类型说明
- feat: 新功能
- fix: 修复问题
- docs: 文档修改
- style: 代码格式修改
- refactor: 代码重构
- test: 测试用例修改
- chore: 其他修改

### 5.3 示例
```
feat(task): add task dependency support

- Add task dependency table
- Add dependency check logic
- Add dependency execution order

Closes #123
```

## 6. 测试规范

### 6.1 单元测试
- 测试类名：以 Test 结尾
- 测试方法名：以 test 开头
- 测试覆盖率要求：>80%

### 6.2 集成测试
- 测试类名：以 IntegrationTest 结尾
- 测试方法名：以 test 开头
- 测试数据：使用测试数据库

### 6.3 前端测试
- 组件测试：使用 Vue Test Utils
- E2E测试：使用 Cypress
- 测试覆盖率要求：>80%

## 7. 文档规范

### 7.1 文档类型
- README.md：项目说明
- API.md：接口文档
- DEVELOPMENT.md：开发文档
- DEPLOYMENT.md：部署文档
- CONTRIBUTING.md：贡献指南

### 7.2 文档格式
- 使用 Markdown 格式
- 标题层级：最多 4 级
- 代码块：指定语言
- 图片：使用相对路径

### 7.3 文档内容
- 清晰的结构
- 完整的说明
- 准确的描述
- 必要的示例

## 8. 发布流程

### 8.1 版本号规范
- 主版本号：重大更新
- 次版本号：功能更新
- 修订号：问题修复

### 8.2 发布步骤
1. 更新版本号
2. 更新更新日志
3. 创建发布分支
4. 执行测试
5. 合并到主分支
6. 创建标签
7. 发布到 Maven 仓库

### 8.3 发布检查清单
- [ ] 所有测试通过
- [ ] 文档已更新
- [ ] 更新日志已更新
- [ ] 版本号已更新
- [ ] 依赖已更新
- [ ] 代码已审查

## 9. 联系方式

### 9.1 问题反馈
- 提交 Issue
- 发送邮件
- 加入讨论组

### 9.2 社区交流
- 微信群
- QQ群
- 论坛

## 10. 许可证

本项目采用 MIT 许可证，详见 [LICENSE](LICENSE) 文件。 