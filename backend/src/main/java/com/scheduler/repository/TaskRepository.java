package com.scheduler.repository;

import com.scheduler.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    
    @Query("SELECT t FROM Task t WHERE t.type = :type")
    List<Task> findByType(@Param("type") String type);
    
    @Query(value = "SELECT * FROM tasks WHERE cron_expression IS NOT NULL", nativeQuery = true)
    List<Task> findScheduledTasks();
    
    @Query("SELECT t FROM Task t WHERE t.executeMachine = :machine")
    List<Task> findByExecuteMachine(@Param("machine") String machine);
}
