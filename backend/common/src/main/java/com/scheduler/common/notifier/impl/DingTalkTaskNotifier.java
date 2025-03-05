package com.scheduler.common.notifier.impl;

import com.scheduler.common.exception.BaseException;
import com.scheduler.common.model.TaskAlert;
import com.scheduler.common.notifier.TaskNotifier;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 钉钉通知器
 */
@Slf4j
@Component
public class DingTalkTaskNotifier implements TaskNotifier {

    @Value("${dingtalk.webhook}")
    private String webhook;

    @Override
    public void send(TaskAlert alert) {
        try {
            // 构建消息内容
            Map<String, Object> message = new HashMap<>();
            message.put("msgtype", "text");
            
            Map<String, String> text = new HashMap<>();
            text.put("content", alert.getContent());
            message.put("text", text);
            
            // 发送消息
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost httpPost = new HttpPost(webhook);
                StringEntity entity = new StringEntity(
                        com.alibaba.fastjson.JSON.toJSONString(message),
                        StandardCharsets.UTF_8);
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
                
                try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                    int statusCode = response.getStatusLine().getStatusCode();
                    String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                    
                    if (statusCode == 200) {
                        alert.setStatus(1); // 发送成功
                        alert.setSendResult("钉钉消息发送成功");
                    } else {
                        alert.setStatus(2); // 发送失败
                        alert.setSendResult("钉钉消息发送失败：" + result);
                        throw new BaseException("钉钉消息发送失败：" + result);
                    }
                }
            }
        } catch (Exception e) {
            log.error("钉钉消息发送失败", e);
            alert.setStatus(2); // 发送失败
            alert.setSendResult("钉钉消息发送失败：" + e.getMessage());
            throw new BaseException("钉钉消息发送失败: " + e.getMessage());
        }
    }

    @Override
    public String getType() {
        return "dingtalk";
    }

    @Override
    public String getName() {
        return "钉钉通知器";
    }

    @Override
    public String getDescription() {
        return "支持钉钉机器人消息的告警通知器";
    }
} 