// src/main/java/com/example/levarehulticops/service/impl/WorkDayServiceImpl.java
package com.example.levarehulticops.workdays.service;

import com.example.levarehulticops.workdays.entity.WorkDay;
import com.example.levarehulticops.workdays.repository.WorkDayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkDayServiceImpl implements WorkDayService {
    private final WorkDayRepository repo;

    @Override
    public WorkDay create(WorkDay workDay) {
        return repo.save(workDay);
    }

    @Override
    public WorkDay update(WorkDay workDay) {
        if (!repo.existsById(workDay.getId())) {
            throw new EntityNotFoundException("WorkDay not found: " + workDay.getId());
        }
        return repo.save(workDay);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("WorkDay not found: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkDay getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("WorkDay not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WorkDay> getAll(Pageable pageable) {
        return repo.findAll(pageable);
    }
}