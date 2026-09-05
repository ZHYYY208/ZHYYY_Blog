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

    private static final String COLS = "id, username, pass_hash, role, ip, created_at";

    private User map(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("pass_hash"),
                rs.getString("role"),
                rs.getString("ip"),
                rs.getString("created_at"));
    }

    public List<User> findAll() {
        return jdbc.query("SELECT " + COLS + " FROM users ORDER BY id DESC", (rs, i) -> map(rs));
    }

    public User findByUsername(String username) {
        List<User> l = jdbc.query(
                "SELECT " + COLS + " FROM users WHERE username = ?", (rs, i) -> map(rs), username);
        return l.isEmpty() ? null : l.get(0);
    }

    public User findById(Long id) {
        List<User> l = jdbc.query(
                "SELECT " + COLS + " FROM users WHERE id = ?", (rs, i) -> map(rs), id);
        return l.isEmpty() ? null : l.get(0);
    }

    public User save(User u) {
        jdbc.update("INSERT INTO users (username, pass_hash, role, ip, created_at) VALUES (?, ?, ?, ?, ?)",
                u.getUsername(), u.getPassHash(),
                u.getRole() == null ? "user" : u.getRole(),
                u.getIp() == null ? "" : u.getIp(),
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

    public int deleteNonOwner() {
        return jdbc.update("DELETE FROM users WHERE role <> 'owner'");
    }

    // 某 IP 在过去 hourHours 小时内注册数量
    public int countByIpSince(String ip, String since) {
        Integer c = jdbc.queryForObject(
                "SELECT COUNT(*) FROM users WHERE ip = ? AND created_at >= ?",
                Integer.class, ip, since);
        return c == null ? 0 : c;
    }

    // 全站从某时间起的注册数量
    public int countSince(String since) {
        Integer c = jdbc.queryForObject(
                "SELECT COUNT(*) FROM users WHERE created_at >= ?", Integer.class, since);
        return c == null ? 0 : c;
    }
}
