package com.levare.hultic.ops.joborders.dao;

import com.levare.hultic.ops.joborders.entity.JobOrder;
import com.levare.hultic.ops.joborders.entity.JobOrderStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for JobOrder.
 * Implements basic retrieval logic using JDBC and SQLite.
 */
public class JobOrderDao {

    private final Connection connection;

    public JobOrderDao(Connection connection) {
        this.connection = connection;
    }

    public List<JobOrder> findAll() {
        List<JobOrder> result = new ArrayList<>();
        String sql = "SELECT * FROM job_orders";

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

    public List<JobOrder> findByStatus(JobOrderStatus status) {
        List<JobOrder> result = new ArrayList<>();
        String sql = "SELECT * FROM job_orders WHERE status = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void insert(JobOrder jobOrder) {
        String sql = "INSERT INTO job_orders (work_order_id, item_id, status, responsible_employee_id, comments) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, jobOrder.getWorkOrder().getId());
            stmt.setLong(2, jobOrder.getItem().getId());
            stmt.setString(3, jobOrder.getStatus().name());
            stmt.setLong(4, jobOrder.getResponsibleUser().getId());
            stmt.setString(5, jobOrder.getComments());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    jobOrder.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(JobOrder jobOrder) {
        String sql = "UPDATE job_orders SET work_order_id = ?, item_id = ?, status = ?, " +
                "responsible_employee_id = ?, comments = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, jobOrder.getWorkOrder().getId());
            stmt.setLong(2, jobOrder.getItem().getId());
            stmt.setString(3, jobOrder.getStatus().name());
            stmt.setLong(4, jobOrder.getResponsibleUser().getId());
            stmt.setString(5, jobOrder.getComments());
            stmt.setLong(6, jobOrder.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM job_orders WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JobOrder mapRow(ResultSet rs) throws SQLException {
        JobOrder jobOrder = new JobOrder();
        jobOrder.setId(rs.getLong("id"));
        jobOrder.setStatus(JobOrderStatus.valueOf(rs.getString("status")));
        jobOrder.setComments(rs.getString("comments"));

        jobOrder.setWorkOrder(null);        // load by ID if needed
        jobOrder.setItem(null);             // load by ID if needed
        jobOrder.setResponsibleUser(null);  // load by ID if needed

        return jobOrder;
    }
}
