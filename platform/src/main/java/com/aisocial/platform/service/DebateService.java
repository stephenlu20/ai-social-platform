package com.aisocial.platform.service;

import com.aisocial.platform.dto.DebateDTO;
import com.aisocial.platform.entity.DebateStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DebateService {

    DebateDTO createChallenge(UUID challengerId, UUID defenderId, String topic);

    DebateDTO acceptChallenge(UUID debateId, UUID userId);

    DebateDTO declineChallenge(UUID debateId, UUID userId);

    Optional<DebateDTO> getDebateById(UUID debateId);

    List<DebateDTO> getDebatesByStatus(DebateStatus status);

    List<DebateDTO> getActiveDebates();

    List<DebateDTO> getVotingDebates();

    List<DebateDTO> getDebatesByUser(UUID userId);

    List<DebateDTO> getPendingChallengesForUser(UUID userId);
}
