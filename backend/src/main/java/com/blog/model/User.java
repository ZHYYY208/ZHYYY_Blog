package com.blog.model;

public class User {
    private Long id;
    private String username;
    private String passHash;
    private String role;
    private String ip;
    private String createdAt;

    public User() {}

    public User(Long id, String username, String passHash, String role, String ip, String createdAt) {
        this.id = id;
        this.username = username;
        this.passHash = passHash;
        this.role = role;
        this.ip = ip;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassHash() { return passHash; }
    public void setPassHash(String passHash) { this.passHash = passHash; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
