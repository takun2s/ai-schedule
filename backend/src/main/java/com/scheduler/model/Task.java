package com.scheduler.model;

import lombok.Data;
import lombok.experimental.Accessors;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;
import com.scheduler.converter.JsonMapConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Entity
@Table(name = "tasks")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})  // 处理代理对象序列化
@Accessors(chain = true)
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String command;

    @Column(name = "cron_expression")
    private String cronExpression;

    private String description;

    // HTTP任务字段
    @Column(name = "http_url")
    private String httpUrl;

    @Column(name = "http_method")
    private String httpMethod;

    @Convert(converter = JsonMapConverter.class)
    @Column(name = "http_headers", columnDefinition = "TEXT")
    private Map<String, String> httpHeaders;

    @Column(name = "http_body", columnDefinition = "TEXT")
    private String httpBody;

    // Python任务字段
    @Column(name = "python_path")
    private String pythonPath;

    @Column(name = "python_version")
    private String pythonVersion;

    @Column(name = "requirements", columnDefinition = "TEXT")
    private String requirements;

    // JAR任务字段
    @Column(name = "jar_path")
    private String jarPath;

    @Column(name = "main_class")
    private String mainClass;

    @Column(name = "java_opts")
    private String javaOpts;

    // Spark任务字段
    @Column(name = "spark_master")
    private String sparkMaster;

    @Column(name = "spark_app_name")
    private String sparkAppName;

    @Column(name = "spark_main_class")
    private String sparkMainClass;

    @Column(name = "spark_config", columnDefinition = "TEXT")
    private String sparkConfig;

    @Column(name = "spark_args", columnDefinition = "TEXT")
    private String sparkArgs;

    // 配置字段
    @Column(name = "alert_email")
    private String alertEmail;

    @Column(name = "alert_on_failure")
    private Boolean alertOnFailure = false;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    private Integer timeout = 3600;

    @Column(name = "execute_machine")
    private String executeMachine;

    private Integer priority = 0;

    @Column(name = "work_dir")
    private String workDir;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "last_execute_time")
    private LocalDateTime lastExecuteTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "next_execute_time")
    private LocalDateTime nextExecuteTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @JsonIgnore
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TaskExecution> executions;

    @JsonIgnore
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DagTask> dagTasks;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
