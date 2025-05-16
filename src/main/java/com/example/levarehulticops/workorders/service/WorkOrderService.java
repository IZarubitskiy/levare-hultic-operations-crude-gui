package com.example.levarehulticops.workorders.service;

import com.example.levarehulticops.workorders.dto.WorkOrderCreateRequest;
import com.example.levarehulticops.workorders.dto.WorkOrderReadDto;
import com.example.levarehulticops.workorders.entity.WorkOrder;
import com.example.levarehulticops.workorders.entity.WorkOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WorkOrderService {
    WorkOrderReadDto create(WorkOrderCreateRequest dto, String username);

    WorkOrder update(WorkOrder workOrder);

    void delete(Long id);

    WorkOrder getById(Long id);

    Page<WorkOrder> getAll(Pageable pageable);

    Page<WorkOrderReadDto> getByStatus(WorkOrderStatus status, Pageable pageable);
}
