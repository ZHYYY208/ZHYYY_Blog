package com.blog.repository;

import com.blog.model.Message;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MessageRepository {

    private final JdbcTemplate jdbc;

    public MessageRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Message> findAll() {
        return jdbc.query(
                "SELECT id, user_id, username, content, created_at FROM messages ORDER BY id DESC",
                (rs, i) -> new Message(
                        rs.getLong("id"),
                        rs.getLong("user_id"),
                        rs.getString("username"),
                        rs.getString("content"),
                        rs.getString("created_at")));
    }

    public Message save(Message m) {
        jdbc.update("INSERT INTO messages (user_id, username, content, created_at) VALUES (?, ?, ?, ?)",
                m.getUserId(), m.getUsername(), m.getContent(), m.getCreatedAt());
        Long id = jdbc.queryForObject("SELECT last_insert_rowid()", Long.class);
        m.setId(id);
        return m;
    }

    public void delete(Long id) {
        jdbc.update("DELETE FROM messages WHERE id = ?", id);
    }
}
