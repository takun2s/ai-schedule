package com.scheduler.service;

import com.scheduler.model.Task;
import com.scheduler.model.TaskExecution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface TaskService {
    Task createTask(Task task);
    Task updateTask(Task task);
    Task updateTask(Long id, Task task);  // 添加新的方法签名
    void deleteTask(Long id);
    Task getTaskById(Long id);
    Page<Task> listTasks(String name, String type, String status, Pageable pageable);
    List<Task> getScheduledTasks();
    TaskExecution getTaskExecutionById(Long id);
    TaskExecution executeTask(Long taskId);
    List<TaskExecution> getTaskExecutions(Long taskId);
    List<Task> getAllTasks();  // 添加新方法
    void stopTask(Long taskId);  // 添加停止任务的方法
}
