package com.aisocial.platform.controller;

import com.aisocial.platform.entity.Follow;
import com.aisocial.platform.entity.User;
import com.aisocial.platform.repository.FollowRepository;
import com.aisocial.platform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.ServletException;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("FollowController Integration Tests")
class FollowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowRepository followRepository;

    private User testUser1;
    private User testUser2;
    private User testUser3;

    @BeforeEach
    void setUp() {
        followRepository.deleteAll();
        userRepository.deleteAll();

        testUser1 = new User("scientist", "Dr. Sarah Chen", "Climate researcher");
        testUser1 = userRepository.save(testUser1);

        testUser2 = new User("journalist", "Mike Thompson", "Investigative journalist");
        testUser2 = userRepository.save(testUser2);

        testUser3 = new User("historian", "Emma Wilson", "History professor");
        testUser3 = userRepository.save(testUser3);
    }

    @Nested
    @DisplayName("POST /api/users/{id}/follow")
    class FollowUserTests {

        @Test
        @DisplayName("Should successfully follow a user")
        void shouldFollowUser() throws Exception {
            mockMvc.perform(post("/api/users/{id}/follow", testUser2.getId())
                            .header("X-User-Id", testUser1.getId().toString()))
                    .andExpect(status().isOk());

            assertTrue(followRepository.existsByFollower_IdAndFollowing_Id(
                    testUser1.getId(), testUser2.getId()));
        }

        @Test
        @DisplayName("Should throw exception when trying to follow self")
        void shouldThrowExceptionWhenFollowingSelf() {
            ServletException exception = assertThrows(ServletException.class, () ->
                    mockMvc.perform(post("/api/users/{id}/follow", testUser1.getId())
                            .header("X-User-Id", testUser1.getId().toString())));

            assertTrue(exception.getCause() instanceof IllegalStateException);
            assertEquals("Users cannot follow themselves", exception.getCause().getMessage());
        }

        @Test
        @DisplayName("Should throw exception when already following")
        void shouldThrowExceptionWhenAlreadyFollowing() {
            Follow follow = new Follow(testUser1, testUser2);
            followRepository.save(follow);

            ServletException exception = assertThrows(ServletException.class, () ->
                    mockMvc.perform(post("/api/users/{id}/follow", testUser2.getId())
                            .header("X-User-Id", testUser1.getId().toString())));

            assertTrue(exception.getCause() instanceof IllegalStateException);
            assertEquals("Already following this user", exception.getCause().getMessage());
        }

        @Test
        @DisplayName("Should throw exception when user to follow does not exist")
        void shouldThrowExceptionWhenUserToFollowNotFound() {
            ServletException exception = assertThrows(ServletException.class, () ->
                    mockMvc.perform(post("/api/users/{id}/follow", "00000000-0000-0000-0000-000000000000")
                            .header("X-User-Id", testUser1.getId().toString())));

            assertTrue(exception.getCause() instanceof IllegalArgumentException);
            assertEquals("User to follow not found", exception.getCause().getMessage());
        }

        @Test
        @DisplayName("Should throw exception when follower does not exist")
        void shouldThrowExceptionWhenFollowerNotFound() {
            ServletException exception = assertThrows(ServletException.class, () ->
                    mockMvc.perform(post("/api/users/{id}/follow", testUser2.getId())
                            .header("X-User-Id", "00000000-0000-0000-0000-000000000000")));

            assertTrue(exception.getCause() instanceof IllegalArgumentException);
            assertEquals("Follower user not found", exception.getCause().getMessage());
        }

        @Test
        @DisplayName("Should return 400 when X-User-Id header is missing")
        void shouldReturn400WhenHeaderMissing() throws Exception {
            mockMvc.perform(post("/api/users/{id}/follow", testUser2.getId()))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /api/users/{id}/follow")
    class UnfollowUserTests {

        @Test
        @DisplayName("Should successfully unfollow a user")
        void shouldUnfollowUser() throws Exception {
            Follow follow = new Follow(testUser1, testUser2);
            followRepository.save(follow);

            mockMvc.perform(delete("/api/users/{id}/follow", testUser2.getId())
                            .header("X-User-Id", testUser1.getId().toString()))
                    .andExpect(status().isNoContent());

            assertFalse(followRepository.existsByFollower_IdAndFollowing_Id(
                    testUser1.getId(), testUser2.getId()));
        }

        @Test
        @DisplayName("Should throw exception when follow relationship does not exist")
        void shouldThrowExceptionWhenNotFollowing() {
            ServletException exception = assertThrows(ServletException.class, () ->
                    mockMvc.perform(delete("/api/users/{id}/follow", testUser2.getId())
                            .header("X-User-Id", testUser1.getId().toString())));

            assertTrue(exception.getCause() instanceof IllegalArgumentException);
            assertEquals("Follow relationship does not exist", exception.getCause().getMessage());
        }

        @Test
        @DisplayName("Should return 400 when X-User-Id header is missing")
        void shouldReturn400WhenHeaderMissing() throws Exception {
            mockMvc.perform(delete("/api/users/{id}/follow", testUser2.getId()))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/users/{id}/followers")
    class GetFollowersTests {

        @Test
        @DisplayName("Should return list of followers")
        void shouldReturnFollowers() throws Exception {
            followRepository.save(new Follow(testUser2, testUser1));
            followRepository.save(new Follow(testUser3, testUser1));

            mockMvc.perform(get("/api/users/{id}/followers", testUser1.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[*].username", containsInAnyOrder("journalist", "historian")));
        }

        @Test
        @DisplayName("Should return empty list when no followers")
        void shouldReturnEmptyListWhenNoFollowers() throws Exception {
            mockMvc.perform(get("/api/users/{id}/followers", testUser1.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("Should return followers with user details")
        void shouldReturnFollowersWithDetails() throws Exception {
            followRepository.save(new Follow(testUser2, testUser1));

            mockMvc.perform(get("/api/users/{id}/followers", testUser1.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id", is(testUser2.getId().toString())))
                    .andExpect(jsonPath("$[0].username", is("journalist")))
                    .andExpect(jsonPath("$[0].displayName", is("Mike Thompson")))
                    .andExpect(jsonPath("$[0].bio", is("Investigative journalist")));
        }
    }

    @Nested
    @DisplayName("GET /api/users/{id}/following")
    class GetFollowingTests {

        @Test
        @DisplayName("Should return list of users being followed")
        void shouldReturnFollowing() throws Exception {
            followRepository.save(new Follow(testUser1, testUser2));
            followRepository.save(new Follow(testUser1, testUser3));

            mockMvc.perform(get("/api/users/{id}/following", testUser1.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[*].username", containsInAnyOrder("journalist", "historian")));
        }

        @Test
        @DisplayName("Should return empty list when not following anyone")
        void shouldReturnEmptyListWhenNotFollowing() throws Exception {
            mockMvc.perform(get("/api/users/{id}/following", testUser1.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("Should return following with user details")
        void shouldReturnFollowingWithDetails() throws Exception {
            followRepository.save(new Follow(testUser1, testUser2));

            mockMvc.perform(get("/api/users/{id}/following", testUser1.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id", is(testUser2.getId().toString())))
                    .andExpect(jsonPath("$[0].username", is("journalist")))
                    .andExpect(jsonPath("$[0].displayName", is("Mike Thompson")))
                    .andExpect(jsonPath("$[0].bio", is("Investigative journalist")));
        }
    }

    @Nested
    @DisplayName("GET /api/users/{id}/is-following")
    class IsFollowingTests {

        @Test
        @DisplayName("Should return true when following")
        void shouldReturnTrueWhenFollowing() throws Exception {
            followRepository.save(new Follow(testUser1, testUser2));

            mockMvc.perform(get("/api/users/{id}/is-following", testUser2.getId())
                            .header("X-User-Id", testUser1.getId().toString()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string("true"));
        }

        @Test
        @DisplayName("Should return false when not following")
        void shouldReturnFalseWhenNotFollowing() throws Exception {
            mockMvc.perform(get("/api/users/{id}/is-following", testUser2.getId())
                            .header("X-User-Id", testUser1.getId().toString()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string("false"));
        }

        @Test
        @DisplayName("Should return 400 when X-User-Id header is missing")
        void shouldReturn400WhenHeaderMissing() throws Exception {
            mockMvc.perform(get("/api/users/{id}/is-following", testUser2.getId()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return false when checking if following self")
        void shouldReturnFalseWhenCheckingFollowingSelf() throws Exception {
            mockMvc.perform(get("/api/users/{id}/is-following", testUser1.getId())
                            .header("X-User-Id", testUser1.getId().toString()))
                    .andExpect(status().isOk())
                    .andExpect(content().string("false"));
        }
    }
}
