package com.aisocial.platform.service;

import com.aisocial.platform.dto.PostResponseDTO;
import com.aisocial.platform.dto.PostSearchRequestDTO;
import com.aisocial.platform.entity.Post;
import com.aisocial.platform.entity.User;
import com.aisocial.platform.repository.FollowRepository;
import com.aisocial.platform.repository.PostRepository;
import com.aisocial.platform.repository.UserRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public PostServiceImpl(PostRepository postRepository,
                           FollowRepository followRepository, 
                           UserRepository userRepository) {
        this.postRepository = postRepository;
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Post createPost(UUID authorId, String content) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Post post = new Post();
        post.setAuthor(author);
        post.setContent(content);
        post.setCreatedAt(Instant.now());

        return postRepository.save(post);
    }

    @Override
    public Post replyToPost(UUID authorId, UUID parentPostId, String content) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Post parent = postRepository.findById(parentPostId)
                .orElseThrow(() -> new IllegalArgumentException("Parent post not found"));

        Post reply = new Post();
        reply.setAuthor(author);
        reply.setContent(content);
        reply.setReplyTo(parent);

        parent.incrementReplyCount();
        postRepository.save(parent);

        return postRepository.save(reply);
    }

    @Override
    public Post repost(UUID authorId, UUID originalPostId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Post original = postRepository.findById(originalPostId)
                .orElseThrow(() -> new IllegalArgumentException("Original post not found"));

        Post repost = new Post();
        repost.setAuthor(author);
        repost.setRepostOf(original);

        original.incrementRepostCount();
        postRepository.save(original);

        return postRepository.save(repost);
    }

    @Override
    public void deletePost(UUID authorId, UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.getAuthor().getId().equals(authorId)) {
            throw new IllegalStateException("User is not the author of this post");
        }

        postRepository.delete(post);
    }

    @Override
    public Optional<Post> getPostById(UUID id) {
        return postRepository.findById(id);
    }

    @Override
    public List<Post> getPostsByUser(User user) {
        return postRepository.findByAuthor(user);
    }

    @Override
    public void likePost(Post post) {
        post.incrementLikeCount();
        postRepository.save(post);
    }

    @Override
    public void incrementReplyCount(Post post) {
        post.incrementReplyCount();
        postRepository.save(post);
    }

    @Override
    public void incrementRepostCount(Post post) {
        post.incrementRepostCount();
        postRepository.save(post);
    }

    @Override
    public List<Post> getFeedForUser(UUID userId) {
        userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<User> followedUsers = followRepository.findFollowingByUserId(userId);

        if (followedUsers.isEmpty()) {
            return List.of();
        }

        return postRepository.findFeedPostsByAuthors(followedUsers);
    }

    @Override
    public List<Post> getReplies(UUID postId) {
        return postRepository.findByReplyTo_IdOrderByCreatedAtAsc(postId);
    }
    
    @Override
    public Page<PostResponseDTO> searchPosts(PostSearchRequestDTO request) {

        User author = null;
        if (request.getAuthorId() != null) {
            author = userRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> new IllegalArgumentException("Author not found"));
        }

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        Page<Post> posts = postRepository.searchPosts(
                request.getQuery(),
                author,
                request.getStart(),
                request.getEnd(),
                pageable
        );

        // Map entity → DTO
        return posts.map(p -> new PostResponseDTO(
                p.getId(),
                p.getAuthor().getId(),
                p.getContent(),
                p.getReplyTo() != null ? p.getReplyTo().getId() : null,
                p.getRepostOf() != null ? p.getRepostOf().getId() : null,
                p.getCreatedAt(),
                p.getLikeCount(),
                p.getReplyCount(),
                p.getRepostCount()
        ));
    }
}
