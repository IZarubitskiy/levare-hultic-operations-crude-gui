package com.levare.hultic.ops.tracking.service;

import com.levare.hultic.ops.joborders.entity.JobOrder;
import com.levare.hultic.ops.tracking.model.ActionTarget;
import com.levare.hultic.ops.tracking.model.ActionType;
import com.levare.hultic.ops.tracking.model.TrackingRecord;
import com.levare.hultic.ops.workorders.entity.WorkOrder;

import java.time.LocalDate;
import java.util.List;

public interface TrackingRecordService {
    TrackingRecord create(TrackingRecord record);

    TrackingRecord getById(Long id);

    List<TrackingRecord> getAll();

    List<TrackingRecord> findByCriteria(
            LocalDate from, LocalDate to,
            ActionTarget actionTarget, ActionType actionType,
            Long workOrderId, Long jobOrderId,
            String pn, String sn
    );

    TrackingRecord workOrderTracking(WorkOrder workOrder, ActionType actionType, String reason);

    TrackingRecord jobOrderTracking(JobOrder jobOrder, ActionType actionType, String reason);
}
