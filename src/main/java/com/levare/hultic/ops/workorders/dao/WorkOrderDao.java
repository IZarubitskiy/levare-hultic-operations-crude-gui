package com.levare.hultic.ops.workorders.dao;

import com.levare.hultic.ops.workorders.entity.Client;
import com.levare.hultic.ops.workorders.entity.WorkOrder;
import com.levare.hultic.ops.workorders.entity.WorkOrderStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for WorkOrder.
 * Handles basic CRUD operations using JDBC.
 */
public class WorkOrderDao {

    private final Connection connection;

    public WorkOrderDao(Connection connection) {
        this.connection = connection;
    }

    public List<WorkOrder> findAll() {
        List<WorkOrder> result = new ArrayList<>();
        String sql = "SELECT * FROM work_orders";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<WorkOrder> findByStatus(WorkOrderStatus status) {
        List<WorkOrder> result = new ArrayList<>();
        String sql = "SELECT * FROM work_orders WHERE status = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public WorkOrder findById(Long id) {
        String sql = "SELECT * FROM work_orders WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        throw new IllegalArgumentException("WorkOrder not found: " + id);
    }

    public void insert(WorkOrder order) {
        String sql = "INSERT INTO work_orders " +
                "(work_order_number, client, well, request_date, delivery_date, status, requestor_id, comments) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, order.getWorkOrderNumber());
            stmt.setString(2, order.getClient().name());
            stmt.setString(3, order.getWell());
            stmt.setDate(4, Date.valueOf(order.getRequestDate()));
            stmt.setDate(5, order.getDeliveryDate() != null ? Date.valueOf(order.getDeliveryDate()) : null);
            stmt.setString(6, order.getStatus().name());
            stmt.setLong(7, order.getRequestor().getId());
            stmt.setString(8, order.getComments());

            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    order.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(WorkOrder order) {
        String sql = "UPDATE work_orders SET " +
                "work_order_number = ?, client = ?, well = ?, request_date = ?, delivery_date = ?, " +
                "status = ?, requestor_id = ?, comments = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, order.getWorkOrderNumber());
            stmt.setString(2, order.getClient().name());
            stmt.setString(3, order.getWell());
            stmt.setDate(4, Date.valueOf(order.getRequestDate()));
            stmt.setDate(5, order.getDeliveryDate() != null ? Date.valueOf(order.getDeliveryDate()) : null);
            stmt.setString(6, order.getStatus().name());
            stmt.setLong(7, order.getRequestor().getId());
            stmt.setString(8, order.getComments());
            stmt.setLong(9, order.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM work_orders WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private WorkOrder mapRow(ResultSet rs) throws SQLException {
        WorkOrder order = new WorkOrder();
        order.setId(rs.getLong("id"));
        order.setWorkOrderNumber(rs.getString("work_order_number"));
        order.setClient(Enum.valueOf(Client.class, rs.getString("client")));
        order.setWell(rs.getString("well"));
        order.setRequestDate(rs.getDate("request_date").toLocalDate());

        Date delivery = rs.getDate("delivery_date");
        if (delivery != null) {
            order.setDeliveryDate(delivery.toLocalDate());
        }

        order.setStatus(WorkOrderStatus.valueOf(rs.getString("status")));
        order.setComments(rs.getString("comments"));
        order.setRequestor(null); // load manually if needed

        return order;
    }
}
