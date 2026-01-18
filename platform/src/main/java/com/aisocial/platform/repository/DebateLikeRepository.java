package com.aisocial.platform.repository;

import com.aisocial.platform.entity.DebateLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DebateLikeRepository extends JpaRepository<DebateLike, UUID> {

    Optional<DebateLike> findByDebateIdAndUserId(UUID debateId, UUID userId);

    boolean existsByDebateIdAndUserId(UUID debateId, UUID userId);

    long countByDebateId(UUID debateId);
}
