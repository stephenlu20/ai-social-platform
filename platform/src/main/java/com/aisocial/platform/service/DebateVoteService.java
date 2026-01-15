package com.aisocial.platform.service;

import com.aisocial.platform.entity.Debate;
import com.aisocial.platform.entity.DebateVote;
import com.aisocial.platform.entity.VoteType;
import com.aisocial.platform.repository.DebateRepository;
import com.aisocial.platform.repository.DebateVoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class DebateVoteService {

    private final DebateVoteRepository debateVoteRepository;
    private final DebateRepository debateRepository;

    public DebateVoteService(DebateVoteRepository debateVoteRepository, DebateRepository debateRepository) {
        this.debateVoteRepository = debateVoteRepository;
        this.debateRepository = debateRepository;
    }

    public boolean isParticipant(UUID debateId, UUID userId) {
        Optional<Debate> debate = debateRepository.findById(debateId);
        if (debate.isEmpty()) {
            return false;
        }
        Debate d = debate.get();
        return userId.equals(d.getChallenger().getId()) || userId.equals(d.getDefender().getId());
    }

    public List<DebateVote> findAll() {
        return debateVoteRepository.findAll();
    }

    public DebateVote save(DebateVote vote) {
        if (isParticipant(vote.getDebateId(), vote.getUserId())) {
            throw new IllegalArgumentException("Debate participants cannot vote on their own debate");
        }
        if (debateVoteRepository.findByDebateIdAndUserId(vote.getDebateId(), vote.getUserId()).isPresent()) {
            throw new IllegalArgumentException("User has already voted on this debate");
        }

        DebateVote savedVote = debateVoteRepository.save(vote);

        // Update debate vote counts
        Debate debate = debateRepository.findById(vote.getDebateId())
                .orElseThrow(() -> new IllegalArgumentException("Debate not found"));
        incrementVoteCount(debate, vote.getVote());
        debateRepository.save(debate);

        return savedVote;
    }

    public DebateVote update(UUID id, VoteType newVote) {
        Optional<DebateVote> existing = debateVoteRepository.findById(id);
        if (existing.isPresent()) {
            DebateVote vote = existing.get();
            VoteType oldVote = vote.getVote();

            // Update debate vote counts if vote type changed
            if (oldVote != newVote) {
                Debate debate = debateRepository.findById(vote.getDebateId())
                        .orElseThrow(() -> new IllegalArgumentException("Debate not found"));
                decrementVoteCount(debate, oldVote);
                incrementVoteCount(debate, newVote);
                debateRepository.save(debate);
            }

            vote.setVote(newVote);
            return debateVoteRepository.save(vote);
        }
        return null; // not found
    }

    public Optional<DebateVote> findById(UUID id) {
        return debateVoteRepository.findById(id);
    }

    public List<DebateVote> findByDebateId(UUID debateId) {
        return debateVoteRepository.findByDebateId(debateId);
    }

    public List<DebateVote> findByUserId(UUID userId) {
        return debateVoteRepository.findByUserId(userId);
    }

    public boolean delete(UUID id) {
        Optional<DebateVote> existing = debateVoteRepository.findById(id);
        if (existing.isPresent()) {
            DebateVote vote = existing.get();

            // Update debate vote counts
            debateRepository.findById(vote.getDebateId()).ifPresent(debate -> {
                decrementVoteCount(debate, vote.getVote());
                debateRepository.save(debate);
            });

            debateVoteRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void incrementVoteCount(Debate debate, VoteType voteType) {
        switch (voteType) {
            case CHALLENGER -> debate.incrementVotesChallenger();
            case DEFENDER -> debate.incrementVotesDefender();
            case TIE -> debate.incrementVotesTie();
        }
    }

    private void decrementVoteCount(Debate debate, VoteType voteType) {
        switch (voteType) {
            case CHALLENGER -> debate.decrementVotesChallenger();
            case DEFENDER -> debate.decrementVotesDefender();
            case TIE -> debate.decrementVotesTie();
        }
    }
}
