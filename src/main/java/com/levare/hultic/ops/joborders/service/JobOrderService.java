package com.levare.hultic.ops.joborders.service;

import com.levare.hultic.ops.joborders.entity.JobOrder;
import com.levare.hultic.ops.joborders.entity.JobOrderStatus;

import java.util.List;

/**
 * Service interface for managing JobOrders in GUI.
 */
public interface JobOrderService {

    JobOrder create(JobOrder jobOrder);

    JobOrder update(Long id, JobOrder jobOrder);

    JobOrder changeStatus(Long id, JobOrderStatus newStatus);

    void delete(Long id);

    JobOrder getById(Long id);

    List<JobOrder> getAll();

    List<JobOrder> getByStatus(JobOrderStatus status);
}
