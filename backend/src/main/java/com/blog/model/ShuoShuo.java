package com.blog.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ShuoShuo {
    public static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Long id;
    private String content;
    private String createdAt;

    public ShuoShuo() {}

    public ShuoShuo(Long id, String content, String createdAt) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public static String now() {
        return LocalDateTime.now().format(FMT);
    }
}
