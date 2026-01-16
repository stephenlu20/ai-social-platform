package com.aisocial.platform.dto;

import com.aisocial.platform.entity.FactCheckStatus;
import java.time.Instant;
import java.util.UUID;

public class PostResponseDTO {

    private UUID id;
    private UserDTO author;
    private String content;
    private UUID replyToId;
    private UUID repostOfId;
    private PostResponseDTO repostOf;
    private Instant createdAt;
    private Integer likeCount;
    private Integer replyCount;
    private Integer repostCount;
    private FactCheckStatus factCheckStatus;
    private Double factCheckScore;
    private Boolean isLikedByCurrentUser;
    private Boolean isRepostedByCurrentUser;
    private FactCheckResultDTO factCheckResult;

    public PostResponseDTO() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UserDTO getAuthor() { return author; }
    public void setAuthor(UserDTO author) { this.author = author; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public UUID getReplyToId() { return replyToId; }
    public void setReplyToId(UUID replyToId) { this.replyToId = replyToId; }

    public UUID getRepostOfId() { return repostOfId; }
    public void setRepostOfId(UUID repostOfId) { this.repostOfId = repostOfId; }

    public PostResponseDTO getRepostOf() { return repostOf; }
    public void setRepostOf(PostResponseDTO repostOf) { this.repostOf = repostOf; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }

    public Integer getReplyCount() { return replyCount; }
    public void setReplyCount(Integer replyCount) { this.replyCount = replyCount; }

    public Integer getRepostCount() { return repostCount; }
    public void setRepostCount(Integer repostCount) { this.repostCount = repostCount; }

    public FactCheckStatus getFactCheckStatus() { return factCheckStatus; }
    public void setFactCheckStatus(FactCheckStatus factCheckStatus) { this.factCheckStatus = factCheckStatus; }

    public Double getFactCheckScore() { return factCheckScore; }
    public void setFactCheckScore(Double factCheckScore) { this.factCheckScore = factCheckScore; }
    
    public Boolean getIsLikedByCurrentUser() { return isLikedByCurrentUser; }
    public void setIsLikedByCurrentUser(Boolean isLikedByCurrentUser) { this.isLikedByCurrentUser = isLikedByCurrentUser; }

    public Boolean getIsRepostedByCurrentUser() { return isRepostedByCurrentUser; }
    public void setIsRepostedByCurrentUser(Boolean isRepostedByCurrentUser) { this.isRepostedByCurrentUser = isRepostedByCurrentUser; }

    public FactCheckResultDTO getFactCheckResult() { return factCheckResult; }
    public void setFactCheckResult(FactCheckResultDTO factCheckResult) { this.factCheckResult = factCheckResult; }
}