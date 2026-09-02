package com.blog.repository;

import com.blog.model.Post;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostRepository {

    private final JdbcTemplate jdbc;

    public PostRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Post> findAll() {
        return jdbc.query(
                "SELECT id, title, summary, content, created_at FROM posts ORDER BY id DESC",
                (rs, i) -> new Post(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("summary"),
                        rs.getString("content"),
                        rs.getString("created_at")));
    }

    public Post findById(Long id) {
        List<Post> list = jdbc.query(
                "SELECT id, title, summary, content, created_at FROM posts WHERE id = ?",
                (rs, i) -> new Post(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("summary"),
                        rs.getString("content"),
                        rs.getString("created_at")),
                id);
        return list.isEmpty() ? null : list.get(0);
    }

    public Post save(Post p) {
        jdbc.update(
                "INSERT INTO posts (title, summary, content, created_at) VALUES (?, ?, ?, ?)",
                p.getTitle(), p.getSummary(), p.getContent(), p.getCreatedAt());
        Long id = jdbc.queryForObject("SELECT last_insert_rowid()", Long.class);
        p.setId(id);
        return p;
    }

    public void delete(Long id) {
        jdbc.update("DELETE FROM posts WHERE id = ?", id);
    }
}
