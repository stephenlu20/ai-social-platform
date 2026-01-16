package com.aisocial.platform.service;

import com.aisocial.platform.dto.FactCheckResultDTO;
import com.aisocial.platform.dto.PostResponseDTO;
import com.aisocial.platform.dto.PostSearchRequestDTO;
import com.aisocial.platform.dto.UserDTO;
import com.aisocial.platform.entity.FactCheckStatus;
import com.aisocial.platform.entity.Post;
import com.aisocial.platform.entity.User;
import com.aisocial.platform.repository.FollowRepository;
import com.aisocial.platform.repository.LikeRepository;
import com.aisocial.platform.repository.PostRepository;
import com.aisocial.platform.repository.UserRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private static final Logger log = LoggerFactory.getLogger(PostServiceImpl.class);

    private final PostRepository postRepository;
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final AIFactCheckService aiFactCheckService;
    private final ObjectMapper objectMapper;

    public PostServiceImpl(PostRepository postRepository,
                           FollowRepository followRepository,
                           UserRepository userRepository,
                           LikeRepository likeRepository,
                           AIFactCheckService aiFactCheckService,
                           ObjectMapper objectMapper) {
        this.postRepository = postRepository;
        this.followRepository = followRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.aiFactCheckService = aiFactCheckService;
        this.objectMapper = objectMapper;
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
    public PostResponseDTO createPostWithFactCheck(UUID authorId, String content, boolean factCheck) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Create the post
        Post post = new Post();
        post.setAuthor(author);
        post.setContent(content);
        post.setCreatedAt(Instant.now());

        FactCheckResultDTO factCheckResult = null;

        // Run fact-check if requested
        if (factCheck) {
            log.info("Running pre-publish fact-check for post by user {}", authorId);
            factCheckResult = aiFactCheckService.previewFactCheck(content);

            // Update post with fact-check results
            if (factCheckResult != null && factCheckResult.getError() == null) {
                post.setFactCheckStatus(mapVerdictToStatus(factCheckResult.getVerdict()));
                post.setFactCheckScore(factCheckResult.getConfidence() != null
                        ? factCheckResult.getConfidence() / 100.0 : null);
                post.setWasCheckedBefore(true);

                try {
                    post.setFactCheckData(objectMapper.writeValueAsString(factCheckResult));
                } catch (Exception e) {
                    log.warn("Could not serialize fact-check data", e);
                }
            }
        }

        // Save the post
        Post savedPost = postRepository.save(post);

        // Convert to DTO and include fact-check result
        PostResponseDTO dto = convertToDTO(savedPost, authorId);
        dto.setFactCheckResult(factCheckResult);

        return dto;
    }

    private FactCheckStatus mapVerdictToStatus(String verdict) {
        if (verdict == null) {
            return FactCheckStatus.UNCHECKED;
        }
        return switch (verdict.toUpperCase()) {
            case "VERIFIED" -> FactCheckStatus.VERIFIED;
            case "LIKELY_TRUE" -> FactCheckStatus.LIKELY_TRUE;
            case "DISPUTED" -> FactCheckStatus.DISPUTED;
            case "FALSE" -> FactCheckStatus.FALSE;
            case "UNVERIFIABLE" -> FactCheckStatus.UNVERIFIABLE;
            default -> FactCheckStatus.UNCHECKED;
        };
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
    public List<PostResponseDTO> getPostsByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Post> posts = postRepository.findByAuthorOrderByCreatedAtDesc(user);

        return posts.stream()
                .map(post -> convertToDTO(post, userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<PostResponseDTO> getRepliesByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Post> replies = postRepository.findByAuthorAndReplyToIsNotNullOrderByCreatedAtDesc(user);

        return replies.stream()
                .map(post -> convertToDTO(post, userId))
                .collect(Collectors.toList());
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
    public List<PostResponseDTO> getFeedForUser(UUID userId) {
        userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<User> followedUsers = followRepository.findFollowingByUserId(userId);

        if (followedUsers.isEmpty()) {
            return List.of();
        }

        List<Post> posts = postRepository.findFeedPostsByAuthors(followedUsers);
        
        return posts.stream()
            .map(post -> convertToDTO(post, userId))  // Pass userId here
            .collect(Collectors.toList());
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

        UUID userId = author.getId();

        return posts.map(post -> convertToDTO(post, userId));
    }

    private PostResponseDTO convertToDTO(Post post, UUID currentUserId) {
        PostResponseDTO dto = new PostResponseDTO();
        dto.setId(post.getId());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setLikeCount(post.getLikeCount());
        dto.setReplyCount(post.getReplyCount());
        dto.setRepostCount(post.getRepostCount());
        dto.setFactCheckStatus(post.getFactCheckStatus());
        dto.setFactCheckScore(post.getFactCheckScore());
        
        // Create UserDTO and populate isFollowing flag
        UserDTO authorDTO = new UserDTO(post.getAuthor());
        if (currentUserId != null && !currentUserId.equals(post.getAuthor().getId())) {
            boolean isFollowing = followRepository.existsByFollower_IdAndFollowing_Id(
                currentUserId, 
                post.getAuthor().getId()
            );
            authorDTO.setIsFollowing(isFollowing);
        } else {
            authorDTO.setIsFollowing(false);
        }
        dto.setAuthor(authorDTO);
        
        if (post.getReplyTo() != null) {
            dto.setReplyToId(post.getReplyTo().getId());
        }
        
        if (post.getRepostOf() != null) {
            dto.setRepostOfId(post.getRepostOf().getId());
            PostResponseDTO repostDto = new PostResponseDTO();
            repostDto.setId(post.getRepostOf().getId());
            repostDto.setContent(post.getRepostOf().getContent());
            
            // Also populate isFollowing for repost author
            UserDTO repostAuthorDTO = new UserDTO(post.getRepostOf().getAuthor());
            if (currentUserId != null && !currentUserId.equals(post.getRepostOf().getAuthor().getId())) {
                boolean isFollowing = followRepository.existsByFollower_IdAndFollowing_Id(
                    currentUserId, 
                    post.getRepostOf().getAuthor().getId()
                );
                repostAuthorDTO.setIsFollowing(isFollowing);
            } else {
                repostAuthorDTO.setIsFollowing(false);
            }
            repostDto.setAuthor(repostAuthorDTO);
            
            dto.setRepostOf(repostDto);
        }
        
        // Check if current user liked this post
        if (currentUserId != null) {
            boolean liked = likeRepository.existsByUser_IdAndPost_Id(currentUserId, post.getId());
            dto.setIsLikedByCurrentUser(liked);
        } else {
            dto.setIsLikedByCurrentUser(false);
        }

        // Check if current user reposted this post
        if (currentUserId != null) {
            boolean reposted = postRepository.existsByAuthor_IdAndRepostOf_Id(currentUserId, post.getId());
            dto.setIsRepostedByCurrentUser(reposted);
        } else {
            dto.setIsRepostedByCurrentUser(false);
        }

        return dto;
    }

    @Override
    public PostResponseDTO convertPostToDTO(Post post, UUID currentUserId) {
        return convertToDTO(post, currentUserId);
    }
}
