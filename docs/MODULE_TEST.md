# 模块测试指南

## 1. 测试策略

### 1.1 测试范围
- 核心模块
  - 任务调度模块
  - 任务执行模块
  - 告警模块
  - 系统配置模块

- 基础模块
  - 用户认证模块
  - 权限管理模块
  - 日志管理模块
  - 数据备份模块

### 1.2 测试类型
- 单元测试
  - 类测试
  - 方法测试
  - 接口测试
  - 工具类测试

- 集成测试
  - 模块间集成
  - 接口集成
  - 数据集成
  - 服务集成

- 性能测试
  - 并发测试
  - 压力测试
  - 稳定性测试
  - 资源消耗测试

### 1.3 测试环境
- 开发环境
  - 本地开发环境
  - 开发服务器环境
  - 测试数据库
  - 测试缓存

- 测试环境
  - 测试服务器
  - 测试数据库
  - 测试缓存
  - 测试工具

## 2. 测试用例

### 2.1 任务调度模块
```java
@Test
public void testTaskScheduling() {
    // 测试任务创建
    Task task = new Task();
    task.setName("测试任务");
    task.setType("HTTP");
    task.setCron("0 0 * * * ?");
    Task savedTask = taskService.createTask(task);
    assertNotNull(savedTask.getId());

    // 测试任务调度
    schedulerService.scheduleTask(savedTask);
    assertTrue(schedulerService.isTaskScheduled(savedTask.getId()));

    // 测试任务取消
    schedulerService.unscheduleTask(savedTask.getId());
    assertFalse(schedulerService.isTaskScheduled(savedTask.getId()));
}

@Test
public void testTaskDependency() {
    // 测试任务依赖
    Task parentTask = createTask("父任务");
    Task childTask = createTask("子任务");
    taskService.addDependency(childTask.getId(), parentTask.getId());

    // 验证依赖关系
    List<Task> dependencies = taskService.getTaskDependencies(childTask.getId());
    assertTrue(dependencies.contains(parentTask));
}
```

### 2.2 任务执行模块
```java
@Test
public void testTaskExecution() {
    // 测试任务执行
    Task task = createTask("执行测试");
    ExecutionRecord record = taskExecutor.execute(task);
    assertNotNull(record);
    assertEquals(ExecutionStatus.SUCCESS, record.getStatus());

    // 测试执行日志
    List<ExecutionLog> logs = executionLogService.getLogs(record.getId());
    assertFalse(logs.isEmpty());
    assertTrue(logs.stream().anyMatch(log -> 
        log.getLevel().equals("INFO") && 
        log.getContent().contains("任务执行成功")));
}

@Test
public void testTaskTimeout() {
    // 测试任务超时
    Task task = createTask("超时测试");
    task.setTimeout(1); // 1秒超时
    ExecutionRecord record = taskExecutor.execute(task);
    assertEquals(ExecutionStatus.TIMEOUT, record.getStatus());
}
```

### 2.3 告警模块
```java
@Test
public void testAlertNotification() {
    // 测试告警配置
    AlertConfig config = new AlertConfig();
    config.setType(AlertType.EMAIL);
    config.setReceiver("test@example.com");
    config.setCondition("status == 'FAILED'");
    alertService.saveConfig(config);

    // 测试告警触发
    Task task = createTask("告警测试");
    ExecutionRecord record = createFailedExecution(task);
    alertService.checkAndNotify(record);

    // 验证告警记录
    List<AlertRecord> alerts = alertService.getAlertRecords(task.getId());
    assertFalse(alerts.isEmpty());
    assertEquals(AlertStatus.SENT, alerts.get(0).getStatus());
}
```

### 2.4 系统配置模块
```java
@Test
public void testSystemConfig() {
    // 测试配置保存
    SystemSetting setting = new SystemSetting();
    setting.setCategory("system");
    setting.setKey("maxConcurrentTasks");
    setting.setValue("10");
    systemSettingService.saveSetting(setting);

    // 测试配置读取
    String value = systemSettingService.getSetting("system", "maxConcurrentTasks");
    assertEquals("10", value);

    // 测试配置更新
    setting.setValue("20");
    systemSettingService.updateSetting(setting);
    value = systemSettingService.getSetting("system", "maxConcurrentTasks");
    assertEquals("20", value);
}
```

