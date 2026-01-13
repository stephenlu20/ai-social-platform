package com.aisocial.platform.repository;

import com.aisocial.platform.entity.DebateArgument;
import com.aisocial.platform.entity.FactCheck;
import com.aisocial.platform.entity.Post;
import com.aisocial.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FactCheckRepository extends JpaRepository<FactCheck, UUID> {

    List<FactCheck> findByPost(Post post);

    List<FactCheck> findByDebateArg(DebateArgument debateArg);

    List<FactCheck> findByRequestedBy(User user);

}
