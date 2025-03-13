package com.scheduler.service.executor;

import com.scheduler.model.*;
import com.scheduler.repository.DagExecutionRepository;
import com.scheduler.repository.DagRepository;
import com.scheduler.repository.DagTaskRepository;
import com.scheduler.repository.TaskExecutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DagExecutor {

    private final DagRepository dagRepository;
    private final DagTaskRepository dagTaskRepository;
    private final DagExecutionRepository dagExecutionRepository ;
    private final TaskExecutionRepository taskExecutionRepository;  // 添加这行
    private final TaskExecutorFactory taskExecutorFactory;  // 改用 factory
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    
    // 存储正在运行的DAG实例
    private final Map<Long, DagExecutionContext> runningDags = new ConcurrentHashMap<>();

    @Transactional
    public void execute(Dag dag, List<DagTask> tasks) {
        try {
            // 先创建并保存 DAG 执行记录
            DagExecution dagExecution = new DagExecution();
            dagExecution.setDag(dag);
            dagExecution.setDagName(dag.getName());
            dagExecution.setStatus(TaskExecution.STATUS_RUNNING);
            dagExecution.setStartTime(LocalDateTime.now());
            
            // 先保存并刷新 DagExecution
            dagExecution = dagExecutionRepository.saveAndFlush(dagExecution);
            dagRepository.flush();  // 确保 dag 记录已保存
            
            final DagExecution finalDagExecution = dagExecution;
            final Long dagExecutionId = dagExecution.getId();
            
            log.info("Creating task executions for DAG execution ID: {}", dagExecutionId);
            
            // 在同一个事务中创建所有任务执行记录
            List<TaskExecution> taskExecutions = tasks.stream().map(dagTask -> {
                TaskExecution execution = new TaskExecution();
                execution.setTask(dagTask.getTask());
                execution.setDagTask(dagTask);
                execution.setDagExecutionId(dagExecutionId);
                execution.setNodeId(dagTask.getNodeId());
                execution.setStatus(TaskExecution.STATUS_PENDING);
                execution.setStartTime(LocalDateTime.now());
                return execution;
            }).collect(Collectors.toList());
            
            // 批量保存所有任务执行记录
            taskExecutionRepository.saveAll(taskExecutions);
            taskExecutionRepository.flush();
            
            log.info("Successfully created {} task executions", taskExecutions.size());
            
            // 创建执行上下文并开始执行
            DagExecutionContext context = new DagExecutionContext(dag, finalDagExecution, tasks);
            runningDags.put(dag.getId(), context);
            
            // 提交异步执行
            executorService.submit(() -> executeDagTasks(context));
            
        } catch (Exception e) {
            log.error("Error executing DAG: {}", dag.getId(), e);
            throw new RuntimeException("Failed to execute DAG: " + e.getMessage(), e);
        }
    }

    public void stop(Long dagId) {
        DagExecutionContext context = runningDags.get(dagId);
        if (context != null) {
            context.stop();
            runningDags.remove(dagId);
        }
    }

    private void executeDagTasks(DagExecutionContext context) {
        // 获取没有依赖的任务
        List<DagTask> readyTasks = context.getTasks().stream()
            .filter(task -> task.getDependencies().isEmpty())
            .collect(Collectors.toList());
            
        // 执行准备好的任务
        readyTasks.forEach(task -> executeTask(context, task));
    }

    private void executeTask(DagExecutionContext context, DagTask dagTask) {
        try {
            Task actualTask = dagTask.getTask();
            if (actualTask == null) {
                throw new RuntimeException("Task not found in DagTask: " + dagTask.getId());
            }

            // 查找已存在的执行记录
            TaskExecution execution = taskExecutionRepository.findByDagExecutionIdAndDagTask(
                context.getDagExecution().getId(), 
                dagTask
            );

            if (execution == null) {
                log.error("Task execution record not found for dagTask: {}", dagTask.getId());
                return;
            }

            // 更新执行状态
            execution.setStatus(TaskExecution.STATUS_RUNNING);
            execution = taskExecutionRepository.save(execution);

            TaskExecution finalExecution = execution;
            TaskCallback successCallback = new TaskCallback() {
                @Override
                public void onComplete(Task task, String output) {
                    finalExecution.setStatus(TaskExecution.STATUS_COMPLETED);
                    finalExecution.setEndTime(LocalDateTime.now());
                    finalExecution.setOutput(output);
                    taskExecutionRepository.save(finalExecution);
                    context.getCompletedTasks().add(dagTask.getId());
                    executeDownstreamTasks(context, dagTask);
                }
            };

            TaskErrorCallback errorCallback = new TaskErrorCallback() {
                @Override
                public void onError(Task task, Exception e) {
                    finalExecution.setStatus(TaskExecution.STATUS_FAILED);
                    finalExecution.setEndTime(LocalDateTime.now());
                    finalExecution.setError(e.getMessage());
                    taskExecutionRepository.save(finalExecution);
                    handleTaskFailure(context, dagTask);
                }
            };

            // 使用 factory 获取对应的执行器
            TaskExecutor executor = taskExecutorFactory.getExecutor(actualTask);
            executor.execute(actualTask, successCallback, errorCallback);
            
        } catch (Exception e) {
            log.error("Failed to execute task: " + dagTask.getId(), e);
            handleTaskFailure(context, dagTask);
        }
    }

    private void handleTaskFailure(DagExecutionContext context, DagTask task) {
        // 如果配置了重试
        if (task.getIsRetry() && task.getRetryCount() < task.getMaxRetryCount()) {
            task.setRetryCount(task.getRetryCount() + 1);
            executeTask(context, task);
        } else {
            runningDags.remove(context.getDag().getId());
        }
    }

    private void executeDownstreamTasks(DagExecutionContext context, DagTask currentTask) {
        if (currentTask.getDownstreamTasks() == null || currentTask.getDownstreamTasks().isEmpty()) {
            return;
        }
        
        for (DagTask downstreamTask : currentTask.getDownstreamTasks()) {
            // 检查所有上游任务是否完成
            if (areAllUpstreamTasksCompleted(downstreamTask, context)) {
                executeTask(context, downstreamTask);
            }
        }
    }
    
    private boolean areAllUpstreamTasksCompleted(DagTask task, DagExecutionContext context) {
        if (task.getUpstreamTasks() == null || task.getUpstreamTasks().isEmpty()) {
            return true;
        }
        
        return task.getUpstreamTasks().stream()
                .allMatch(upstreamTask -> context.getCompletedTasks().contains(upstreamTask.getId()));
    }

    @lombok.Data
    private static class DagExecutionContext {
        private final Dag dag;
        private final DagExecution dagExecution;  // 添加 DagExecution
        private final List<DagTask> tasks;
        private final Map<String, DagTask> taskMap;
        private final Set<Long> completedTasks;
        private volatile boolean stopped = false;

        public DagExecutionContext(Dag dag, DagExecution dagExecution, List<DagTask> tasks) {
            this.dag = dag;
            this.dagExecution = dagExecution;
            this.tasks = tasks;
            this.taskMap = tasks.stream().collect(
                Collectors.toMap(DagTask::getNodeId, t -> t)
            );
            this.completedTasks = Collections.synchronizedSet(new HashSet<>());
        }

        public Set<Long> getCompletedTasks() {
            return completedTasks;
        }

        public void stop() {
            this.stopped = true;
        }
    }
}
