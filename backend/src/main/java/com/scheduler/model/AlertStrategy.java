package com.scheduler.model;

import lombok.Data;

import java.util.List;

@Data
public class AlertStrategy {
    private Long id;
    private String name;
    private String alertType;  // EMAIL, SMS, WEBHOOK
    private List<String> receivers;  // 接收人列表
    private String webhookUrl;  // webhook地址
    private boolean alertOnFailure;  // 失败时告警
    private boolean alertOnTimeout;  // 超时告警
    private boolean alertOnRetry;    // 重试告警
    private int timeoutThreshold;    // 超时阈值（秒）
    private int retryThreshold;      // 重试阈值
    
    // Getters and Setters
}
