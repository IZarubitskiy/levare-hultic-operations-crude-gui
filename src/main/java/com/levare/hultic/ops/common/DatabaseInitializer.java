package com.levare.hultic.ops.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseInitializer {

    public static void initialize(Connection connection) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(DatabaseInitializer.class.getResourceAsStream("/db/schema.sql")))
        ) {
            if (reader == null) {
                throw new IllegalStateException("❌ schema.sql not found in /db/");
            }

            String sql = reader.lines().collect(Collectors.joining("\n"));
            System.out.println("📄 SQL to execute:\n" + sql);

            try (Statement stmt = connection.createStatement()) {
                stmt.execute(sql);
                System.out.println("✅ Database initialized.");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Failed to initialize database.");
            e.printStackTrace();
        }
    }
}
