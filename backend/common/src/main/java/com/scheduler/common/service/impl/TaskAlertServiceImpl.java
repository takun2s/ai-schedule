package com.scheduler.common.service.impl;

import com.scheduler.common.config.SmsConfig;
import com.scheduler.common.config.WeChatConfig;
import com.scheduler.common.exception.BaseException;
import com.scheduler.common.mapper.TaskAlertMapper;
import com.scheduler.common.mapper.TaskExecutionMapper;
import com.scheduler.common.model.TaskAlert;
import com.scheduler.common.model.TaskExecution;
import com.scheduler.common.model.Task;
import com.scheduler.common.service.TaskAlertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Base64;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

/**
 * 任务告警服务实现类
 */
@Slf4j
@Service
public class TaskAlertServiceImpl implements TaskAlertService {

    @Autowired
    private TaskAlertMapper taskAlertMapper;

    @Autowired
    private TaskExecutionMapper taskExecutionMapper;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SmsConfig smsConfig;

    @Autowired
    private WeChatConfig wechatConfig;

    @Override
    public TaskAlert getById(Long id) {
        return taskAlertMapper.selectById(id);
    }

    @Override
    public List<TaskAlert> getList(TaskAlert alert) {
        return taskAlertMapper.selectList(alert);
    }

    @Override
    public void add(TaskAlert alert) {
        try {
            taskAlertMapper.insert(alert);
        } catch (Exception e) {
            log.error("添加告警记录失败", e);
            throw new BaseException("添加告警记录失败");
        }
    }

    @Override
    public void update(TaskAlert alert) {
        try {
            taskAlertMapper.update(alert);
        } catch (Exception e) {
            log.error("更新告警记录失败", e);
            throw new BaseException("更新告警记录失败");
        }
    }

    @Override
    public void delete(Long id) {
        try {
            taskAlertMapper.deleteById(id);
        } catch (Exception e) {
            log.error("删除告警记录失败", e);
            throw new BaseException("删除告警记录失败");
        }
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        try {
            taskAlertMapper.updateStatus(id, status);
        } catch (Exception e) {
            log.error("更新告警状态失败", e);
            throw new BaseException("更新告警状态失败");
        }
    }

    @Override
    public List<TaskAlert> getUnsentList() {
        return taskAlertMapper.selectUnsentList();
    }

    @Override
    public int getCountByTaskId(Long taskId) {
        return taskAlertMapper.selectCountByTaskId(taskId);
    }

    @Override
    public void sendAlert(TaskAlert alert) {
        try {
            // 根据告警类型发送不同的通知
            switch (alert.getType()) {
                case "email":
                    sendEmailAlert(alert.getTarget(), alert.getSubject(), alert.getContent());
                    break;
                case "sms":
                    sendSmsAlert(alert.getTarget(), alert.getContent());
                    break;
                case "dingtalk":
                    sendDingTalkAlert(alert.getTarget(), alert.getSubject(), alert.getContent());
                    break;
                case "wechat":
                    sendWeChatAlert(alert.getTarget(), alert.getSubject(), alert.getContent());
                    break;
                case "webhook":
                    sendWebhookAlert(alert.getTarget(), alert.getSubject(), alert.getContent());
                    break;
                default:
                    log.warn("不支持的告警类型: {}", alert.getType());
                    throw new BaseException("不支持的告警类型: " + alert.getType());
            }

            // 更新告警状态
            alert.setStatus(1); // 已发送
            taskAlertMapper.updateStatus(alert.getId(), 1);
        } catch (Exception e) {
            log.error("发送告警失败", e);
            // 更新告警状态
            alert.setStatus(2); // 发送失败
            taskAlertMapper.updateStatus(alert.getId(), 2);
            throw new BaseException("发送告警失败: " + e.getMessage());
        }
    }

    @Override
    public void retrySend(Long alertId) {
        try {
            // 获取告警记录
            TaskAlert alert = taskAlertMapper.selectById(alertId);
            if (alert == null) {
                throw new BaseException("告警记录不存在");
            }

            // 更新告警状态
            alert.setStatus(0); // 待发送
            taskAlertMapper.updateStatus(alertId, 0);

            // 发送告警
            sendAlert(alert);
        } catch (Exception e) {
            log.error("重试发送告警失败", e);
            throw new BaseException("重试发送告警失败");
        }
    }

