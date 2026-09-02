package com.blog.model;

public class TechNode {
    private Long id;
    private Long parentId;
    private String name;
    private String color;
    private String createdAt;

    public TechNode() {}

    public TechNode(Long id, Long parentId, String name, String color, String createdAt) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.color = color;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
