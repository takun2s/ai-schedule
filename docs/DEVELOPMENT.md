# 开发规范文档

## 1. 项目架构

### 1.1 目录结构
```
scheduler/
├── backend/                # 后端项目
│   ├── src/               # 源代码
│   │   ├── main/         # 主要代码
│   │   │   ├── java/     # Java代码
│   │   │   └── resources/# 配置文件
│   │   └── test/         # 测试代码
│   ├── pom.xml           # Maven配置
│   └── README.md         # 项目说明
├── frontend/              # 前端项目
│   ├── src/              # 源代码
│   │   ├── api/          # API接口
│   │   ├── assets/       # 静态资源
│   │   ├── components/   # 组件
│   │   ├── router/       # 路由配置
│   │   ├── store/        # 状态管理
│   │   ├── utils/        # 工具函数
│   │   └── views/        # 页面
│   ├── package.json      # 依赖配置
│   └── README.md         # 项目说明
└── docs/                 # 文档
    ├── API.md           # API文档
    ├── DEVELOPMENT.md   # 开发文档
    └── README.md        # 项目说明
```

### 1.2 技术栈
1. 后端技术栈
   - Spring Boot 2.7.x
   - Spring Security
   - MyBatis Plus
   - Redis
   - MySQL
   - ZooKeeper
   - Quartz

2. 前端技术栈
   - Vue 3
   - TypeScript
   - Element Plus
   - Vite
   - Pinia
   - Vue Router
   - Axios

## 2. 开发标准

### 2.1 Java开发规范

#### 2.1.1 命名规范
1. 类命名
   - 使用大驼峰命名法
   - 名词或名词短语
   - 避免缩写
   - 示例：`TaskService`、`UserController`

2. 方法命名
   - 使用小驼峰命名法
   - 动词或动词短语
   - 避免缩写
   - 示例：`getTaskById`、`createUser`

3. 变量命名
   - 使用小驼峰命名法
   - 名词或名词短语
   - 避免缩写
   - 示例：`taskId`、`userName`

4. 常量命名
   - 全大写
   - 单词间用下划线分隔
   - 示例：`MAX_RETRY_COUNT`、`DEFAULT_TIMEOUT`

5. 包命名
   - 全小写
   - 单词间用点分隔
   - 示例：`com.scheduler.task`、`com.scheduler.user`

#### 2.1.2 代码格式
1. 缩进
   - 使用4个空格
   - 不使用Tab字符

2. 行宽
   - 最大120个字符
   - 超过时换行

3. 大括号
   - 左大括号不换行
   - 右大括号独占一行

4. 空行
   - 类定义前后空一行
   - 方法定义前后空一行
   - 逻辑块之间空一行

#### 2.1.3 注释规范
1. 类注释
```java
/**
 * 任务服务类
 *
 * @author 作者名
 * @date 2024-01-01
 */
public class TaskService {
    // 类内容
}
```

2. 方法注释
```java
/**
 * 创建任务
 *
 * @param task 任务信息
 * @return 任务ID
 * @throws BusinessException 业务异常
 */
public Long createTask(Task task) {
    // 方法内容
}
```

3. 代码注释
```java
// 检查任务状态
if (task.getStatus() == TaskStatus.RUNNING) {
    // 任务正在运行，不能删除
    throw new BusinessException("任务正在运行，不能删除");
}
```

### 2.2 Vue开发规范

#### 2.2.1 命名规范
1. 组件命名
   - 使用大驼峰命名法
   - 示例：`TaskList.vue`、`UserForm.vue`

2. 方法命名
   - 使用小驼峰命名法
   - 示例：`getTaskList`、`handleSubmit`

3. 变量命名
   - 使用小驼峰命名法
   - 示例：`taskId`、`userName`

4. 常量命名
   - 全大写
   - 单词间用下划线分隔
   - 示例：`MAX_RETRY_COUNT`、`DEFAULT_TIMEOUT`

