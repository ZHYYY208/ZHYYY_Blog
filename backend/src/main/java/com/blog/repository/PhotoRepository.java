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

    public List<Photo> findAll() {
        return jdbc.query(
                "SELECT id, title, url, created_at FROM photos ORDER BY id DESC",
                (rs, i) -> new Photo(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("url"),
                        rs.getString("created_at")));
    }

    public Photo save(Photo photo) {
        jdbc.update("INSERT INTO photos (title, url, created_at) VALUES (?, ?, ?)",
                photo.getTitle(), photo.getUrl(), photo.getCreatedAt());
        Long id = jdbc.queryForObject("SELECT last_insert_rowid()", Long.class);
        photo.setId(id);
        return photo;
    }

    public void delete(Long id) {
        jdbc.update("DELETE FROM photos WHERE id = ?", id);
    }
}
