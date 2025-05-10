// src/main/java/com/example/levarehulticops/service/impl/JobOrderServiceImpl.java
package com.example.levarehulticops.service.impl;

import com.example.levarehulticops.entity.JobOrder;
import com.example.levarehulticops.repository.JobOrderRepository;
import com.example.levarehulticops.service.JobOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class JobOrderServiceImpl implements JobOrderService {
    private final JobOrderRepository repo;

    @Override
    public JobOrder create(JobOrder jobOrder) {
        return repo.save(jobOrder);
    }

    @Override
    public JobOrder update(JobOrder jobOrder) {
        if (!repo.existsById(jobOrder.getId())) {
            throw new EntityNotFoundException("JobOrder not found: " + jobOrder.getId());
        }
        return repo.save(jobOrder);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("JobOrder not found: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public JobOrder getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("JobOrder not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobOrder> getAll(Pageable pageable) {
        return repo.findAll(pageable);
    }
}
