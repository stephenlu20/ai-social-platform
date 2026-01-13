package com.aisocial.platform.repository;

import com.aisocial.platform.entity.Debate;
import com.aisocial.platform.entity.DebateArgument;
import com.aisocial.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DebateArgumentRepository extends JpaRepository<DebateArgument, UUID> {

    List<DebateArgument> findByDebateOrderByRoundNumberAsc(Debate debate);

    Optional<DebateArgument> findByDebateAndUserAndRoundNumber(Debate debate, User user, Integer roundNumber);
}
