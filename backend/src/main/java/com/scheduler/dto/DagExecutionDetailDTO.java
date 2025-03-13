package com.scheduler.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DagExecutionDetailDTO {
    private Long id;
    private Long dagId;
    private String dagName;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long duration;
    private String error;
    private LocalDateTime createdAt;
    private List<TaskSummary> taskSummaries;  // 替换原来的 taskExecutions
    
    // 添加统计信息
    private int totalTasks;
    private int completedTasks;
    private int pendingTasks;
    private int runningTasks;
    private int failedTasks;
    private double progress;
    
    @Data
    public static class TaskSummary {
        private Long id;
        private String nodeId;
        private String taskName;
        private String status;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Long duration;
        private String error;
        private String output;
    }
}
