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

    private User map(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("pass_hash"),
                rs.getString("role"),
                rs.getString("created_at"));
    }

    public List<User> findAll() {
        return jdbc.query("SELECT id, username, pass_hash, role, created_at FROM users ORDER BY id DESC",
                (rs, i) -> map(rs));
    }

    public User findByUsername(String username) {
        List<User> l = jdbc.query(
                "SELECT id, username, pass_hash, role, created_at FROM users WHERE username = ?",
                (rs, i) -> map(rs), username);
        return l.isEmpty() ? null : l.get(0);
    }

    public User findById(Long id) {
        List<User> l = jdbc.query(
                "SELECT id, username, pass_hash, role, created_at FROM users WHERE id = ?",
                (rs, i) -> map(rs), id);
        return l.isEmpty() ? null : l.get(0);
    }

    public User save(User u) {
        jdbc.update("INSERT INTO users (username, pass_hash, role, created_at) VALUES (?, ?, ?, ?)",
                u.getUsername(), u.getPassHash(),
                u.getRole() == null ? "user" : u.getRole(),
                u.getCreatedAt());
        Long id = jdbc.queryForObject("SELECT last_insert_rowid()", Long.class);
        u.setId(id);
        return u;
    }

    public void updatePassword(Long id, String passHash) {
        jdbc.update("UPDATE users SET pass_hash = ? WHERE id = ?", passHash, id);
    }

    public void updateProfile(Long id, String username, String role) {
        jdbc.update("UPDATE users SET username = ?, role = ? WHERE id = ?", username, role, id);
    }

    public void delete(Long id) {
        jdbc.update("DELETE FROM users WHERE id = ?", id);
    }
}
