package com.scheduler.common.service;

import com.scheduler.common.model.Task;
import java.util.List;

/**
 * 任务服务接口
 */
public interface TaskService {
    
    /**
     * 根据ID查询任务
     */
    Task getById(Long id);
    
    /**
     * 查询任务列表
     */
    List<Task> getList(Task task);
    
    /**
     * 新增任务
     */
    void add(Task task);
    
    /**
     * 修改任务
     */
    void update(Task task);
    
    /**
     * 删除任务
     */
    void delete(Long id);
    
    /**
     * 修改任务状态
     */
    void updateStatus(Long id, Integer status);
    
    /**
     * 手动执行任务
     */
    void execute(Long id);
    
    /**
     * 停止任务
     */
    void stop(Long id);
    
    /**
     * 恢复任务
     */
    void resume(Long id);
} 