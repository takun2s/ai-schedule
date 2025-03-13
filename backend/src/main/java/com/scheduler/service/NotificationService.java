package com.scheduler.service;

import com.scheduler.model.message.EmailMessage;
import com.scheduler.model.message.WebhookMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendEmailNotification(String to, String subject, String content) {
        EmailMessage message = new EmailMessage(to, subject, content);
        rabbitTemplate.convertAndSend("notification.exchange", "email", message);
    }

    public void sendWebhookNotification(String url, String payload) {
        WebhookMessage message = new WebhookMessage(url, payload);
        rabbitTemplate.convertAndSend("notification.exchange", "webhook", message);
    }
}
