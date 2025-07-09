package com.levare.hultic.ops.items.dao;

import com.levare.hultic.ops.iteminfos.dao.ItemInfoDao;
import com.levare.hultic.ops.items.entity.Item;
import com.levare.hultic.ops.items.entity.ItemCondition;
import com.levare.hultic.ops.items.entity.ItemStatus;
import com.levare.hultic.ops.workorders.entity.Client;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * DAO для Item, делегирует загрузку ItemInfo внешнему DAO.
 */
@Data
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor
public class ItemDao {
    Connection connection;
    ItemInfoDao itemInfoDao;

    public List<Item> findAll() {
        return queryItems("SELECT * FROM items");
    }

    public List<Item> findByConditionAndOwnership(ItemCondition condition, Client ownership) {
        return queryItems(
                "SELECT * FROM items WHERE item_condition = ? AND ownership = ?",
                condition.name(), ownership.name()
        );
    }

    public List<Item> findByConditionsAndStatusAndOwnership(
            List<ItemCondition> conditions,
            ItemStatus status,
            Client ownership
    ) {
        if (conditions.isEmpty()) return List.of();

        StringBuilder sql = new StringBuilder("SELECT * FROM items WHERE item_condition IN (");
        sql.append("?,".repeat(conditions.size()));
        sql.setLength(sql.length() - 1);
        sql.append(") AND item_status = ? AND ownership = ?");

        var params = new ArrayList<String>();
        conditions.forEach(c -> params.add(c.name()));
        params.add(status.name());
        params.add(ownership.name());

        return queryItems(sql.toString(), params.toArray(new String[0]));
    }

    public void insert(Item item) {
        String sql = """
                INSERT INTO items (
                    item_info_id,
                    serial_number,
                    ownership,
                    item_condition,
                    item_status,
                    job_order_id,
                    comments
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, item.getItemInfo().getId());
            stmt.setString(2, item.getSerialNumber());
            stmt.setString(3, item.getOwnership().name());
            stmt.setString(4, item.getItemCondition().name());
            stmt.setString(5, item.getItemStatus().name());
            if (item.getJobOrderId() != null) {
                stmt.setLong(6, item.getJobOrderId());
            } else {
                stmt.setNull(6, Types.BIGINT);
            }
            stmt.setString(7, item.getComments());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) item.setId(keys.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting Item", e);
        }
    }

    public void update(Item item) {
        String sql = """
                UPDATE items SET
                    item_info_id   = ?,
                    serial_number  = ?,
                    ownership      = ?,
                    item_condition = ?,
                    item_status    = ?,
                    job_order_id   = ?,
                    comments       = ?
                WHERE id = ?
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, item.getItemInfo().getId());
            stmt.setString(2, item.getSerialNumber());
            stmt.setString(3, item.getOwnership().name());
            stmt.setString(4, item.getItemCondition().name());
            stmt.setString(5, item.getItemStatus().name());
            if (item.getJobOrderId() != null) {
                stmt.setLong(6, item.getJobOrderId());
            } else {
                stmt.setNull(6, Types.BIGINT);
            }
            stmt.setString(7, item.getComments());
            stmt.setLong(8, item.getId());
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

        // Загрузка ItemInfo через DAO
        long infoId = rs.getLong("item_info_id");
        item.setItemInfo(itemInfoDao.findById(infoId));

        item.setSerialNumber(rs.getString("serial_number"));
        item.setOwnership(Client.valueOf(rs.getString("ownership")));
        item.setItemCondition(ItemCondition.valueOf(rs.getString("item_condition")));
        item.setItemStatus(ItemStatus.valueOf(rs.getString("item_status")));

        long joId = rs.getLong("job_order_id");
        if (rs.wasNull()) {
            item.setJobOrderId(null);
        } else {
            item.setJobOrderId(joId);
        }

        item.setComments(rs.getString("comments"));
        return item;
    }
}
