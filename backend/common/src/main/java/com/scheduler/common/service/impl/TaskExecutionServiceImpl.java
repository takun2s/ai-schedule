package com.scheduler.common.service.impl;

import com.scheduler.common.exception.BaseException;
import com.scheduler.common.mapper.TaskExecutionMapper;
import com.scheduler.common.mapper.TaskMapper;
import com.scheduler.common.model.Task;
import com.scheduler.common.model.TaskExecution;
import com.scheduler.common.service.TaskExecutionService;
import com.scheduler.common.service.TaskAlertService;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 任务执行服务实现类
 */
@Slf4j
@Service
public class TaskExecutionServiceImpl implements TaskExecutionService {

    @Value("${zookeeper.connect-string}")
    private String connectString;

    @Value("${zookeeper.session-timeout}")
    private int sessionTimeout;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskExecutionMapper taskExecutionMapper;

    @Autowired
    private TaskAlertService taskAlertService;

    private ZooKeeper zooKeeper;
    private final CountDownLatch latch = new CountDownLatch(1);

    @PostConstruct
    public void init() {
        try {
            zooKeeper = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        latch.countDown();
                    }
                }
            });
            latch.await();
            start();
        } catch (Exception e) {
            log.error("初始化ZooKeeper失败", e);
            throw new BaseException("初始化ZooKeeper失败");
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            stop();
            if (zooKeeper != null) {
                zooKeeper.close();
            }
        } catch (Exception e) {
            log.error("关闭ZooKeeper失败", e);
        }
    }

    public void start() {
        try {
            // 创建执行节点
            String executionPath = "/task-scheduler/executions";
            Stat stat = zooKeeper.exists(executionPath, false);
            if (stat == null) {
                zooKeeper.create(executionPath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }

            // 监听执行节点
            watchExecutions();
        } catch (Exception e) {
            log.error("启动执行器失败", e);
            throw new BaseException("启动执行器失败");
        }
    }

    public void stop() {
        // 停止监听
    }

    @Override
    public void executeTask(Task task) {
        try {
            // 创建任务执行记录
            TaskExecution execution = new TaskExecution();
            execution.setTaskId(task.getId());
            execution.setType(String.valueOf(task.getType()));
            execution.setStatus(0); // 初始状态
            execution.setStartTime(System.currentTimeMillis());
            
            // 执行任务
            boolean success = executeTaskInternal(task, execution);
            
            // 更新执行状态
            execution.setStatus(success ? 1 : 2); // 1: 成功, 2: 失败
            execution.setEndTime(System.currentTimeMillis());
            execution.setDuration(execution.getEndTime() - execution.getStartTime());
            
            // 发送告警
            if (success) {
                taskAlertService.sendSuccessAlert(task, execution);
            } else {
                taskAlertService.sendFailureAlert(task, execution);
            }
            
        } catch (Exception e) {
            log.error("执行任务失败", e);
            throw new RuntimeException("执行任务失败", e);
        }
    }

    @Override
    public void retry(Long executionId) {
        try {
            // 获取执行记录
            TaskExecution execution = taskExecutionMapper.selectById(executionId);
            if (execution == null) {
                throw new BaseException("执行记录不存在");
            }

            // 获取任务信息
            Task task = taskMapper.selectById(execution.getTaskId());
            if (task == null) {
                throw new BaseException("任务不存在");
            }

            // 更新执行状态
            execution.setStatus(0); // 待执行
            taskExecutionMapper.updateStatus(executionId, 0);

            // 执行任务
            executeTaskInternal(task, execution);
        } catch (Exception e) {
            log.error("重试执行失败", e);
            throw new BaseException("重试执行失败");
        }
    }

    @Override
    public void terminate(Long executionId) {
        try {
            // 更新执行状态
            taskExecutionMapper.updateStatus(executionId, 3); // 已取消
        } catch (Exception e) {
            log.error("终止任务失败", e);
            throw new BaseException("终止任务失败");
        }
    }

    @Override
    public TaskExecution getById(Long id) {
        return taskExecutionMapper.selectById(id);
    }

    @Override
    public List<TaskExecution> getList(TaskExecution execution) {
        return taskExecutionMapper.selectList(execution);
    }

    @Override
    public void add(TaskExecution execution) {
        taskExecutionMapper.insert(execution);
    }

    @Override
    public void update(TaskExecution execution) {
        taskExecutionMapper.update(execution);
    }

    @Override
    public void delete(Long id) {
        taskExecutionMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        taskExecutionMapper.updateStatus(id, status);
    }

    @Override
    public TaskExecution getLastByTaskId(Long taskId) {
        return taskExecutionMapper.selectLastByTaskId(taskId);
    }

    @Override
    public int getCountByTaskId(Long taskId) {
        return taskExecutionMapper.selectCountByTaskId(taskId);
    }

    @Override
    public void execute(Long taskId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BaseException("任务不存在");
        }
        executeTaskInternal(task, null);
    }

    private void watchExecutions() throws KeeperException, InterruptedException {
        String executionPath = "/task-scheduler/executions";
        List<String> children = zooKeeper.getChildren(executionPath, event -> {
            if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                try {
                    watchExecutions();
                } catch (Exception e) {
                    log.error("监听执行节点失败", e);
                }
            }
        });

        for (String child : children) {
            String childPath = executionPath + "/" + child;
            byte[] data = zooKeeper.getData(childPath, event -> {
                if (event.getType() == Watcher.Event.EventType.NodeDataChanged) {
                    try {
                        watchExecution(childPath);
                    } catch (Exception e) {
                        log.error("监听执行节点数据失败", e);
                    }
                }
            }, null);

            if (data != null) {
                watchExecution(childPath);
            }
        }
    }

    private void watchExecution(String path) throws KeeperException, InterruptedException {
        byte[] data = zooKeeper.getData(path, event -> {
            if (event.getType() == Watcher.Event.EventType.NodeDataChanged) {
                try {
                    watchExecution(path);
                } catch (Exception e) {
                    log.error("监听执行节点数据失败", e);
                }
            }
        }, null);

        if (data != null) {
            // 处理执行节点数据
            String executionId = path.substring(path.lastIndexOf("/") + 1);
            TaskExecution execution = taskExecutionMapper.selectById(Long.parseLong(executionId));
            if (execution != null) {
                // 更新执行状态
                execution.setStatus(3); // 已取消
                execution.setEndTime(System.currentTimeMillis());
                execution.setDuration(execution.getEndTime() - execution.getStartTime());
                execution.setErrorMessage("任务被手动终止");
                taskExecutionMapper.update(execution);
            }
        }
    }

    private boolean executeTaskInternal(Task task, TaskExecution execution) {
        try {
            // 根据任务类型执行不同的任务
            switch (task.getType()) {
                case 1: // Shell任务
                    return executeShellTask(task, execution);
                case 2: // Jar任务
                    return executeJarTask(task, execution);
                case 3: // HTTP任务
                    return executeHttpTask(task, execution);
                default:
                    throw new BaseException("不支持的任务类型");
            }
        } catch (Exception e) {
            log.error("执行任务失败", e);
            if (execution != null) {
                execution.setErrorMessage(e.getMessage());
            }
            return false;
        }
    }

    private boolean executeShellTask(Task task, TaskExecution execution) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("sh", "-c", task.getContent());
            processBuilder.directory(new File(task.getWorkingDir()));
            
            Process process = processBuilder.start();
            
            // 读取输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            
            // 等待进程完成
            int exitCode = process.waitFor();
            
            if (execution != null) {
                execution.setResult(output.toString());
                execution.setErrorMessage(exitCode != 0 ? "任务执行失败，退出码：" + exitCode : null);
            }
            
            return exitCode == 0;
        } catch (Exception e) {
            log.error("执行Shell任务失败", e);
            if (execution != null) {
                execution.setErrorMessage(e.getMessage());
            }
            return false;
        }
    }

    private boolean executeJarTask(Task task, TaskExecution execution) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("java", "-jar", task.getContent());
            processBuilder.directory(new File(task.getWorkingDir()));
            
            Process process = processBuilder.start();
            
            // 读取输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            
            // 等待进程完成
            int exitCode = process.waitFor();
            
            if (execution != null) {
                execution.setResult(output.toString());
                execution.setErrorMessage(exitCode != 0 ? "任务执行失败，退出码：" + exitCode : null);
            }
            
            return exitCode == 0;
        } catch (Exception e) {
            log.error("执行Jar任务失败", e);
            if (execution != null) {
                execution.setErrorMessage(e.getMessage());
            }
            return false;
        }
    }

    private boolean executeHttpTask(Task task, TaskExecution execution) {
        try {
            // 这里需要实现HTTP任务的执行逻辑
            // 可以使用RestTemplate或其他HTTP客户端
            return true;
        } catch (Exception e) {
            log.error("执行HTTP任务失败", e);
            if (execution != null) {
                execution.setErrorMessage(e.getMessage());
            }
            return false;
        }
    }
} 