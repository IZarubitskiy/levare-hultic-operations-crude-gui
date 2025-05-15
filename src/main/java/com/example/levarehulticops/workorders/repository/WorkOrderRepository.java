package com.example.levarehulticops.workorders.repository;

import com.example.levarehulticops.workorders.entity.WorkOrder;
import com.example.levarehulticops.workorders.entity.WorkOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

    Page<WorkOrder> findAllByStatus(WorkOrderStatus status, Pageable pageable);

}
