package com.example.levarehulticops.workordertemplates.service;

import com.example.levarehulticops.workordertemplates.entity.WorkOrderTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WorkOrderTemplateService {
    WorkOrderTemplate create(WorkOrderTemplate tpl);

    WorkOrderTemplate update(WorkOrderTemplate tpl);

    void delete(Long id);

    WorkOrderTemplate getById(Long id);

    Page<WorkOrderTemplate> getAll(Pageable pageable);
}
