package com.aisocial.platform.service;

import com.aisocial.platform.entity.Like;
import com.aisocial.platform.entity.Post;
import com.aisocial.platform.entity.User;
import com.aisocial.platform.repository.LikeRepository;
import com.aisocial.platform.repository.PostRepository;
import com.aisocial.platform.repository.UserRepository; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public LikeServiceImpl(LikeRepository likeRepository, UserRepository userRepository, PostRepository postRepository) {
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @Override
    @Transactional
    public Like likePost(UUID userId, UUID postId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        // Allow liking own posts
        return likeRepository.findByUserAndPost(user, post)
            .orElseGet(() -> {
                Like like = new Like(user, post);
                post.incrementLikeCount();
                postRepository.save(post);
                return likeRepository.save(like);
            });
    }

    @Override
    @Transactional
    public void unlikePost(UUID userId, UUID postId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        likeRepository.findByUserAndPost(user, post).ifPresent(like -> {
            likeRepository.delete(like);
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            postRepository.save(post);
        });
    }

    @Override
    public long countLikes(UUID postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        return likeRepository.countByPost(post);
    }
}
