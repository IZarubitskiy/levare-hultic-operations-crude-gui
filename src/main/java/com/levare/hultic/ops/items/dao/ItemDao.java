package com.levare.hultic.ops.items.dao;

import com.levare.hultic.ops.iteminfos.entity.ItemInfo;
import com.levare.hultic.ops.iteminfos.entity.ItemType;
import com.levare.hultic.ops.items.entity.Item;
import com.levare.hultic.ops.items.entity.ItemCondition;
import com.levare.hultic.ops.items.entity.ItemStatus;
import com.levare.hultic.ops.workorders.entity.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Item.
 * Реализует базовые CRUD и фильтрацию по SQLite.
 */
public class ItemDao {

    private final Connection connection;

    public ItemDao(Connection connection) {
        this.connection = connection;
    }

    public List<Item> findAll() {
        String sql = "SELECT * FROM items";
        return queryItems(sql);
    }

    public List<Item> findByConditionAndOwnership(ItemCondition condition, Client ownership) {
        String sql = "SELECT * FROM items WHERE item_condition = ? AND ownership = ?";
        return queryItems(sql, condition.name(), ownership.name());
    }

    public List<Item> findByConditionsAndStatusAndOwnership(
            List<ItemCondition> conditions,
            ItemStatus status,
            Client ownership
    ) {
        if (conditions.isEmpty()) {
            return List.of();
        }

        StringBuilder sql = new StringBuilder("SELECT * FROM items WHERE item_condition IN (");
        sql.append("?,".repeat(conditions.size()));
        sql.setLength(sql.length() - 1); // убрать последнюю запятую
        sql.append(") AND item_status = ? AND ownership = ?");

        List<String> params = new ArrayList<>();
        for (ItemCondition c : conditions) {
            params.add(c.name());
        }
        params.add(status.name());
        params.add(ownership.name());

        return queryItems(sql.toString(), params.toArray(new String[0]));
    }

    public void insert(Item item) {
        String sql = """
            INSERT INTO items (
                item_info_id,
                client_part_number,
                serial_number,
                ownership,
                item_condition,
                item_status,
                job_order_id,
                comments
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, item.getItemInfo().getId());
            stmt.setString(2, item.getClientPartNumber());
            stmt.setString(3, item.getSerialNumber());
            stmt.setString(4, item.getOwnership().name());
            stmt.setString(5, item.getItemCondition().name());
            stmt.setString(6, item.getItemStatus().name());
            if (item.getJobOrder() != null) {
                stmt.setLong(7, item.getJobOrder().getId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }
            stmt.setString(8, item.getComments());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    item.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting Item", e);
        }
    }

    public void update(Item item) {
        String sql = """
            UPDATE items SET
                item_info_id = ?,
                client_part_number = ?,
                serial_number = ?,
                ownership = ?,
                item_condition = ?,
                item_status = ?,
                job_order_id = ?,
                comments = ?
            WHERE id = ?
            """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, item.getItemInfo().getId());
            stmt.setString(2, item.getClientPartNumber());
            stmt.setString(3, item.getSerialNumber());
            stmt.setString(4, item.getOwnership().name());
            stmt.setString(5, item.getItemCondition().name());
            stmt.setString(6, item.getItemStatus().name());
            if (item.getJobOrder() != null) {
                stmt.setLong(7, item.getJobOrder().getId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }
            stmt.setString(8, item.getComments());
            stmt.setLong(9, item.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating Item", e);
        }
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM items WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting Item", e);
        }
    }

    // ----------------------
    // Вспомогательные методы
    // ----------------------

    private List<Item> queryItems(String sql, String... params) {
        List<Item> list = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setString(i + 1, params[i]);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying Items", e);
        }
        return list;
    }

    private Item mapRow(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setId(rs.getLong("id"));
        // Загрузка ItemInfo по foreign key
        long infoId = rs.getLong("item_info_id");
        item.setItemInfo(loadItemInfoById(infoId));

        item.setClientPartNumber(rs.getString("client_part_number"));
        item.setSerialNumber(rs.getString("serial_number"));
        item.setOwnership(Client.valueOf(rs.getString("ownership")));
        item.setItemCondition(ItemCondition.valueOf(rs.getString("item_condition")));
        item.setItemStatus(ItemStatus.valueOf(rs.getString("item_status")));
        item.setComments(rs.getString("comments"));
        // JobOrder пока не грузим
        item.setJobOrder(null);
        return item;
    }

    private ItemInfo loadItemInfoById(long id) {
        String sql = "SELECT * FROM item_info WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ItemInfo info = new ItemInfo();
                    info.setId(rs.getLong("id"));
                    info.setPartNumber(rs.getString("part_number"));
                    info.setDescription(rs.getString("description"));
                    info.setItemType(ItemType.valueOf(rs.getString("item_type")));  // <-- конвертация строки в enum
                    info.setComments(rs.getString("comments"));
                    return info;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading ItemInfo", e);
        }
        return null;
    }
}
