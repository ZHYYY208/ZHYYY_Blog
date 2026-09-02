package com.blog.repository;

import com.blog.model.ShuoShuo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ShuoShuoRepository {

    private final JdbcTemplate jdbc;
    private final ObjectMapper mapper;

    public ShuoShuoRepository(JdbcTemplate jdbc, ObjectMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    private List<String> parseImages(String s) {
        if (s == null || s.isEmpty()) return List.of();
        try {
            return mapper.readValue(s, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    private String toJson(List<String> images) {
        if (images == null || images.isEmpty()) return "[]";
        try {
            return mapper.writeValueAsString(images);
        } catch (Exception e) {
            return "[]";
        }
    }

    public List<ShuoShuo> findAll() {
        return jdbc.query(
                "SELECT id, content, images, created_at FROM shuoshuo ORDER BY id DESC",
                (rs, i) -> new ShuoShuo(
                        rs.getLong("id"),
                        rs.getString("content"),
                        parseImages(rs.getString("images")),
                        rs.getString("created_at")));
    }

    public ShuoShuo save(ShuoShuo s) {
        jdbc.update("INSERT INTO shuoshuo (content, images, created_at) VALUES (?, ?, ?)",
                s.getContent(), toJson(s.getImages()), s.getCreatedAt());
        Long id = jdbc.queryForObject("SELECT last_insert_rowid()", Long.class);
        s.setId(id);
        return s;
    }

    public void delete(Long id) {
        jdbc.update("DELETE FROM shuoshuo WHERE id = ?", id);
    }
}
