package com.scheduler.repository;

import com.scheduler.model.AlertLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertLogRepository extends JpaRepository<AlertLog, Long> {
    // 基础的CRUD方法由JpaRepository提供
}
