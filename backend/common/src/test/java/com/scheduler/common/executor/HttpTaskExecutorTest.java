package com.scheduler.common.executor;

import com.scheduler.common.model.Task;
import com.scheduler.common.model.TaskExecution;
import com.scheduler.common.executor.impl.HttpTaskExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HttpTaskExecutorTest {

    @Autowired
    private HttpTaskExecutor httpTaskExecutor;

    @Test
    void testExecute() {
        // 创建测试任务
        Task task = new Task();
        task.setContent("https://api.example.com/test");
        task.setParams("{\"key\":\"value\"}");
        
        // 创建执行记录
        TaskExecution execution = new TaskExecution();
        
        // 执行任务
        httpTaskExecutor.execute(task, execution);
        
        // 验证结果
        assertEquals(2, execution.getStatus()); // 执行成功
        assertNotNull(execution.getResult());
    }

    @Test
    void testGetType() {
        assertEquals(1, httpTaskExecutor.getType());
    }

    @Test
    void testGetName() {
        assertEquals("HTTP任务执行器", httpTaskExecutor.getName());
    }

    @Test
    void testGetDescription() {
        assertTrue(httpTaskExecutor.getDescription().contains("HTTP"));
    }
} 