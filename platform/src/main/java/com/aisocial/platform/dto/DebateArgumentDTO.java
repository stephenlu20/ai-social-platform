package com.aisocial.platform.dto;

import com.aisocial.platform.entity.DebateArgument;
import com.aisocial.platform.entity.FactCheckStatus;

import java.time.Instant;
import java.util.UUID;

public class DebateArgumentDTO {

    private UUID id;
    private UUID debateId;
    private DebateDTO.ParticipantDTO user;
    private Integer roundNumber;
    private String content;
    private FactCheckStatus factCheckStatus;
    private Double factCheckScore;
    private Instant createdAt;

    public DebateArgumentDTO() {
    }

    public DebateArgumentDTO(DebateArgument argument) {
        this.id = argument.getId();
        this.debateId = argument.getDebate() != null ? argument.getDebate().getId() : null;
        this.user = DebateDTO.ParticipantDTO.fromUser(argument.getUser());
        this.roundNumber = argument.getRoundNumber();
        this.content = argument.getContent();
        this.factCheckStatus = argument.getFactCheckStatus();
        this.factCheckScore = argument.getFactCheckScore();
        this.createdAt = argument.getCreatedAt();
    }

    public static DebateArgumentDTO fromEntity(DebateArgument argument) {
        return new DebateArgumentDTO(argument);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getDebateId() {
        return debateId;
    }

    public void setDebateId(UUID debateId) {
        this.debateId = debateId;
    }

    public DebateDTO.ParticipantDTO getUser() {
        return user;
    }

    public void setUser(DebateDTO.ParticipantDTO user) {
        this.user = user;
    }

    public Integer getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(Integer roundNumber) {
        this.roundNumber = roundNumber;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public FactCheckStatus getFactCheckStatus() {
        return factCheckStatus;
    }

    public void setFactCheckStatus(FactCheckStatus factCheckStatus) {
        this.factCheckStatus = factCheckStatus;
    }

    public Double getFactCheckScore() {
        return factCheckScore;
    }

    public void setFactCheckScore(Double factCheckScore) {
        this.factCheckScore = factCheckScore;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
