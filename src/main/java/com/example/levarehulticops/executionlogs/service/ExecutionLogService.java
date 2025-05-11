package com.example.levarehulticops.executionlogs.service;

import com.example.levarehulticops.executionlogs.entity.ExecutionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExecutionLogService {
    ExecutionLog create(ExecutionLog log);
    ExecutionLog update(ExecutionLog log);
    void delete(Long id);
    ExecutionLog getById(Long id);
    Page<ExecutionLog> getAll(Pageable pageable);
}
