package com.scheduler.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务执行记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskExecution extends BaseEntity {
    /**
     * 执行记录ID
     */
    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 执行类型（1：Shell命令 2：JAR包）
     */
    private String type;

    /**
     * 执行状态（0：等待中 1：执行中 2：执行成功 3：执行失败）
     */
    private Integer status;

    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 执行时长（毫秒）
     */
    private Long duration;

    /**
     * 执行结果
     */
    private String result;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 执行节点
     */
    private String nodeId;

    /**
     * 执行IP
     */
    private String ip;

    /**
     * 退出码
     */
    private Integer exitCode;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    private Integer delFlag;
} 