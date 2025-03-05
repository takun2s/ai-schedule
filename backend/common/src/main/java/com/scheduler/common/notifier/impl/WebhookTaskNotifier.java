package com.scheduler.common.notifier.impl;

import com.scheduler.common.exception.BaseException;
import com.scheduler.common.model.TaskAlert;
import com.scheduler.common.notifier.TaskNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Webhook通知器
 */
@Slf4j
@Component
public class WebhookTaskNotifier implements TaskNotifier {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void send(TaskAlert alert) {
        try {
            String webhookUrl = alert.getTarget();
            if (webhookUrl == null || webhookUrl.isEmpty()) {
                log.error("Webhook URL不能为空");
                return;
            }

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("subject", alert.getSubject());
            requestBody.put("content", alert.getContent());

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 发送请求
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            restTemplate.postForEntity(webhookUrl, request, String.class);

            log.info("Webhook告警发送成功");
        } catch (Exception e) {
            log.error("Webhook告警发送失败", e);
            throw new RuntimeException("Webhook告警发送失败", e);
        }
    }

    @Override
    public String getType() {
        return "webhook";
    }

    @Override
    public String getName() {
        return "Webhook通知器";
    }

    @Override
    public String getDescription() {
        return "通过Webhook发送告警通知";
    }
} 