    @Override
    public void checkTaskAlert(Long taskId, Long executionId) {
        try {
            // 获取任务告警配置
            List<TaskAlert> alerts = taskAlertMapper.selectByTaskId(taskId);
            if (alerts.isEmpty()) {
                return;
            }

            // 检查是否需要告警
            for (TaskAlert alert : alerts) {
                if (shouldAlert(alert, executionId)) {
                    // 创建告警记录
                    TaskAlert record = new TaskAlert();
                    record.setTaskId(taskId);
                    record.setExecutionId(executionId);
                    record.setType(alert.getType());
                    record.setTarget(alert.getTarget());
                    record.setContent(alert.getContent());
                    record.setStatus(0); // 待发送
                    taskAlertMapper.insert(record);
                }
            }
        } catch (Exception e) {
            log.error("检查任务告警失败", e);
            throw new BaseException("检查任务告警失败");
        }
    }

    private boolean shouldAlert(TaskAlert alert, Long executionId) {
        try {
            // 获取执行记录
            TaskExecution execution = taskExecutionMapper.selectById(executionId);
            if (execution == null) {
                return false;
            }

            // 根据告警规则判断
            switch (alert.getRule()) {
                case "FAILURE": // 执行失败
                    return execution.getStatus() == 2;
                case "TIMEOUT": // 执行超时
                    return execution.getStatus() == 2 && execution.getErrorMessage() != null 
                            && execution.getErrorMessage().contains("任务执行超时");
                case "CUSTOM": // 自定义规则
                    return evaluateCustomRule(alert.getRuleContent(), execution);
                default:
                    log.warn("不支持的告警规则: {}", alert.getRule());
                    return false;
            }
        } catch (Exception e) {
            log.error("检查告警规则失败", e);
            return false;
        }
    }

    private boolean evaluateCustomRule(String ruleContent, TaskExecution execution) {
        try {
            // 解析自定义规则
            Map<String, Object> rule = JSON.parseObject(ruleContent, Map.class);
            String type = (String) rule.get("type");
            Object value = rule.get("value");

            switch (type) {
                case "EXIT_CODE":
                    return execution.getExitCode() != null && execution.getExitCode().equals(value);
                case "DURATION":
                    return execution.getDuration() != null && execution.getDuration() > (Long) value;
                case "ERROR_MESSAGE":
                    return execution.getErrorMessage() != null && 
                            execution.getErrorMessage().contains((String) value);
                case "RESULT":
                    return execution.getResult() != null && 
                            execution.getResult().contains((String) value);
                default:
                    log.warn("不支持的自定义规则类型: {}", type);
                    return false;
            }
        } catch (Exception e) {
            log.error("评估自定义规则失败", e);
            return false;
        }
    }

    @Override
    public void sendSuccessAlert(Task task, TaskExecution execution) {
        if (task.getAlertType() == null || task.getAlertReceiver() == null) {
            return;
        }

        String subject = String.format("任务执行成功通知 - %s", task.getName());
        String content = String.format("任务名称: %s\n执行时间: %s\n执行时长: %d秒\n执行结果: %s",
                task.getName(),
                execution.getStartTime(),
                execution.getDuration() / 1000,
                execution.getResult());

        sendAlert(task.getAlertType(), task.getAlertReceiver(), subject, content);
    }

    @Override
    public void sendFailureAlert(Task task, TaskExecution execution) {
        if (task.getAlertType() == null || task.getAlertReceiver() == null) {
            return;
        }

        String subject = String.format("任务执行失败通知 - %s", task.getName());
        String content = String.format("任务名称: %s\n执行时间: %s\n执行时长: %d秒\n错误信息: %s",
                task.getName(),
                execution.getStartTime(),
                execution.getDuration() / 1000,
                execution.getErrorMessage());

        sendAlert(task.getAlertType(), task.getAlertReceiver(), subject, content);
    }

    @Override
    public void sendTimeoutAlert(Task task, TaskExecution execution) {
        if (task.getAlertType() == null || task.getAlertReceiver() == null) {
            return;
        }

        String subject = String.format("任务执行超时通知 - %s", task.getName());
        String content = String.format("任务名称: %s\n执行时间: %s\n超时时间: %d秒\n执行结果: %s",
                task.getName(),
                execution.getStartTime(),
                task.getTimeout(),
                execution.getResult());

        sendAlert(task.getAlertType(), task.getAlertReceiver(), subject, content);
    }

    private void sendAlert(String alertType, String receiver, String subject, String content) {
        try {
            switch (alertType) {
                case "email":
                    sendEmailAlert(receiver, subject, content);
                    break;
                case "webhook":
                    sendWebhookAlert(receiver, subject, content);
                    break;
                case "dingtalk":
                    sendDingTalkAlert(receiver, subject, content);
                    break;
                default:
                    log.warn("不支持的告警类型: {}", alertType);
            }
        } catch (Exception e) {
            log.error("发送告警失败", e);
        }
    }

