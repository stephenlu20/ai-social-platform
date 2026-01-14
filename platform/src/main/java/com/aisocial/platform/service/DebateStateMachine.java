package com.aisocial.platform.service;

import com.aisocial.platform.entity.Debate;
import com.aisocial.platform.entity.DebateArgument;
import com.aisocial.platform.entity.DebateStatus;
import com.aisocial.platform.entity.User;
import com.aisocial.platform.repository.DebateArgumentRepository;
import com.aisocial.platform.repository.DebateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class DebateStateMachine {

    private static final int MAX_ROUNDS = 3;

    private final DebateArgumentRepository debateArgumentRepository;
    private final DebateRepository debateRepository;

    public DebateStateMachine(DebateArgumentRepository debateArgumentRepository, DebateRepository debateRepository) {
        this.debateArgumentRepository = debateArgumentRepository;
        this.debateRepository = debateRepository;
    }

    /**
     * Check if a user can submit an argument in the current state.
     */
    public boolean canSubmitArgument(Debate debate, User user) {
        if (debate.getStatus() != DebateStatus.ACTIVE) {
            return false;
        }

        if (!isParticipant(debate, user)) {
            return false;
        }

        if (!user.getId().equals(debate.getWhoseTurn().getId())) {
            return false;
        }

        return !hasUserArguedInRound(debate, user, debate.getCurrentRound());     
    }

    @Transactional
    public DebateArgument submitArgument(Debate debate, User user, String content) {
        if (!canSubmitArgument(debate, user)) {
            throw new IllegalStateException("User cannot submit an argument at this time");
        }

        DebateArgument argument = new DebateArgument(debate, user, debate.getCurrentRound(), content);
        argument = debateArgumentRepository.save(argument);

        advanceState(debate);

        return argument;
    }

    public User getExpectedTurn(Debate debate) {
        if (debate.getStatus() != DebateStatus.ACTIVE) {
            return null;
        }

        int currentRound = debate.getCurrentRound();
        User firstMover = getFirstMoverForRound(debate, currentRound);
        User secondMover = getOtherParticipant(debate, firstMover);

        boolean firstMoverArgued = hasUserArguedInRound(debate, firstMover, currentRound);

        if (!firstMoverArgued) {
            return firstMover;
        }
        return secondMover;
    }

    /**
     * Check if both users have argued in a specific round.
     */
    public boolean isRoundComplete(Debate debate, int roundNumber) {
        boolean challengerArgued = hasUserArguedInRound(debate, debate.getChallenger(), roundNumber);
        boolean defenderArgued = hasUserArguedInRound(debate, debate.getDefender(), roundNumber);
        return challengerArgued && defenderArgued;
    }

    /**
     * Get who should argue first in a given round.
     * Round 1 & 3: Challenger first
     * Round 2: Defender first
     */
    public User getFirstMoverForRound(Debate debate, int roundNumber) {
        if (roundNumber == 2) {
            return debate.getDefender();
        }
        return debate.getChallenger();
    }

    private void advanceState(Debate debate) {
        int currentRound = debate.getCurrentRound();

        if (isRoundComplete(debate, currentRound)) {
            if (currentRound >= MAX_ROUNDS) {
                debate.setStatus(DebateStatus.VOTING);
                debate.setWhoseTurn(null);
                debate.setVotingEndsAt(Instant.now().plus(24, ChronoUnit.HOURS));
            } else {
                debate.setCurrentRound(currentRound + 1);
                debate.setWhoseTurn(getFirstMoverForRound(debate, currentRound + 1));
            }
        } else {
            debate.setWhoseTurn(getOtherParticipant(debate, debate.getWhoseTurn()));
        }
        
        debateRepository.save(debate);
    }
    
    private boolean isParticipant(Debate debate, User user) {
        return user.getId().equals(debate.getChallenger().getId()) ||
               user.getId().equals(debate.getDefender().getId());
    }

    private boolean hasUserArguedInRound(Debate debate, User user, int roundNumber) {
        return debateArgumentRepository
                 .findByDebateAndUserAndRoundNumber(debate, user, roundNumber).isPresent();
    }

    private User getOtherParticipant(Debate debate, User user) {
        if (user.getId().equals(debate.getChallenger().getId())) {
            return debate.getDefender();
        }
        return debate.getChallenger();
    }
}
