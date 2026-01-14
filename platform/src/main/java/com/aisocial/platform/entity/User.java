package com.aisocial.platform.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String username;

    @Column(name = "display_name", length = 50)
    private String displayName;

    @Column(length = 160)
    private String bio;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "trust_score", precision = 5, scale = 2)
    private BigDecimal trustScore = new BigDecimal("50.00");

    @Column(name = "posts_fact_checked")
    private Integer postsFactChecked = 0;

    @Column(name = "posts_verified")
    private Integer postsVerified = 0;
    
    @Column(name = "posts_false")
    private  Integer postsFalse = 0;

    @Column(name = "debates_won")
    private Integer debatesWon = 0;

    @Column(name = "debate_lost")
    private Integer debatesLost = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public User() {
    }

    public User(String username, String displayName, String bio) {
        this.username = username;
        this.displayName = displayName;
        this.bio = bio;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (trustScore == null) {
            trustScore = new BigDecimal("50.00");
        }
    }

    /**
     * Calculates the user's trust score based on their fact-check history.
     * Formula:
     * - Base score: 50
     * - Verified posts: +2 each (max +30)
     * - False posts: -5 each (no min)
     * - Final score clamped to 0-100
     */
    public BigDecimal calculateTrustScore() {
        double score = 50.0;

        // Verified posts bonus (capped at +30)
        double verifiedBonus = Math.min(postsVerified * 2.0, 30.0);
        score += verifiedBonus;

        // False posts penalty (no cap)
        score -= postsFalse * 5.0;

        // Clamp to 0-100
        score = Math.max(0.0, Math.min(100.0, score));

        this.trustScore = BigDecimal.valueOf(score).setScale(2, java.math.RoundingMode.HALF_UP);
        return this.trustScore;
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

    public void incrementPostsFactChecked() {
        this.postsFactChecked++;
    }

    public void incrementPostsVerified() {
        this.postsVerified++;
        calculateTrustScore();
    }

    public void incrementPostsFalse() {
        this.postsFalse++;
        calculateTrustScore();
    }

    public void incrementDebatesWon() {
        this.debatesWon++;
        calculateTrustScore();
    }

    public void incrementDebatesLost() {
        this.debatesLost++;
        calculateTrustScore();
    }
}
