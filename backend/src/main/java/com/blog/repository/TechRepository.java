package com.blog.repository;

import com.blog.model.TechNode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TechRepository {

    private final JdbcTemplate jdbc;

    public TechRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<TechNode> findAll() {
        return jdbc.query(
                "SELECT id, parent_id, name, color, created_at FROM tech_stack ORDER BY id",
                (rs, i) -> new TechNode(
                        rs.getLong("id"),
                        rs.getLong("parent_id"),
                        rs.getString("name"),
                        rs.getString("color"),
                        rs.getString("created_at")));
    }

    public TechNode save(TechNode n) {
        jdbc.update("INSERT INTO tech_stack (parent_id, name, color, created_at) VALUES (?, ?, ?, ?)",
                n.getParentId(), n.getName(), n.getColor(), n.getCreatedAt());
        Long id = jdbc.queryForObject("SELECT last_insert_rowid()", Long.class);
        n.setId(id);
        return n;
    }

    public void delete(Long id) {
        jdbc.update("DELETE FROM tech_stack WHERE id = ? OR parent_id = ?", id, id);
    }
}
