package com.levare.hultic.ops.items.dao;

import com.levare.hultic.ops.items.entity.SerialNumber;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.sql.*;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor
public class SerialNumberRegister {
    Connection connection;

    /**
     * Возвращает, какой IDENTITY присвоит СУБД следующей вставке.
     */
    public long getNextId() {
        // IDENT_CURRENT = текущее максимальное; IDENT_INCR = шаг (обычно 1)
        String sql = """
            SELECT COALESCE(IDENT_CURRENT('serial_numbers'), 0)
                 + IDENT_INCR('serial_numbers')
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
    public void insert(SerialNumber sn) {
        String sql = """
            INSERT INTO serial_numbers (serial_number, part_number)
            VALUES (?, ?)
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, sn.getSerialNumber());
            ps.setString(2, sn.getPartNumber());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting SerialNumber", e);
        }
    }

    /**
     * Удобный метод: вычисляет следующий ID, форматирует SN,
     * сохраняет запись и возвращает объект.
     */
    public SerialNumber create(String partNumber) {
        long id = getNextId();                               // например, 123
        String serial = String.format("SN%06d", id);         // "SN000123"
        SerialNumber sn = new SerialNumber(id, serial, partNumber);
        insert(sn);
        return sn;
    }
}
