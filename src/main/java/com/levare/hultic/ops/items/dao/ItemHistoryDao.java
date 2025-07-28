package com.levare.hultic.ops.items.dao;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor
public class ItemHistoryDao {
    Connection connection;

    public void insert(
                       String old_item_id,
                       String new_item_id,
                       LocalDate date,
                       String reason) {
        String sql = """
                    INSERT INTO item_history
                      (new_item_id, old_item_id,
                       changed_at, reason)
                    VALUES (?,?,?,?)
                """;
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, old_item_id);
            st.setString(2, new_item_id);
            st.setLong(3, date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
            st.setString(4, reason);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting item_history", e);
        }
    }
}