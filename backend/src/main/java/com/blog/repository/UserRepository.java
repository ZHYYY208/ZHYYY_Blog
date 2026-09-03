package com.blog.repository;

import com.blog.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbc;

    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<User> findAll() {
        return jdbc.query(
                "SELECT id, username, pass_hash, created_at FROM users ORDER BY id DESC",
                (rs, i) -> new User(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("pass_hash"),
                        rs.getString("created_at")));
    }

    public User findByUsername(String username) {
        List<User> l = jdbc.query(
                "SELECT id, username, pass_hash, created_at FROM users WHERE username = ?",
                (rs, i) -> new User(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("pass_hash"),
                        rs.getString("created_at")),
                username);
        return l.isEmpty() ? null : l.get(0);
    }

    public User findById(Long id) {
        List<User> l = jdbc.query(
                "SELECT id, username, pass_hash, created_at FROM users WHERE id = ?",
                (rs, i) -> new User(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("pass_hash"),
                        rs.getString("created_at")),
                id);
        return l.isEmpty() ? null : l.get(0);
    }

    public User save(User u) {
        jdbc.update("INSERT INTO users (username, pass_hash, created_at) VALUES (?, ?, ?)",
                u.getUsername(), u.getPassHash(), u.getCreatedAt());
        Long id = jdbc.queryForObject("SELECT last_insert_rowid()", Long.class);
        u.setId(id);
        return u;
    }

    public void updatePassword(Long id, String passHash) {
        jdbc.update("UPDATE users SET pass_hash = ? WHERE id = ?", passHash, id);
    }

    public void delete(Long id) {
        jdbc.update("DELETE FROM users WHERE id = ?", id);
    }
}
