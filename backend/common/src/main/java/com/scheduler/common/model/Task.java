package com.scheduler.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Task extends BaseEntity {
    /**
     * 任务ID
     */
    private Long id;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 任务类型（1：Shell命令 2：JAR包）
     */
    private Integer type;

    /**
     * 任务内容
     */
    private String content;

    /**
     * 执行参数
     */
    private String params;

    /**
     * 执行时间表达式
     */
    private String cronExpression;

    /**
     * 任务状态（0：停用 1：启用）
     */
    private Integer status;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 重试间隔（秒）
     */
    private Integer retryInterval;

    /**
     * 超时时间（秒）
     */
    private Integer timeout;

    /**
     * 告警方式（1：邮件 2：短信 3：Webhook）
     */
    private String alertType;

    /**
     * 告警接收人
     */
    private String alertReceiver;

    /**
     * 告警阈值（失败次数）
     */
    private Integer alertThreshold;

    /**
     * 工作目录
     */
    private String workingDir;

    /**
     * 环境变量
     */
    private String env;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    private Integer delFlag;
} 