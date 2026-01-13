package com.aisocial.platform.dto;

import com.aisocial.platform.entity.Debate;
import com.aisocial.platform.entity.DebateStatus;
import com.aisocial.platform.entity.User;

import java.time.Instant;
import java.util.UUID;

public class DebateDTO {

    private UUID id;
    private String topic;
    private ParticipantDTO challenger;
    private ParticipantDTO defender;
    private DebateStatus status;
    private Integer currentRound;
    private UUID whoseTurnId;
    private UUID winnerId;
    private Integer votesChallenger;
    private Integer votesDefender;
    private Integer votesTie;
    private Integer totalVotes;
    private Instant votingEndsAt;
    private Instant createdAt;

    public DebateDTO() {
    }

    public DebateDTO(Debate debate) {
        this.id = debate.getId();
        this.topic = debate.getTopic();
        this.challenger = ParticipantDTO.fromUser(debate.getChallenger());
        this.defender = ParticipantDTO.fromUser(debate.getDefender());
        this.status = debate.getStatus();
        this.currentRound = debate.getCurrentRound();
        this.whoseTurnId = debate.getWhoseTurn() != null ? debate.getWhoseTurn().getId() : null;
        this.winnerId = debate.getWinner() != null ? debate.getWinner().getId() : null;
        this.votesChallenger = debate.getVotesChallenger();
        this.votesDefender = debate.getVotesDefender();
        this.votesTie = debate.getVotesTie();
        this.totalVotes = debate.getTotalVotes();
        this.votingEndsAt = debate.getVotingEndsAt();
        this.createdAt = debate.getCreatedAt();
    }

    public static DebateDTO fromEntity(Debate debate) {
        return new DebateDTO(debate);
    }

    /**
     * Lightweight user representation for debate participants.
     */
    public static class ParticipantDTO {
        private UUID id;
        private String username;
        private String displayName;
        private String avatarUrl;

        public ParticipantDTO() {
        }

        public ParticipantDTO(UUID id, String username, String displayName, String avatarUrl) {
            this.id = id;
            this.username = username;
            this.displayName = displayName;
            this.avatarUrl = avatarUrl;
        }

        public static ParticipantDTO fromUser(User user) {
            if (user == null) return null;
            return new ParticipantDTO(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getAvatarUrl()
            );
        }

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }

        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
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

    public ParticipantDTO getChallenger() {
        return challenger;
    }

    public void setChallenger(ParticipantDTO challenger) {
        this.challenger = challenger;
    }

    public ParticipantDTO getDefender() {
        return defender;
    }

    public void setDefender(ParticipantDTO defender) {
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

    public UUID getWhoseTurnId() {
        return whoseTurnId;
    }

    public void setWhoseTurnId(UUID whoseTurnId) {
        this.whoseTurnId = whoseTurnId;
    }

    public UUID getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(UUID winnerId) {
        this.winnerId = winnerId;
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

    public Integer getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(Integer totalVotes) {
        this.totalVotes = totalVotes;
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
}
