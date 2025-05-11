package com.example.levarehulticops.service.impl;

import javax.persistence.OptimisticLockException;
import com.example.levarehulticops.dto.JobOrderReadDto;
import com.example.levarehulticops.dto.JobOrderUpdateRequest;
import com.example.levarehulticops.entity.Employee;
import com.example.levarehulticops.entity.Item;
import com.example.levarehulticops.entity.JobOrder;
import com.example.levarehulticops.entity.WorkOrder;
import com.example.levarehulticops.entity.enums.JobOrderStatus;
import com.example.levarehulticops.mapper.JobOrderMapper;
import com.example.levarehulticops.repository.EmployeeRepository;
import com.example.levarehulticops.repository.ItemRepository;
import com.example.levarehulticops.repository.JobOrderRepository;
import com.example.levarehulticops.repository.WorkOrderRepository;
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
    private final JobOrderRepository jobOrderRepository;
    private final WorkOrderRepository workOrderRepository;
    private final ItemRepository itemRepository;
    private final EmployeeRepository employeeRepository;
    private final JobOrderMapper mapper;

    @Override
    public JobOrder create(JobOrder jobOrder) {
        return jobOrderRepository.save(jobOrder);
    }

    @Override
    public JobOrderReadDto updateJobOrder(Long id, JobOrderUpdateRequest dto) {
        // 1. Load existing JobOrder entity
        JobOrder jobOrder = jobOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("JobOrder not found, id=" + id));

        // 2. Check optimistic lock version
        if (!jobOrder.getVersion().equals(dto.version())) {
            throw new OptimisticLockException("JobOrder was modified concurrently");
        }

        // 3. Validate that workOrderId and itemId can only be changed when status is CREATED
        if (jobOrder.getStatus() != JobOrderStatus.CREATED
                && (dto.workOrderId() != null || dto.itemId() != null)) {
            throw new IllegalStateException(
                    "workOrderId and itemId may only be modified when status is CREATED"
            );
        }

        // 4. Apply optional changes to workOrder and item if provided
        if (dto.workOrderId() != null) {
            WorkOrder newWorkOrder = workOrderRepository.findById(dto.workOrderId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "WorkOrder not found, id=" + dto.workOrderId()));
            jobOrder.setWorkOrder(newWorkOrder);
        }

        if (dto.itemId() != null) {
            Item newItem = itemRepository.findById(dto.itemId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Item not found, id=" + dto.itemId()));
            jobOrder.setItem(newItem);
        }

        // 5. Always update responsible employee and comments
        Employee responsible = employeeRepository.findById(dto.responsibleEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Employee not found, id=" + dto.responsibleEmployeeId()));
        jobOrder.setResponsibleEmployee(responsible);
        jobOrder.setComments(dto.comments());

        // 6. Save changes—version will be incremented automatically
        JobOrder updated = jobOrderRepository.save(jobOrder);

        // 7. Map to DTO and return
        return mapper.toReadDto(updated);
    }

    @Override
    public void delete(Long id) {
        if (!jobOrderRepository.existsById(id)) {
            throw new EntityNotFoundException("JobOrder not found: " + id);
        }
        jobOrderRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public JobOrder getById(Long id) {
        return jobOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("JobOrder not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobOrder> getAll(Pageable pageable) {
        return jobOrderRepository.findAll(pageable);
    }

    @Override
    public Page<JobOrder> getByStatus(JobOrderStatus status, Pageable pageable) {
        return jobOrderRepository.findByStatus(status, pageable);
    }
}
