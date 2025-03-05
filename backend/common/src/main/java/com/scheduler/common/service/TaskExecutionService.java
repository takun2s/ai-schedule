package com.scheduler.common.service;

import com.scheduler.common.model.Task;
import com.scheduler.common.model.TaskExecution;
import java.util.List;

/**
 * 任务执行服务接口
 */
public interface TaskExecutionService {
    
    /**
     * 根据ID查询执行记录
     */
    TaskExecution getById(Long id);
    
    /**
     * 查询执行记录列表
     */
    List<TaskExecution> getList(TaskExecution execution);
    
    /**
     * 新增执行记录
     */
    void add(TaskExecution execution);
    
    /**
     * 修改执行记录
     */
    void update(TaskExecution execution);
    
    /**
     * 删除执行记录
     */
    void delete(Long id);
    
    /**
     * 修改执行状态
     */
    void updateStatus(Long id, Integer status);
    
    /**
     * 查询任务最近一次执行记录
     */
    TaskExecution getLastByTaskId(Long taskId);
    
    /**
     * 查询任务执行记录数量
     */
    int getCountByTaskId(Long taskId);
    
    /**
     * 执行任务
     */
    void execute(Long taskId);
    
    /**
     * 重试任务
     */
    void retry(Long executionId);
    
    /**
     * 终止任务
     */
    void terminate(Long executionId);

    /**
     * 执行任务
     */
    void executeTask(Task task);
} 