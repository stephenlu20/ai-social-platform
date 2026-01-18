package com.aisocial.platform.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "debates")
public class Debate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "VARCHAR(36)")
    private UUID id;

    @Column(nullable = false, length = 280)
    private String topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenger_id", nullable = false)
    private User challenger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "defender_id", nullable = false)
    private User defender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DebateStatus status = DebateStatus.PENDING;

    @Column(name = "current_round")
    private Integer currentRound = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "whose_turn_id")
    private User whoseTurn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private User winner;

    @Column(name = "votes_challenger")
    private Integer votesChallenger = 0;

    @Column(name = "votes_defender")
    private Integer votesDefender = 0;

    @Column(name = "votes_tie")
    private Integer votesTie = 0;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "voting_ends_at")
    private Instant votingEndsAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Debate() {
    }

    public Debate(String topic, User challenger, User defender) {
        this.topic = topic;
        this.challenger = challenger;
        this.defender = defender;
        this.whoseTurn = challenger;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (status == null) {
            status = DebateStatus.PENDING;
        }
        if (currentRound == null) {
            currentRound = 1;
        }
        if (votesChallenger == null) {
            votesChallenger = 0;
        }
        if (votesDefender == null) {
            votesDefender = 0;
        }
        if (votesTie == null) {
            votesTie = 0;
        }
        if (likeCount == null) {
            likeCount = 0;
        }
    }

    public void incrementVotesChallenger() {
        this.votesChallenger++;
    }

    public void decrementVotesChallenger() {
        if (this.votesChallenger > 0) this.votesChallenger--;
    }

    public void incrementVotesDefender() {
        this.votesDefender++;
    }

    public void decrementVotesDefender() {
        if (this.votesDefender > 0) this.votesDefender--;
    }

    public void incrementVotesTie() {
        this.votesTie++;
    }

    public void decrementVotesTie() {
        if (this.votesTie > 0) this.votesTie--;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) this.likeCount--;
    }

    public void advanceRound() {
        this.currentRound++;
    }

    public int getTotalVotes() {
        return votesChallenger + votesDefender + votesTie;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public User getChallenger() {
        return challenger;
    }

    public void setChallenger(User challenger) {
        this.challenger = challenger;
    }

    public User getDefender() {
        return defender;
    }

    public void setDefender(User defender) {
        this.defender = defender;
    }

    public DebateStatus getStatus() {
        return status;
    }

    public void setStatus(DebateStatus status) {
        this.status = status;
    }

    public Integer getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(Integer currentRound) {
        this.currentRound = currentRound;
    }

    public User getWhoseTurn() {
        return whoseTurn;
    }

    public void setWhoseTurn(User whoseTurn) {
        this.whoseTurn = whoseTurn;
    }

    public User getWinner() {
        return winner;
    }

    public void setWinner(User winner) {
        this.winner = winner;
    }

    public Integer getVotesChallenger() {
        return votesChallenger;
    }

    public void setVotesChallenger(Integer votesChallenger) {
        this.votesChallenger = votesChallenger;
    }

    public Integer getVotesDefender() {
        return votesDefender;
    }

    public void setVotesDefender(Integer votesDefender) {
        this.votesDefender = votesDefender;
    }

    public Integer getVotesTie() {
        return votesTie;
    }

    public void setVotesTie(Integer votesTie) {
        this.votesTie = votesTie;
    }

    public Instant getVotingEndsAt() {
        return votingEndsAt;
    }

    public void setVotingEndsAt(Instant votingEndsAt) {
        this.votingEndsAt = votingEndsAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
}
