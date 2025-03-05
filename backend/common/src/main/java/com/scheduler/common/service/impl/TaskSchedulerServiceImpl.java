package com.scheduler.common.service.impl;

import com.scheduler.common.exception.BaseException;
import com.scheduler.common.mapper.TaskMapper;
import com.scheduler.common.model.Task;
import com.scheduler.common.service.TaskSchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * 任务调度服务实现类
 */
@Slf4j
@Service
public class TaskSchedulerServiceImpl implements TaskSchedulerService {

    @Value("${zookeeper.connect-string}")
    private String connectString;

    @Value("${zookeeper.session-timeout}")
    private int sessionTimeout;

    @Autowired
    private TaskMapper taskMapper;

    private ZooKeeper zooKeeper;
    private final ConcurrentHashMap<Long, Task> scheduledTasks = new ConcurrentHashMap<>();
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

    @Override
    public void start() {
        try {
            // 创建根节点
            String rootPath = "/task-scheduler";
            Stat stat = zooKeeper.exists(rootPath, false);
            if (stat == null) {
                zooKeeper.create(rootPath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }

            // 创建节点
            String nodePath = rootPath + "/" + getNodeId();
            zooKeeper.create(nodePath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

            // 加载任务
            List<Task> tasks = taskMapper.selectEnabledList();
            for (Task task : tasks) {
                addTask(task);
            }
        } catch (Exception e) {
            log.error("启动调度器失败", e);
            throw new BaseException("启动调度器失败");
        }
    }

    @Override
    public void stop() {
        try {
            // 删除节点
            String nodePath = "/task-scheduler/" + getNodeId();
            zooKeeper.delete(nodePath, -1);

            // 清空任务
            scheduledTasks.clear();
        } catch (Exception e) {
            log.error("停止调度器失败", e);
            throw new BaseException("停止调度器失败");
        }
    }

    @Override
    public void addTask(Task task) {
        try {
            // 创建任务节点
            String taskPath = "/task-scheduler/tasks/" + task.getId();
            Stat stat = zooKeeper.exists(taskPath, false);
            if (stat == null) {
                zooKeeper.create(taskPath, task.getCronExpression().getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }

            // 添加到本地缓存
            scheduledTasks.put(task.getId(), task);
        } catch (Exception e) {
            log.error("添加任务失败", e);
            throw new BaseException("添加任务失败");
        }
    }

    @Override
    public void removeTask(Long taskId) {
        try {
            // 删除任务节点
            String taskPath = "/task-scheduler/tasks/" + taskId;
            zooKeeper.delete(taskPath, -1);

            // 从本地缓存移除
            scheduledTasks.remove(taskId);
        } catch (Exception e) {
            log.error("移除任务失败", e);
            throw new BaseException("移除任务失败");
        }
    }

    @Override
    public void updateTask(Task task) {
        try {
            // 更新任务节点
            String taskPath = "/task-scheduler/tasks/" + task.getId();
            zooKeeper.setData(taskPath, task.getCronExpression().getBytes(), -1);

            // 更新本地缓存
            scheduledTasks.put(task.getId(), task);
        } catch (Exception e) {
            log.error("更新任务失败", e);
            throw new BaseException("更新任务失败");
        }
    }

    @Override
    public void pauseTask(Long taskId) {
        try {
            // 更新任务状态
            Task task = scheduledTasks.get(taskId);
            if (task != null) {
                task.setStatus(0);
                taskMapper.updateStatus(taskId, 0);
            }
        } catch (Exception e) {
            log.error("暂停任务失败", e);
            throw new BaseException("暂停任务失败");
        }
    }

    @Override
    public void resumeTask(Long taskId) {
        try {
            // 更新任务状态
            Task task = scheduledTasks.get(taskId);
            if (task != null) {
                task.setStatus(1);
                taskMapper.updateStatus(taskId, 1);
            }
        } catch (Exception e) {
            log.error("恢复任务失败", e);
            throw new BaseException("恢复任务失败");
        }
    }

    @Override
    public void executeTask(Long taskId) {
        try {
            // 发送任务执行消息
            String message = String.format("{\"taskId\":%d,\"type\":\"manual\"}", taskId);
            zooKeeper.create("/task-scheduler/executions/" + taskId + "_" + System.currentTimeMillis(),
                    message.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
        } catch (Exception e) {
            log.error("执行任务失败", e);
            throw new BaseException("执行任务失败");
        }
    }

    @Override
    public List<Task> getScheduledTasks() {
        return new ArrayList<>(scheduledTasks.values());
    }

    @Override
    public Long getNextExecutionTime(Long taskId) {
        // TODO: 实现获取下次执行时间的逻辑
        return null;
    }

    private String getNodeId() {
        return System.getProperty("user.name") + "_" + System.currentTimeMillis();
    }
} 