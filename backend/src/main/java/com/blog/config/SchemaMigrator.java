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
            // 旧版 photos.category(文本) → photo_categories 表 + category_id
            if (!hasColumn("photos", "category_id")) {
                jdbc.execute("ALTER TABLE photos ADD COLUMN category_id INTEGER");
            }
            migrateLegacyPhotoCategory();
        } catch (Exception e) {
            // 忽略重复执行等竞态
        }
    }

    private void migrateLegacyPhotoCategory() {
        boolean hasOld = hasColumn("photos", "category");
        if (!hasOld) {
            return;
        }
        // 为已有非空 category 文本建立/关联 photo_categories
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id, category FROM photos WHERE category IS NOT NULL AND category <> ''");
        for (Map<String, Object> row : rows) {
            Long pid = ((Number) row.get("id")).longValue();
            String name = (String) row.get("category");
            Long cid = ensureCategory(name);
            if (cid != null) {
                jdbc.update("UPDATE photos SET category_id = ? WHERE id = ?", cid, pid);
            }
        }
    }

    private Long ensureCategory(String name) {
        List<Long> l = jdbc.queryForList(
                "SELECT id FROM photo_categories WHERE name = ?", Long.class, name);
        if (!l.isEmpty()) {
            return l.get(0);
        }
        jdbc.update("INSERT INTO photo_categories (name, created_at) VALUES (?, datetime('now','localtime'))",
                name);
        List<Long> created = jdbc.queryForList(
                "SELECT id FROM photo_categories WHERE name = ?", Long.class, name);
        return created.isEmpty() ? null : created.get(0);
    }
}
