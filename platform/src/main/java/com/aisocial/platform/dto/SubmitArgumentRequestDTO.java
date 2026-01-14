package com.aisocial.platform.dto;

public class SubmitArgumentRequestDTO {

    private String content;

    public SubmitArgumentRequestDTO() {
    }

    public SubmitArgumentRequestDTO(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
