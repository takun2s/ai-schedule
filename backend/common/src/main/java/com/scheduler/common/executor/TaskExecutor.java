package com.scheduler.common.executor;

import com.scheduler.common.model.Task;
import com.scheduler.common.model.TaskExecution;

/**
 * 任务执行器接口
 */
public interface TaskExecutor {
    
    /**
     * 执行任务
     */
    void execute(Task task, TaskExecution execution);
    
    /**
     * 获取执行器类型
     */
    Integer getType();
    
    /**
     * 获取执行器名称
     */
    String getName();
    
    /**
     * 获取执行器描述
     */
    String getDescription();
} 