package com.levare.hultic.ops.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseInitializer {

    public static void initialize(Connection connection) {
        try {
            InputStream in = DatabaseInitializer.class.getResourceAsStream("/db/schema.sql");

            if (in == null) {
                throw new IllegalStateException("❌ schema.sql not found in classpath under /db/");
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String fullSql = reader.lines().collect(Collectors.joining("\n"));
                System.out.println("📄 SQL to execute:\n" + fullSql);

                // 🔪 Разбиваем по ;
                String[] statements = fullSql.split("(?<=;)\n");

                try (Statement stmt = connection.createStatement()) {
                    for (String sql : statements) {
                        sql = sql.trim();
                        if (!sql.isEmpty()) {
                            stmt.execute(sql);
                        }
                    }
                    System.out.println("✅ Database initialized.");
                }
            }

        } catch (Exception e) {
            System.err.println("⚠️ Failed to initialize database.");
            e.printStackTrace();
        }
    }
}
