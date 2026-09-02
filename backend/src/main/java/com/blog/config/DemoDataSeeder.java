package com.blog.config;

import com.blog.util.TimeUtil;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DemoDataSeeder implements ApplicationRunner {

    private final JdbcTemplate jdbc;

    public DemoDataSeeder(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(ApplicationArguments args) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM music", Integer.class);
        if (count != null && count > 0) {
            return;
        }
        String now = TimeUtil.now();
        jdbc.update("INSERT INTO music (title, artist, url, created_at) VALUES (?, ?, ?, ?)",
                "SoundHelix Song 1", "SoundHelix", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3", now);
        jdbc.update("INSERT INTO music (title, artist, url, created_at) VALUES (?, ?, ?, ?)",
                "SoundHelix Song 2", "SoundHelix", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3", now);
        jdbc.update("INSERT INTO music (title, artist, url, created_at) VALUES (?, ?, ?, ?)",
                "SoundHelix Song 3", "SoundHelix", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3", now);
    }
}
