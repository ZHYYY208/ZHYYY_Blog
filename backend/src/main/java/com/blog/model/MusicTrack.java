package com.blog.model;

public class MusicTrack {
    private Long id;
    private String title;
    private String artist;
    private String url;
    private String createdAt;

    public MusicTrack() {}

    public MusicTrack(Long id, String title, String artist, String url, String createdAt) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.url = url;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
