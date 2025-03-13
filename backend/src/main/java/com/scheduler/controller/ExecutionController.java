package com.scheduler.controller;

import com.scheduler.common.Result;
import com.scheduler.model.TaskExecution;
import com.scheduler.service.ExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/executions")
public class ExecutionController {
    
    @Autowired
    private ExecutionService executionService;
    
    @GetMapping("/tasks")
    public Result getTaskExecutions() {
        List<TaskExecution> executions = executionService.getTaskExecutions();
        List<Map<String, Object>> result = new ArrayList<>();

        for (TaskExecution execution : executions) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", execution.getId());
            map.put("taskId", execution.getTask().getId());
            map.put("taskName", execution.getTaskName());  // 添加任务名称
            map.put("status", execution.getStatus());
            map.put("startTime", execution.getStartTime());
            map.put("endTime", execution.getEndTime());
            map.put("output", execution.getOutput());
            
            // 统一使用 error 字段
            String errorMsg = execution.getError();
            map.put("error", errorMsg);
            map.put("errorMessage", errorMsg);  // 为了向后兼容保留
            
            result.add(map);
        }

        return Result.success(result);
    }
    
    @GetMapping("/dags")
    public Result getDagExecutions() {
        return Result.success(executionService.getDagExecutions());
    }
    
    @GetMapping("/tasks/{id}")
    public Result getTaskExecutionDetail(@PathVariable Long id) {
        return Result.success(executionService.getTaskExecutionDetail(id));
    }
    
    @GetMapping("/dags/{id}")
    public Result getDagExecutionDetail(@PathVariable Long id) {
        return Result.success(executionService.getDagExecutionDetail(id));
    }
}
