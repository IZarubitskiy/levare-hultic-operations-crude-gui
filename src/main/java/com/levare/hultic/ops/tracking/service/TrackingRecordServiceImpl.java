package com.levare.hultic.ops.tracking.service;

import com.levare.hultic.ops.tracking.dao.TrackingRecordDao;
import com.levare.hultic.ops.tracking.model.TrackingRecord;
import com.levare.hultic.ops.tracking.model.ActionTarget;
import com.levare.hultic.ops.tracking.model.ActionType;

import java.time.LocalDate;
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
}
