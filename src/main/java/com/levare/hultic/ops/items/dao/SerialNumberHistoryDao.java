package com.levare.hultic.ops.items.dao;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.sql.*;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor
public class SerialNumberHistoryDao {
    Connection connection;

    public void insert(long itemId,
                       String oldSerial,
                       String newSerial,
                       long changedAtEpoch,
                       String reason,
                       Long changedByUserId) {
        String sql = """
            INSERT INTO serial_number_history
              (item_id, old_serial_number, new_serial_number,
               changed_at, reason, changed_by)
            VALUES (?,?,?,?,?,?)
        """;
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong   (1, itemId);
            st.setString (2, oldSerial);
            st.setString (3, newSerial);
            st.setLong   (4, changedAtEpoch);
            st.setString (5, reason);
            if (changedByUserId != null) st.setLong(6, changedByUserId);
            else                          st.setNull(6, Types.BIGINT);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting serial_number_history", e);
        }
    }

    // можете добавить методы поиска по item_id и т. д.
}