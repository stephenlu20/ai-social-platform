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
        return debateVoteRepository.save(vote);
    }

    public DebateVote update(UUID id, VoteType newVote) {
        Optional<DebateVote> existing = debateVoteRepository.findById(id);
        if (existing.isPresent()) {
            DebateVote vote = existing.get();
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
            debateVoteRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
