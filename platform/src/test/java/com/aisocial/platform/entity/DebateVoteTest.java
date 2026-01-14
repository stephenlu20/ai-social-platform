package com.aisocial.platform.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DebateVote Entity Tests")
class DebateVoteTest {

    private DebateVote vote;
    private UUID debateId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        debateId = UUID.randomUUID();
        userId = UUID.randomUUID();
        vote = new DebateVote();
        vote.setDebateId(debateId);
        vote.setUserId(userId);
        vote.setVote(VoteType.CHALLENGER);
    }

    @Nested
    @DisplayName("Constructor and Default Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create DebateVote with default values")
        void shouldCreateDebateVoteWithDefaults() {
            assertNotNull(vote.getCreatedAt()); // defaults to Instant.now()
            assertEquals(debateId, vote.getDebateId());
            assertEquals(userId, vote.getUserId());
            assertEquals(VoteType.CHALLENGER, vote.getVote());
            assertNull(vote.getId());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get all fields")
        void shouldSetAndGetAllFields() {
            UUID id = UUID.randomUUID();
            Instant created = Instant.now();

            vote.setId(id);
            vote.setDebateId(UUID.randomUUID());
            vote.setUserId(UUID.randomUUID());
            vote.setVote(VoteType.DEFENDER);
            vote.setCreatedAt(created);

            assertEquals(id, vote.getId());
            assertEquals(VoteType.DEFENDER, vote.getVote());
            assertEquals(created, vote.getCreatedAt());
        }
    }
}
