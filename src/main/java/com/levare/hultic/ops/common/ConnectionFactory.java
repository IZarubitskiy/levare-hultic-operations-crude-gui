package com.levare.hultic.ops.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private static final String DB_URL = "jdbc:sqlite:" + System.getProperty("user.dir") + "/data.db";
    private static Connection instance;

    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            instance = DriverManager.getConnection(DB_URL);
            System.out.println("🔌 SQLite Connection opened: " + DB_URL);
        } else {
            System.out.println("↩️ Reusing SQLite Connection");
        }
        return instance;
    }
}
