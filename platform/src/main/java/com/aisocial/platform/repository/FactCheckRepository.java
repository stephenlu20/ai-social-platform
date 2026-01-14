package com.aisocial.platform.repository;

import com.aisocial.platform.entity.FactCheck;
import com.aisocial.platform.entity.FactCheckStatus;
import com.aisocial.platform.entity.Post;
import com.aisocial.platform.entity.DebateArgument;
import com.aisocial.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FactCheckRepository extends JpaRepository<FactCheck, UUID> {

    // Find all fact checks for a specific post
    List<FactCheck> findByPost(Post post);

    // Find all fact checks for a specific debate argument
    List<FactCheck> findByDebateArg(DebateArgument debateArg);

    // Find all fact checks requested by a specific user
    List<FactCheck> findByRequestedBy(User user);

    // Find fact checks by status
    List<FactCheck> findByStatus(FactCheckStatus status);

    // Find fact checks for a post with a specific status
    List<FactCheck> findByPostAndStatus(Post post, FactCheckStatus status);

    // Find fact checks for a debate argument with a specific status
    List<FactCheck> findByDebateArgAndStatus(DebateArgument debateArg, FactCheckStatus status);
}
