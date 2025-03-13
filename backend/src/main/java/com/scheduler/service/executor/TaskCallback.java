package com.scheduler.service.executor;

import com.scheduler.model.Task;

@FunctionalInterface
public interface TaskCallback {
    void onComplete(Task task, String output);  // 添加output参数
}
