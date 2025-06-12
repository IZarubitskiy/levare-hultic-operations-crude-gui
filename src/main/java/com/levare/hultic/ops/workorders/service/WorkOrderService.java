package com.levare.hultic.ops.workorders.service;

import com.levare.hultic.ops.workorders.entity.WorkOrder;
import com.levare.hultic.ops.workorders.entity.WorkOrderStatus;

import java.util.List;

/**
 * Service interface for managing WorkOrders in JavaFX GUI.
 */
public interface WorkOrderService {

    WorkOrder create(WorkOrder workOrder);

    WorkOrder update(WorkOrder workOrder);

    void delete(Long id);

    WorkOrder getById(Long id);

    List<WorkOrder> getAll();

    List<WorkOrder> getByStatus(WorkOrderStatus status);
}
