package com.scheduler.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "dingtalk")
public class DingTalkConfig {
    private String webhook;
} 