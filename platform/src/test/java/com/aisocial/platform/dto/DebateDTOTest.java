package com.aisocial.platform.dto;

import com.aisocial.platform.entity.Debate;
import com.aisocial.platform.entity.DebateStatus;
import com.aisocial.platform.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DebateDTO Tests")
class DebateDTOTest {

    private User challenger;
    private User defender;
    private Debate debate;

    @BeforeEach
    void setUp() {
        challenger = new User("challenger", "Challenger User", "Bio");
        challenger.setId(UUID.randomUUID());
        challenger.setAvatarUrl("https://example.com/challenger.png");

        defender = new User("defender", "Defender User", "Bio");
        defender.setId(UUID.randomUUID());
        defender.setAvatarUrl("https://example.com/defender.png");

        debate = new Debate("Is Java better than Python?", challenger, defender);
        debate.setId(UUID.randomUUID());
        debate.setStatus(DebateStatus.ACTIVE);
        debate.setCurrentRound(2);
        debate.setVotesChallenger(5);
        debate.setVotesDefender(3);
        debate.setVotesTie(1);
        debate.setCreatedAt(Instant.now());
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create DTO with default constructor")
        void shouldCreateDTOWithDefaultConstructor() {
            DebateDTO dto = new DebateDTO();

            assertNull(dto.getId());
            assertNull(dto.getTopic());
            assertNull(dto.getChallenger());
            assertNull(dto.getDefender());
            assertNull(dto.getStatus());
        }

        @Test
        @DisplayName("Should create DTO from Debate entity")
        void shouldCreateDTOFromDebateEntity() {
            DebateDTO dto = new DebateDTO(debate);

            assertEquals(debate.getId(), dto.getId());
            assertEquals("Is Java better than Python?", dto.getTopic());
            assertEquals(DebateStatus.ACTIVE, dto.getStatus());
            assertEquals(2, dto.getCurrentRound());
            assertEquals(5, dto.getVotesChallenger());
            assertEquals(3, dto.getVotesDefender());
            assertEquals(1, dto.getVotesTie());
            assertEquals(9, dto.getTotalVotes());
        }

        @Test
        @DisplayName("Should map challenger participant correctly")
        void shouldMapChallengerParticipant() {
            DebateDTO dto = new DebateDTO(debate);

            assertNotNull(dto.getChallenger());
            assertEquals(challenger.getId(), dto.getChallenger().getId());
            assertEquals("challenger", dto.getChallenger().getUsername());
            assertEquals("Challenger User", dto.getChallenger().getDisplayName());
            assertEquals("https://example.com/challenger.png", dto.getChallenger().getAvatarUrl());
        }

        @Test
        @DisplayName("Should map defender participant correctly")
        void shouldMapDefenderParticipant() {
            DebateDTO dto = new DebateDTO(debate);

            assertNotNull(dto.getDefender());
            assertEquals(defender.getId(), dto.getDefender().getId());
            assertEquals("defender", dto.getDefender().getUsername());
            assertEquals("Defender User", dto.getDefender().getDisplayName());
        }

        @Test
        @DisplayName("Should map whoseTurnId correctly")
        void shouldMapWhoseTurnId() {
            debate.setWhoseTurn(defender);
            DebateDTO dto = new DebateDTO(debate);

            assertEquals(defender.getId(), dto.getWhoseTurnId());
        }

        @Test
        @DisplayName("Should handle null whoseTurn")
        void shouldHandleNullWhoseTurn() {
            debate.setWhoseTurn(null);
            DebateDTO dto = new DebateDTO(debate);

            assertNull(dto.getWhoseTurnId());
        }

        @Test
        @DisplayName("Should handle null winner")
        void shouldHandleNullWinner() {
            debate.setWinner(null);
            DebateDTO dto = new DebateDTO(debate);

            assertNull(dto.getWinnerId());
        }

        @Test
        @DisplayName("Should map winner when set")
        void shouldMapWinnerWhenSet() {
            debate.setWinner(challenger);
            DebateDTO dto = new DebateDTO(debate);

            assertEquals(challenger.getId(), dto.getWinnerId());
        }
    }

    @Nested
    @DisplayName("Static Factory Method Tests")
    class StaticFactoryTests {

