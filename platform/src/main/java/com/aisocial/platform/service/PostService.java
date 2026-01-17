package com.aisocial.platform.service;

import com.aisocial.platform.dto.PostResponseDTO;
import com.aisocial.platform.dto.PostSearchRequestDTO;
import com.aisocial.platform.dto.PostStyleDTO;
import com.aisocial.platform.entity.Post;
import com.aisocial.platform.entity.User;
import org.springframework.data.domain.Page;    

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostService {

    Post createPost(UUID authorId, String content);

    PostResponseDTO createPostWithFactCheck(UUID authorId, String content, boolean factCheck);

    PostResponseDTO createPostWithFactCheckAndStyle(UUID authorId, String content, boolean factCheck, PostStyleDTO style);

    Post replyToPost(UUID authorId, UUID parentPostId, String content);

    Post repost(UUID authorId, UUID originalPostId);

    void deletePost(UUID authorId, UUID postId);

    Optional<Post> getPostById(UUID id);

    List<Post> getPostsByUser(User user);

    List<PostResponseDTO> getPostsByUserId(UUID userId);

    List<PostResponseDTO> getRepliesByUserId(UUID userId);

    void likePost(Post post);

    void incrementReplyCount(Post post);

    void incrementRepostCount(Post post);

    List<PostResponseDTO> getFeedForUser(UUID userId);

    List<Post> getReplies(UUID postId);

    Page<PostResponseDTO> searchPosts(PostSearchRequestDTO request);

    PostResponseDTO convertPostToDTO(Post post, UUID currentUserId);
}
