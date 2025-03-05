package com.scheduler.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MailConfig.class, SmsConfig.class, DingTalkConfig.class, WeChatConfig.class})
public class AlertConfig {
} 