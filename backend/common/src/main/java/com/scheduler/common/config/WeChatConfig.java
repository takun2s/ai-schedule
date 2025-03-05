package com.scheduler.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 企业微信配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wechat")
public class WeChatConfig {
    /**
     * 企业ID
     */
    private String corpId;

    /**
     * 应用ID
     */
    private String agentId;

    /**
     * 应用密钥
     */
    private String secret;

    /**
     * 接收消息的用户ID列表
     */
    private String[] userIds;

    /**
     * 接收消息的部门ID列表
     */
    private String[] deptIds;

    /**
     * 接收消息的标签ID列表
     */
    private String[] tagIds;
} 