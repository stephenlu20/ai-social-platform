package com.aisocial.platform.dto;

import com.aisocial.platform.entity.User;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class UserDTO {

    private UUID id;
    private String username;
    private String displayName;
    private String bio;
    private String avatarUrl;
    private BigDecimal trustScore;
    private Integer postsFactChecked;
    private Integer postsVerified;
    private Integer postsFalse;
    private Integer debatesWon;
    private Integer debatesLost;
    private Instant createdAt;

    private Long followerCount;
    private Long followingCount;
    private Long postCount;
    private Boolean isFollowing;

    public UserDTO() {
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.displayName = user.getDisplayName();
        this.bio = user.getBio();
        this.avatarUrl = user.getAvatarUrl();
        this.trustScore = user.getTrustScore();
        this.postsFactChecked = user.getPostsFactChecked();
        this.postsVerified = user.getPostsVerified();
        this.postsFalse = user.getPostsFalse();
        this.debatesWon = user.getDebatesWon();
        this.debatesLost = user.getDebatesLost();
        this.createdAt = user.getCreatedAt();
    }

    public static UserDTO fromEntity(User user) {
        return new UserDTO(user);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public BigDecimal getTrustScore() {
        return trustScore;
    }

    public void setTrustScore(BigDecimal trustScore) {
        this.trustScore = trustScore;
    }

    public Integer getPostsFactChecked() {
        return postsFactChecked;
    }

    public void setPostsFactChecked(Integer postsFactChecked) {
        this.postsFactChecked = postsFactChecked;
    }

    public Integer getPostsVerified() {
        return postsVerified;
    }

    public void setPostsVerified(Integer postsVerified) {
        this.postsVerified = postsVerified;
    }

    public Integer getPostsFalse() {
        return postsFalse;
    }

    public void setPostsFalse(Integer postsFalse) {
        this.postsFalse = postsFalse;
    }

    public Integer getDebatesWon() {
        return debatesWon;
    }

    public void setDebatesWon(Integer debatesWon) {
        this.debatesWon = debatesWon;
    }

    public Integer getDebatesLost() {
        return debatesLost;
    }

    public void setDebatesLost(Integer debatesLost) {
        this.debatesLost = debatesLost;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Long getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(Long followerCount) {
        this.followerCount = followerCount;
    }

    public Long getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(Long followingCount) {
        this.followingCount = followingCount;
    }

    public Long getPostCount() {
        return postCount;
    }

    public void setPostCount(Long postCount) {
        this.postCount = postCount;
    }

    public Boolean getIsFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(Boolean isFollowing) {
        this.isFollowing = isFollowing;
    }
}