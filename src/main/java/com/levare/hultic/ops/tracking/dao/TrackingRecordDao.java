package com.levare.hultic.ops.tracking.dao;

import com.levare.hultic.ops.tracking.model.ActionTarget;
import com.levare.hultic.ops.tracking.model.ActionType;
import com.levare.hultic.ops.tracking.model.TrackingRecord;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class TrackingRecordDao {
    private final Connection connection;

    public TrackingRecordDao(Connection connection) {
        this.connection = connection;
    }

    public TrackingRecord insert(TrackingRecord record) {
        String sql = """
                INSERT INTO tracking_records
                    (record_date, action_target, action_type,
                     target_work_order_id, target_job_order_id,
                     target_pn, target_sn, target_description, reason)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, Timestamp.valueOf(record.getRecordDate()));
            ps.setString(2, record.getActionTarget().name());
            ps.setString(3, record.getActionType().name());
            if (record.getTargetWorkOrderId() != null)
                ps.setLong(4, record.getTargetWorkOrderId());
            else ps.setNull(4, Types.BIGINT);
            if (record.getTargetJobOrderId() != null)
                ps.setLong(5, record.getTargetJobOrderId());
            else ps.setNull(5, Types.BIGINT);
            ps.setString(6, record.getTargetPN());
            ps.setString(7, record.getTargetSN());
            ps.setString(8, record.getTargetDescription());
            ps.setString(9, record.getReason());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    record.setId(keys.getLong(1));
                }
            }
            return record;
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting TrackingRecord", e);
        }
    }

    public TrackingRecord getById(Long id) {
        String sql = "SELECT * FROM tracking_records WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading TrackingRecord by id", e);
        }
    }

    public List<TrackingRecord> findAll() {
        String sql = "SELECT * FROM tracking_records ORDER BY record_date DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            List<TrackingRecord> out = new ArrayList<>();
            while (rs.next()) out.add(mapRow(rs));
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Error loading all TrackingRecords", e);
        }
    }

    public List<TrackingRecord> findByCriteria(
            LocalDate from, LocalDate to,
            ActionTarget actionTarget, ActionType actionType,
            Long workOrderId, Long jobOrderId,
            String pn, String sn
    ) {
        // Построим динамический WHERE
        StringBuilder sql = new StringBuilder("""
                SELECT * FROM tracking_records
                WHERE 1=1
                """);
        List<Object> params = new ArrayList<>();

        if (from != null) {
            sql.append(" AND record_date >= ?");
            params.add(Date.valueOf(from));
        }
        if (to != null) {
            sql.append(" AND record_date <= ?");
            params.add(Date.valueOf(to));
        }
        if (actionTarget != null) {
            sql.append(" AND action_target = ?");
            params.add(actionTarget.name());
        }
        if (actionType != null) {
            sql.append(" AND action_type = ?");
            params.add(actionType.name());
        }
        if (workOrderId != null) {
            sql.append(" AND target_work_order_id = ?");
            params.add(workOrderId);
        }
        if (jobOrderId != null) {
            sql.append(" AND target_job_order_id = ?");
            params.add(jobOrderId);
        }
        if (pn != null && !pn.isBlank()) {
            sql.append(" AND target_pn LIKE ?");
            params.add("%" + pn.trim() + "%");
        }
        if (sn != null && !sn.isBlank()) {
            sql.append(" AND target_sn LIKE ?");
            params.add("%" + sn.trim() + "%");
        }

        sql.append(" ORDER BY record_date DESC");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<TrackingRecord> out = new ArrayList<>();
                while (rs.next()) out.add(mapRow(rs));
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying TrackingRecord by criteria", e);
        }
    }

    private TrackingRecord mapRow(ResultSet rs) throws SQLException {
        TrackingRecord rec = new TrackingRecord();
        rec.setId(rs.getLong("id"));

        // Считываем epoch миллисекунд и преобразуем в LocalDate
        long epochMilli = rs.getLong("record_date");
        LocalDateTime recordDate = Instant.ofEpochMilli(epochMilli)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        rec.setRecordDate(recordDate);

        // Остальные поля
        rec.setActionTarget(ActionTarget.valueOf(rs.getString("action_target")));
        rec.setActionType(ActionType.valueOf(rs.getString("action_type")));

        long woId = rs.getLong("target_work_order_id");
        rec.setTargetWorkOrderId(rs.wasNull() ? null : woId);

        long joId = rs.getLong("target_job_order_id");
        rec.setTargetJobOrderId(rs.wasNull() ? null : joId);

        rec.setTargetPN(rs.getString("target_pn"));
        rec.setTargetSN(rs.getString("target_sn"));
        rec.setTargetDescription(rs.getString("target_description"));
        rec.setReason(rs.getString("reason"));

        return rec;
    }
}
