package com.aisocial.platform.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class UserResponseDTO {
    private UUID id;
    private String username;
    private String displayName;
    private BigDecimal trustScore;
    private Instant createdAt;

    public UserResponseDTO(UUID id, String username, String displayName, BigDecimal trustScore, Instant createdAt) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.trustScore = trustScore;
        this.createdAt = createdAt;
    }

    // Getters
    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }
    public BigDecimal getTrustScore() { return trustScore; }
    public Instant getCreatedAt() { return createdAt; }
}
