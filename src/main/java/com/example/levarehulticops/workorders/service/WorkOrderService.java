
package com.example.levarehulticops.workorders.service;

import com.example.levarehulticops.workorders.entity.WorkOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WorkOrderService {
    WorkOrder create(WorkOrder workOrder);
    WorkOrder update(WorkOrder workOrder);
    void delete(Long id);
    WorkOrder getById(Long id);
    Page<WorkOrder> getAll(Pageable pageable);
}
