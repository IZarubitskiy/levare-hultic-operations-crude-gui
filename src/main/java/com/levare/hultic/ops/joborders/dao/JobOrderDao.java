package com.levare.hultic.ops.joborders.dao;

import com.levare.hultic.ops.joborders.entity.JobOrder;
import com.levare.hultic.ops.joborders.entity.JobOrderStatus;
import com.levare.hultic.ops.joborders.entity.JobOrderType;
import com.levare.hultic.ops.users.dao.UserDao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * DAO for JobOrder, using JDBC and delegating user loading to UserDao.
 */
@Data
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor
public class JobOrderDao {
    Connection connection;
    UserDao userDao;

    /**
     * Retrieve all JobOrders
     */
    public List<JobOrder> findAll() {
        List<JobOrder> result = new ArrayList<>();
        String sql = "SELECT * FROM job_orders";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying job_orders", e);
        }
        return result;
    }

    /**
     * Find JobOrders by status
     */
    public List<JobOrder> findByStatus(JobOrderStatus status) {
        List<JobOrder> result = new ArrayList<>();
        String sql = "SELECT * FROM job_orders WHERE status = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying job_orders by status=" + status, e);
        }
        return result;
    }

    /**
     * Find a JobOrder by ID
     */
    public JobOrder findById(Long id) {
        String sql = "SELECT * FROM job_orders WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying job_orders by id=" + id, e);
        }
        return null;
    }

    /**
     * Insert a new JobOrder
     */
    public JobOrder insert(JobOrder jobOrder) {
        String sql = "INSERT INTO job_orders (work_order_id, item_id, type, status, responsible_user_id, comments) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (jobOrder.getWorkOrderId() != null) {
                stmt.setLong(1, jobOrder.getWorkOrderId());
            } else {
                stmt.setNull(1, java.sql.Types.BIGINT);
            }
            stmt.setLong(2, jobOrder.getItemId());
            stmt.setString(3, jobOrder.getJobOrderType().name());
            stmt.setString(4, jobOrder.getStatus().name());
            stmt.setLong(5, jobOrder.getResponsibleUser().getId());
            stmt.setString(6, jobOrder.getComments());

            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    jobOrder.setId(keys.getLong(1));
                }
            }
            return jobOrder;
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting JobOrder", e);
        }
    }

    /**
     * Update an existing JobOrder
     */
    public void update(JobOrder jobOrder) {
        String sql = "UPDATE job_orders SET work_order_id = ?, item_id = ?, status = ?, " +
                "responsible_user_id = ?, comments = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, jobOrder.getWorkOrderId());
            stmt.setLong(2, jobOrder.getItemId());
            stmt.setString(3, jobOrder.getStatus().name());
            stmt.setLong(4, jobOrder.getResponsibleUser().getId());
            stmt.setString(5, jobOrder.getComments());
            stmt.setLong(6, jobOrder.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating JobOrder id=" + jobOrder.getId(), e);
        }
    }

    /**
     * Delete a JobOrder by ID
     */
    public void deleteById(Long id) {
        String sql = "DELETE FROM job_orders WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting JobOrder id=" + id, e);
        }
    }

    /**
     * Map a ResultSet row to a JobOrder entity
     */
    private JobOrder mapRow(ResultSet rs) throws SQLException {
        JobOrder jobOrder = new JobOrder();
        jobOrder.setId(rs.getLong("id"));
        jobOrder.setWorkOrderId(rs.getLong("work_order_id"));
        jobOrder.setItemId(rs.getLong("item_id"));
        jobOrder.setJobOrderType(JobOrderType.valueOf(rs.getString("type")));
        jobOrder.setStatus(JobOrderStatus.valueOf(rs.getString("status")));

        long userId = rs.getLong("responsible_user_id");
        if (rs.wasNull()) {
            jobOrder.setResponsibleUser(null);
        } else {
            jobOrder.setResponsibleUser(userDao.findById(userId));
        }

        jobOrder.setComments(rs.getString("comments"));
        return jobOrder;
    }
}
