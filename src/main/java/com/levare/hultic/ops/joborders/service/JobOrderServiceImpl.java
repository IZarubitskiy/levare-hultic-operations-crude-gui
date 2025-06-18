package com.levare.hultic.ops.joborders.service;

import com.levare.hultic.ops.items.service.ItemService;
import com.levare.hultic.ops.joborders.dao.JobOrderDao;
import com.levare.hultic.ops.joborders.entity.JobOrder;
import com.levare.hultic.ops.joborders.entity.JobOrderStatus;
import com.levare.hultic.ops.workorders.service.WorkOrderService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Default implementation of JobOrderService using manual DAO.
 */
public class JobOrderServiceImpl implements JobOrderService {

    private final JobOrderDao jobOrderDao;

    public JobOrderServiceImpl(JobOrderDao jobOrderDao, WorkOrderService workOrderService, ItemService itemService) {
        this.jobOrderDao = jobOrderDao;
    }

    @Override
    public JobOrder create(JobOrder jobOrder) {
        jobOrder.setStatus(JobOrderStatus.CREATED);
        jobOrderDao.insert(jobOrder);
        return jobOrder;
    }

    @Override
    public JobOrder update(Long id, JobOrder updatedJobOrder) {
        JobOrder existing = getById(id);

        updatedJobOrder.setId(existing.getId());
        jobOrderDao.update(updatedJobOrder);
        return updatedJobOrder;
    }

    @Override
    public JobOrder changeStatus(Long id, JobOrderStatus newStatus) {
        JobOrder jobOrder = getById(id);
        jobOrder.setStatus(newStatus);
        jobOrderDao.update(jobOrder);
        return jobOrder;
    }

    @Override
    public void delete(Long id) {
        getById(id); // throws if not found
        jobOrderDao.deleteById(id);
    }

    @Override
    public JobOrder getById(Long id) {
        return jobOrderDao.findAll().stream()
                .filter(jo -> Objects.equals(jo.getId(), id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("JobOrder not found: " + id));
    }

    @Override
    public List<JobOrder> getAll() {
        return jobOrderDao.findAll();
    }

    @Override
    public List<JobOrder> getByStatus(JobOrderStatus status) {
        return jobOrderDao.findByStatus(status);
    }
}