    private void sendEmailAlert(String receiver, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(receiver);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    private void sendWebhookAlert(String receiver, String subject, String content) {
        try {
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 构建请求体
            Map<String, Object> body = new HashMap<>();
            body.put("subject", subject);
            body.put("content", content);
            body.put("timestamp", System.currentTimeMillis());

            // 发送请求
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(receiver, request, String.class);
            
            log.info("Webhook告警发送成功: {}", receiver);
        } catch (Exception e) {
            log.error("Webhook告警发送失败: {}", receiver, e);
            throw new RuntimeException("Webhook告警发送失败", e);
        }
    }

    private void sendDingTalkAlert(String receiver, String subject, String content) {
        try {
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 构建钉钉消息
            Map<String, Object> message = new HashMap<>();
            message.put("msgtype", "text");

            Map<String, String> text = new HashMap<>();
            text.put("content", subject + "\n" + content);
            message.put("text", text);

            // 发送请求
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(message, headers);
            String url = "https://oapi.dingtalk.com/robot/send?access_token=" + receiver;
            restTemplate.postForEntity(url, request, String.class);
            
            log.info("钉钉告警发送成功: {}", receiver);
        } catch (Exception e) {
            log.error("钉钉告警发送失败: {}", receiver, e);
            throw new RuntimeException("钉钉告警发送失败", e);
        }
    }

    private void sendSmsAlert(String receiver, String content) {
        try {
            // 构建短信请求
            Map<String, String> params = new HashMap<>();
            params.put("PhoneNumbers", receiver);
            params.put("SignName", smsConfig.getSignName());
            params.put("TemplateCode", smsConfig.getTemplateCode());
            params.put("TemplateParam", JSON.toJSONString(Collections.singletonMap("content", content)));

            // 发送短信
            String response = sendSmsRequest(params);
            log.info("短信发送成功: {}, 响应: {}", receiver, response);
        } catch (Exception e) {
            log.error("短信发送失败: {}", receiver, e);
            throw new RuntimeException("短信发送失败", e);
        }
    }

    private void sendWeChatAlert(String receiver, String subject, String content) {
        try {
            // 构建企业微信消息
            Map<String, Object> message = new HashMap<>();
            message.put("msgtype", "text");

            Map<String, String> text = new HashMap<>();
            text.put("content", subject + "\n" + content);
            message.put("text", text);

            // 发送请求
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(message, headers);
            
            String url = wechatConfig.getCorpId() + "&key=" + receiver;
            restTemplate.postForEntity(url, request, String.class);
            
            log.info("企业微信告警发送成功: {}", receiver);
        } catch (Exception e) {
            log.error("企业微信告警发送失败: {}", receiver, e);
            throw new RuntimeException("企业微信告警发送失败", e);
        }
    }

    private String sendSmsRequest(Map<String, String> params) {
        // 构建请求参数
        params.put("AccessKeyId", smsConfig.getAccessKey());
        params.put("Timestamp", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .format(new Date()));
        params.put("SignatureMethod", "HMAC-SHA1");
        params.put("SignatureVersion", "1.0");
        params.put("SignatureNonce", UUID.randomUUID().toString());
        params.put("Version", "2017-05-25");
        params.put("Action", "SendSms");
        params.put("Format", "JSON");

        // 计算签名
        String signature = calculateSignature(params);
        params.put("Signature", signature);

        // 发送请求
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        params.forEach(map::add);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://dysmsapi.aliyuncs.com/", request, String.class);
        
        return response.getBody();
    }

    private String calculateSignature(Map<String, String> params) {
        try {
            // 按字典序排序参数
            String[] sortedKeys = params.keySet().toArray(new String[0]);
            Arrays.sort(sortedKeys);
            
            // 拼接参数
            StringBuilder sb = new StringBuilder();
            for (String key : sortedKeys) {
                if (sb.length() > 0) {
                    sb.append("&");
                }
                sb.append(key).append("=").append(params.get(key));
            }
            
            // 计算签名
            String stringToSign = sb.toString();
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(smsConfig.getSecretKey().getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signData);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("计算签名失败", e);
            throw new BaseException("计算签名失败: " + e.getMessage());
        }
    }

    private String percentEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8")
                    .replace("+", "%20")
                    .replace("*", "%2A")
                    .replace("%7E", "~");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("URL编码失败", e);
        }
    }
} 