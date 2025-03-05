package com.scheduler.common.notifier.impl;

import com.scheduler.common.exception.BaseException;
import com.scheduler.common.model.TaskAlert;
import com.scheduler.common.notifier.TaskNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * 邮件通知器
 */
@Slf4j
@Component
public class EmailTaskNotifier implements TaskNotifier {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void send(TaskAlert alert) {
        try {
            // 创建邮件消息
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(alert.getTarget());
            message.setSubject(alert.getSubject());
            message.setText(alert.getContent());

            // 发送邮件
            mailSender.send(message);

            // 更新告警状态
            alert.setStatus(1); // 发送成功
            alert.setSendResult("邮件发送成功");
        } catch (Exception e) {
            log.error("邮件发送失败", e);
            alert.setStatus(2); // 发送失败
            alert.setSendResult("邮件发送失败：" + e.getMessage());
            throw new BaseException("邮件发送失败: " + e.getMessage());
        }
    }

    @Override
    public String getType() {
        return "email";
    }

    @Override
    public String getName() {
        return "邮件通知器";
    }

    @Override
    public String getDescription() {
        return "支持邮件发送的告警通知器";
    }
} 