package com.aisocial.platform.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "debate_arguments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"debate_id", "user_id", "round_number"})
})
public class DebateArgument {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debate_id", nullable = false)
    private Debate debate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "round_number", nullable = false)
    private Integer roundNumber;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "fact_check_status")
    private FactCheckStatus factCheckStatus = FactCheckStatus.UNCHECKED;

    @Column(name = "fact_check_score")
    private Double factCheckScore;

    @Column(name = "fact_check_data", columnDefinition = "TEXT")
    private String factCheckData;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public DebateArgument() {}

    public DebateArgument(Debate debate, User user, Integer roundNumber, String content) {
        this.debate = debate;
        this.user = user;
        this.roundNumber = roundNumber;
        this.content = content;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
        if (factCheckStatus == null) factCheckStatus = FactCheckStatus.UNCHECKED;
    }

    // -------------------------
    // Getters and Setters
    // -------------------------
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Debate getDebate() { return debate; }
    public void setDebate(Debate debate) { this.debate = debate; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Integer getRoundNumber() { return roundNumber; }
    public void setRoundNumber(Integer roundNumber) { this.roundNumber = roundNumber; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public FactCheckStatus getFactCheckStatus() { return factCheckStatus; }
    public void setFactCheckStatus(FactCheckStatus factCheckStatus) { this.factCheckStatus = factCheckStatus; }

    public Double getFactCheckScore() { return factCheckScore; }
    public void setFactCheckScore(Double factCheckScore) { this.factCheckScore = factCheckScore; }

    public String getFactCheckData() { return factCheckData; }
    public void setFactCheckData(String factCheckData) { this.factCheckData = factCheckData; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
