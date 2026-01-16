package com.aisocial.platform.controller;

import com.aisocial.platform.entity.Post;
import com.aisocial.platform.service.PostService;
import com.aisocial.platform.service.LikeService;
import com.aisocial.platform.dto.CreatePostRequestDTO;
import com.aisocial.platform.dto.PostResponseDTO;
import com.aisocial.platform.dto.PostSearchRequestDTO;
import com.aisocial.platform.dto.ReplyPostRequestDTO;
import com.aisocial.platform.dto.RepostRequestDTO;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "*") // adjust for your frontend
public class PostController {

    private final PostService postService;
    private final LikeService likeService;

    public PostController(PostService postService, LikeService likeService) {
        this.postService = postService;
        this.likeService = likeService;
    }

    /**
     * Create a new post
     */
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody CreatePostRequestDTO request) {
        Post post = postService.createPost(request.getUserId(), request.getContent());
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }

    @GetMapping("/feed/{userId}")
    public ResponseEntity<List<PostResponseDTO>> getFeed(@PathVariable UUID userId) {
        List<PostResponseDTO> feed = postService.getFeedForUser(userId);
        return ResponseEntity.ok(feed);
    }

    /**
     * Get all posts by a specific user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostResponseDTO>> getPostsByUser(@PathVariable UUID userId) {
        List<PostResponseDTO> posts = postService.getPostsByUserId(userId);
        return ResponseEntity.ok(posts);
    }

    /**
     * Get all replies made by a specific user
     */
    @GetMapping("/user/{userId}/replies")
    public ResponseEntity<List<PostResponseDTO>> getRepliesByUser(@PathVariable UUID userId) {
        List<PostResponseDTO> replies = postService.getRepliesByUserId(userId);
        return ResponseEntity.ok(replies);
    }

    /**
     * Reply to an existing post
     */
    @PostMapping("/{postId}/reply")
    public ResponseEntity<Post> replyToPost(
            @PathVariable UUID postId,
            @RequestBody ReplyPostRequestDTO request
    ) {
        Post reply = postService.replyToPost(request.getUserId(), postId, request.getContent());
        return new ResponseEntity<>(reply, HttpStatus.CREATED);
    }

    /**
     * Repost an existing post
     */
    @PostMapping("/{postId}/repost")
    public ResponseEntity<Post> repost(
            @PathVariable UUID postId,
            @RequestBody RepostRequestDTO request
    ) {
        Post repost = postService.repost(request.getUserId(), postId);
        return new ResponseEntity<>(repost, HttpStatus.CREATED);
    }

    /**
     * Delete a post
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable UUID postId,
            @RequestParam UUID userId
    ) {
        postService.deletePost(userId, postId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Void> unlikePost(@PathVariable UUID postId, @RequestParam UUID userId) {
        likeService.unlikePost(userId, postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}/likes/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable UUID postId) {
        long count = likeService.countLikes(postId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{postId}/replies")
    public ResponseEntity<List<Post>> getReplies(@PathVariable UUID postId) {
        List<Post> replies = postService.getReplies(postId);
        return ResponseEntity.ok(replies);
    }

    @PostMapping("/search")
    public Page<PostResponseDTO> searchPosts(@RequestBody PostSearchRequestDTO request) {
        return postService.searchPosts(request);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable UUID postId, 
            @RequestParam UUID userId) {
        
        boolean isNowLiked = likeService.toggleLike(userId, postId);
        long likeCount = likeService.countLikes(postId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("liked", isNowLiked);
        response.put("likeCount", likeCount);
        
        return ResponseEntity.ok(response);
    }
}