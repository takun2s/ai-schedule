package com.scheduler.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "task_executions")
public class TaskExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dag_task_id")
    private DagTask dagTask;  // 添加 DagTask 关联

    @Column(name = "dag_execution_id")
    private Long dagExecutionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dag_execution_id", insertable = false, updatable = false)
    private DagExecution dagExecution;

    @Column(name = "node_id")
    private String nodeId;  // DAG中的节点ID

    private String status;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    private Long duration;
    
    private String error;
    
    private String output;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "task_name")
    private String taskName;  // 添加任务名称字段

    // 添加状态常量
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_RUNNING = "RUNNING";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_TIMEOUT = "TIMEOUT";
    public static final String STATUS_CANCELED = "CANCELED";
    public static final String STATUS_STOPPED = "STOPPED";

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
        if (task != null) {
            taskName = task.getName();  // 在创建时设置任务名称
        }
    }

    public void setTask(Task task) {
        this.task = task;
        if (task != null) {
            this.taskName = task.getName();  // 在设置 task 时同步设置任务名称
        }
    }

    public void setDagExecutionId(Long dagExecutionId) {
        this.dagExecutionId = dagExecutionId;
    }

    public Long getDagExecutionId() {
        return this.dagExecutionId;
    }
}
