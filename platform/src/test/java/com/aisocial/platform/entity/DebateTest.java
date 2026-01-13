package com.aisocial.platform.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Debate Entity Tests")
class DebateTest {

    private User challenger;
    private User defender;
    private Debate debate;

    @BeforeEach
    void setUp() {
        challenger = new User("challenger", "Challenger User", "Bio");
        defender = new User("defender", "Defender User", "Bio");
        debate = new Debate("Is Java better than Python?", challenger, defender);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create debate with topic, challenger, and defender")
        void shouldCreateDebateWithAllFields() {
            assertEquals("Is Java better than Python?", debate.getTopic());
            assertEquals(challenger, debate.getChallenger());
            assertEquals(defender, debate.getDefender());
            assertEquals(challenger, debate.getWhoseTurn());
        }

        @Test
        @DisplayName("Should create debate with default constructor")
        void shouldCreateDebateWithDefaultConstructor() {
            Debate emptyDebate = new Debate();
            assertNull(emptyDebate.getTopic());
            assertNull(emptyDebate.getChallenger());
            assertNull(emptyDebate.getDefender());
        }
    }

    @Nested
    @DisplayName("PrePersist Tests")
    class PrePersistTests {

        @Test
        @DisplayName("Should set createdAt on persist")
        void shouldSetCreatedAt() {
            assertNull(debate.getCreatedAt());
            debate.onCreate();
            assertNotNull(debate.getCreatedAt());
        }

        @Test
        @DisplayName("Should initialize status to PENDING")
        void shouldInitializeStatus() {
            debate.onCreate();
            assertEquals(DebateStatus.PENDING, debate.getStatus());
        }

        @Test
        @DisplayName("Should initialize currentRound to 1")
        void shouldInitializeCurrentRound() {
            debate.onCreate();
            assertEquals(1, debate.getCurrentRound());
        }

        @Test
        @DisplayName("Should initialize vote counts to 0")
        void shouldInitializeVoteCounts() {
            debate.onCreate();
            assertEquals(0, debate.getVotesChallenger());
            assertEquals(0, debate.getVotesDefender());
            assertEquals(0, debate.getVotesTie());
        }
    }

    @Nested
    @DisplayName("Vote Increment Tests")
    class VoteIncrementTests {

        @Test
        @DisplayName("Should increment challenger votes")
        void shouldIncrementChallengerVotes() {
            debate.onCreate();
            debate.incrementVotesChallenger();
            assertEquals(1, debate.getVotesChallenger());
        }

        @Test
        @DisplayName("Should increment defender votes")
        void shouldIncrementDefenderVotes() {
            debate.onCreate();
            debate.incrementVotesDefender();
            assertEquals(1, debate.getVotesDefender());
        }

        @Test
        @DisplayName("Should increment tie votes")
        void shouldIncrementTieVotes() {
            debate.onCreate();
            debate.incrementVotesTie();
            assertEquals(1, debate.getVotesTie());
        }

        @Test
        @DisplayName("Should calculate total votes")
        void shouldCalculateTotalVotes() {
            debate.onCreate();
            debate.incrementVotesChallenger();
            debate.incrementVotesChallenger();
            debate.incrementVotesDefender();
            debate.incrementVotesTie();
            assertEquals(4, debate.getTotalVotes());
        }
    }

    @Nested
    @DisplayName("Round Advancement Tests")
    class RoundAdvancementTests {

        @Test
        @DisplayName("Should advance round")
        void shouldAdvanceRound() {
            debate.onCreate();
            assertEquals(1, debate.getCurrentRound());
            debate.advanceRound();
            assertEquals(2, debate.getCurrentRound());
            debate.advanceRound();
            assertEquals(3, debate.getCurrentRound());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get all fields")
        void shouldSetAndGetAllFields() {
            UUID id = UUID.randomUUID();
            User winner = challenger;
            Instant votingEnds = Instant.now().plusSeconds(3600);
            Instant createdAt = Instant.now();

            debate.setId(id);
            debate.setTopic("New Topic");
            debate.setStatus(DebateStatus.VOTING);
            debate.setCurrentRound(3);
            debate.setWhoseTurn(defender);
            debate.setWinner(winner);
            debate.setVotesChallenger(10);
            debate.setVotesDefender(8);
            debate.setVotesTie(2);
            debate.setVotingEndsAt(votingEnds);
            debate.setCreatedAt(createdAt);

            assertEquals(id, debate.getId());
            assertEquals("New Topic", debate.getTopic());
            assertEquals(DebateStatus.VOTING, debate.getStatus());
            assertEquals(3, debate.getCurrentRound());
            assertEquals(defender, debate.getWhoseTurn());
            assertEquals(winner, debate.getWinner());
            assertEquals(10, debate.getVotesChallenger());
            assertEquals(8, debate.getVotesDefender());
            assertEquals(2, debate.getVotesTie());
            assertEquals(votingEnds, debate.getVotingEndsAt());
            assertEquals(createdAt, debate.getCreatedAt());
        }
    }
}
