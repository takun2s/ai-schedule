package com.scheduler.repository;

import com.scheduler.model.DagTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DagTaskRepository extends JpaRepository<DagTask, Long> {
    
    @Query("SELECT dt FROM DagTask dt WHERE dt.dag.id = :dagId")
    List<DagTask> findByDagId(@Param("dagId") Long dagId);

    List<DagTask> findByDagIdOrderBySequenceAsc(Long dagId);
}
