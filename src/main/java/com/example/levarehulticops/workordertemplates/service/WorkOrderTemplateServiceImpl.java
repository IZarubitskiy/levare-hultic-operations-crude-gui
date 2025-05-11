// src/main/java/com/example/levarehulticops/service/impl/WorkOrderTemplateServiceImpl.java
package com.example.levarehulticops.workordertemplates.service;

import com.example.levarehulticops.workordertemplates.entity.WorkOrderTemplate;
import com.example.levarehulticops.workordertemplates.repository.WorkOrderTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkOrderTemplateServiceImpl implements WorkOrderTemplateService {
    private final WorkOrderTemplateRepository repo;

    @Override
    public WorkOrderTemplate create(WorkOrderTemplate tpl) {
        return repo.save(tpl);
    }

    @Override
    public WorkOrderTemplate update(WorkOrderTemplate tpl) {
        if (!repo.existsById(tpl.getId())) {
            throw new EntityNotFoundException("Template not found: " + tpl.getId());
        }
        return repo.save(tpl);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("Template not found: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkOrderTemplate getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Template not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WorkOrderTemplate> getAll(Pageable pageable) {
        return repo.findAll(pageable);
    }
}
