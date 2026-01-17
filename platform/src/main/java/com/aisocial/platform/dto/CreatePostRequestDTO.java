package com.aisocial.platform.dto;

import java.util.UUID;

public class CreatePostRequestDTO {
    private UUID userId;
    private String content;
    private Boolean factCheck = false;
    private PostStyleDTO style;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getFactCheck() {
        return factCheck;
    }

    public void setFactCheck(Boolean factCheck) {
        this.factCheck = factCheck;
    }

    public boolean shouldFactCheck() {
        return factCheck != null && factCheck;
    }

    public PostStyleDTO getStyle() {
        return style;
    }

    public void setStyle(PostStyleDTO style) {
        this.style = style;
    }
}