package com.blog.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class SiteConfigRepository {

    private final JdbcTemplate jdbc;

    public SiteConfigRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Map<String, String> findAll() {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT key, value FROM site_config");
        return rows.stream().collect(Collectors.toMap(
                r -> (String) r.get("key"),
                r -> (String) r.get("value"),
                (a, b) -> b));
    }

    public void set(String key, String value) {
        jdbc.update("INSERT INTO site_config (key, value) VALUES (?, ?) " +
                "ON CONFLICT(key) DO UPDATE SET value = excluded.value", key, value);
    }
}
