package com.aisocial.platform.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FactCheck Entity Tests")
class FactCheckTest {

    private FactCheck factCheck;
    private User user;
    private Post post;
    private DebateArgument debateArg;

    @BeforeEach
    void setUp() {
        user = new User("alice", "Alice Display", "Bio");
        post = new Post(); // assuming Post default constructor exists
        debateArg = new DebateArgument();
        factCheck = new FactCheck();
        factCheck.setPost(post);
        factCheck.setDebateArg(debateArg);
        factCheck.setRequestedBy(user);
    }

    @Nested
    @DisplayName("Constructor and Default Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should initialize status to UNCHECKED and createdAt null")
        void shouldInitializeDefaults() {
            assertEquals(FactCheckStatus.UNCHECKED, factCheck.getStatus());
            assertNull(factCheck.getCreatedAt());
            assertNull(factCheck.getOverallScore());
            assertNull(factCheck.getClaims());
        }
    }

    @Nested
    @DisplayName("PrePersist Tests")
    class PrePersistTests {

        @Test
        @DisplayName("Should set createdAt on persist")
        void shouldSetCreatedAt() {
            assertNull(factCheck.getCreatedAt());
            factCheck.onCreate();
            assertNotNull(factCheck.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get all fields")
        void shouldSetAndGetAllFields() {
            UUID id = UUID.randomUUID();
            Double score = 9.5;
            String claims = "{\"claim\":\"true\"}";
            Instant createdAt = Instant.now();

            factCheck.setId(id);
            factCheck.setStatus(FactCheckStatus.VERIFIED);
            factCheck.setOverallScore(score);
            factCheck.setClaims(claims);
            factCheck.setCreatedAt(createdAt);

            assertEquals(id, factCheck.getId());
            assertEquals(FactCheckStatus.VERIFIED, factCheck.getStatus());
            assertEquals(score, factCheck.getOverallScore());
            assertEquals(claims, factCheck.getClaims());
            assertEquals(createdAt, factCheck.getCreatedAt());
        }
    }
}
