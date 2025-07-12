package com.levare.hultic.ops.items.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.sql.*;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor
public class SerialNumberDao {
    Connection connection;

    /**
     * Возвращает, какой ID будет у следующей вставки в таблицу serial_numbers.
     */
    public long getNextId() {
        String sql = """
                    SELECT COALESCE(MAX(id), 0) + 1
                      FROM serial_numbers
                """;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            throw new SQLException("Не удалось получить следующий ID");
        } catch (SQLException e) {
            throw new RuntimeException("Error getting next ID", e);
        }
    }

    /**
     * Вставляет новую запись с готовым serialNumber и partNumber.
     */
    public void insert(String serialNumber, String partNumber) {
        String sql = """
                    INSERT INTO serial_numbers (serial_number, part_number)
                    VALUES (?, ?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, serialNumber);
            ps.setString(2, partNumber);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting SerialNumber", e);
        }
    }
}
