package com.aisocial.platform.repository;

import com.aisocial.platform.entity.Debate;
import com.aisocial.platform.entity.DebateStatus;
import com.aisocial.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DebateRepository extends JpaRepository<Debate, UUID> {

    List<Debate> findByStatus(DebateStatus status);

    List<Debate> findByChallenger(User challenger);

    List<Debate> findByDefender(User defender);

    @Query("""
        SELECT d
        FROM Debate d
        WHERE d.challenger = :user OR d.defender = :user
        ORDER BY d.createdAt DESC
    """)
    List<Debate> findByParticipant(@Param("user") User user);

    @Query("""
        SELECT d
        FROM Debate d
        WHERE (d.challenger = :user OR d.defender = :user)
        AND d.status = :status
        ORDER BY d.createdAt DESC
    """)
    List<Debate> findByParticipantAndStatus(@Param("user") User user, @Param("status") DebateStatus status);

    @Query("""
        SELECT d
        FROM Debate d
        WHERE d.defender = :user AND d.status = 'PENDING'
        ORDER BY d.createdAt DESC
    """)
    List<Debate> findPendingChallengesForUser(@Param("user") User user);

    @Query("""
        SELECT d
        FROM Debate d
        WHERE d.status IN :statuses
        ORDER BY d.createdAt DESC
    """)
    List<Debate> findByStatusIn(@Param("statuses") List<DebateStatus> statuses);

    boolean existsByChallenger(User challenger);

    boolean existsByDefender(User defender);
}
