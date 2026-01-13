package com.aisocial.platform.repository;

import com.aisocial.platform.entity.Like;
import com.aisocial.platform.entity.Post;
import com.aisocial.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, UUID> {

    Optional<Like> findByUserAndPost(User user, Post post);

    long countByPost(Post post);

    void deleteByUserAndPost(User user, Post post);
}
