package com.scheduler.common.service.impl;

import com.scheduler.common.exception.BaseException;
import com.scheduler.common.mapper.TaskMapper;
import com.scheduler.common.model.Task;
import com.scheduler.common.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 任务服务实现类
 */
@Slf4j
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public Task getById(Long id) {
        return taskMapper.selectById(id);
    }

    @Override
    public List<Task> getList(Task task) {
        return taskMapper.selectList(task);
    }

    @Override
    public void add(Task task) {
        // 检查任务名称是否已存在
        Task existTask = new Task();
        existTask.setName(task.getName());
        List<Task> existTasks = taskMapper.selectList(existTask);
        if (!existTasks.isEmpty()) {
            throw new BaseException("任务名称已存在");
        }
        taskMapper.insert(task);
    }

    @Override
    public void update(Task task) {
        // 检查任务是否存在
        if (getById(task.getId()) == null) {
            throw new BaseException("任务不存在");
        }
        // 检查任务名称是否已存在
        Task existTask = new Task();
        existTask.setName(task.getName());
        List<Task> existTasks = taskMapper.selectList(existTask);
        if (!existTasks.isEmpty() && !existTasks.get(0).getId().equals(task.getId())) {
            throw new BaseException("任务名称已存在");
        }
        taskMapper.update(task);
    }

    @Override
    public void delete(Long id) {
        // 检查任务是否存在
        if (getById(id) == null) {
            throw new BaseException("任务不存在");
        }
        taskMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        // 检查任务是否存在
        if (getById(id) == null) {
            throw new BaseException("任务不存在");
        }
        taskMapper.updateStatus(id, status);
    }

    @Override
    public void execute(Long id) {
        // 检查任务是否存在
        Task task = getById(id);
        if (task == null) {
            throw new BaseException("任务不存在");
        }
        // TODO: 发送任务执行消息到消息队列
    }

    @Override
    public void stop(Long id) {
        updateStatus(id, 0);
    }

    @Override
    public void resume(Long id) {
        updateStatus(id, 1);
    }
} 