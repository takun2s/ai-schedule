package com.scheduler.controller;

import com.scheduler.common.Result;
import com.scheduler.model.Dashboard;
import com.scheduler.service.DashboardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "仪表盘接口")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final DashboardService dashboardService;

    @ApiOperation("获取仪表盘统计数据")
    @GetMapping("/stats")
    public Result<Dashboard> getStats() {
        return Result.success(dashboardService.getStats());
    }

    @ApiOperation("获取最近一次统计数据")
    @GetMapping("/latest")
    public Result<Dashboard> getLatestStats() {
        return Result.success(dashboardService.getLatestStats());
    }

    @GetMapping("/task-stats")
    public Result getTaskStats() {
        return Result.success(dashboardService.getTaskStats());
    }

    @GetMapping("/dag-stats")
    public Result getDagStats() {
        return Result.success(dashboardService.getDagStats());
    }
}
