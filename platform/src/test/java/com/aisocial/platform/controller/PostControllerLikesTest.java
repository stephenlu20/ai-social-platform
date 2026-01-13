package com.aisocial.platform.controller;

import com.aisocial.platform.entity.Like;
import com.aisocial.platform.repository.UserRepository;
import com.aisocial.platform.service.LikeService;
import com.aisocial.platform.service.PostService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PostController.class)
class PostControllerLikesTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public PostService postService() {
            return org.mockito.Mockito.mock(PostService.class);
        }

        @Bean
        public LikeService likeService() {
            return org.mockito.Mockito.mock(LikeService.class);
        }

        @Bean
        public UserRepository userRepository() {
            return org.mockito.Mockito.mock(UserRepository.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LikeService likeService;

    @Test
    void shouldLikePost() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        UUID likeId = UUID.randomUUID();

        // Mock Like object
        Like liked = new Like();
        liked.setId(likeId);

        // Mock the service behavior
        when(likeService.likePost(userId, postId)).thenReturn(liked);

        // Perform request
        mockMvc.perform(post("/posts/{postId}/like", postId)
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(likeId.toString()));
    }
}
