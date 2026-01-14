package com.aisocial.platform.service;

import com.aisocial.platform.entity.Like;
import java.util.UUID;

public interface LikeService {

    Like likePost(UUID userId, UUID postId);

    void unlikePost(UUID userId, UUID postId);

    long countLikes(UUID postId);
}
