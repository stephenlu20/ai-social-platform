package com.aisocial.platform.service;

import com.aisocial.platform.entity.Debate;
import com.aisocial.platform.entity.DebateVote;
import com.aisocial.platform.entity.User;
import com.aisocial.platform.entity.VoteType;
import com.aisocial.platform.repository.DebateRepository;
import com.aisocial.platform.repository.DebateVoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DebateVoteService Unit Tests")
class DebateVoteServiceUnitTest {

    @Mock
    private DebateVoteRepository debateVoteRepository;

    @Mock
    private DebateRepository debateRepository;

    @InjectMocks
    private DebateVoteService debateVoteService;

    private User challenger;
    private User defender;
    private User outsider;
    private Debate debate;

    @BeforeEach
    void setUp() {
        challenger = new User("challenger", "Challenger", "Bio");
        challenger.setId(UUID.randomUUID());

        defender = new User("defender", "Defender", "Bio");
        defender.setId(UUID.randomUUID());

        outsider = new User("outsider", "Outsider", "Bio");
        outsider.setId(UUID.randomUUID());

        debate = new Debate("Test Topic", challenger, defender);
        debate.setId(UUID.randomUUID());
    }

    @Nested
    @DisplayName("isParticipant Tests")
    class IsParticipantTests {

        @Test
        @DisplayName("Should return true for challenger")
        void shouldReturnTrueForChallenger() {
            when(debateRepository.findById(debate.getId())).thenReturn(Optional.of(debate));

            assertTrue(debateVoteService.isParticipant(debate.getId(), challenger.getId()));
        }

        @Test
        @DisplayName("Should return true for defender")
        void shouldReturnTrueForDefender() {
            when(debateRepository.findById(debate.getId())).thenReturn(Optional.of(debate));

            assertTrue(debateVoteService.isParticipant(debate.getId(), defender.getId()));
        }

        @Test
        @DisplayName("Should return false for outsider")
        void shouldReturnFalseForOutsider() {
            when(debateRepository.findById(debate.getId())).thenReturn(Optional.of(debate));

            assertFalse(debateVoteService.isParticipant(debate.getId(), outsider.getId()));
        }

        @Test
        @DisplayName("Should return false when debate not found")
        void shouldReturnFalseWhenDebateNotFound() {
            UUID nonExistentDebateId = UUID.randomUUID();
            when(debateRepository.findById(nonExistentDebateId)).thenReturn(Optional.empty());

            assertFalse(debateVoteService.isParticipant(nonExistentDebateId, outsider.getId()));
        }
    }

    @Nested
    @DisplayName("save Tests")
    class SaveTests {

        @Test
        @DisplayName("Should save vote when user is not a participant")
        void shouldSaveVoteWhenNotParticipant() {
            DebateVote vote = new DebateVote();
            vote.setDebateId(debate.getId());
            vote.setUserId(outsider.getId());
            vote.setVote(VoteType.CHALLENGER);

            when(debateRepository.findById(debate.getId())).thenReturn(Optional.of(debate));
            when(debateVoteRepository.save(any(DebateVote.class))).thenReturn(vote);

            DebateVote result = debateVoteService.save(vote);

            assertNotNull(result);
            verify(debateVoteRepository).save(vote);
        }

        @Test
        @DisplayName("Should throw exception when challenger tries to vote")
        void shouldThrowWhenChallengerVotes() {
            DebateVote vote = new DebateVote();
            vote.setDebateId(debate.getId());
            vote.setUserId(challenger.getId());
            vote.setVote(VoteType.CHALLENGER);

            when(debateRepository.findById(debate.getId())).thenReturn(Optional.of(debate));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> debateVoteService.save(vote)
            );

            assertEquals("Debate participants cannot vote on their own debate", exception.getMessage());
            verify(debateVoteRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when defender tries to vote")
        void shouldThrowWhenDefenderVotes() {
            DebateVote vote = new DebateVote();
            vote.setDebateId(debate.getId());
            vote.setUserId(defender.getId());
            vote.setVote(VoteType.DEFENDER);

            when(debateRepository.findById(debate.getId())).thenReturn(Optional.of(debate));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> debateVoteService.save(vote)
            );

            assertEquals("Debate participants cannot vote on their own debate", exception.getMessage());
            verify(debateVoteRepository, never()).save(any());
        }
    }
}
