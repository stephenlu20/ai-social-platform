package com.aisocial.platform.repository;

import com.aisocial.platform.entity.DebateArgument;
import com.aisocial.platform.entity.Debate;
import com.aisocial.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DebateArgumentRepository extends JpaRepository<DebateArgument, UUID> {

    // Find all arguments for a given debate, optionally ordered by round number
    List<DebateArgument> findByDebateOrderByRoundNumberAsc(Debate debate);

    // Find all arguments by a specific user
    List<DebateArgument> findByUser(User user);

    // Find a specific argument by debate, user, and round number
    Optional<DebateArgument> findByDebateAndUserAndRoundNumber(Debate debate, User user, Integer roundNumber);

    // Delete all arguments for a debate
    void deleteById(UUID id);

    //Count arguments in a specific round
    int countByDebateAndRoundNumber(Debate debate, Integer roundNumber);

    // Find all arguments for a debate by debate ID
    List<DebateArgument> findByDebateIdOrderByRoundNumberAscCreatedAtAsc(UUID debateId);
}
