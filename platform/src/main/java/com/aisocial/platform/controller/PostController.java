package com.aisocial.platform.controller;

import com.aisocial.platform.entity.Post;
import com.aisocial.platform.service.PostService;
import com.aisocial.platform.dto.CreatePostRequestDTO;
import com.aisocial.platform.dto.ReplyPostRequestDTO;
import com.aisocial.platform.dto.RepostRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "*") // adjust for your frontend
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * Create a new post
     */
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody CreatePostRequestDTO request) {
        Post post = postService.createPost(request.getUserId(), request.getContent());
        return new ResponseEntity<>(post, HttpStatus.CREATED);
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
}