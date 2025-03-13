package com.scheduler.controller;

import com.scheduler.common.Result;
import com.scheduler.model.Task;
import com.scheduler.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "任务管理")
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @ApiOperation("获取任务列表")
    @GetMapping
    public Result<?> getAllTasks() {
        return Result.success(taskService.getAllTasks());
    }

    @ApiOperation("创建任务")
    @PostMapping
    public Result<?> createTask(@RequestBody Task task) {
        return Result.success(taskService.createTask(task));
    }

    @ApiOperation("更新任务")
    @PutMapping("/{id}")
    public Result<?> updateTask(@PathVariable Long id, @RequestBody Task task) {
        return Result.success(taskService.updateTask(id, task));
    }

    @ApiOperation("删除任务")
    @DeleteMapping("/{id}")
    public Result<?> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return Result.success();
    }

    @PostMapping("/{id}/execute")
    public Result executeTask(@PathVariable Long id) {
        try {
            taskService.executeTask(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }

    @PostMapping("/{id}/stop")
    public Result stopTask(@PathVariable Long id) {
        try {
            taskService.stopTask(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }
}
