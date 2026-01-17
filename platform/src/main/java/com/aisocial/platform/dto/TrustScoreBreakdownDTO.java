package com.aisocial.platform.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Detailed breakdown of a user's trust score calculation.
 */
public class TrustScoreBreakdownDTO {

    private UUID userId;
    private BigDecimal totalScore;
    private Double baseScore;

    // Fact-check stats
    private Integer postsFactChecked;
    private Integer postsVerified;
    private Integer postsFalse;

    // Debate stats
    private Integer debatesWon;
    private Integer debatesLost;

    // Calculated bonuses/penalties
    private Double verifiedBonus;
    private Double falsePenalty;

    // Trust tier
    private String tier;

    public TrustScoreBreakdownDTO() {}

    // Getters and setters
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public BigDecimal getTotalScore() { return totalScore; }
    public void setTotalScore(BigDecimal totalScore) { this.totalScore = totalScore; }

    public Double getBaseScore() { return baseScore; }
    public void setBaseScore(Double baseScore) { this.baseScore = baseScore; }

    public Integer getPostsFactChecked() { return postsFactChecked; }
    public void setPostsFactChecked(Integer postsFactChecked) { this.postsFactChecked = postsFactChecked; }

    public Integer getPostsVerified() { return postsVerified; }
    public void setPostsVerified(Integer postsVerified) { this.postsVerified = postsVerified; }

    public Integer getPostsFalse() { return postsFalse; }
    public void setPostsFalse(Integer postsFalse) { this.postsFalse = postsFalse; }

    public Integer getDebatesWon() { return debatesWon; }
    public void setDebatesWon(Integer debatesWon) { this.debatesWon = debatesWon; }

    public Integer getDebatesLost() { return debatesLost; }
    public void setDebatesLost(Integer debatesLost) { this.debatesLost = debatesLost; }

    public Double getVerifiedBonus() { return verifiedBonus; }
    public void setVerifiedBonus(Double verifiedBonus) { this.verifiedBonus = verifiedBonus; }

    public Double getFalsePenalty() { return falsePenalty; }
    public void setFalsePenalty(Double falsePenalty) { this.falsePenalty = falsePenalty; }

    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }
}
