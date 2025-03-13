package com.scheduler.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Data
@Entity
@Table(name = "dag_tasks")
public class DagTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "node_id")
    private String nodeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dag_id", nullable = false)
    private Dag dag;

    @Column(nullable = false)
    private Integer sequence = 0;
    
    @Column(name = "retry_count", columnDefinition = "integer default 0")
    private Integer retryCount = 0;
    
    @Column(name = "is_retry", columnDefinition = "boolean default false")
    private Boolean isRetry = false;
    
    @Column(name = "max_retry_count", columnDefinition = "integer default 3")
    private Integer maxRetryCount = 3;
    
    @Column(name = "retry_interval")
    private Integer retryInterval;
    
    @Column
    private Integer level;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "dag_task_downstream",
        joinColumns = @JoinColumn(name = "source_task_id", nullable = false),
        inverseJoinColumns = @JoinColumn(name = "target_task_id", nullable = false)
    )
    private List<DagTask> downstreamTasks = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "downstreamTasks")
    private List<DagTask> upstreamTasks = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "dag_task_dependencies",
        joinColumns = @JoinColumn(name = "task_id", nullable = false),
        inverseJoinColumns = @JoinColumn(name = "dependency_id", nullable = false)
    )
    private List<DagTask> dependencies = new ArrayList<>();

    @Column(name = "execute_time")
    private LocalDateTime executeTime;
    
    @Column(name = "execute_machine")
    private String executeMachine;

    // 添加与执行记录的关联
    @OneToMany(mappedBy = "dagTask", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TaskExecution> executions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (executeTime == null) {
            executeTime = LocalDateTime.now();
        }
    }

    public List<DagTask> getDownstreamTasks() {
        return downstreamTasks;
    }

    public void setDownstreamTasks(List<DagTask> downstreamTasks) {
        this.downstreamTasks = downstreamTasks;
    }

    public List<DagTask> getUpstreamTasks() {
        return upstreamTasks;
    }

    public void setUpstreamTasks(List<DagTask> upstreamTasks) {
        this.upstreamTasks = upstreamTasks;
    }

    public List<DagTask> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<DagTask> dependencies) {
        this.dependencies = dependencies;
    }
}
