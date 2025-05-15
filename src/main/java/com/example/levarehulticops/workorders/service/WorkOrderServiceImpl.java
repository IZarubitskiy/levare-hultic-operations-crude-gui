// src/main/java/com/example/levarehulticops/service/impl/WorkOrderServiceImpl.java
package com.example.levarehulticops.workorders.service;

import com.example.levarehulticops.employees.service.EmployeeService;
import com.example.levarehulticops.workorders.dto.WorkOrderCreateRequest;
import com.example.levarehulticops.workorders.dto.WorkOrderReadDto;
import com.example.levarehulticops.workorders.entity.WorkOrder;
import com.example.levarehulticops.workorders.entity.WorkOrderStatus;
import com.example.levarehulticops.workorders.mapper.WorkOrderMapper;
import com.example.levarehulticops.workorders.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkOrderServiceImpl implements WorkOrderService {
    private final WorkOrderRepository workOrderRepository;
    private final WorkOrderMapper workOrderMapper;
    private final EmployeeService employeeService;

    @Override
    @Transactional
    public WorkOrderReadDto create(WorkOrderCreateRequest dto, Long requestorId) {
        // 1. Map DTO + requestorId → entity (stubbed related entities inside mapper)
        WorkOrder wo = workOrderMapper.toEntity(dto, requestorId);

        // 2. Fill in system-managed fields
        wo.setRequestDate(LocalDate.now(ZoneId.of("Africa/Cairo")));
        wo.setStatus(WorkOrderStatus.CREATED);

        // 3. Persist
        workOrderRepository.save(wo);

        // 4. Return DTO for the created entity
        return workOrderMapper.toReadDto(wo);
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

    @Override
    public Page<WorkOrderReadDto> getByStatus(WorkOrderStatus status, Pageable pageable) {
        return workOrderRepository.findAllByStatus(status, pageable)
                .map(workOrderMapper::toReadDto);
    }
}
