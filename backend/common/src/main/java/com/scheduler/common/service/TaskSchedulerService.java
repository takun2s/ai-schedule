package com.scheduler.common.service;

import com.scheduler.common.model.Task;
import java.util.List;

/**
 * 任务调度服务接口
 */
public interface TaskSchedulerService {
    
    /**
     * 启动调度器
     */
    void start();
    
    /**
     * 停止调度器
     */
    void stop();
    
    /**
     * 添加任务到调度器
     */
    void addTask(Task task);
    
    /**
     * 从调度器移除任务
     */
    void removeTask(Long taskId);
    
    /**
     * 更新任务调度
     */
    void updateTask(Task task);
    
    /**
     * 暂停任务调度
     */
    void pauseTask(Long taskId);
    
    /**
     * 恢复任务调度
     */
    void resumeTask(Long taskId);
    
    /**
     * 立即执行任务
     */
    void executeTask(Long taskId);
    
    /**
     * 获取所有调度中的任务
     */
    List<Task> getScheduledTasks();
    
    /**
     * 获取任务下次执行时间
     */
    Long getNextExecutionTime(Long taskId);
} 