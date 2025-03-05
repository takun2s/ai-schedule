package com.scheduler.common.notifier;

import com.scheduler.common.model.TaskAlert;
import com.scheduler.common.notifier.impl.EmailTaskNotifier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.mail.host=smtp.example.com",
    "spring.mail.port=587",
    "spring.mail.username=test@example.com",
    "spring.mail.password=test-password"
})
class EmailTaskNotifierTest {

    @Autowired
    private EmailTaskNotifier emailTaskNotifier;

    @Test
    void testSend() {
        // 创建测试告警
        TaskAlert alert = new TaskAlert();
        alert.setTarget("test@example.com");
        alert.setContent("测试告警内容");
        
        // 发送告警
        emailTaskNotifier.send(alert);
        
        // 验证结果
        assertEquals(1, alert.getStatus()); // 发送成功
        assertNotNull(alert.getSendResult());
    }

    @Test
    void testGetType() {
        assertEquals("email", emailTaskNotifier.getType());
    }

    @Test
    void testGetName() {
        assertEquals("邮件通知器", emailTaskNotifier.getName());
    }

    @Test
    void testGetDescription() {
        assertTrue(emailTaskNotifier.getDescription().contains("邮件"));
    }
} 