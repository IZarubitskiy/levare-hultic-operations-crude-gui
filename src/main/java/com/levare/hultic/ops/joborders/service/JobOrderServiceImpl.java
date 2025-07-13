package com.levare.hultic.ops.joborders.service;

import com.levare.hultic.ops.items.service.ItemService;
import com.levare.hultic.ops.items.service.ItemServiceImpl;
import com.levare.hultic.ops.joborders.dao.JobOrderDao;
import com.levare.hultic.ops.joborders.entity.JobOrder;
import com.levare.hultic.ops.joborders.entity.JobOrderStatus;
import com.levare.hultic.ops.tracking.model.ActionType;
import com.levare.hultic.ops.tracking.service.TrackingRecordService;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Objects;

import static lombok.AccessLevel.PRIVATE;

/**
 * Default implementation of JobOrderService using manual DAO.
 */
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class JobOrderServiceImpl implements JobOrderService {

    JobOrderDao jobOrderDao;
    ItemService itemService;
    TrackingRecordService trackingRecordService;

    public JobOrderServiceImpl(JobOrderDao jobOrderDao, ItemService itemService, TrackingRecordService trackingRecordService) {
        this.jobOrderDao = jobOrderDao;
        this.itemService = itemService;
        this.trackingRecordService = trackingRecordService;
    }

    @Override
    public JobOrder create(JobOrder jobOrder) {
        jobOrder.setStatus(JobOrderStatus.CREATED);

        trackingRecordService.jobOrderTracking(
                jobOrderDao.insert(jobOrder),
                itemService.getById(jobOrder.getId()),
                ActionType.CREATION,
                "New Jo creation");
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
        JobOrderStatus oldStatus = jobOrder.getStatus();
        jobOrder.setStatus(newStatus);
        jobOrderDao.update(jobOrder);
        trackingRecordService.jobOrderTracking(
                jobOrderDao.insert(jobOrder),
                itemService.getById(jobOrder.getId()),
                ActionType.STATUS_CHANGE,
                "Status Changed from " + oldStatus.name() + " to " + newStatus.name());
        return jobOrder;
    }

    @Override
    public void delete(Long id, String reason) {
        getById(id); // throws if not found


        trackingRecordService.jobOrderTracking(
                jobOrderDao.insert(getById(id)),
                itemService.getById(getById(id).getId()),
                ActionType.REMOVAL,
                reason);
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
