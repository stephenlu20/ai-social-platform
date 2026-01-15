package com.aisocial.platform.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(length = 280)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to_id")
    private Post replyTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repost_of_id")
    private Post repostOf;

    // Optional JSON for style/metadata
    @Column(columnDefinition = "TEXT")
    private String style;

    @Enumerated(EnumType.STRING)
    @Column(name = "fact_check_status")
    private FactCheckStatus factCheckStatus = FactCheckStatus.UNCHECKED;

    @Column(name = "fact_check_score")
    private Double factCheckScore;

    @Column(name = "fact_check_data", columnDefinition = "TEXT")
    private String factCheckData;

    @Column(name = "was_checked_before")
    private Boolean wasCheckedBefore = false;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "reply_count")
    private Integer replyCount = 0;

    @Column(name = "repost_count")
    private Integer repostCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Post() {}

    public Post(User author, String content) {
        this.author = author;
        this.content = content;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (likeCount == null) likeCount = 0;
        if (replyCount == null) replyCount = 0;
        if (repostCount == null) repostCount = 0;
        if (wasCheckedBefore == null) wasCheckedBefore = false;
        if (factCheckStatus == null) factCheckStatus = FactCheckStatus.UNCHECKED;
    }

    // -------------------------
    // Increment helpers
    // -------------------------
    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void incrementReplyCount() {
        this.replyCount++;
    }

    public void incrementRepostCount() {
        this.repostCount++;
    }

    // -------------------------
    // Getters and Setters
    // -------------------------
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Post getReplyTo() { return replyTo; }
    public void setReplyTo(Post replyTo) { this.replyTo = replyTo; }

    public Post getRepostOf() { return repostOf; }
    public void setRepostOf(Post repostOf) { this.repostOf = repostOf; }

    public String getStyle() { return style; }
    public void setStyle(String style) { this.style = style; }

    public FactCheckStatus getFactCheckStatus() { return factCheckStatus; }
    public void setFactCheckStatus(FactCheckStatus factCheckStatus) { this.factCheckStatus = factCheckStatus; }

    public Double getFactCheckScore() { return factCheckScore; }
    public void setFactCheckScore(Double factCheckScore) { this.factCheckScore = factCheckScore; }

    public String getFactCheckData() { return factCheckData; }
    public void setFactCheckData(String factCheckData) { this.factCheckData = factCheckData; }

    public Boolean getWasCheckedBefore() { return wasCheckedBefore; }
    public void setWasCheckedBefore(Boolean wasCheckedBefore) { this.wasCheckedBefore = wasCheckedBefore; }

    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }

    public Integer getReplyCount() { return replyCount; }
    public void setReplyCount(Integer replyCount) { this.replyCount = replyCount; }

    public Integer getRepostCount() { return repostCount; }
    public void setRepostCount(Integer repostCount) { this.repostCount = repostCount; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
