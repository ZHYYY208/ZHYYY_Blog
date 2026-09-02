package com.blog.repository;

import com.blog.model.Photo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PhotoRepository {

    private final JdbcTemplate jdbc;

    public PhotoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final String SELECT =
            "SELECT p.id, p.title, p.category_id, pc.name AS category_name, p.url, p.created_at " +
            "FROM photos p LEFT JOIN photo_categories pc ON p.category_id = pc.id ";

    private Photo mapRow(java.sql.ResultSet rs) throws java.sql.SQLException {
        long cid = rs.getLong("category_id");
        Long catId = rs.wasNull() ? null : cid;
        return new Photo(
                rs.getLong("id"),
                rs.getString("title"),
                catId,
                rs.getString("category_name"),
                rs.getString("url"),
                rs.getString("created_at"));
    }

    public List<Photo> findAll() {
        return jdbc.query(SELECT + " ORDER BY p.id DESC", (rs, i) -> mapRow(rs));
    }

    public List<Photo> findByCategoryId(Long categoryId) {
        return jdbc.query(SELECT + " WHERE p.category_id = ? ORDER BY p.id DESC",
                (rs, i) -> mapRow(rs), categoryId);
    }

    public List<Photo> findByNullCategory() {
        return jdbc.query(SELECT + " WHERE p.category_id IS NULL ORDER BY p.id DESC",
                (rs, i) -> mapRow(rs));
    }

    public Photo save(Photo photo) {
        jdbc.update("INSERT INTO photos (title, category_id, url, created_at) VALUES (?, ?, ?, ?)",
                photo.getTitle(),
                photo.getCategoryId(),
                photo.getUrl(),
                photo.getCreatedAt());
        Long id = jdbc.queryForObject("SELECT last_insert_rowid()", Long.class);
        photo.setId(id);
        return photo;
    }

    public void setCategory(Long id, Long categoryId) {
        jdbc.update("UPDATE photos SET category_id = ? WHERE id = ?", categoryId, id);
    }

    public void delete(Long id) {
        jdbc.update("DELETE FROM photos WHERE id = ?", id);
    }
}
