package com.aisocial.platform.dto;

import java.time.Instant;
import java.util.UUID;

public class PostResponseDTO {

    private UUID id;
    private UUID authorId;
    private String content;
    private UUID replyToId;
    private UUID repostOfId;
    private Instant createdAt;
    private Integer likeCount;
    private Integer replyCount;
    private Integer repostCount;

    public PostResponseDTO() {}

    public PostResponseDTO(UUID id, UUID authorId, String content, UUID replyToId, UUID repostOfId,
                           Instant createdAt, Integer likeCount, Integer replyCount, Integer repostCount) {
        this.id = id;
        this.authorId = authorId;
        this.content = content;
        this.replyToId = replyToId;
        this.repostOfId = repostOfId;
        this.createdAt = createdAt;
        this.likeCount = likeCount;
        this.replyCount = replyCount;
        this.repostCount = repostCount;
    }

    // ---------------------
    // Getters and Setters
    // ---------------------
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getAuthorId() { return authorId; }
    public void setAuthorId(UUID authorId) { this.authorId = authorId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public UUID getReplyToId() { return replyToId; }
    public void setReplyToId(UUID replyToId) { this.replyToId = replyToId; }

    public UUID getRepostOfId() { return repostOfId; }
    public void setRepostOfId(UUID repostOfId) { this.repostOfId = repostOfId; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }

    public Integer getReplyCount() { return replyCount; }
    public void setReplyCount(Integer replyCount) { this.replyCount = replyCount; }

    public Integer getRepostCount() { return repostCount; }
    public void setRepostCount(Integer repostCount) { this.repostCount = repostCount; }
}
