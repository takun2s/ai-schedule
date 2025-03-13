package com.scheduler.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "dashboard_stats")
@NoArgsConstructor
@AllArgsConstructor
public class Dashboard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_tasks")
    private Long totalTasks;

    @Column(name = "running_tasks")
    private Long runningTasks;

    @Column(name = "failed_tasks")
    private Long failedTasks;

    @Column(name = "total_dags")
    private Long totalDags;

    @Column(name = "running_dags")
    private Long runningDags;

    @Column(name = "failed_dags")
    private Long failedDags;

    @Column(name = "alert_count_24h")
    private Long alertCount24h;

    @Column(name = "last_update_time")
    private LocalDateTime lastUpdateTime;
}
