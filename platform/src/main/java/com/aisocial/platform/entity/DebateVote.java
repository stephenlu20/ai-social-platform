package com.aisocial.platform.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "debate_votes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"debate_id", "user_id"})
})
public class DebateVote {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "debate_id", nullable = false)
    private UUID debateId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "vote", nullable = false)
    private VoteType vote;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    // ---- getters and setters ----
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getDebateId() { return debateId; }
    public void setDebateId(UUID debateId) { this.debateId = debateId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public VoteType getVote() { return vote; }
    public void setVote(VoteType vote) { this.vote = vote; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
