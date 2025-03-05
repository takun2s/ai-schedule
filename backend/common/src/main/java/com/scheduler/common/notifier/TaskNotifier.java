package com.scheduler.common.notifier;

import com.scheduler.common.model.TaskAlert;

/**
 * 任务告警通知器接口
 */
public interface TaskNotifier {
    
    /**
     * 发送告警通知
     */
    void send(TaskAlert alert);
    
    /**
     * 获取通知器类型
     */
    String getType();
    
    /**
     * 获取通知器名称
     */
    String getName();
    
    /**
     * 获取通知器描述
     */
    String getDescription();
} 