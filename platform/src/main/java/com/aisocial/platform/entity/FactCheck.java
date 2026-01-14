package com.aisocial.platform.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "fact_checks")
public class FactCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debate_arg_id")
    private DebateArgument debateArg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by")
    private User requestedBy;

    @Enumerated(EnumType.STRING)
    private FactCheckStatus status = FactCheckStatus.UNCHECKED;

    private Double overallScore;

    @Column(columnDefinition = "TEXT")
    private String claims; // JSONB in DB

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public FactCheck() {}

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }

    public DebateArgument getDebateArg() { return debateArg; }
    public void setDebateArg(DebateArgument debateArg) { this.debateArg = debateArg; }

    public User getRequestedBy() { return requestedBy; }
    public void setRequestedBy(User requestedBy) { this.requestedBy = requestedBy; }

    public FactCheckStatus getStatus() { return status; }
    public void setStatus(FactCheckStatus status) { this.status = status; }

    public Double getOverallScore() { return overallScore; }
    public void setOverallScore(Double overallScore) { this.overallScore = overallScore; }

    public String getClaims() { return claims; }
    public void setClaims(String claims) { this.claims = claims; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
