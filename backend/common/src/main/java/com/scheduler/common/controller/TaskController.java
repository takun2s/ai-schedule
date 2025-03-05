package com.scheduler.common.controller;

import com.scheduler.common.api.Result;
import com.scheduler.common.model.Task;
import com.scheduler.common.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 任务控制器
 */
@Slf4j
@Tag(name = "任务管理")
@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Operation(summary = "获取任务列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('task:list')")
    public Result<List<Task>> list(Task task) {
        return Result.success(taskService.getList(task));
    }

    @Operation(summary = "获取任务详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('task:query')")
    public Result<Task> getInfo(@PathVariable Long id) {
        return Result.success(taskService.getById(id));
    }

    @Operation(summary = "新增任务")
    @PostMapping
    @PreAuthorize("hasAuthority('task:add')")
    public Result<Void> add(@RequestBody Task task) {
        taskService.add(task);
        return Result.success();
    }

    @Operation(summary = "修改任务")
    @PutMapping
    @PreAuthorize("hasAuthority('task:edit')")
    public Result<Void> edit(@RequestBody Task task) {
        taskService.update(task);
        return Result.success();
    }

    @Operation(summary = "删除任务")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('task:delete')")
    public Result<Void> remove(@PathVariable Long id) {
        taskService.delete(id);
        return Result.success();
    }

    @Operation(summary = "手动执行任务")
    @PostMapping("/{id}/execute")
    @PreAuthorize("hasAuthority('task:execute')")
    public Result<Void> execute(@PathVariable Long id) {
        taskService.execute(id);
        return Result.success();
    }

    @Operation(summary = "停止任务")
    @PostMapping("/{id}/stop")
    @PreAuthorize("hasAuthority('task:edit')")
    public Result<Void> stop(@PathVariable Long id) {
        taskService.stop(id);
        return Result.success();
    }

    @Operation(summary = "恢复任务")
    @PostMapping("/{id}/resume")
    @PreAuthorize("hasAuthority('task:edit')")
    public Result<Void> resume(@PathVariable Long id) {
        taskService.resume(id);
        return Result.success();
    }
} 