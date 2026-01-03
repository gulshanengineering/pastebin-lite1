package com.kodnest.pastebin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "pastes")
public class Paste {

    @Id
    @Column(length = 50)
    private String id;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(name = "created_at_ms", nullable = false)
    private Long createdAtMs;

    @Column(name = "expires_at_ms")
    private Long expiresAtMs;

    @Column(name = "max_views")
    private Integer maxViews;

    @Column(nullable = false)
    private Integer views;

    public Paste() {
    }

    public Paste(String id, String content, Long createdAtMs,
                 Long expiresAtMs, Integer maxViews, Integer views) {
        this.id = id;
        this.content = content;
        this.createdAtMs = createdAtMs;
        this.expiresAtMs = expiresAtMs;
        this.maxViews = maxViews;
        this.views = views;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getCreatedAtMs() { return createdAtMs; }
    public void setCreatedAtMs(Long createdAtMs) { this.createdAtMs = createdAtMs; }

    public Long getExpiresAtMs() { return expiresAtMs; }
    public void setExpiresAtMs(Long expiresAtMs) { this.expiresAtMs = expiresAtMs; }

    public Integer getMaxViews() { return maxViews; }
    public void setMaxViews(Integer maxViews) { this.maxViews = maxViews; }

    public Integer getViews() { return views; }
    public void setViews(Integer views) { this.views = views; }
}
