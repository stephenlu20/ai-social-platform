package com.aisocial.platform.repository;

import com.aisocial.platform.entity.DebateVote;
import com.aisocial.platform.entity.VoteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DebateVoteRepository extends JpaRepository<DebateVote, UUID> {

    // Find all votes for a specific debate
    List<DebateVote> findByDebateId(UUID debateId);

    // Find all votes by a specific user
    List<DebateVote> findByUserId(UUID userId);

    // Find a specific vote by debate and user (unique constraint)
    Optional<DebateVote> findByDebateIdAndUserId(UUID debateId, UUID userId);

    // Count votes by type for a debate
    long countByDebateIdAndVote(UUID debateId, VoteType vote);

    // Delete all votes for a specific debate
    void deleteByDebateId(UUID debateId);

}
