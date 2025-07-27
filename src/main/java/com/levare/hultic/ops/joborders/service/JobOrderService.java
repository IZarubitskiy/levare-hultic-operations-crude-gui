package com.levare.hultic.ops.joborders.service;

import com.levare.hultic.ops.joborders.entity.JobOrder;
import com.levare.hultic.ops.joborders.entity.JobOrderStatus;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for managing JobOrders in GUI.
 */
public interface JobOrderService {

    JobOrder create(JobOrder jobOrder);

    void updatePlanDate(Long id, LocalDate newPlanDate);

    void changeStatus(Long id, JobOrderStatus newStatus);

    void finish (Long id, LocalDate finishDate);

    void delete(Long id, String reason);

    JobOrder getById(Long id);

    List<JobOrder> getAll();

    List<JobOrder> getByStatus(JobOrderStatus status);
}
