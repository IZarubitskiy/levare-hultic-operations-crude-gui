// src/main/java/com/example/levarehulticops/service/impl/WorkOrderServiceImpl.java
package com.example.levarehulticops.service.impl;

import com.example.levarehulticops.entity.WorkOrder;
import com.example.levarehulticops.repository.WorkOrderRepository;
import com.example.levarehulticops.service.WorkOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkOrderServiceImpl implements WorkOrderService {
    private final WorkOrderRepository workOrderRepository;

    @Override
    public WorkOrder create(WorkOrder workOrder) {
        return workOrderRepository.save(workOrder);
    }

    @Override
    public WorkOrder update(WorkOrder workOrder) {
        if (!workOrderRepository.existsById(workOrder.getId())) {
            throw new EntityNotFoundException("WorkOrder not found: " + workOrder.getId());
        }
        return workOrderRepository.save(workOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkOrder getById(Long id) {
        return workOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("WorkOrder not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WorkOrder> getAll(Pageable pageable) {
        return workOrderRepository.findAll(pageable);
    }

    @Override
    public void delete(Long id) {
        if (!workOrderRepository.existsById(id)) {
            throw new EntityNotFoundException("WorkOrder not found: " + id);
        }
        workOrderRepository.deleteById(id);
    }
}
