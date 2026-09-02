package com.blog.config;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SchemaMigrator implements ApplicationRunner {

    private final JdbcTemplate jdbc;

    public SchemaMigrator(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private boolean hasColumn(String table, String column) {
        List<Map<String, Object>> cols = jdbc.queryForList("PRAGMA table_info(" + table + ")");
        for (Map<String, Object> c : cols) {
            if (column.equalsIgnoreCase(String.valueOf(c.get("name")))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            if (!hasColumn("shuoshuo", "images")) {
                jdbc.execute("ALTER TABLE shuoshuo ADD COLUMN images TEXT NOT NULL DEFAULT '[]'");
            }
            if (!hasColumn("photos", "category")) {
                jdbc.execute("ALTER TABLE photos ADD COLUMN category TEXT NOT NULL DEFAULT ''");
            }
        } catch (Exception e) {
            // 忽略重复执行等竞态
        }
    }
}
