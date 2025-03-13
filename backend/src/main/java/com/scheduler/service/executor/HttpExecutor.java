package com.scheduler.service.executor;

import com.scheduler.model.Task;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class HttpExecutor implements TaskExecutor {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void execute(Task task, TaskCallback onSuccess, TaskErrorCallback onError) {
        try {
            // 检查必要参数
            if (task.getHttpUrl() == null || task.getHttpUrl().trim().isEmpty()) {
                throw new IllegalArgumentException("HTTP URL is required");
            }
            
            // 默认使用 GET 方法
            String methodStr = task.getHttpMethod();
            HttpMethod method = (methodStr != null && !methodStr.trim().isEmpty()) 
                ? HttpMethod.valueOf(methodStr.toUpperCase()) 
                : HttpMethod.GET;

            // 修改请求头处理逻辑
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, String> headerStr = task.getHttpHeaders();
            if (headerStr != null) {
                headerStr.forEach((key, value) -> {
                    if (key != null && value != null) {
                        headers.set(key, value);  // 使用 set 而不是 add
                    }
                });
            }

            // 构建请求体
            String body = task.getHttpBody();
            HttpEntity<?> entity = body != null && !body.trim().isEmpty()
                ? new HttpEntity<>(body, headers)
                : new HttpEntity<>(headers);

            // 发送请求
            ResponseEntity<String> response = restTemplate.exchange(
                task.getHttpUrl(),
                method,
                entity,
                String.class
            );

            // 检查响应状态
            if (response.getStatusCode().is2xxSuccessful()) {
                onSuccess.onComplete(task, response.getBody());  // 传递响应内容作为输出
            } else {
                onError.onError(task, new RuntimeException(
                    String.format("HTTP request failed with status: %s, body: %s", 
                        response.getStatusCode(), 
                        response.getBody())
                ));
            }
        } catch (Exception e) {
            log.error("Failed to execute HTTP task: {}", task.getId(), e);
            onError.onError(task, e);
        }
    }

    @Override
    public void validate(Task task) {
        if (task.getHttpUrl() == null || task.getHttpUrl().trim().isEmpty()) {
            throw new IllegalArgumentException("HTTP URL is required");
        }
        
        if (task.getHttpMethod() != null && !task.getHttpMethod().trim().isEmpty()) {
            try {
                HttpMethod.valueOf(task.getHttpMethod().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid HTTP method: " + task.getHttpMethod());
            }
        }

        if (task.getHttpHeaders() != null) {
            try {
                // 验证所有值都可以转换为字符串
                task.getHttpHeaders().forEach((key, value) -> {
                    if (key == null || value == null) {
                        throw new IllegalArgumentException("Header key and value cannot be null");
                    }
                    String.valueOf(value); // 确保可以转换为字符串
                });
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid HTTP headers format: " + e.getMessage());
            }
        }
    }

    @Override
    public void stop() {
        // HTTP请求不支持停止操作
    }

    @Override
    public String getType() {
        return "HTTP";
    }
}
