package com.levare.hultic.ops.common;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private static final String DB_FILE = "data.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_FILE;
    private static Connection instance;

    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            instance = DriverManager.getConnection(DB_URL);
            System.out.println("🔌 SQLite Connection opened: " + DB_URL);
            System.out.println("📁 Absolute path: " + new File(DB_FILE).getAbsolutePath());
        } else {
            System.out.println("↩️ Reusing existing SQLite connection");
        }
        return instance;
    }
}
