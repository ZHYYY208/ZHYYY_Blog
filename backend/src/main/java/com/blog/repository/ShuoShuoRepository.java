package com.blog.repository;

import com.blog.model.ShuoShuo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ShuoShuoRepository {

    private final JdbcTemplate jdbc;

    public ShuoShuoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<ShuoShuo> findAll() {
        return jdbc.query(
                "SELECT id, content, created_at FROM shuoshuo ORDER BY id DESC",
                (rs, i) -> new ShuoShuo(
                        rs.getLong("id"),
                        rs.getString("content"),
                        rs.getString("created_at")));
    }

    public ShuoShuo save(ShuoShuo s) {
        jdbc.update("INSERT INTO shuoshuo (content, created_at) VALUES (?, ?)",
                s.getContent(), s.getCreatedAt());
        Long id = jdbc.queryForObject("SELECT last_insert_rowid()", Long.class);
        s.setId(id);
        return s;
    }

    public void delete(Long id) {
        jdbc.update("DELETE FROM shuoshuo WHERE id = ?", id);
    }
}
