package com.levare.hultic.ops.items.dao;

import com.levare.hultic.ops.items.entity.Item;
import com.levare.hultic.ops.items.entity.ItemCondition;
import com.levare.hultic.ops.items.entity.ItemStatus;
import com.levare.hultic.ops.workorders.entity.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Item.
 * Implements filtering logic for SQLite without Spring Data.
 */
public class ItemDao {

    private final Connection connection;

    public ItemDao(Connection connection) {
        this.connection = connection;
    }

    public List<Item> findAll() {
        List<Item> result = new ArrayList<>();
        String sql = "SELECT * FROM items";

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

    public List<Item> findByConditionAndOwnership(ItemCondition condition, Client ownership) {
        List<Item> result = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE item_condition = ? AND ownership = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, condition.name());
            stmt.setString(2, ownership.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<Item> findByConditionsAndStatusAndOwnership(
            List<ItemCondition> conditions,
            ItemStatus status,
            Client ownership
    ) {
        if (conditions.isEmpty()) return List.of();

        StringBuilder sql = new StringBuilder(
                "SELECT * FROM items WHERE item_condition IN ("
        );

        sql.append("?,".repeat(conditions.size()));
        sql.setLength(sql.length() - 1); // remove last comma
        sql.append(") AND item_status = ? AND ownership = ?");

        List<Item> result = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            int i = 1;
            for (ItemCondition condition : conditions) {
                stmt.setString(i++, condition.name());
            }
            stmt.setString(i++, status.name());
            stmt.setString(i, ownership.name());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    private Item mapRow(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setId(rs.getLong("id"));
        item.setClientPartNumber(rs.getString("client_part_number"));
        item.setSerialNumber(rs.getString("serial_number"));
        item.setOwnership(Client.valueOf(rs.getString("ownership")));
        item.setItemCondition(ItemCondition.valueOf(rs.getString("item_condition")));
        item.setItemStatus(ItemStatus.valueOf(rs.getString("item_status")));
        item.setComments(rs.getString("comments"));

        // itemInfo and jobOrder can be loaded via additional query or lazy-loaded
        item.setItemInfo(null); // optional: load by part_number if needed
        item.setJobOrder(null); // optional: load by job_order_id if needed

        return item;
    }

    public void insert(Item item) {
        String sql = "INSERT INTO items (" +
                "item_info_part_number, client_part_number, serial_number, ownership, " +
                "item_condition, item_status, job_order_id, comments" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, item.getItemInfo() != null ? item.getItemInfo().getPartNumber() : null);
            stmt.setString(2, item.getClientPartNumber());
            stmt.setString(3, item.getSerialNumber());
            stmt.setString(4, item.getOwnership().name());
            stmt.setString(5, item.getItemCondition().name());
            stmt.setString(6, item.getItemStatus().name());
            stmt.setObject(7, item.getJobOrder() != null ? item.getJobOrder().getId() : null);
            stmt.setString(8, item.getComments());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    item.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Item item) {
        String sql = "UPDATE items SET " +
                "item_info_part_number = ?, client_part_number = ?, serial_number = ?, ownership = ?, " +
                "item_condition = ?, item_status = ?, job_order_id = ?, comments = ? " +
                "WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, item.getItemInfo() != null ? item.getItemInfo().getPartNumber() : null);
            stmt.setString(2, item.getClientPartNumber());
            stmt.setString(3, item.getSerialNumber());
            stmt.setString(4, item.getOwnership().name());
            stmt.setString(5, item.getItemCondition().name());
            stmt.setString(6, item.getItemStatus().name());
            stmt.setObject(7, item.getJobOrder() != null ? item.getJobOrder().getId() : null);
            stmt.setString(8, item.getComments());
            stmt.setLong(9, item.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM items WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
