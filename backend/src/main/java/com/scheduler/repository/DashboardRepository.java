package com.scheduler.repository;

import com.scheduler.model.Dashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface DashboardRepository extends JpaRepository<Dashboard, Long> {
    
    @Query("SELECT COUNT(DISTINCT te.task.id) FROM TaskExecution te")
    Long countTasksByStatus(String status);

    @Query("SELECT COUNT(DISTINCT te.dagExecutionId) FROM TaskExecution te WHERE te.dagTask IS NOT NULL")
    Long countDagsByStatus(String status);

    @Query("SELECT COUNT(al) FROM AlertLog al WHERE al.createTime >= :time")
    Long countAlertLogsLast24Hours(@Param("time") LocalDateTime time);  // 添加 @Param 注解

    Dashboard findFirstByOrderByLastUpdateTimeDesc();
}
