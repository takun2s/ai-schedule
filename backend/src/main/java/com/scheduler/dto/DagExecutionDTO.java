package com.scheduler.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DagExecutionDTO {
    private Long id;
    private Long dagId;
    private String dagName;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long duration;
    private String error;
    private LocalDateTime createdAt;
    
    private int totalTasks;
    private int completedTasks;
    private int pendingTasks;
    private int runningTasks;
    private int failedTasks;
    private double progress;  // 完成百分比
    private List<TaskSummary> taskSummaries;

    @Data
    public static class TaskSummary {
        private String nodeId;
        private String taskName;
        private String status;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
    }
}
