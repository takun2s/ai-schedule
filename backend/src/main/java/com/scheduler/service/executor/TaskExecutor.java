package com.scheduler.service.executor;

import com.scheduler.model.Task;
import com.scheduler.service.executor.TaskErrorCallback;

public interface TaskExecutor {
    /**
     * 执行任务
     * @param task 任务实体
     * @param onSuccess 成功回调
     * @param onError 失败回调
     */
    void execute(Task task, TaskCallback onSuccess, TaskErrorCallback onError);

    /**
     * 验证任务参数
     * @param task 任务实体
     * @throws IllegalArgumentException 如果参数无效
     */
    void validate(Task task);

    /**
     * 停止任务执行
     */
    void stop();

    /**
     * 获取执行器类型
     * @return 执行器类型
     */
    String getType();
}