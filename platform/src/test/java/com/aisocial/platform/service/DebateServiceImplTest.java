package com.aisocial.platform.service;

import com.aisocial.platform.dto.DebateDTO;
import com.aisocial.platform.entity.Debate;
import com.aisocial.platform.entity.DebateStatus;
import com.aisocial.platform.entity.User;
import com.aisocial.platform.repository.DebateRepository;
import com.aisocial.platform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Debate Service Tests")
class DebateServiceImplTest {

    @Mock
    private DebateRepository debateRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DebateServiceImpl debateService;

    private User challenger;
    private User defender;

    @BeforeEach
    void setUp() {
        challenger = new User("challenger", "Challenger", "Bio");
        challenger.setId(UUID.randomUUID());

        defender = new User("defender", "Defender", "Bio");
        defender.setId(UUID.randomUUID());
    }

    @Nested
    @DisplayName("Create Challenge Tests")
    class CreateChallengeTests {

        @Test
        @DisplayName("Should create a debate challenge successfully")
        void shouldCreateChallenge() {
            String topic = "Is Java better than Python?";

            when(userRepository.findById(challenger.getId())).thenReturn(Optional.of(challenger));
            when(userRepository.findById(defender.getId())).thenReturn(Optional.of(defender));
            when(debateRepository.save(any(Debate.class))).thenAnswer(invocation -> {
                Debate d = invocation.getArgument(0);
                d.setId(UUID.randomUUID());
                return d;
            });

            DebateDTO result = debateService.createChallenge(challenger.getId(), defender.getId(), topic);

            assertNotNull(result);
            assertEquals(topic, result.getTopic());
            assertEquals(DebateStatus.PENDING, result.getStatus());
            assertEquals(challenger.getId(), result.getChallenger().getId());
            assertEquals(defender.getId(), result.getDefender().getId());

            verify(debateRepository).save(any(Debate.class));
        }

        @Test
        @DisplayName("Should throw exception when challenging yourself")
        void shouldThrowWhenChallengingSelf() {
            assertThrows(IllegalArgumentException.class,
                    () -> debateService.createChallenge(challenger.getId(), challenger.getId(), "Topic"));

            verify(debateRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when challenger not found")
        void shouldThrowWhenChallengerNotFound() {
            when(userRepository.findById(challenger.getId())).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> debateService.createChallenge(challenger.getId(), defender.getId(), "Topic"));
        }

        @Test
        @DisplayName("Should throw exception when defender not found")
        void shouldThrowWhenDefenderNotFound() {
            when(userRepository.findById(challenger.getId())).thenReturn(Optional.of(challenger));
            when(userRepository.findById(defender.getId())).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> debateService.createChallenge(challenger.getId(), defender.getId(), "Topic"));
        }
    }

    @Nested
    @DisplayName("Accept Challenge Tests")
    class AcceptChallengeTests {

        @Test
        @DisplayName("Should accept pending challenge successfully")
        void shouldAcceptChallenge() {
            Debate debate = new Debate("Topic", challenger, defender);
            debate.setId(UUID.randomUUID());
            debate.setStatus(DebateStatus.PENDING);

            when(debateRepository.findById(debate.getId())).thenReturn(Optional.of(debate));
            when(debateRepository.save(any(Debate.class))).thenAnswer(invocation -> invocation.getArgument(0));

            DebateDTO result = debateService.acceptChallenge(debate.getId(), defender.getId());

            assertEquals(DebateStatus.ACTIVE, result.getStatus());
            assertEquals(challenger.getId(), result.getWhoseTurnId());
            verify(debateRepository).save(debate);
        }

        @Test
        @DisplayName("Should throw when non-defender tries to accept")
        void shouldThrowWhenNonDefenderAccepts() {
            Debate debate = new Debate("Topic", challenger, defender);
            debate.setId(UUID.randomUUID());
            debate.setStatus(DebateStatus.PENDING);

            when(debateRepository.findById(debate.getId())).thenReturn(Optional.of(debate));

            assertThrows(IllegalStateException.class,
                    () -> debateService.acceptChallenge(debate.getId(), challenger.getId()));
        }

