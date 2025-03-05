# 测试用例文档

## 1. 单元测试

### 1.1 后端单元测试

1. 测试框架配置
```xml
<!-- pom.xml -->
<dependencies>
    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.8.2</version>
        <scope>test</scope>
    </dependency>
    <!-- Mockito -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>4.5.1</version>
        <scope>test</scope>
    </dependency>
    <!-- JaCoCo -->
    <plugin>
        <groupId>org.jacoco</groupId>
        <artifactId>jacoco-maven-plugin</artifactId>
        <version>0.8.7</version>
        <executions>
            <execution>
                <goals>
                    <goal>prepare-agent</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
</dependencies>
```

2. 服务层测试示例
```java
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskMapper taskMapper;
    
    @Mock
    private TaskExecutionMapper executionMapper;
    
    @InjectMocks
    private TaskService taskService;
    
    @Test
    void createTask_ShouldSucceed() {
        // 准备测试数据
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setName("测试任务");
        taskDTO.setType(1);
        taskDTO.setCron("0 0 * * * ?");
        
        // 模拟Mapper行为
        when(taskMapper.insert(any(Task.class))).thenReturn(1);
        
        // 执行测试
        Task result = taskService.createTask(taskDTO);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("测试任务", result.getName());
        assertEquals(1, result.getType());
        verify(taskMapper).insert(any(Task.class));
    }
    
    @Test
    void executeTask_ShouldThrowException_WhenTaskNotFound() {
        // 准备测试数据
        Long taskId = 1L;
        
        // 模拟Mapper行为
        when(taskMapper.selectById(taskId)).thenReturn(null);
        
        // 执行测试并验证异常
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.executeTask(taskId);
        });
    }
}
```

3. 控制器层测试示例
```java
@WebMvcTest(TaskController.class)
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private TaskService taskService;
    
    @Test
    void getTaskList_ShouldReturnTaskList() throws Exception {
        // 准备测试数据
        List<Task> tasks = Arrays.asList(
            new Task(1L, "任务1", 1, "0 0 * * * ?"),
            new Task(2L, "任务2", 2, "0 0 * * * ?")
        );
        
        // 模拟Service行为
        when(taskService.getTaskList(any())).thenReturn(tasks);
        
        // 执行测试
        mockMvc.perform(get("/api/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(2))
            .andExpect(jsonPath("$.data[0].name").value("任务1"))
            .andExpect(jsonPath("$.data[1].name").value("任务2"));
    }
}
```

### 1.2 前端单元测试

1. 测试框架配置
```javascript
// package.json
{
  "devDependencies": {
    "@vue/test-utils": "^2.0.0",
    "jest": "^27.0.0",
    "babel-jest": "^27.0.0"
  }
}
```

2. 组件测试示例
```javascript
// TaskList.spec.js
import { mount } from '@vue/test-utils'
import TaskList from './TaskList.vue'

describe('TaskList', () => {
  test('renders task list correctly', () => {
    const tasks = [
      { id: 1, name: '任务1', type: 1, cron: '0 0 * * * ?' },
      { id: 2, name: '任务2', type: 2, cron: '0 0 * * * ?' }
    ]
    
    const wrapper = mount(TaskList, {
      props: {
        tasks
      }
    })
    
    expect(wrapper.findAll('.task-item')).toHaveLength(2)
    expect(wrapper.text()).toContain('任务1')
    expect(wrapper.text()).toContain('任务2')
  })
  
  test('emits delete event when delete button is clicked', async () => {
    const wrapper = mount(TaskList, {
      props: {
        tasks: [{ id: 1, name: '任务1' }]
      }
    })
    
    await wrapper.find('.delete-btn').trigger('click')
    expect(wrapper.emitted('delete')).toBeTruthy()
  })
})
```

## 2. 集成测试

### 2.1 后端集成测试

1. 测试配置
```java
@SpringBootTest
@AutoConfigureMockMvc
class TaskIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private TaskMapper taskMapper;
    
    @BeforeEach
    void setUp() {
        // 清理测试数据
        taskMapper.deleteAll();
    }
    
    @Test
    void createAndExecuteTask_ShouldSucceed() throws Exception {
        // 创建任务
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setName("集成测试任务");
        taskDTO.setType(1);
        taskDTO.setCron("0 0 * * * ?");
        
        String response = mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(taskDTO)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
            
        Task task = objectMapper.readValue(response, Task.class);
        
        // 执行任务
        mockMvc.perform(post("/api/tasks/{id}/execute", task.getId()))
            .andExpect(status().isOk());
            
        // 验证执行记录
        List<TaskExecution> executions = executionMapper.selectByTaskId(task.getId());
        assertFalse(executions.isEmpty());
        assertEquals(1, executions.get(0).getStatus());
    }
}
```

### 2.2 前端集成测试

1. 测试配置
```javascript
// cypress.config.js
module.exports = {
  e2e: {
    baseUrl: 'http://localhost:5173',
    supportFile: 'cypress/support/e2e.js',
    specPattern: 'cypress/e2e/**/*.cy.{js,jsx,ts,tsx}'
  }
}
```

