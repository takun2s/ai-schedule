package com.scheduler.repository;

import com.scheduler.model.Dag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DagRepository extends JpaRepository<Dag, Long> {
    
    @Query("SELECT d FROM Dag d WHERE d.cronExpression IS NOT NULL AND d.nextExecuteTime <= :now")
    List<Dag> findScheduledDags(LocalDateTime now);
}