#### 2.2.2 代码格式
1. 缩进
   - 使用2个空格
   - 不使用Tab字符

2. 行宽
   - 最大100个字符
   - 超过时换行

3. 引号
   - 使用单引号
   - 示例：`'task'`、`'user'`

4. 分号
   - 语句末尾使用分号
   - 示例：`const task = {};`

#### 2.2.3 组件规范
1. 组件结构
```vue
<template>
  <div class="task-list">
    <!-- 模板内容 -->
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'TaskList',
  props: {
    // 属性定义
  },
  setup() {
    // 组件逻辑
  }
})
</script>

<style lang="scss" scoped>
.task-list {
  // 样式定义
}
</style>
```

2. 组件通信
   - 使用props传递数据
   - 使用emit发送事件
   - 使用provide/inject共享数据

3. 状态管理
   - 使用Pinia管理全局状态
   - 使用ref/reactive管理局部状态

### 2.3 Git规范

#### 2.3.1 分支管理
1. 主分支
   - master：生产环境分支
   - develop：开发环境分支

2. 功能分支
   - feature/*：新功能分支
   - fix/*：问题修复分支
   - release/*：发布分支

3. 分支命名
   - feature/任务名称
   - fix/问题描述
   - release/版本号

#### 2.3.2 提交规范
1. 提交信息格式
```
<type>(<scope>): <subject>

<body>

<footer>
```

2. 类型说明
   - feat：新功能
   - fix：修复问题
   - docs：文档修改
   - style：代码格式修改
   - refactor：代码重构
   - test：测试用例修改
   - chore：其他修改

3. 示例
```
feat(task): add task dependency support

- Add task dependency table
- Add dependency check logic
- Add dependency execution order

Closes #123
```

#### 2.3.3 工作流程
1. 创建分支
```bash
git checkout -b feature/task-dependency
```

2. 提交代码
```bash
git add .
git commit -m "feat(task): add task dependency support"
```

3. 推送到远程
```bash
git push origin feature/task-dependency
```

4. 创建Pull Request
   - 选择目标分支
   - 填写描述信息
   - 等待代码审查

## 3. 扩展指南

### 3.1 添加新任务类型

1. 创建任务执行器
```java
@Component
public class CustomTaskExecutor implements TaskExecutor {
    @Override
    public void execute(Task task) {
        // 实现任务执行逻辑
    }
}
```

2. 注册任务执行器
```java
@Configuration
public class TaskConfig {
    @Bean
    public TaskExecutor customTaskExecutor() {
        return new CustomTaskExecutor();
    }
}
```

3. 添加任务类型
```java
public enum TaskType {
    HTTP,
    SHELL,
    DATABASE,
    CUSTOM
}
```

### 3.2 添加新告警方式

1. 创建告警通知器
```java
@Component
public class CustomNotifier implements AlertNotifier {
    @Override
    public void notify(Alert alert) {
        // 实现告警通知逻辑
    }
}
```

2. 注册告警通知器
```java
@Configuration
public class AlertConfig {
    @Bean
    public AlertNotifier customNotifier() {
        return new CustomNotifier();
    }
}
```

3. 添加告警类型
```java
public enum AlertType {
    EMAIL,
    DINGTALK,
    WECHAT_WORK,
    CUSTOM
}
```

### 3.3 添加新页面

1. 创建页面组件
```vue
<template>
  <div class="custom-page">
    <!-- 页面内容 -->
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'CustomPage',
  setup() {
    // 页面逻辑
  }
})
</script>
```

2. 添加路由配置
```typescript
const routes = [
  {
    path: '/custom',
    name: 'Custom',
    component: () => import('@/views/CustomPage.vue')
  }
]
```

3. 添加菜单项
```typescript
const menus = [
  {
    title: '自定义页面',
    path: '/custom',
    icon: 'custom'
  }
]
```

## 4. 部署指南

### 4.1 后端部署

1. 打包
```bash
mvn clean package -DskipTests
```

2. 配置文件
```yaml
server:
  port: 8080
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/scheduler
    username: root
    password: root
  redis:
    host: localhost
    port: 6379
```

3. 启动服务
```bash
java -jar scheduler.jar
```

### 4.2 前端部署

1. 打包
```bash
npm run build
```

2. 配置文件
```javascript
export default {
  baseUrl: 'http://localhost:8080',
  uploadUrl: 'http://localhost:8080/upload'
}
```

3. 部署到Nginx
```nginx
server {
    listen 80;
    server_name localhost;

    location / {
        root /usr/share/nginx/html;
        index index.html;
    }

    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 5. 测试指南

### 5.1 单元测试

1. 测试类命名
```java
public class TaskServiceTest {
    @Test
    public void testCreateTask() {
        // 测试代码
    }
}
```

2. 测试方法命名
   - test + 被测试方法名
   - 示例：`testCreateTask`、`testUpdateTask`

3. 测试用例结构
```java
@Test
public void testCreateTask() {
    // 准备测试数据
    Task task = new Task();
    task.setName("测试任务");

    // 执行测试
    Long taskId = taskService.createTask(task);

    // 验证结果
    assertNotNull(taskId);
    Task savedTask = taskService.getTaskById(taskId);
    assertEquals("测试任务", savedTask.getName());
}
```

### 5.2 接口测试

1. 测试类命名
```java
public class TaskControllerTest {
    @Test
    public void testCreateTask() {
        // 测试代码
    }
}
```

2. 测试方法命名
   - test + 接口路径 + 操作
   - 示例：`testCreateTask`、`testUpdateTask`

3. 测试用例结构
```java
@Test
public void testCreateTask() {
    // 准备测试数据
    Task task = new Task();
    task.setName("测试任务");

    // 执行测试
    MvcResult result = mockMvc.perform(post("/api/v1/task")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(task)))
            .andExpect(status().isOk())
            .andReturn();

    // 验证结果
    String content = result.getResponse().getContentAsString();
    ApiResponse response = objectMapper.readValue(content, ApiResponse.class);
    assertNotNull(response.getData());
}
```

### 5.3 前端测试

1. 组件测试
```typescript
import { mount } from '@vue/test-utils'
import TaskList from './TaskList.vue'

describe('TaskList', () => {
  test('renders task list', () => {
    const wrapper = mount(TaskList)
    expect(wrapper.exists()).toBe(true)
  })
})
```

2. 路由测试
```typescript
import { createRouter, createWebHistory } from 'vue-router'
import { mount } from '@vue/test-utils'

describe('Router', () => {
  test('navigates to task page', async () => {
    const router = createRouter({
      history: createWebHistory(),
      routes: [
        {
          path: '/task',
          component: TaskPage
        }
      ]
    })

    await router.push('/task')
    expect(router.currentRoute.value.path).toBe('/task')
  })
})
```

## 6. 常见问题

### 6.1 开发环境配置

1. JDK配置
```bash
# 设置JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk

# 添加到PATH
export PATH=$JAVA_HOME/bin:$PATH
```

2. Maven配置
```xml
<settings>
    <mirrors>
        <mirror>
            <id>aliyun</id>
            <mirrorOf>central</mirrorOf>
            <name>Aliyun Maven Central</name>
            <url>https://maven.aliyun.com/repository/central</url>
        </mirror>
    </mirrors>
</settings>
```

3. Node.js配置
```bash
# 安装nvm
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash

# 安装Node.js
nvm install 16
nvm use 16
```

### 6.2 调试技巧

1. 后端调试
   - 使用断点调试
   - 使用日志输出
   - 使用Postman测试接口

2. 前端调试
   - 使用Vue DevTools
   - 使用浏览器开发者工具
   - 使用console输出

### 6.3 性能优化

1. 后端优化
   - 使用缓存
   - 优化SQL查询
   - 使用连接池
   - 异步处理

2. 前端优化
   - 路由懒加载
   - 组件按需加载
   - 图片懒加载
   - 代码分割 