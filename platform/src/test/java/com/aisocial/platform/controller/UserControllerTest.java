package com.aisocial.platform.controller;

import com.aisocial.platform.dto.UserResponseDTO;
import com.aisocial.platform.dto.UserSearchRequestDTO;
import com.aisocial.platform.entity.User;
import com.aisocial.platform.repository.UserRepository;
import com.aisocial.platform.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("UserController Integration Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUser1 = new User("scientist", "Dr. Sarah Chen", "Climate researcher. Facts matter.");
        testUser1.setTrustScore(new BigDecimal("92.00"));
        testUser1.setAvatarUrl("https://example.com/scientist.png");
        testUser1.setPostsVerified(15);
        testUser1.setPostsFalse(1);
        testUser1.setDebatesWon(5);
        testUser1.setDebatesLost(2);
        testUser1 = userRepository.save(testUser1);

        testUser2 = new User("journalist", "Mike Thompson", "Investigative journalist");
        testUser2.setTrustScore(new BigDecimal("78.00"));
        testUser2.setAvatarUrl("https://example.com/journalist.png");
        testUser2 = userRepository.save(testUser2);
    }

    @Nested
    @DisplayName("GET /api/users")
    class GetAllUsersTests {

        @Test
        @DisplayName("Should return all users")
        void shouldReturnAllUsers() throws Exception {
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[*].username", containsInAnyOrder("scientist", "journalist")));
        }

        @Test
        @DisplayName("Should return empty list when no users")
        void shouldReturnEmptyListWhenNoUsers() throws Exception {
            userRepository.deleteAll();

            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("Should return user with all fields")
        void shouldReturnUserWithAllFields() throws Exception {
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[?(@.username == 'scientist')].displayName", contains("Dr. Sarah Chen")))
                    .andExpect(jsonPath("$[?(@.username == 'scientist')].bio", contains("Climate researcher. Facts matter.")))
                    .andExpect(jsonPath("$[?(@.username == 'scientist')].trustScore", contains(92.00)))
                    .andExpect(jsonPath("$[?(@.username == 'scientist')].avatarUrl", contains("https://example.com/scientist.png")));
        }
    }

    @Nested
    @DisplayName("GET /api/users/me")
    class GetCurrentUserTests {

        @Test
        @DisplayName("Should return current user from X-User-Id header")
        void shouldReturnCurrentUser() throws Exception {
            mockMvc.perform(get("/api/users/me")
                            .header("X-User-Id", testUser1.getId().toString()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.username", is("scientist")))
                    .andExpect(jsonPath("$.displayName", is("Dr. Sarah Chen")));
        }

        @Test
        @DisplayName("Should return 404 for invalid user ID")
        void shouldReturn404ForInvalidUserId() throws Exception {
            mockMvc.perform(get("/api/users/me")
                            .header("X-User-Id", "00000000-0000-0000-0000-000000000000"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 400 when X-User-Id header missing")
        void shouldReturn400WhenHeaderMissing() throws Exception {
            mockMvc.perform(get("/api/users/me"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/users/{id}")
    class GetUserByIdTests {

        @Test
        @DisplayName("Should return user by ID")
        void shouldReturnUserById() throws Exception {
            mockMvc.perform(get("/api/users/{id}", testUser1.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.username", is("scientist")))
                    .andExpect(jsonPath("$.displayName", is("Dr. Sarah Chen")))
                    .andExpect(jsonPath("$.bio", is("Climate researcher. Facts matter.")))
                    .andExpect(jsonPath("$.trustScore", is(92.00)));
        }

        @Test
        @DisplayName("Should return 404 for non-existent user")
        void shouldReturn404ForNonExistentUser() throws Exception {
            mockMvc.perform(get("/api/users/{id}", "00000000-0000-0000-0000-000000000000"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return user stats")
        void shouldReturnUserStats() throws Exception {
            mockMvc.perform(get("/api/users/{id}", testUser1.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.postsVerified", is(15)))
                    .andExpect(jsonPath("$.postsFalse", is(1)))
                    .andExpect(jsonPath("$.debatesWon", is(5)))
                    .andExpect(jsonPath("$.debatesLost", is(2)));
        }
    }

    @Nested
    @DisplayName("GET /api/users/username/{username}")
    class GetUserByUsernameTests {

        @Test
        @DisplayName("Should return user by username")
        void shouldReturnUserByUsername() throws Exception {
            mockMvc.perform(get("/api/users/username/{username}", "scientist"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(testUser1.getId().toString())))
                    .andExpect(jsonPath("$.displayName", is("Dr. Sarah Chen")));
        }

        @Test
        @DisplayName("Should return 404 for non-existent username")
        void shouldReturn404ForNonExistentUsername() throws Exception {
            mockMvc.perform(get("/api/users/username/{username}", "nonexistent"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/users/{id}/trust-breakdown")
    class GetTrustBreakdownTests {

        @Test
        @DisplayName("Should return trust score breakdown")
        void shouldReturnTrustBreakdown() throws Exception {
            mockMvc.perform(get("/api/users/{id}/trust-breakdown", testUser1.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.userId", is(testUser1.getId().toString())))
                    .andExpect(jsonPath("$.totalScore", is(92.00)))
                    .andExpect(jsonPath("$.postsVerified", is(15)))
                    .andExpect(jsonPath("$.postsFalse", is(1)))
                    .andExpect(jsonPath("$.debatesWon", is(5)))
                    .andExpect(jsonPath("$.debatesLost", is(2)));
        }

        @Test
        @DisplayName("Should return calculated bonuses and penalties")
        void shouldReturnCalculatedBonusesAndPenalties() throws Exception {
            mockMvc.perform(get("/api/users/{id}/trust-breakdown", testUser1.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.verifiedBonus", is(30.0))) // 15 * 2 = 30, capped at 30
                    .andExpect(jsonPath("$.falsePenalty", is(5.0)));   // 1 * 5 = 5
        }

        @Test
        @DisplayName("Should return 404 for non-existent user")
        void shouldReturn404ForNonExistentUser() throws Exception {
            mockMvc.perform(get("/api/users/{id}/trust-breakdown", "00000000-0000-0000-0000-000000000000"))
                    .andExpect(status().isNotFound());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<UserResponseDTO>> searchUsers(UserSearchRequestDTO request) {
        Page<UserResponseDTO> result = userService.searchUsers(request);
        return ResponseEntity.ok(result);
    }
}