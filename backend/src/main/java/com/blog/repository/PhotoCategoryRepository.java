package com.blog.repository;

import com.blog.model.PhotoCategory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PhotoCategoryRepository {

    private final JdbcTemplate jdbc;

    public PhotoCategoryRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<PhotoCategory> findAll() {
        return jdbc.query(
                "SELECT id, name, created_at FROM photo_categories ORDER BY id",
                (rs, i) -> new PhotoCategory(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("created_at")));
    }

    public PhotoCategory findByName(String name) {
        List<PhotoCategory> l = jdbc.query(
                "SELECT id, name, created_at FROM photo_categories WHERE name = ?",
                (rs, i) -> new PhotoCategory(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("created_at")),
                name);
        return l.isEmpty() ? null : l.get(0);
    }

    public PhotoCategory save(PhotoCategory c) {
        jdbc.update("INSERT INTO photo_categories (name, created_at) VALUES (?, ?)",
                c.getName(), c.getCreatedAt());
        Long id = jdbc.queryForObject("SELECT last_insert_rowid()", Long.class);
        c.setId(id);
        return c;
    }

    public void delete(Long id) {
        jdbc.update("DELETE FROM photo_categories WHERE id = ?", id);
    }
}
