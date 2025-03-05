package com.scheduler.common.service;

import com.scheduler.common.model.Task;
import com.scheduler.common.model.TaskExecution;
import com.scheduler.common.model.TaskAlert;
import java.util.List;

/**
 * 任务告警服务接口
 */
public interface TaskAlertService {
    
    /**
     * 根据ID查询告警记录
     */
    TaskAlert getById(Long id);
    
    /**
     * 查询告警记录列表
     */
    List<TaskAlert> getList(TaskAlert alert);
    
    /**
     * 新增告警记录
     */
    void add(TaskAlert alert);
    
    /**
     * 修改告警记录
     */
    void update(TaskAlert alert);
    
    /**
     * 删除告警记录
     */
    void delete(Long id);
    
    /**
     * 修改告警状态
     */
    void updateStatus(Long id, Integer status);
    
    /**
     * 查询未发送的告警记录
     */
    List<TaskAlert> getUnsentList();
    
    /**
     * 查询任务告警记录数量
     */
    int getCountByTaskId(Long taskId);
    
    /**
     * 发送告警
     */
    void sendAlert(TaskAlert alert);
    
    /**
     * 重试发送告警
     */
    void retrySend(Long alertId);
    
    /**
     * 检查任务是否需要告警
     */
    void checkTaskAlert(Long taskId, Long executionId);
    
    /**
     * 发送任务执行成功告警
     */
    void sendSuccessAlert(Task task, TaskExecution execution);
    
    /**
     * 发送任务执行失败告警
     */
    void sendFailureAlert(Task task, TaskExecution execution);
    
    /**
     * 发送任务执行超时告警
     */
    void sendTimeoutAlert(Task task, TaskExecution execution);
} 