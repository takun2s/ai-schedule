package com.scheduler.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务告警记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskAlert extends BaseEntity {
    /**
     * 告警记录ID
     */
    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 执行记录ID
     */
    private Long executionId;

    /**
     * 告警类型（1：邮件 2：短信 3：Webhook）
     */
    private String type;

    /**
     * 告警接收人
     */
    private String target;

    /**
     * 告警主题
     */
    private String subject;

    /**
     * 告警内容
     */
    private String content;

    /**
     * 告警规则
     */
    private String rule;

    /**
     * 告警规则内容
     */
    private String ruleContent;

    /**
     * 告警状态（0：未发送 1：发送成功 2：发送失败）
     */
    private Integer status;

    /**
     * 发送时间
     */
    private Long sendTime;

    /**
     * 发送结果
     */
    private String sendResult;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    private Integer delFlag;
} 