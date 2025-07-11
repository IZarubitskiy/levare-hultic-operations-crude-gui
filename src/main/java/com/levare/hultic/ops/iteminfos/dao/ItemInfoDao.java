package com.levare.hultic.ops.iteminfos.dao;

import com.levare.hultic.ops.iteminfos.entity.ItemInfo;
import com.levare.hultic.ops.iteminfos.entity.ItemType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Data Access Object for ItemInfo.
 * Handles basic CRUD operations using SQLite.
 */
@Data
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor
public class ItemInfoDao {

    Connection connection;

    /**
     * Возвращает все записи из таблицы item_info.
     */
    public List<ItemInfo> findAll() {
        List<ItemInfo> items = new ArrayList<>();
        String sql = "SELECT id, part_number, description, item_type, series, comments FROM item_info";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                items.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying item_info", e);
        }

        return items;
    }

    /**
     * Ищет запись по её первичному ключу.
     */
    public ItemInfo findById(long id) {
        String sql = "SELECT id, part_number, description, item_type, series, comments FROM item_info WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying item_info by id=" + id, e);
        }
        return null;
    }

    /**
     * Вставка новой записи.
     */
    public void insert(ItemInfo item) {
        String sql = "INSERT INTO item_info (part_number, description, item_type, comments) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, item.getPartNumber());
            stmt.setString(2, item.getDescription());
            stmt.setString(3, item.getItemType().name());
            stmt.setString(4, item.getComments());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    item.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting ItemInfo", e);
        }
    }

    /**
     * Удаление по id.
     */
    public void deleteById(long id) {
        String sql = "DELETE FROM item_info WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting ItemInfo id=" + id, e);
        }
    }

    /**
     * Обновление существующей записи.
     */
    public void update(ItemInfo item) {
        String sql = "UPDATE item_info SET part_number = ?, description = ?, item_type = ?, comments = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, item.getPartNumber());
            stmt.setString(2, item.getDescription());
            stmt.setString(3, item.getItemType().name());
            stmt.setString(4, item.getComments());
            stmt.setLong(5, item.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating ItemInfo id=" + item.getId(), e);
        }
    }

    /**
     * Преобразует текущую строку ResultSet в объект ItemInfo.
     */
    private ItemInfo mapRow(ResultSet rs) throws SQLException {
        ItemInfo item = new ItemInfo();
        item.setId(rs.getLong("id"));
        item.setPartNumber(rs.getString("part_number"));
        item.setDescription(rs.getString("description"));
        item.setItemType(ItemType.valueOf(rs.getString("item_type").toUpperCase()));
        item.setSeries(rs.getInt("series"));
        item.setComments(rs.getString("comments"));
        return item;
    }
}
