package com.levare.hultic.ops.tracking.service;

import com.levare.hultic.ops.items.entity.Item;
import com.levare.hultic.ops.items.service.ItemService;
import com.levare.hultic.ops.joborders.entity.JobOrder;
import com.levare.hultic.ops.tracking.dao.TrackingRecordDao;
import com.levare.hultic.ops.tracking.model.ActionTarget;
import com.levare.hultic.ops.tracking.model.ActionType;
import com.levare.hultic.ops.tracking.model.TrackingRecord;
import com.levare.hultic.ops.workorders.entity.WorkOrder;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class TrackingRecordServiceImpl implements TrackingRecordService {
    private final TrackingRecordDao dao;

    public TrackingRecordServiceImpl(TrackingRecordDao dao) {
        this.dao = dao;
    }

    @Override
    public TrackingRecord create(TrackingRecord record) {
        return dao.insert(record);
    }

    @Override
    public TrackingRecord getById(Long id) {
        return dao.getById(id);
    }

    @Override
    public List<TrackingRecord> getAll() {
        return dao.findAll();
    }

    @Override
    public List<TrackingRecord> findByCriteria(
            LocalDate from, LocalDate to,
            ActionTarget actionTarget, ActionType actionType,
            Long workOrderId, Long jobOrderId,
            String pn, String sn
    ) {
        return dao.findByCriteria(from, to, actionTarget, actionType,
                workOrderId, jobOrderId, pn, sn);
    }

    @Override
    public TrackingRecord workOrderTracking(WorkOrder workOrder, ActionType actionType, String reason) {
        TrackingRecord workOrderTrackingRecord = new TrackingRecord();
        workOrderTrackingRecord.setRecordDate(LocalDate.now(ZoneId.of("Africa/Cairo")));
        workOrderTrackingRecord.setActionTarget(ActionTarget.WORK_ORDER);
        workOrderTrackingRecord.setActionType(actionType);
        workOrderTrackingRecord.setTargetWorkOrderId(workOrder.getId());
        workOrderTrackingRecord.setReason(reason);
        workOrderTrackingRecord.setClient(workOrder.getClient());
        dao.insert(workOrderTrackingRecord);
        return workOrderTrackingRecord;
    }

    @Override
    public TrackingRecord jobOrderTracking(JobOrder jobOrder, Item item, ActionType actionType, String reason) {
        TrackingRecord jobOrderTrackingRecord = new TrackingRecord();

        jobOrderTrackingRecord.setRecordDate(LocalDate.now(ZoneId.of("Africa/Cairo")));
        jobOrderTrackingRecord.setActionTarget(ActionTarget.JOB_ORDER);
        jobOrderTrackingRecord.setActionType(actionType);
        jobOrderTrackingRecord.setTargetJobOrderId(jobOrder.getId());
        jobOrderTrackingRecord.setReason(reason);
        jobOrderTrackingRecord.setTargetPN(item.getItemInfo().getPartNumber());
        jobOrderTrackingRecord.setTargetDescription(item.getItemInfo().getDescription());
        jobOrderTrackingRecord.setTargetSN(item.getSerialNumber());
        dao.insert(jobOrderTrackingRecord);
        return jobOrderTrackingRecord;
    }

    @Override
    public TrackingRecord itemTracking(Item item, ActionType actionType, String reason){
        TrackingRecord itemOrderTrackingRecord = new TrackingRecord();
        itemOrderTrackingRecord.setRecordDate(LocalDate.now(ZoneId.of("Africa/Cairo")));
        itemOrderTrackingRecord.setActionTarget(ActionTarget.ITEM);
        itemOrderTrackingRecord.setActionType(actionType);
        itemOrderTrackingRecord.setReason(reason);
        itemOrderTrackingRecord.setTargetPN(item.getItemInfo().getPartNumber());
        itemOrderTrackingRecord.setTargetDescription(item.getItemInfo().getDescription());
        itemOrderTrackingRecord.setTargetSN(item.getSerialNumber());
        dao.insert(itemOrderTrackingRecord);
        return itemOrderTrackingRecord;
    }

}
