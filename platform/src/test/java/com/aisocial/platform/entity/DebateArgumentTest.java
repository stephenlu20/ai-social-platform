package com.aisocial.platform.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DebateArgument Entity Tests")
class DebateArgumentTest {

    private User user;
    private Debate debate;
    private DebateArgument argument;

    @BeforeEach
    void setUp() {
        user = new User("alice", "Alice Display", "Bio Alice");
        debate = new Debate("Debate Topic", user, user);
        argument = new DebateArgument(debate, user, 1, "Initial content");
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create DebateArgument with all fields")
        void shouldCreateDebateArgumentWithAllFields() {
            assertEquals(debate, argument.getDebate());
            assertEquals(user, argument.getUser());
            assertEquals(1, argument.getRoundNumber());
            assertEquals("Initial content", argument.getContent());
            assertEquals(FactCheckStatus.UNCHECKED, argument.getFactCheckStatus());
            assertNull(argument.getFactCheckScore());
            assertNull(argument.getFactCheckData());
            assertNull(argument.getCreatedAt());
        }

        @Test
        @DisplayName("Should create DebateArgument with default constructor")
        void shouldCreateDebateArgumentWithDefaultConstructor() {
            DebateArgument emptyArg = new DebateArgument();
            assertNull(emptyArg.getDebate());
            assertNull(emptyArg.getUser());
            assertNull(emptyArg.getRoundNumber());
            assertNull(emptyArg.getContent());
            assertEquals(FactCheckStatus.UNCHECKED, emptyArg.getFactCheckStatus());
            assertNull(emptyArg.getFactCheckScore());
            assertNull(emptyArg.getFactCheckData());
            assertNull(emptyArg.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("PrePersist Tests")
    class PrePersistTests {

        @Test
        @DisplayName("Should set createdAt on persist")
        void shouldSetCreatedAt() {
            assertNull(argument.getCreatedAt());
            argument.onCreate();
            assertNotNull(argument.getCreatedAt());
        }

        @Test
        @DisplayName("Should initialize factCheckStatus if null")
        void shouldInitializeFactCheckStatus() {
            argument.setFactCheckStatus(null);
            argument.onCreate();
            assertEquals(FactCheckStatus.UNCHECKED, argument.getFactCheckStatus());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get all fields")
        void shouldSetAndGetAllFields() {
            UUID id = UUID.randomUUID();
            Double score = 4.5;
            String data = "Fact check data";
            Instant createdAt = Instant.now();

            argument.setId(id);
            argument.setRoundNumber(2);
            argument.setContent("Updated content");
            argument.setFactCheckStatus(FactCheckStatus.VERIFIED);
            argument.setFactCheckScore(score);
            argument.setFactCheckData(data);
            argument.setCreatedAt(createdAt);

            assertEquals(id, argument.getId());
            assertEquals(2, argument.getRoundNumber());
            assertEquals("Updated content", argument.getContent());
            assertEquals(FactCheckStatus.VERIFIED, argument.getFactCheckStatus());
            assertEquals(score, argument.getFactCheckScore());
            assertEquals(data, argument.getFactCheckData());
            assertEquals(createdAt, argument.getCreatedAt());
        }
    }
}