        @Test
        @DisplayName("Should throw when challenge not pending")
        void shouldThrowWhenNotPending() {
            Debate debate = new Debate("Topic", challenger, defender);
            debate.setId(UUID.randomUUID());
            debate.setStatus(DebateStatus.ACTIVE);

            when(debateRepository.findById(debate.getId())).thenReturn(Optional.of(debate));

            assertThrows(IllegalStateException.class,
                    () -> debateService.acceptChallenge(debate.getId(), defender.getId()));
        }
    }

    @Nested
    @DisplayName("Decline Challenge Tests")
    class DeclineChallengeTests {

        @Test
        @DisplayName("Should decline pending challenge successfully")
        void shouldDeclineChallenge() {
            Debate debate = new Debate("Topic", challenger, defender);
            debate.setId(UUID.randomUUID());
            debate.setStatus(DebateStatus.PENDING);

            when(debateRepository.findById(debate.getId())).thenReturn(Optional.of(debate));

            DebateDTO result = debateService.declineChallenge(debate.getId(), defender.getId());

            assertNotNull(result);
            verify(debateRepository).delete(debate);
        }

        @Test
        @DisplayName("Should throw when non-defender tries to decline")
        void shouldThrowWhenNonDefenderDeclines() {
            Debate debate = new Debate("Topic", challenger, defender);
            debate.setId(UUID.randomUUID());
            debate.setStatus(DebateStatus.PENDING);

            when(debateRepository.findById(debate.getId())).thenReturn(Optional.of(debate));

            assertThrows(IllegalStateException.class,
                    () -> debateService.declineChallenge(debate.getId(), challenger.getId()));
        }
    }

    @Nested
    @DisplayName("Query Tests")
    class QueryTests {

        @Test
        @DisplayName("Should get debate by ID")
        void shouldGetDebateById() {
            Debate debate = new Debate("Topic", challenger, defender);
            debate.setId(UUID.randomUUID());

            when(debateRepository.findById(debate.getId())).thenReturn(Optional.of(debate));

            Optional<DebateDTO> result = debateService.getDebateById(debate.getId());

            assertTrue(result.isPresent());
            assertEquals(debate.getId(), result.get().getId());
        }

        @Test
        @DisplayName("Should return empty when debate not found")
        void shouldReturnEmptyWhenNotFound() {
            UUID id = UUID.randomUUID();
            when(debateRepository.findById(id)).thenReturn(Optional.empty());

            Optional<DebateDTO> result = debateService.getDebateById(id);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should get active debates")
        void shouldGetActiveDebates() {
            Debate debate1 = new Debate("Topic 1", challenger, defender);
            debate1.setId(UUID.randomUUID());
            debate1.setStatus(DebateStatus.ACTIVE);

            when(debateRepository.findByStatusIn(List.of(DebateStatus.ACTIVE, DebateStatus.VOTING)))
                    .thenReturn(List.of(debate1));

            List<DebateDTO> result = debateService.getActiveDebates();

            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should get debates by user")
        void shouldGetDebatesByUser() {
            Debate debate = new Debate("Topic", challenger, defender);
            debate.setId(UUID.randomUUID());

            when(userRepository.findById(challenger.getId())).thenReturn(Optional.of(challenger));
            when(debateRepository.findByParticipant(challenger)).thenReturn(List.of(debate));

            List<DebateDTO> result = debateService.getDebatesByUser(challenger.getId());

            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should get pending challenges for user")
        void shouldGetPendingChallenges() {
            Debate debate = new Debate("Topic", challenger, defender);
            debate.setId(UUID.randomUUID());
            debate.setStatus(DebateStatus.PENDING);

            when(userRepository.findById(defender.getId())).thenReturn(Optional.of(defender));
            when(debateRepository.findPendingChallengesForUser(defender)).thenReturn(List.of(debate));

            List<DebateDTO> result = debateService.getPendingChallengesForUser(defender.getId());

            assertEquals(1, result.size());
        }
    }
}
