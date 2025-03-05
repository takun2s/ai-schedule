package com.scheduler.common.notifier.impl;

import com.scheduler.common.exception.BaseException;
import com.scheduler.common.model.TaskAlert;
import com.scheduler.common.notifier.TaskNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 短信通知器
 */
@Slf4j
@Component
public class SmsTaskNotifier implements TaskNotifier {

    @Value("${sms.access-key}")
    private String accessKey;

    @Value("${sms.secret-key}")
    private String secretKey;

    @Value("${sms.sign-name}")
    private String signName;

    @Value("${sms.template-code}")
    private String templateCode;

    @Override
    public void send(TaskAlert alert) {
        try {
            // TODO: 调用短信服务商API发送短信
            // 这里以阿里云短信服务为例
            // 实际项目中需要根据使用的短信服务商实现具体的发送逻辑
            
            // 模拟发送成功
            alert.setStatus(1); // 发送成功
            alert.setSendResult("短信发送成功");
        } catch (Exception e) {
            log.error("短信发送失败", e);
            alert.setStatus(2); // 发送失败
            alert.setSendResult("短信发送失败：" + e.getMessage());
            throw new BaseException("短信发送失败: " + e.getMessage());
        }
    }

    @Override
    public String getType() {
        return "sms";
    }

    @Override
    public String getName() {
        return "短信通知器";
    }

    @Override
    public String getDescription() {
        return "支持短信发送的告警通知器";
    }
} 