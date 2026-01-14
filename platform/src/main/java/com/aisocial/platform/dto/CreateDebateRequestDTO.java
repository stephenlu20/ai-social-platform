package com.aisocial.platform.dto;

import java.util.UUID;

public class CreateDebateRequestDTO {

    private UUID defenderId;
    private String topic;

    public CreateDebateRequestDTO() {
    }

    public CreateDebateRequestDTO(UUID defenderId, String topic) {
        this.defenderId = defenderId;
        this.topic = topic;
    }

    public UUID getDefenderId() {
        return defenderId;
    }

    public void setDefenderId(UUID defenderId) {
        this.defenderId = defenderId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
