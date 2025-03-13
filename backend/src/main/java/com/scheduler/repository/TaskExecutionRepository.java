package com.scheduler.repository;

import com.scheduler.model.DagTask;
import com.scheduler.model.TaskExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskExecutionRepository extends JpaRepository<TaskExecution, Long> {
    
    @Query("SELECT COUNT(te) FROM TaskExecution te WHERE te.status = ?1")
    long countByStatus(String status);

    @Query("SELECT COUNT(te) FROM TaskExecution te WHERE te.dagTask IS NOT NULL AND te.status = 'RUNNING'")
    long countRunningDagTasks();

    @Query("SELECT COUNT(te) FROM TaskExecution te WHERE te.dagTask IS NOT NULL AND te.status = 'COMPLETED'")
    long countCompletedDagTasks();

    @Query("SELECT COUNT(te) FROM TaskExecution te WHERE te.dagTask IS NOT NULL AND te.status = 'FAILED'")
    long countFailedDagTasks();

    @Query("SELECT COUNT(te) FROM TaskExecution te WHERE te.dagTask IS NOT NULL AND te.status = 'PENDING'")
    long countPendingDagTasks();

    List<TaskExecution> findByTaskIdOrderByStartTimeDesc(Long taskId);
    List<TaskExecution> findByDagExecutionId(Long dagExecutionId);
    List<TaskExecution> findTop100ByOrderByStartTimeDesc();
    List<TaskExecution> findByTaskId(Long taskId);
    TaskExecution findByDagExecutionIdAndDagTask(Long dagExecutionId, DagTask dagTask);
}