### 2.5 用户认证模块
```java
@Test
public void testUserAuthentication() {
    // 测试用户注册
    User user = new User();
    user.setUsername("testuser");
    user.setPassword("password123");
    user.setEmail("test@example.com");
    User savedUser = userService.register(user);
    assertNotNull(savedUser.getId());

    // 测试用户登录
    String token = authService.login("testuser", "password123");
    assertNotNull(token);
    assertTrue(token.length() > 0);

    // 测试token验证
    UserDetails userDetails = authService.validateToken(token);
    assertEquals("testuser", userDetails.getUsername());
}

@Test
public void testPasswordReset() {
    // 测试密码重置请求
    String resetToken = authService.requestPasswordReset("test@example.com");
    assertNotNull(resetToken);

    // 测试密码重置
    authService.resetPassword(resetToken, "newpassword123");
    
    // 验证新密码
    String token = authService.login("testuser", "newpassword123");
    assertNotNull(token);
}
```

### 2.6 权限管理模块
```java
@Test
public void testRolePermission() {
    // 测试角色创建
    Role role = new Role();
    role.setName("ADMIN");
    role.setDescription("系统管理员");
    Role savedRole = roleService.createRole(role);
    assertNotNull(savedRole.getId());

    // 测试权限分配
    Permission permission = new Permission();
    permission.setName("TASK_MANAGE");
    permission.setDescription("任务管理权限");
    roleService.addPermission(savedRole.getId(), permission);

    // 测试用户角色分配
    User user = userService.getUserByUsername("testuser");
    userService.assignRole(user.getId(), savedRole.getId());

    // 验证权限
    List<Permission> permissions = userService.getUserPermissions(user.getId());
    assertTrue(permissions.stream().anyMatch(p -> p.getName().equals("TASK_MANAGE")));
}

@Test
public void testAccessControl() {
    // 测试权限检查
    User user = userService.getUserByUsername("testuser");
    boolean hasPermission = permissionService.checkPermission(user.getId(), "TASK_MANAGE");
    assertTrue(hasPermission);

    // 测试无权限访问
    boolean noPermission = permissionService.checkPermission(user.getId(), "SYSTEM_MANAGE");
    assertFalse(noPermission);
}
```

### 2.7 日志管理模块
```java
@Test
public void testLogManagement() {
    // 测试日志记录
    LogEntry log = new LogEntry();
    log.setLevel("INFO");
    log.setModule("TASK");
    log.setContent("任务执行成功");
    log.setUserId("testuser");
    LogEntry savedLog = logService.saveLog(log);
    assertNotNull(savedLog.getId());

    // 测试日志查询
    List<LogEntry> logs = logService.queryLogs(
        "TASK",
        "INFO",
        LocalDateTime.now().minusHours(1),
        LocalDateTime.now()
    );
    assertFalse(logs.isEmpty());

    // 测试日志导出
    String exportPath = logService.exportLogs(
        "TASK",
        LocalDateTime.now().minusDays(1),
        LocalDateTime.now()
    );
    assertTrue(new File(exportPath).exists());
}

@Test
public void testLogRotation() {
    // 测试日志轮转
    logService.rotateLogs();
    
    // 验证旧日志归档
    List<File> archivedLogs = logService.getArchivedLogs();
    assertFalse(archivedLogs.isEmpty());
    
    // 测试日志清理
    logService.cleanOldLogs(30); // 清理30天前的日志
    List<File> remainingLogs = logService.getArchivedLogs();
    assertTrue(remainingLogs.stream().allMatch(f -> 
        f.lastModified() > System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000L));
}
```

### 2.8 数据备份模块
```java
@Test
public void testDataBackup() {
    // 测试数据库备份
    String backupPath = backupService.createDatabaseBackup();
    assertTrue(new File(backupPath).exists());

    // 测试文件备份
    String fileBackupPath = backupService.createFileBackup();
    assertTrue(new File(fileBackupPath).exists());

    // 测试备份验证
    boolean isValid = backupService.validateBackup(backupPath);
    assertTrue(isValid);
}

@Test
public void testDataRestore() {
    // 测试数据恢复
    String backupPath = backupService.getLatestBackup();
    backupService.restoreFromBackup(backupPath);

    // 验证数据完整性
    List<Task> tasks = taskService.getAllTasks();
    assertFalse(tasks.isEmpty());

    // 测试增量备份
    String incrementalBackup = backupService.createIncrementalBackup();
    assertTrue(new File(incrementalBackup).exists());
}
```

