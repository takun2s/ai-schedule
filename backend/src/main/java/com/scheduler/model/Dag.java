package com.scheduler.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "dags", indexes = {
    @Index(name = "idx_dag_next_execute_time", columnList = "next_execute_time")  // 移除 status 索引
})
public class Dag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "cron_expression")
    private String cronExpression;

    @Column(name = "last_execute_time")
    private LocalDateTime lastExecuteTime;

    @Column(name = "next_execute_time")
    private LocalDateTime nextExecuteTime;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "alert_email")
    private String alertEmail;

    @Column(name = "alert_on_failure", columnDefinition = "boolean default false")
    private Boolean alertOnFailure = false;

    @Column(columnDefinition = "TEXT")
    private String config;

    @Column(columnDefinition = "TEXT")
    private String nodes;  // 存储节点信息的JSON字符串

    @Column(columnDefinition = "TEXT")
    private String edges;  // 存储边信息的JSON字符串

    @JsonIgnore  // 添加这个注解
    @OneToMany(mappedBy = "dag", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DagTask> tasks = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }

    @Version
    private Long version;

    public String getNodes() {
        return nodes;
    }

    public void setNodes(String nodes) {
        this.nodes = nodes;
    }

    public String getEdges() {
        return edges;
    }

    public void setEdges(String edges) {
        this.edges = edges;
    }
}