2. 测试示例
```javascript
// task.cy.js
describe('Task Management', () => {
  beforeEach(() => {
    cy.login()
    cy.visit('/tasks')
  })
  
  it('should create a new task', () => {
    cy.get('.create-task-btn').click()
    cy.get('input[name="name"]').type('测试任务')
    cy.get('select[name="type"]').select('1')
    cy.get('input[name="cron"]').type('0 0 * * * ?')
    cy.get('.submit-btn').click()
    
    cy.get('.task-list').should('contain', '测试任务')
  })
  
  it('should execute a task', () => {
    cy.get('.task-item').first().find('.execute-btn').click()
    cy.get('.task-status').should('contain', '执行中')
    
    cy.wait(5000)
    cy.get('.task-status').should('contain', '已完成')
  })
})
```

## 3. 性能测试

### 3.1 后端性能测试

1. JMeter测试脚本
```xml
<?xml version="1.0" encoding="UTF-8"?>
<jmeterTestPlan version="1.2" properties="5.0">
  <hashTree>
    <TestPlan guiclass="TestPlanGui" testclass="TestPlan" testname="Task API Test">
      <elementProp name="TestPlan.user_defined_variables" elementType="Arguments">
        <collectionProp name="Arguments.arguments"/>
      </elementProp>
      <boolProp name="TestPlan.functional_mode">false</boolProp>
      <stringProp name="TestPlan.user_define_classpath"></stringProp>
    </TestPlan>
    <hashTree>
      <ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup" testname="Task API Thread Group">
        <intProp name="ThreadGroup.num_threads">100</intProp>
        <intProp name="ThreadGroup.ramp_time">10</intProp>
        <boolProp name="ThreadGroup.scheduler">false</boolProp>
        <stringProp name="ThreadGroup.duration"></stringProp>
        <stringProp name="ThreadGroup.delay"></stringProp>
      </ThreadGroup>
      <hashTree>
        <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="Get Task List">
          <elementProp name="HTTPsampler.Arguments" elementType="Arguments">
            <collectionProp name="Arguments.arguments"/>
          </elementProp>
          <stringProp name="HTTPSampler.domain">localhost</stringProp>
          <stringProp name="HTTPSampler.port">8080</stringProp>
          <stringProp name="HTTPSampler.protocol">http</stringProp>
          <stringProp name="HTTPSampler.path">/api/tasks</stringProp>
          <stringProp name="HTTPSampler.method">GET</stringProp>
          <boolProp name="HTTPSampler.follow_redirects">true</boolProp>
          <boolProp name="HTTPSampler.auto_redirects">false</boolProp>
          <boolProp name="HTTPSampler.use_keepalive">true</boolProp>
          <boolProp name="HTTPSampler.DO_MULTIPART_POST">false</boolProp>
        </HTTPSamplerProxy>
        <hashTree/>
      </hashTree>
    </hashTree>
  </hashTree>
</jmeterTestPlan>
```

### 3.2 前端性能测试

1. Lighthouse测试配置
```javascript
// lighthouse.config.js
module.exports = {
  ci: {
    collect: {
      startServerCommand: 'npm run dev',
      url: ['http://localhost:5173/tasks'],
      numberOfRuns: 5
    },
    assert: {
      assertions: {
        'categories:performance': ['error', {minScore: 0.9}],
        'categories:accessibility': ['error', {minScore: 0.9}],
        'categories:best-practices': ['error', {minScore: 0.9}]
      }
    }
  }
}
```

## 4. 测试覆盖率要求

### 4.1 后端覆盖率要求
- 类覆盖率：> 80%
- 方法覆盖率：> 80%
- 行覆盖率：> 70%
- 分支覆盖率：> 70%

### 4.2 前端覆盖率要求
- 语句覆盖率：> 80%
- 分支覆盖率：> 80%
- 函数覆盖率：> 80%
- 行覆盖率：> 80%

## 5. 测试报告

### 5.1 后端测试报告
```bash
# 生成测试报告
mvn test jacoco:report

# 查看测试报告
open target/site/jacoco/index.html
```

### 5.2 前端测试报告
```bash
# 生成测试报告
npm run test:coverage

# 查看测试报告
open coverage/lcov-report/index.html
```

## 6. 持续集成测试

### 6.1 GitHub Actions配置
```yaml
# .github/workflows/test.yml
name: Test

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v2
    
    - name: Set up JDK 8
      uses: actions/setup-java@v2
      with:
        java-version: '8'
        distribution: 'adopt'
        
    - name: Set up Node.js
      uses: actions/setup-node@v2
      with:
        node-version: '16'
        
    - name: Run Backend Tests
      run: |
        cd backend
        mvn test
        
    - name: Run Frontend Tests
      run: |
        cd frontend
        npm install
        npm test
```

## 7. 测试最佳实践

### 7.1 测试原则
1. 测试独立性
   - 每个测试用例应该独立运行
   - 测试之间不应该相互依赖
   - 测试数据应该独立管理

2. 测试可维护性
   - 使用测试工具类
   - 抽取公共测试数据
   - 保持测试代码简洁

3. 测试可读性
   - 使用清晰的测试命名
   - 添加必要的测试注释
   - 遵循测试代码规范

### 7.2 测试策略
1. 单元测试
   - 测试核心业务逻辑
   - 模拟外部依赖
   - 关注边界条件

2. 集成测试
   - 测试组件交互
   - 使用测试数据库
   - 模拟外部服务

3. 端到端测试
   - 测试完整业务流程
   - 模拟真实用户操作
   - 验证系统功能 