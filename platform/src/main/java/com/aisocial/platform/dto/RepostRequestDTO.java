package com.aisocial.platform.dto;

import java.util.UUID;

public class RepostRequestDTO {
    private UUID userId;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}