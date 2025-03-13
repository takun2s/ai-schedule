package com.scheduler.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "alert_logs")
public class AlertLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dag_id")
    private Dag dag;

    @Column(nullable = false)
    private String type;  // EMAIL, SMS, etc.

    @Column(nullable = false)
    private String status;  // SUCCESS, FAILED

    @Column(name = "alert_to", nullable = false)
    private String alertTo;  // 接收人

    @Column(columnDefinition = "TEXT")
    private String content;  // 告警内容

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;  // 如果发送失败，记录错误信息

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }

    // 状态常量
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILED = "FAILED";

    // 类型常量
    public static final String TYPE_EMAIL = "EMAIL";
    public static final String TYPE_SMS = "SMS";
}
