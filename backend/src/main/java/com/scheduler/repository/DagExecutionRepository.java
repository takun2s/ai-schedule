package com.scheduler.repository;

import com.scheduler.model.DagExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DagExecutionRepository extends JpaRepository<DagExecution, Long> {
    @Query("SELECT DISTINCT de FROM DagExecution de " +
           "LEFT JOIN FETCH de.dag d " +
           "LEFT JOIN FETCH de.taskExecutions te " +
           "WHERE de.id = :id")
    Optional<DagExecution> findByIdWithTaskExecutions(@Param("id") Long id);
    
    @Query(value = "SELECT DISTINCT de.* FROM dag_executions de " +
           "LEFT JOIN task_executions te ON de.id = te.dag_execution_id " +
           "ORDER BY de.start_time DESC LIMIT 100", nativeQuery = true)
    List<DagExecution> findTop100ByOrderByStartTimeDesc();
    
    @Query("SELECT DISTINCT de FROM DagExecution de " +
           "LEFT JOIN FETCH de.taskExecutions te " +
           "LEFT JOIN FETCH de.dag " +
           "ORDER BY de.startTime DESC")
    List<DagExecution> findAllWithTaskExecutions();
}
