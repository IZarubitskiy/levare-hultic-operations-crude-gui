package com.levare.hultic.ops.iteminfos.dao;

import com.levare.hultic.ops.iteminfos.entity.ItemInfo;
import com.levare.hultic.ops.iteminfos.entity.ItemType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for ItemInfo.
 * Handles basic CRUD operations using SQLite.
 */
public class ItemInfoDao {

    private final Connection connection;

    public ItemInfoDao(Connection connection) {
        this.connection = connection;
    }

    public List<ItemInfo> findAll() {
        List<ItemInfo> items = new ArrayList<>();
        String sql = "SELECT id, part_number, description, item_type, comments FROM item_info";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ItemInfo item = new ItemInfo();
                item.setId(rs.getLong("id"));
                item.setPartNumber(rs.getString("part_number"));
                item.setDescription(rs.getString("description"));
                item.setItemType(ItemType.valueOf(rs.getString("item_type")));
                item.setComments(rs.getString("comments"));
                // analogList will be handled separately
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

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
            e.printStackTrace();
        }
    }

    public void deleteById(long id) {
        String sql = "DELETE FROM item_info WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
            e.printStackTrace();
        }
    }
}