        @Test
        @DisplayName("Should create DTO using fromEntity factory method")
        void shouldCreateDTOUsingFromEntity() {
            DebateDTO dto = DebateDTO.fromEntity(debate);

            assertEquals(debate.getId(), dto.getId());
            assertEquals("Is Java better than Python?", dto.getTopic());
            assertEquals(DebateStatus.ACTIVE, dto.getStatus());
        }

        @Test
        @DisplayName("fromEntity should produce same result as constructor")
        void fromEntityShouldMatchConstructor() {
            DebateDTO fromConstructor = new DebateDTO(debate);
            DebateDTO fromFactory = DebateDTO.fromEntity(debate);

            assertEquals(fromConstructor.getId(), fromFactory.getId());
            assertEquals(fromConstructor.getTopic(), fromFactory.getTopic());
            assertEquals(fromConstructor.getStatus(), fromFactory.getStatus());
            assertEquals(fromConstructor.getCurrentRound(), fromFactory.getCurrentRound());
            assertEquals(fromConstructor.getTotalVotes(), fromFactory.getTotalVotes());
        }
    }

    @Nested
    @DisplayName("ParticipantDTO Tests")
    class ParticipantDTOTests {

        @Test
        @DisplayName("Should create ParticipantDTO from User")
        void shouldCreateParticipantDTOFromUser() {
            DebateDTO.ParticipantDTO participant = DebateDTO.ParticipantDTO.fromUser(challenger);

            assertEquals(challenger.getId(), participant.getId());
            assertEquals("challenger", participant.getUsername());
            assertEquals("Challenger User", participant.getDisplayName());
            assertEquals("https://example.com/challenger.png", participant.getAvatarUrl());
        }

        @Test
        @DisplayName("Should return null for null user")
        void shouldReturnNullForNullUser() {
            DebateDTO.ParticipantDTO participant = DebateDTO.ParticipantDTO.fromUser(null);

            assertNull(participant);
        }

        @Test
        @DisplayName("Should set and get ParticipantDTO fields")
        void shouldSetAndGetParticipantDTOFields() {
            DebateDTO.ParticipantDTO participant = new DebateDTO.ParticipantDTO();
            UUID id = UUID.randomUUID();

            participant.setId(id);
            participant.setUsername("testuser");
            participant.setDisplayName("Test User");
            participant.setAvatarUrl("https://test.com/avatar.png");

            assertEquals(id, participant.getId());
            assertEquals("testuser", participant.getUsername());
            assertEquals("Test User", participant.getDisplayName());
            assertEquals("https://test.com/avatar.png", participant.getAvatarUrl());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get all fields")
        void shouldSetAndGetAllFields() {
            DebateDTO dto = new DebateDTO();
            UUID id = UUID.randomUUID();
            UUID whoseTurnId = UUID.randomUUID();
            UUID winnerId = UUID.randomUUID();
            Instant votingEnds = Instant.now().plusSeconds(3600);
            Instant createdAt = Instant.now();

            dto.setId(id);
            dto.setTopic("New Topic");
            dto.setStatus(DebateStatus.VOTING);
            dto.setCurrentRound(3);
            dto.setWhoseTurnId(whoseTurnId);
            dto.setWinnerId(winnerId);
            dto.setVotesChallenger(10);
            dto.setVotesDefender(8);
            dto.setVotesTie(2);
            dto.setTotalVotes(20);
            dto.setVotingEndsAt(votingEnds);
            dto.setCreatedAt(createdAt);

            assertEquals(id, dto.getId());
            assertEquals("New Topic", dto.getTopic());
            assertEquals(DebateStatus.VOTING, dto.getStatus());
            assertEquals(3, dto.getCurrentRound());
            assertEquals(whoseTurnId, dto.getWhoseTurnId());
            assertEquals(winnerId, dto.getWinnerId());
            assertEquals(10, dto.getVotesChallenger());
            assertEquals(8, dto.getVotesDefender());
            assertEquals(2, dto.getVotesTie());
            assertEquals(20, dto.getTotalVotes());
            assertEquals(votingEnds, dto.getVotingEndsAt());
            assertEquals(createdAt, dto.getCreatedAt());
        }
    }
}
