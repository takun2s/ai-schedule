package com.scheduler.common.executor.impl;

import com.scheduler.common.exception.BaseException;
import com.scheduler.common.executor.TaskExecutor;
import com.scheduler.common.model.Task;
import com.scheduler.common.model.TaskExecution;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * HTTP任务执行器
 */
@Slf4j
@Component
public class HttpTaskExecutor implements TaskExecutor {

    @Override
    public void execute(Task task, TaskExecution execution) {
        String url = task.getContent();
        String method = "GET";
        String params = task.getParams();
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 创建HTTP请求
            if ("POST".equalsIgnoreCase(method)) {
                HttpPost httpPost = new HttpPost(url);
                if (params != null && !params.isEmpty()) {
                    StringEntity entity = new StringEntity(params, StandardCharsets.UTF_8);
                    entity.setContentType("application/json");
                    httpPost.setEntity(entity);
                }
                
                // 执行请求
                try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                    handleResponse(response, execution);
                }
            } else {
                HttpGet httpGet = new HttpGet(url);
                // 执行请求
                try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                    handleResponse(response, execution);
                }
            }
        } catch (Exception e) {
            log.error("HTTP任务执行失败", e);
            execution.setStatus(3); // 执行失败
            execution.setErrorMessage(e.getMessage());
            throw new BaseException("HTTP任务执行失败: " + e.getMessage());
        }
    }

    private void handleResponse(CloseableHttpResponse response, TaskExecution execution) throws Exception {
        int statusCode = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();
        String result = entity != null ? EntityUtils.toString(entity, StandardCharsets.UTF_8) : null;
        
        if (statusCode >= 200 && statusCode < 300) {
            execution.setStatus(2); // 执行成功
            execution.setResult(result);
        } else {
            execution.setStatus(3); // 执行失败
            execution.setErrorMessage("HTTP请求失败，状态码：" + statusCode);
            throw new BaseException("HTTP请求失败，状态码：" + statusCode);
        }
    }

    @Override
    public Integer getType() {
        return 1; // HTTP类型
    }

    @Override
    public String getName() {
        return "HTTP任务执行器";
    }

    @Override
    public String getDescription() {
        return "支持HTTP GET和POST请求的任务执行器";
    }
} 