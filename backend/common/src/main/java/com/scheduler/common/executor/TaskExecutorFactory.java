package com.scheduler.common.executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 任务执行器工厂
 */
@Component
public class TaskExecutorFactory {

    private final Map<Integer, TaskExecutor> executorMap = new ConcurrentHashMap<>();

    @Autowired
    public TaskExecutorFactory(List<TaskExecutor> executors) {
        for (TaskExecutor executor : executors) {
            executorMap.put(executor.getType(), executor);
        }
    }

    /**
     * 获取任务执行器
     */
    public TaskExecutor getExecutor(Integer type) {
        TaskExecutor executor = executorMap.get(type);
        if (executor == null) {
            throw new IllegalArgumentException("不支持的任务类型：" + type);
        }
        return executor;
    }

    /**
     * 获取所有任务执行器
     */
    public List<TaskExecutor> getAllExecutors() {
        return new ArrayList<>(executorMap.values());
    }
} 