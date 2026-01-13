package com.aisocial.platform.controller;

import com.aisocial.platform.entity.Post;
import com.aisocial.platform.service.PostService;
import com.aisocial.platform.dto.CreatePostRequestDTO;
import com.aisocial.platform.dto.ReplyPostRequestDTO;
import com.aisocial.platform.dto.RepostRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostController Unit Tests")
class PostControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    private ObjectMapper objectMapper;

    private UUID userId;
    private UUID postId;
    private Post post;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();

        userId = UUID.randomUUID();
        postId = UUID.randomUUID();

        post = new Post();
        post.setId(postId);
        post.setContent("Test content");
    }

    @Test
    @DisplayName("Should create a new post")
    void shouldCreatePost() throws Exception {
        CreatePostRequestDTO requestDTO = new CreatePostRequestDTO();
        requestDTO.setUserId(userId);
        requestDTO.setContent("Test content");

        when(postService.createPost(eq(userId), eq("Test content"))).thenReturn(post);

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(post.getId().toString()))
                .andExpect(jsonPath("$.content").value("Test content"));
    }

    @Test
    @DisplayName("Should reply to a post")
    void shouldReplyToPost() throws Exception {
        ReplyPostRequestDTO requestDTO = new ReplyPostRequestDTO();
        requestDTO.setUserId(userId);
        requestDTO.setContent("Reply content");

        Post replyPost = new Post();
        replyPost.setId(UUID.randomUUID());
        replyPost.setContent("Reply content");

        when(postService.replyToPost(eq(userId), eq(postId), eq("Reply content"))).thenReturn(replyPost);

        mockMvc.perform(post("/posts/{postId}/reply", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(replyPost.getId().toString()))
                .andExpect(jsonPath("$.content").value("Reply content"));
    }

    @Test
    @DisplayName("Should repost a post")
    void shouldRepost() throws Exception {
        RepostRequestDTO requestDTO = new RepostRequestDTO();
        requestDTO.setUserId(userId);

        Post repost = new Post();
        repost.setId(UUID.randomUUID());
        repost.setContent("Test content");

        when(postService.repost(eq(userId), eq(postId))).thenReturn(repost);

        mockMvc.perform(post("/posts/{postId}/repost", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(repost.getId().toString()))
                .andExpect(jsonPath("$.content").value("Test content"));
    }

    @Test
    @DisplayName("Should delete a post")
    void shouldDeletePost() throws Exception {
        doNothing().when(postService).deletePost(userId, postId);

        mockMvc.perform(delete("/posts/{postId}", postId)
                        .param("userId", userId.toString()))
                .andExpect(status().isNoContent());
    }
}
