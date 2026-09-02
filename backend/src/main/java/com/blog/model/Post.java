package com.blog.model;

public class Post {
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String createdAt;

    public Post() {}

    public Post(Long id, String title, String summary, String content, String createdAt) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
