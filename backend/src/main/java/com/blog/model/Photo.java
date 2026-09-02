package com.blog.model;

public class Photo {
    private Long id;
    private String title;
    private String category;
    private String url;
    private String createdAt;

    public Photo() {}

    public Photo(Long id, String title, String category, String url, String createdAt) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.url = url;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
