package com.scheduler.service.impl;

import com.scheduler.model.Dashboard;
import com.scheduler.model.Task;
import com.scheduler.model.TaskExecution;
import com.scheduler.repository.DashboardRepository;
import com.scheduler.repository.TaskRepository;
import com.scheduler.repository.TaskExecutionRepository;
import com.scheduler.repository.DagRepository;
import com.scheduler.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final TaskRepository taskRepository;
    private final DashboardRepository dashboardRepository;
    private final TaskExecutionRepository taskExecutionRepository;
    private final DagRepository dagRepository;  // 添加 DagRepository 依赖

    @Override
    @Transactional
    public Dashboard getStats() {
        Dashboard stats = new Dashboard();
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        
        // 使用任务执行记录统计
        stats.setTotalTasks(taskRepository.count());
        stats.setRunningTasks(taskExecutionRepository.countByStatus(TaskExecution.STATUS_RUNNING));
        stats.setFailedTasks(taskExecutionRepository.countByStatus(TaskExecution.STATUS_FAILED));
        
        // 使用DAG执行记录统计
        stats.setTotalDags(dagRepository.count());
        stats.setRunningDags(taskExecutionRepository.countRunningDagTasks());
        stats.setFailedDags(taskExecutionRepository.countFailedDagTasks());
        
        // 告警统计 - 传入时间参数
        stats.setAlertCount24h(dashboardRepository.countAlertLogsLast24Hours(yesterday));
        stats.setLastUpdateTime(LocalDateTime.now());
        
        return dashboardRepository.save(stats);
    }

    @Override
    public Dashboard getLatestStats() {
        return dashboardRepository.findFirstByOrderByLastUpdateTimeDesc();
    }

    @Override
    public Map<String, Object> getTaskStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", taskRepository.count());
        stats.put("running", taskExecutionRepository.countByStatus(TaskExecution.STATUS_RUNNING));
        stats.put("completed", taskExecutionRepository.countByStatus(TaskExecution.STATUS_COMPLETED));
        stats.put("failed", taskExecutionRepository.countByStatus(TaskExecution.STATUS_FAILED));
        stats.put("pending", taskExecutionRepository.countByStatus(TaskExecution.STATUS_PENDING));
        return stats;
    }

    @Override
    public Map<String, Object> getDagStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", dagRepository.count());
        stats.put("running", taskExecutionRepository.countRunningDagTasks());
        stats.put("completed", taskExecutionRepository.countCompletedDagTasks());
        stats.put("failed", taskExecutionRepository.countFailedDagTasks());
        stats.put("pending", taskExecutionRepository.countPendingDagTasks());
        return stats;
    }
}
