package com.scheduler.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 短信配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "sms")
public class SmsConfig {
    /**
     * 短信服务商
     */
    private String provider;

    /**
     * 短信服务商API地址
     */
    private String apiUrl;

    /**
     * 短信服务商API密钥
     */
    private String accessKey;

    /**
     * 短信服务商API密钥ID
     */
    private String secretKey;

    /**
     * 短信签名
     */
    private String signName;

    /**
     * 短信模板ID
     */
    private String templateCode;
} 