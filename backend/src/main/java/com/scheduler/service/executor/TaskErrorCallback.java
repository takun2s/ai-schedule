package com.scheduler.service.executor;

import com.scheduler.model.Task;

@FunctionalInterface
public interface TaskErrorCallback {
    void onError(Task task, Exception e);
}