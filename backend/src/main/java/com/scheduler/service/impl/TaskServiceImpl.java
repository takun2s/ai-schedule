package com.scheduler.service.impl;

import com.scheduler.model.Task;
import com.scheduler.model.TaskExecution;
import com.scheduler.repository.TaskRepository;
import com.scheduler.repository.TaskExecutionRepository;
import com.scheduler.service.TaskService;
import com.scheduler.service.executor.TaskExecutor;
import com.scheduler.service.executor.TaskCallback;
import com.scheduler.service.executor.TaskErrorCallback;
import com.scheduler.service.executor.TaskExecutorFactory;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import lombok.RequiredArgsConstructor;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    
    private final TaskRepository taskRepository;
    private final TaskExecutionRepository taskExecutionRepository;
    private final TaskExecutorFactory taskExecutorFactory;  // 改用factory

    @Override
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Long id, Task task) {
        Task existingTask = getTaskById(id);
        // 复制新的属性值到已存在的任务
        existingTask.setName(task.getName());
        existingTask.setType(task.getType());
        existingTask.setCommand(task.getCommand());
        existingTask.setDescription(task.getDescription());
        // 保存更新后的任务
        return taskRepository.save(existingTask);
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    @Override
    public Page<Task> listTasks(String name, String type, String status, Pageable pageable) {
        Specification<Task> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (name != null) {
                predicates.add(cb.like(root.get("name"), "%" + name + "%"));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            // 移除状态过滤
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return taskRepository.findAll(spec, pageable);
    }

    @Override
    public List<Task> getScheduledTasks() {
        return taskRepository.findScheduledTasks();
    }

    @Override
    public TaskExecution getTaskExecutionById(Long id) {
        return taskExecutionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task execution not found with id: " + id));
    }

    @Override
    public TaskExecution executeTask(Long taskId) {
        Task task = getTaskById(taskId);
        TaskExecution execution = new TaskExecution();
        execution.setTask(task);
        execution.setTaskName(task.getName());  // 显式设置任务名称
        execution.setStartTime(LocalDateTime.now());
        execution.setStatus(TaskExecution.STATUS_RUNNING);
        
        // 保存执行记录
        TaskExecution savedExecution = taskExecutionRepository.save(execution);
        
        // 创建成功回调
        TaskCallback successCallback = new TaskCallback() {
            @Override
            public void onComplete(Task task, String output) {  // 添加output参数
                execution.setStatus(TaskExecution.STATUS_COMPLETED);
                execution.setEndTime(LocalDateTime.now());
                execution.setOutput(output);  // 设置输出到execution
                taskExecutionRepository.save(execution);
            }
        };
        
        // 创建失败回调
        TaskErrorCallback errorCallback = new TaskErrorCallback() {
            @Override
            public void onError(Task task, Exception e) {
                execution.setStatus(TaskExecution.STATUS_FAILED);
                execution.setEndTime(LocalDateTime.now());
                execution.setError(e.getMessage());  // 修改这里：使用 setError 而不是 setErrorMessage
                taskExecutionRepository.save(execution);
            }
        };

        try {
            // 使用factory获取对应的执行器
            TaskExecutor executor = taskExecutorFactory.getExecutor(task);
            executor.execute(task, successCallback, errorCallback);
        } catch (Exception e) {
            execution.setStatus(TaskExecution.STATUS_FAILED);
            execution.setEndTime(LocalDateTime.now());
            execution.setError(e.getMessage());  // 这里也要修改
            taskExecutionRepository.save(execution);
        }
        
        return savedExecution;
    }

    @Override
    public void stopTask(Long taskId) {
        Task task = getTaskById(taskId);
        
        // 查找并更新最新的执行记录
        List<TaskExecution> executions = taskExecutionRepository.findByTaskIdOrderByStartTimeDesc(taskId);
        if (!executions.isEmpty()) {
            TaskExecution latestExecution = executions.get(0);
            if (TaskExecution.STATUS_RUNNING.equals(latestExecution.getStatus())) {
                latestExecution.setStatus(TaskExecution.STATUS_STOPPED);
                latestExecution.setEndTime(LocalDateTime.now());
                taskExecutionRepository.save(latestExecution);
            }
        }
        // 删除对 taskExecutor.stopTask 的调用
    }

    @Override
    public List<TaskExecution> getTaskExecutions(Long taskId) {
        return taskExecutionRepository.findByTaskIdOrderByStartTimeDesc(taskId);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
}
