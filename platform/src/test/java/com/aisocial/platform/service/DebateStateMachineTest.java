package com.aisocial.platform.service;

import com.aisocial.platform.entity.Debate;
import com.aisocial.platform.entity.DebateArgument;
import com.aisocial.platform.entity.DebateStatus;
import com.aisocial.platform.entity.User;
import com.aisocial.platform.repository.DebateArgumentRepository;
import com.aisocial.platform.repository.DebateRepository;
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
@DisplayName("Debate State Machine Tests")
class DebateStateMachineTest {

    @Mock
    private DebateArgumentRepository debateArgumentRepository;

    @Mock
    private DebateRepository debateRepository;

    @InjectMocks
    private DebateStateMachine stateMachine;

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
        debate.setStatus(DebateStatus.ACTIVE);
        debate.setCurrentRound(1);
        debate.setWhoseTurn(challenger);
    }

    @Nested
    @DisplayName("canSubmitArgument Tests")
    class CanSubmitArgumentTests {

        @Test
        @DisplayName("Should allow challenger to argue when it's their turn")
        void shouldAllowChallengerWhenTheirTurn() {
            when(debateArgumentRepository.findByDebateAndUserAndRoundNumber(debate, challenger, 1))
                    .thenReturn(Optional.empty());

            assertTrue(stateMachine.canSubmitArgument(debate, challenger));
        }

        @Test
        @DisplayName("Should not allow defender when it's challenger's turn")
        void shouldNotAllowDefenderWhenChallengersTurn() {
            assertFalse(stateMachine.canSubmitArgument(debate, defender));
        }

        @Test
        @DisplayName("Should not allow outsider to argue")
        void shouldNotAllowOutsider() {
            assertFalse(stateMachine.canSubmitArgument(debate, outsider));
        }

        @Test
        @DisplayName("Should not allow argument when debate is not active")
        void shouldNotAllowWhenNotActive() {
            debate.setStatus(DebateStatus.PENDING);
            assertFalse(stateMachine.canSubmitArgument(debate, challenger));
        }

        @Test
        @DisplayName("Should not allow argument when user already argued this round")
        void shouldNotAllowWhenAlreadyArgued() {
            DebateArgument existingArg = new DebateArgument(debate, challenger, 1, "Already argued");
            when(debateArgumentRepository.findByDebateAndUserAndRoundNumber(debate, challenger, 1))
                    .thenReturn(Optional.of(existingArg));

            assertFalse(stateMachine.canSubmitArgument(debate, challenger));
        }
    }

    @Nested
    @DisplayName("submitArgument Tests")
    class SubmitArgumentTests {

        @Test
        @DisplayName("Should submit argument and switch turn to defender")
        void shouldSubmitAndSwitchTurn() {
            when(debateArgumentRepository.findByDebateAndUserAndRoundNumber(debate, challenger, 1))
                    .thenReturn(Optional.empty());
            when(debateArgumentRepository.save(any(DebateArgument.class)))
                    .thenAnswer(inv -> inv.getArgument(0));
            // After challenger argues, defender hasn't argued yet
            when(debateArgumentRepository.findByDebateAndUserAndRoundNumber(debate, defender, 1))
                    .thenReturn(Optional.empty());

            DebateArgument result = stateMachine.submitArgument(debate, challenger, "My argument");

            assertNotNull(result);
            assertEquals(defender, debate.getWhoseTurn());
            assertEquals(1, debate.getCurrentRound());
            verify(debateRepository).save(debate);
        }

        @Test
        @DisplayName("Should throw exception when user cannot submit")
        void shouldThrowWhenCannotSubmit() {
            debate.setWhoseTurn(defender);

            assertThrows(IllegalStateException.class,
                    () -> stateMachine.submitArgument(debate, challenger, "My argument"));
        }
    }

    @Nested
    @DisplayName("Round Advancement Tests")
    class RoundAdvancementTests {

        @Test
        @DisplayName("Should advance to round 2 after both argue in round 1")
        void shouldAdvanceToRound2() {
            // Setup: defender submits the second argument of round 1
            // Challenger has already argued, it's defender's turn
            debate.setWhoseTurn(defender);

            when(debateArgumentRepository.findByDebateAndUserAndRoundNumber(debate, challenger, 1))
                    .thenReturn(Optional.of(new DebateArgument(debate, challenger, 1, "challenger arg")));
            when(debateArgumentRepository.findByDebateAndUserAndRoundNumber(debate, defender, 1))
                    .thenReturn(Optional.empty())  // Before submission
                    .thenReturn(Optional.of(new DebateArgument(debate, defender, 1, "defender arg"))); // After submission
            when(debateArgumentRepository.save(any(DebateArgument.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Defender submits, completing round 1
            stateMachine.submitArgument(debate, defender, "Defender arg");

            // Should advance to round 2 with defender first (alternating)
            assertEquals(2, debate.getCurrentRound());
            assertEquals(defender, debate.getWhoseTurn());
        }

        @Test
        @DisplayName("Should transition to VOTING after round 3 completes")
        void shouldTransitionToVotingAfterRound3() {
            debate.setCurrentRound(3);
            debate.setWhoseTurn(defender);

            // Both have argued in round 3 after this submission
            when(debateArgumentRepository.findByDebateAndUserAndRoundNumber(debate, challenger, 3))
                    .thenReturn(Optional.of(new DebateArgument(debate, challenger, 3, "arg")));
            when(debateArgumentRepository.findByDebateAndUserAndRoundNumber(debate, defender, 3))
                    .thenReturn(Optional.empty())
                    .thenReturn(Optional.of(new DebateArgument(debate, defender, 3, "arg")));
            when(debateArgumentRepository.save(any(DebateArgument.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            stateMachine.submitArgument(debate, defender, "Final argument");

            assertEquals(DebateStatus.VOTING, debate.getStatus());
            assertNull(debate.getWhoseTurn());
            assertNotNull(debate.getVotingEndsAt());
        }
    }

    @Nested
    @DisplayName("First Mover Logic Tests")
    class FirstMoverLogicTests {

        @Test
        @DisplayName("Challenger should be first mover in round 1")
        void challengerFirstInRound1() {
            assertEquals(challenger, stateMachine.getFirstMoverForRound(debate, 1));
        }

        @Test
        @DisplayName("Defender should be first mover in round 2")
        void defenderFirstInRound2() {
            assertEquals(defender, stateMachine.getFirstMoverForRound(debate, 2));
        }

        @Test
        @DisplayName("Challenger should be first mover in round 3")
        void challengerFirstInRound3() {
            assertEquals(challenger, stateMachine.getFirstMoverForRound(debate, 3));
        }
    }

    @Nested
    @DisplayName("getExpectedTurn Tests")
    class GetExpectedTurnTests {

        @Test
        @DisplayName("Should return null when debate is not active")
        void shouldReturnNullWhenNotActive() {
            debate.setStatus(DebateStatus.VOTING);
            assertNull(stateMachine.getExpectedTurn(debate));
        }

        @Test
        @DisplayName("Should return first mover when no one has argued")
        void shouldReturnFirstMoverWhenNoOneArgued() {
            when(debateArgumentRepository.findByDebateAndUserAndRoundNumber(debate, challenger, 1))
                    .thenReturn(Optional.empty());

            assertEquals(challenger, stateMachine.getExpectedTurn(debate));
        }

        @Test
        @DisplayName("Should return second mover when first has argued")
        void shouldReturnSecondMoverWhenFirstArgued() {
            when(debateArgumentRepository.findByDebateAndUserAndRoundNumber(debate, challenger, 1))
                    .thenReturn(Optional.of(new DebateArgument(debate, challenger, 1, "arg")));

            assertEquals(defender, stateMachine.getExpectedTurn(debate));
        }

        @Test
        @DisplayName("Should return defender as first mover in round 2")
        void shouldReturnDefenderFirstInRound2() {
            debate.setCurrentRound(2);
            when(debateArgumentRepository.findByDebateAndUserAndRoundNumber(debate, defender, 2))
                    .thenReturn(Optional.empty());

            assertEquals(defender, stateMachine.getExpectedTurn(debate));
        }
    }

    @Nested
    @DisplayName("isRoundComplete Tests")
    class IsRoundCompleteTests {

        @Test
        @DisplayName("Should return false when no one has argued")
        void shouldReturnFalseWhenNoOneArgued() {
            when(debateArgumentRepository.findByDebateAndUserAndRoundNumber(debate, challenger, 1))
                    .thenReturn(Optional.empty());
            when(debateArgumentRepository.findByDebateAndUserAndRoundNumber(debate, defender, 1))
                    .thenReturn(Optional.empty());

            assertFalse(stateMachine.isRoundComplete(debate, 1));
        }

        @Test
        @DisplayName("Should return false when only one has argued")
        void shouldReturnFalseWhenOnlyOneArgued() {
            when(debateArgumentRepository.findByDebateAndUserAndRoundNumber(debate, challenger, 1))
                    .thenReturn(Optional.of(new DebateArgument(debate, challenger, 1, "arg")));
            when(debateArgumentRepository.findByDebateAndUserAndRoundNumber(debate, defender, 1))
                    .thenReturn(Optional.empty());

            assertFalse(stateMachine.isRoundComplete(debate, 1));
        }

        @Test
        @DisplayName("Should return true when both have argued")
        void shouldReturnTrueWhenBothArgued() {
            when(debateArgumentRepository.findByDebateAndUserAndRoundNumber(debate, challenger, 1))
                    .thenReturn(Optional.of(new DebateArgument(debate, challenger, 1, "arg")));
            when(debateArgumentRepository.findByDebateAndUserAndRoundNumber(debate, defender, 1))
                    .thenReturn(Optional.of(new DebateArgument(debate, defender, 1, "arg")));

            assertTrue(stateMachine.isRoundComplete(debate, 1));
        }
    }
}
