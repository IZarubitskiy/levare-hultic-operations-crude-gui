package com.levare.hultic.ops.workorders.dao;

import com.levare.hultic.ops.common.DateUtils;
import com.levare.hultic.ops.users.dao.UserDao;
import com.levare.hultic.ops.workorders.entity.Client;
import com.levare.hultic.ops.workorders.entity.WorkOrder;
import com.levare.hultic.ops.workorders.entity.WorkOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.levare.hultic.ops.common.DateUtils.epochToLocalDate;
import static lombok.AccessLevel.PRIVATE;

/**
 * DAO for WorkOrder, delegating related entity loading to DAOs.
 */
@Data
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor
public class WorkOrderDao {
    Connection connection;
    UserDao userDao;

    public List<WorkOrder> findAll() {
        List<WorkOrder> list = new ArrayList<>();
        String sql = "SELECT * FROM work_orders";
        try (PreparedStatement st = connection.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying work_orders", e);
        }
        return list;
    }

    public WorkOrder findById(Long id) {
        String sql = "SELECT * FROM work_orders WHERE id = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying work_orders by id=" + id, e);
        }
        return null;
    }

    public List<WorkOrder> findByStatus(WorkOrderStatus status) {
        List<WorkOrder> list = new ArrayList<>();
        String sql = "SELECT * FROM work_orders WHERE status = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, status.name());
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying work_orders by status=" + status, e);
        }
        return list;
    }

    private WorkOrder mapRow(ResultSet rs) throws SQLException {
        WorkOrder order = WorkOrder.builder()
                .id(rs.getLong("id"))
                .workOrderNumber(rs.getString("work_order_number"))
                .client(Client.valueOf(rs.getString("client")))
                .well(rs.getString("well"))
                .requestDate(epochToLocalDate(rs.getLong("request_date")))
                .deliveryDate(epochToLocalDate(rs.getLong("delivery_date")))
                .status(WorkOrderStatus.valueOf(rs.getString("status")))
                .comments(rs.getString("comments"))
                .build();

        long reqId = rs.getLong("requestor_id");
        if (!rs.wasNull()) {
            order.setRequestor(userDao.findById(reqId));
        }

        // Load associated item IDs
        Set<Long> ids = new HashSet<>(findItemIdsByWorkOrderId(order.getId()));
        order.setItemsId(ids);

        return order;
    }

    private List<Long> findItemIdsByWorkOrderId(long workOrderId) {
        String sql = "SELECT item_id FROM work_order_items WHERE work_order_id = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, workOrderId);
            try (ResultSet rs = st.executeQuery()) {
                List<Long> ids = new ArrayList<>();
                while (rs.next()) {
                    ids.add(rs.getLong("item_id"));
                }
                return ids;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading item IDs for WorkOrder id=" + workOrderId, e);
        }
    }

    public WorkOrder insert(WorkOrder order) {
        String sql = """
                INSERT INTO work_orders
                  (work_order_number, client, well,
                   request_date, delivery_date, status,
                   requestor_id, comments)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement st = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, order.getWorkOrderNumber());
            st.setString(2, order.getClient().name());
            st.setString(3, order.getWell());
            st.setLong(4, order.getRequestDate()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
            st.setLong(5, order.getDeliveryDate()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
            st.setString(6, order.getStatus().name());
            st.setObject(7, order.getRequestor() != null ? order.getRequestor().getId() : null);
            st.setString(8, order.getComments());
            st.executeUpdate();
            try (ResultSet keys = st.getGeneratedKeys()) {
                if (keys.next()) {
                    order.setId(keys.getLong(1));
                }
            }
            // Insert associations in join table
            saveItemLinks(order);
            return order;
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting WorkOrder", e);
        }
    }

    public WorkOrder update(WorkOrder order) {
        String sql = """
                UPDATE work_orders SET
                  work_order_number = ?,
                  client            = ?,
                  well              = ?,
                  delivery_date     = ?,
                  status            = ?,
                  requestor_id      = ?,
                  comments          = ?
                WHERE id = ?
                """;
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, order.getWorkOrderNumber());
            st.setString(2, order.getClient().name());
            st.setString(3, order.getWell());
            st.setLong(4, order.getDeliveryDate()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
            st.setString(5, order.getStatus().name());
            st.setObject(6, order.getRequestor() != null ? order.getRequestor().getId() : null);
            st.setString(7, order.getComments());
            st.setLong(8, order.getId());
            st.executeUpdate();

            // Refresh join table links
            clearItemLinks(order.getId());
            saveItemLinks(order);
            return order;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating WorkOrder id=" + order.getId(), e);
        }
    }

    private void clearItemLinks(Long workOrderId) throws SQLException {
        String sql = "DELETE FROM work_order_items WHERE work_order_id = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, workOrderId);
            st.executeUpdate();
        }
    }

    private void saveItemLinks(WorkOrder order) throws SQLException {
        String sql = "INSERT INTO work_order_items (work_order_id, item_id) VALUES (?, ?)";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            for (Long itemId : order.getItemsId()) {
                st.setLong(1, order.getId());
                st.setLong(2, itemId);
                st.addBatch();
            }
            st.executeBatch();
        }
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM work_orders WHERE id = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, id);
            st.executeUpdate();
            clearItemLinks(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting WorkOrder id=" + id, e);
        }
    }
}