## 3. 测试流程

### 3.1 测试准备
- 环境准备
  - 配置测试环境
  - 准备测试数据
  - 部署测试服务
  - 配置测试工具

- 用例准备
  - 编写测试用例
  - 准备测试数据
  - 设计测试场景
  - 制定测试计划

### 3.2 测试执行
- 单元测试
  ```bash
  # 执行所有单元测试
  mvn test -Dtest=*Test

  # 执行特定模块测试
  mvn test -Dtest=TaskServiceTest,TaskExecutorTest,AlertServiceTest,SystemSettingTest,UserServiceTest,RoleServiceTest,LogServiceTest,BackupServiceTest

  # 执行带覆盖率的测试
  mvn test jacoco:report
  ```

- 集成测试
  ```bash
  # 执行所有集成测试
  mvn verify -Dtest=*IntegrationTest

  # 执行特定集成测试
  mvn verify -Dtest=TaskIntegrationTest,AlertIntegrationTest,AuthIntegrationTest

  # 执行带环境的集成测试
  mvn verify -Dtest=*IntegrationTest -Dspring.profiles.active=test
  ```

- 性能测试
  ```bash
  # 执行所有性能测试
  mvn test -Dtest=*PerformanceTest

  # 执行特定性能测试
  mvn test -Dtest=TaskPerformanceTest,AlertPerformanceTest,AuthPerformanceTest

  # 执行带参数的性能测试
  mvn test -Dtest=*PerformanceTest -Dtest.concurrent.users=100 -Dtest.duration=300
  ```

### 3.3 测试报告
- 测试结果
  - 测试用例执行情况
  - 测试覆盖率报告
  - 性能测试报告
  - 问题统计报告

- 问题跟踪
  - 问题记录
  - 问题分类
  - 问题修复
  - 问题验证

## 4. 测试工具

### 4.1 单元测试工具
- JUnit 5
  - 测试框架
  - 断言工具
  - 测试生命周期
  - 参数化测试

- Mockito
  - 模拟对象
  - 行为验证
  - 异常模拟
  - 参数匹配

### 4.2 集成测试工具
- TestContainers
  - 数据库测试
  - Redis测试
  - 消息队列测试
  - 服务集成测试

- Spring Test
  - 应用上下文测试
  - 事务管理测试
  - 安全测试
  - 接口测试

### 4.3 性能测试工具
- JMeter
  - 并发测试
  - 压力测试
  - 性能监控
  - 测试报告

- Gatling
  - 场景测试
  - 实时监控
  - 性能分析
  - 报告生成

## 5. 测试规范

### 5.1 命名规范
- 测试类命名
  - 以Test结尾
  - 使用驼峰命名
  - 清晰表达测试目的
  - 遵循包命名规则

- 测试方法命名
  - 以test开头
  - 使用驼峰命名
  - 描述测试场景
  - 包含预期结果

### 5.2 代码规范
- 测试代码结构
  - 准备测试数据
  - 执行测试操作
  - 验证测试结果
  - 清理测试数据

- 测试代码质量
  - 代码可读性
  - 代码复用性
  - 代码维护性
  - 代码覆盖率

### 5.3 文档规范
- 测试文档
  - 测试计划
  - 测试用例
  - 测试报告
  - 问题记录

- 测试说明
  - 测试目的
  - 测试范围
  - 测试步骤
  - 测试结果

## 6. 持续集成

### 6.1 自动化测试
- 测试自动化
  - 单元测试自动化
  - 集成测试自动化
  - 性能测试自动化
  - 回归测试自动化

- 测试触发
  - 代码提交触发
  - 定时触发
  - 手动触发
  - 条件触发

### 6.2 测试报告
- 测试结果
  - 测试通过率
  - 测试覆盖率
  - 性能指标
  - 问题统计

- 报告展示
  - 测试仪表盘
  - 趋势图表
  - 问题追踪
  - 改进建议

### 6.3 质量门禁
- 测试要求
  - 单元测试覆盖率
  - 集成测试通过率
  - 性能测试指标
  - 代码质量要求

- 发布控制
  - 测试通过控制
  - 覆盖率控制
  - 性能控制
  - 质量控制 