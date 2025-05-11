package com.example.levarehulticops.executionlogs.repository;

import com.example.levarehulticops.executionlogs.entity.ExecutionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExecutionLogRepository extends JpaRepository<ExecutionLog, Long> {
}
