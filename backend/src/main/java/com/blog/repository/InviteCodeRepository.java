package com.blog.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class InviteCodeRepository {

    private final JdbcTemplate jdbc;

    public InviteCodeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> list() {
        return jdbc.queryForList(
                "SELECT id, code, used, created_at, used_at FROM invite_codes ORDER BY id DESC");
    }

    public int countAvailable() {
        Integer c = jdbc.queryForObject(
                "SELECT COUNT(*) FROM invite_codes WHERE used = 0", Integer.class);
        return c == null ? 0 : c;
    }

    public void insert(String code, String now) {
        jdbc.update("INSERT INTO invite_codes (code, used, created_at) VALUES (?, 0, ?)", code, now);
    }

    // 校验并占用一个邀请码；返回是否成功
    public boolean consume(String code, String usedAt) {
        int n = jdbc.update(
                "UPDATE invite_codes SET used = 1, used_at = ? WHERE code = ? AND used = 0", usedAt, code);
        return n > 0;
    }
}
