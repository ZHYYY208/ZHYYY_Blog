package com.blog.model;

import java.util.List;

public class ShuoShuo {
    private Long id;
    private String content;
    private List<String> images;
    private String createdAt;

    public ShuoShuo() {}

    public ShuoShuo(Long id, String content, List<String> images, String createdAt) {
        this.id = id;
        this.content = content;
        this.images = images;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
