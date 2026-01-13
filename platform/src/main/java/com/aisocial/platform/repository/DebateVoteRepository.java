package com.aisocial.platform.repository;

import com.aisocial.platform.entity.Debate;
import com.aisocial.platform.entity.DebateVote;
import com.aisocial.platform.entity.VoteType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DebateVoteRepository extends JpaRepository<DebateVote, UUID> {

    Optional<DebateVote> findByDebateIdAndUserId(UUID debateId, UUID userId);

    long countByDebateIdAndVote(UUID debateId, VoteType vote);

    List<DebateVote> findByDebate(Debate dabate);
}
