package com.aisocial.platform.service;

import com.aisocial.platform.dto.DebateDTO;
import com.aisocial.platform.entity.Debate;
import com.aisocial.platform.entity.DebateStatus;
import com.aisocial.platform.entity.User;
import com.aisocial.platform.repository.DebateRepository;
import com.aisocial.platform.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DebateServiceImpl implements DebateService {

    private final DebateRepository debateRepository;
    private final UserRepository userRepository;

    public DebateServiceImpl(DebateRepository debateRepository, UserRepository userRepository) {
        this.debateRepository = debateRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public DebateDTO createChallenge(UUID challengerId, UUID defenderId, String topic) {
        if (challengerId.equals(defenderId)) {
            throw new IllegalArgumentException("Cannot challenge yourself");
        }

        User challenger = userRepository.findById(challengerId)
                .orElseThrow(() -> new IllegalArgumentException("Challenger not found"));

        User defender = userRepository.findById(defenderId)
                .orElseThrow(() -> new IllegalArgumentException("Defender not found"));

        Debate debate = new Debate(topic, challenger, defender);
        debate.setStatus(DebateStatus.ACTIVE);
        debate.setWhoseTurn(challenger);

        Debate saved = debateRepository.save(debate);
        return DebateDTO.fromEntity(saved);
    }

    @Override
    @Transactional
    public DebateDTO acceptChallenge(UUID debateId, UUID userId) {
        Debate debate = debateRepository.findById(debateId)
                .orElseThrow(() -> new IllegalArgumentException("Debate not found"));

        if (!debate.getDefender().getId().equals(userId)) {
            throw new IllegalStateException("Only the defender can accept the challenge");
        }

        if (debate.getStatus() != DebateStatus.PENDING) {
            throw new IllegalStateException("Challenge is not pending");
        }

        debate.setStatus(DebateStatus.ACTIVE);
        debate.setWhoseTurn(debate.getChallenger());

        Debate saved = debateRepository.save(debate);
        return DebateDTO.fromEntity(saved);
    }

    @Override
    @Transactional
    public void declineChallenge(UUID debateId, UUID userId) {
        Debate debate = debateRepository.findById(debateId)
                .orElseThrow(() -> new IllegalArgumentException("Debate not found"));

        if (!debate.getDefender().getId().equals(userId)) {
            throw new IllegalStateException("Only the defender can decline the challenge");
        }

        if (debate.getStatus() != DebateStatus.PENDING) {
            throw new IllegalStateException("Challenge is not pending");
        }

        debateRepository.delete(debate);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DebateDTO> getDebateById(UUID debateId) {
        return debateRepository.findById(debateId)
                .map(DebateDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DebateDTO> getDebatesByStatus(DebateStatus status) {
        return debateRepository.findByStatus(status).stream()
                .map(DebateDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DebateDTO> getActiveDebates() {
        return debateRepository.findByStatusIn(List.of(DebateStatus.ACTIVE)).stream()
                .map(DebateDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DebateDTO> getCompletedDebates() {
        return debateRepository.findByStatusIn(List.of(DebateStatus.COMPLETED)).stream()
                .map(DebateDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DebateDTO> getVotingDebates() {
        return debateRepository.findByStatus(DebateStatus.VOTING).stream()
                .map(DebateDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DebateDTO> getDebatesByUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return debateRepository.findByParticipant(user).stream()
                .map(DebateDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DebateDTO> getPendingChallengesForUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return debateRepository.findPendingChallengesForUser(user).stream()
                .map(DebateDTO::fromEntity)
                .toList();
    }
}
