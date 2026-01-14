package com.aisocial.platform.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class UserSearchRequestDTO {
    private String username;           // partial match
    private String displayName;        // partial match
    private BigDecimal minTrustScore;  // optional
    private BigDecimal maxTrustScore;  // optional
    private Instant createdAfter;      // optional
    private Instant createdBefore;     // optional
    private int page = 0;              // pagination
    private int size = 20;             // pagination

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public BigDecimal getMinTrustScore() { return minTrustScore; }
    public void setMinTrustScore(BigDecimal minTrustScore) { this.minTrustScore = minTrustScore; }

    public BigDecimal getMaxTrustScore() { return maxTrustScore; }
    public void setMaxTrustScore(BigDecimal maxTrustScore) { this.maxTrustScore = maxTrustScore; }

    public Instant getCreatedAfter() { return createdAfter; }
    public void setCreatedAfter(Instant createdAfter) { this.createdAfter = createdAfter; }

    public Instant getCreatedBefore() { return createdBefore; }
    public void setCreatedBefore(Instant createdBefore) { this.createdBefore = createdBefore; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}
