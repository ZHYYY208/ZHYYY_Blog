package com.blog.model;

public class Message {
    private Long id;
    private Long userId;
    private String username;
    private String role;
    private String content;
    private String createdAt;

    public Message() {}

    public Message(Long id, Long userId, String username, String role, String content, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
