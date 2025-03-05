package com.scheduler.common.notifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 任务告警通知器工厂
 */
@Component
public class TaskNotifierFactory {

    private final Map<String, TaskNotifier> notifierMap = new ConcurrentHashMap<>();

    @Autowired
    public TaskNotifierFactory(List<TaskNotifier> notifiers) {
        for (TaskNotifier notifier : notifiers) {
            notifierMap.put(notifier.getType(), notifier);
        }
    }

    /**
     * 获取告警通知器
     */
    public TaskNotifier getNotifier(String type) {
        TaskNotifier notifier = notifierMap.get(type);
        if (notifier == null) {
            throw new IllegalArgumentException("不支持的通知类型：" + type);
        }
        return notifier;
    }

    /**
     * 获取所有告警通知器
     */
    public List<TaskNotifier> getAllNotifiers() {
        return new ArrayList<>(notifierMap.values());
    }
} 