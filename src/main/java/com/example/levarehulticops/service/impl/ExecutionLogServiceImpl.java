// src/main/java/com/example/levarehulticops/service/impl/ExecutionLogServiceImpl.java
package com.example.levarehulticops.service.impl;

import com.example.levarehulticops.entity.ExecutionLog;
import com.example.levarehulticops.repository.ExecutionLogRepository;
import com.example.levarehulticops.service.ExecutionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class ExecutionLogServiceImpl implements ExecutionLogService {
    private final ExecutionLogRepository repo;

    @Override
    public ExecutionLog create(ExecutionLog log) {
        return repo.save(log);
    }

    @Override
    public ExecutionLog update(ExecutionLog log) {
        if (!repo.existsById(log.getId())) {
            throw new EntityNotFoundException("ExecutionLog not found: " + log.getId());
        }
        return repo.save(log);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("ExecutionLog not found: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ExecutionLog getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ExecutionLog not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExecutionLog> getAll(Pageable pageable) {
        return repo.findAll(pageable);
    }
}
