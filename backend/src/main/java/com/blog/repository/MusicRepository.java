package com.blog.repository;

import com.blog.model.MusicTrack;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MusicRepository {

    private final JdbcTemplate jdbc;

    public MusicRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<MusicTrack> findAll() {
        return jdbc.query(
                "SELECT id, title, artist, url, created_at FROM music ORDER BY id DESC",
                (rs, i) -> new MusicTrack(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("url"),
                        rs.getString("created_at")));
    }

    public MusicTrack save(MusicTrack m) {
        jdbc.update("INSERT INTO music (title, artist, url, created_at) VALUES (?, ?, ?, ?)",
                m.getTitle(), m.getArtist(), m.getUrl(), m.getCreatedAt());
        Long id = jdbc.queryForObject("SELECT last_insert_rowid()", Long.class);
        m.setId(id);
        return m;
    }

    public void delete(Long id) {
        jdbc.update("DELETE FROM music WHERE id = ?", id);
    }
}
