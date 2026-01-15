package com.aisocial.platform.service;

import com.aisocial.platform.context.UserContext;
import com.aisocial.platform.dto.UserDTO;
import com.aisocial.platform.entity.Follow;
import com.aisocial.platform.entity.User;
import com.aisocial.platform.repository.FollowRepository;
import com.aisocial.platform.repository.PostRepository;
import com.aisocial.platform.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser1;
    private User testUser2;
    private User testUser3;

    @BeforeEach
    void setUp() {
        testUser1 = new User("user1", "User One", "Bio 1");
        testUser1.setId(UUID.randomUUID());
        testUser1.setTrustScore(new BigDecimal("75.00"));

        testUser2 = new User("user2", "User Two", "Bio 2");
        testUser2.setId(UUID.randomUUID());
        testUser2.setTrustScore(new BigDecimal("80.00"));

        testUser3 = new User("user3", "User Three", "Bio 3");
        testUser3.setId(UUID.randomUUID());
        testUser3.setTrustScore(new BigDecimal("60.00"));
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    @Nested
    @DisplayName("getAllUsers")
    class GetAllUsersTests {

        @Test
        @DisplayName("Should return all users with enriched data")
        void shouldReturnAllUsersWithEnrichedData() {
            when(userRepository.findAll()).thenReturn(Arrays.asList(testUser1, testUser2));
            when(followRepository.countByFollowing_Id(any())).thenReturn(10L);
            when(followRepository.countByFollower_Id(any())).thenReturn(5L);
            when(postRepository.countByAuthor(any())).thenReturn(0L);

            List<UserDTO> users = userService.getAllUsers();

            assertEquals(2, users.size());
            assertEquals(10L, users.get(0).getFollowerCount());
            assertEquals(5L, users.get(0).getFollowingCount());
            assertEquals(0L, users.get(0).getPostCount());
        }

        @Test
        @DisplayName("Should return empty list when no users")
        void shouldReturnEmptyListWhenNoUsers() {
            when(userRepository.findAll()).thenReturn(Collections.emptyList());

            List<UserDTO> users = userService.getAllUsers();

            assertTrue(users.isEmpty());
        }

        @Test
        @DisplayName("Should map all user fields correctly")
        void shouldMapAllUserFieldsCorrectly() {
            testUser1.setAvatarUrl("https://example.com/avatar.png");
            testUser1.setPostsVerified(10);
            testUser1.setPostsFalse(2);
            
            when(userRepository.findAll()).thenReturn(List.of(testUser1));
            when(followRepository.countByFollowing_Id(any())).thenReturn(0L);
            when(followRepository.countByFollower_Id(any())).thenReturn(0L);
            when(postRepository.countByAuthor(any())).thenReturn(0L);

            List<UserDTO> users = userService.getAllUsers();

            assertEquals(1, users.size());
            UserDTO dto = users.get(0);
            assertEquals("user1", dto.getUsername());
            assertEquals("User One", dto.getDisplayName());
            assertEquals("Bio 1", dto.getBio());
            assertEquals("https://example.com/avatar.png", dto.getAvatarUrl());
            assertEquals(new BigDecimal("75.00"), dto.getTrustScore());
            assertEquals(10, dto.getPostsVerified());
            assertEquals(2, dto.getPostsFalse());
        }
    }

    @Nested
    @DisplayName("getUserById")
    class GetUserByIdTests {

        @Test
        @DisplayName("Should return user by ID with enriched data")
        void shouldReturnUserByIdWithEnrichedData() {
            when(userRepository.findById(testUser1.getId())).thenReturn(Optional.of(testUser1));
            when(followRepository.countByFollowing_Id(testUser1.getId())).thenReturn(100L);
            when(followRepository.countByFollower_Id(testUser1.getId())).thenReturn(50L);
            when(postRepository.countByAuthor(testUser1)).thenReturn(0L);

            Optional<UserDTO> result = userService.getUserById(testUser1.getId());

            assertTrue(result.isPresent());
            assertEquals("user1", result.get().getUsername());
            assertEquals(100L, result.get().getFollowerCount());
            assertEquals(50L, result.get().getFollowingCount());
        }

        @Test
        @DisplayName("Should return empty when user not found")
        void shouldReturnEmptyWhenUserNotFound() {
            when(userRepository.findById(any())).thenReturn(Optional.empty());

            Optional<UserDTO> result = userService.getUserById(UUID.randomUUID());

            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("Should include isFollowing flag when viewer specified")
        void shouldIncludeIsFollowingFlagWhenViewerSpecified() {
            when(userRepository.findById(testUser2.getId())).thenReturn(Optional.of(testUser2));
            when(followRepository.countByFollowing_Id(any())).thenReturn(0L);
            when(followRepository.countByFollower_Id(any())).thenReturn(0L);
            when(postRepository.countByAuthor(any())).thenReturn(0L);
            when(followRepository.existsByFollower_IdAndFollowing_Id(testUser1.getId(), testUser2.getId()))
                    .thenReturn(true);

            Optional<UserDTO> result = userService.getUserById(testUser2.getId(), testUser1.getId());

            assertTrue(result.isPresent());
            assertTrue(result.get().getIsFollowing());
        }

        @Test
        @DisplayName("Should return isFollowing false when viewer is same as user")
        void shouldReturnIsFollowingFalseWhenViewerIsSameAsUser() {
            when(userRepository.findById(testUser1.getId())).thenReturn(Optional.of(testUser1));
            when(followRepository.countByFollowing_Id(any())).thenReturn(0L);
            when(followRepository.countByFollower_Id(any())).thenReturn(0L);
            when(postRepository.countByAuthor(any())).thenReturn(0L);

            Optional<UserDTO> result = userService.getUserById(testUser1.getId(), testUser1.getId());

            assertTrue(result.isPresent());
            assertFalse(result.get().getIsFollowing());
        }

        @Test
        @DisplayName("Should return isFollowing false when not following")
        void shouldReturnIsFollowingFalseWhenNotFollowing() {
            when(userRepository.findById(testUser2.getId())).thenReturn(Optional.of(testUser2));
            when(followRepository.countByFollowing_Id(any())).thenReturn(0L);
            when(followRepository.countByFollower_Id(any())).thenReturn(0L);
            when(postRepository.countByAuthor(any())).thenReturn(0L);
            when(followRepository.existsByFollower_IdAndFollowing_Id(testUser1.getId(), testUser2.getId()))
                    .thenReturn(false);

            Optional<UserDTO> result = userService.getUserById(testUser2.getId(), testUser1.getId());

            assertTrue(result.isPresent());
            assertFalse(result.get().getIsFollowing());
        }
    }

    @Nested
    @DisplayName("getUserByUsername")
    class GetUserByUsernameTests {

        @Test
        @DisplayName("Should return user by username")
        void shouldReturnUserByUsername() {
            when(userRepository.findByUsername("user1")).thenReturn(Optional.of(testUser1));
            when(followRepository.countByFollowing_Id(any())).thenReturn(0L);
            when(followRepository.countByFollower_Id(any())).thenReturn(0L);
            when(postRepository.countByAuthor(any())).thenReturn(0L);

            Optional<UserDTO> result = userService.getUserByUsername("user1");

            assertTrue(result.isPresent());
            assertEquals("User One", result.get().getDisplayName());
        }

        @Test
        @DisplayName("Should return empty when username not found")
        void shouldReturnEmptyWhenUsernameNotFound() {
            when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

            Optional<UserDTO> result = userService.getUserByUsername("nonexistent");

            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("Should include isFollowing flag when viewer specified")
        void shouldIncludeIsFollowingFlagWhenViewerSpecified() {
            when(userRepository.findByUsername("user2")).thenReturn(Optional.of(testUser2));
            when(followRepository.countByFollowing_Id(any())).thenReturn(0L);
            when(followRepository.countByFollower_Id(any())).thenReturn(0L);
            when(postRepository.countByAuthor(any())).thenReturn(0L);
            when(followRepository.existsByFollower_IdAndFollowing_Id(testUser1.getId(), testUser2.getId()))
                    .thenReturn(true);

            Optional<UserDTO> result = userService.getUserByUsername("user2", testUser1.getId());

            assertTrue(result.isPresent());
            assertTrue(result.get().getIsFollowing());
        }
    }

    @Nested
    @DisplayName("followUser")
    class FollowUserTests {

        @Test
        @DisplayName("Should follow user successfully")
        void shouldFollowUserSuccessfully() {
            when(userRepository.findById(testUser1.getId())).thenReturn(Optional.of(testUser1));
            when(userRepository.findById(testUser2.getId())).thenReturn(Optional.of(testUser2));
            when(followRepository.existsByFollower_IdAndFollowing_Id(testUser1.getId(), testUser2.getId()))
                    .thenReturn(false);
            when(followRepository.save(any(Follow.class))).thenAnswer(invocation -> invocation.getArgument(0));

            assertDoesNotThrow(() -> userService.followUser(testUser1.getId(), testUser2.getId()));

            verify(followRepository).save(any(Follow.class));
        }

        @Test
        @DisplayName("Should throw when user tries to follow themselves")
        void shouldThrowWhenUserTriesToFollowThemselves() {
            IllegalStateException exception = assertThrows(IllegalStateException.class,
                    () -> userService.followUser(testUser1.getId(), testUser1.getId()));

            assertEquals("Users cannot follow themselves", exception.getMessage());
            verify(followRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw when follower not found")
        void shouldThrowWhenFollowerNotFound() {
            when(userRepository.findById(testUser1.getId())).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> userService.followUser(testUser1.getId(), testUser2.getId()));

            assertEquals("Follower user not found", exception.getMessage());
            verify(followRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw when user to follow not found")
        void shouldThrowWhenUserToFollowNotFound() {
            when(userRepository.findById(testUser1.getId())).thenReturn(Optional.of(testUser1));
            when(userRepository.findById(testUser2.getId())).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> userService.followUser(testUser1.getId(), testUser2.getId()));

            assertEquals("User to follow not found", exception.getMessage());
            verify(followRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw when already following")
        void shouldThrowWhenAlreadyFollowing() {
            when(userRepository.findById(testUser1.getId())).thenReturn(Optional.of(testUser1));
            when(userRepository.findById(testUser2.getId())).thenReturn(Optional.of(testUser2));
            when(followRepository.existsByFollower_IdAndFollowing_Id(testUser1.getId(), testUser2.getId()))
                    .thenReturn(true);

            IllegalStateException exception = assertThrows(IllegalStateException.class,
                    () -> userService.followUser(testUser1.getId(), testUser2.getId()));

            assertEquals("Already following this user", exception.getMessage());
            verify(followRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("unfollowUser")
    class UnfollowUserTests {

        @Test
        @DisplayName("Should unfollow user successfully")
        void shouldUnfollowUserSuccessfully() {
            when(followRepository.existsByFollower_IdAndFollowing_Id(testUser1.getId(), testUser2.getId()))
                    .thenReturn(true);
            doNothing().when(followRepository).deleteByFollower_IdAndFollowing_Id(testUser1.getId(), testUser2.getId());

            assertDoesNotThrow(() -> userService.unfollowUser(testUser1.getId(), testUser2.getId()));

            verify(followRepository).deleteByFollower_IdAndFollowing_Id(testUser1.getId(), testUser2.getId());
        }

        @Test
        @DisplayName("Should throw when follow relationship does not exist")
        void shouldThrowWhenFollowRelationshipDoesNotExist() {
            when(followRepository.existsByFollower_IdAndFollowing_Id(testUser1.getId(), testUser2.getId()))
                    .thenReturn(false);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> userService.unfollowUser(testUser1.getId(), testUser2.getId()));

            assertEquals("Follow relationship does not exist", exception.getMessage());
            verify(followRepository, never()).deleteByFollower_IdAndFollowing_Id(any(), any());
        }
    }

    @Nested
    @DisplayName("getFollowers")
    class GetFollowersTests {

        @Test
        @DisplayName("Should return followers with enriched data")
        void shouldReturnFollowersWithEnrichedData() {
            when(followRepository.findFollowersByUserId(testUser1.getId()))
                    .thenReturn(Arrays.asList(testUser2, testUser3));
            when(followRepository.countByFollowing_Id(any())).thenReturn(5L);
            when(followRepository.countByFollower_Id(any())).thenReturn(3L);
            when(postRepository.countByAuthor(any())).thenReturn(0L);

            List<UserDTO> followers = userService.getFollowers(testUser1.getId());

            assertEquals(2, followers.size());
            assertTrue(followers.stream().anyMatch(u -> u.getUsername().equals("user2")));
            assertTrue(followers.stream().anyMatch(u -> u.getUsername().equals("user3")));
            assertEquals(5L, followers.get(0).getFollowerCount());
            assertEquals(3L, followers.get(0).getFollowingCount());
        }

        @Test
        @DisplayName("Should return empty list when no followers")
        void shouldReturnEmptyListWhenNoFollowers() {
            when(followRepository.findFollowersByUserId(testUser1.getId()))
                    .thenReturn(Collections.emptyList());

            List<UserDTO> followers = userService.getFollowers(testUser1.getId());

            assertTrue(followers.isEmpty());
        }
    }

    @Nested
    @DisplayName("getFollowing")
    class GetFollowingTests {

        @Test
        @DisplayName("Should return following users with enriched data")
        void shouldReturnFollowingUsersWithEnrichedData() {
            when(followRepository.findFollowingByUserId(testUser1.getId()))
                    .thenReturn(Arrays.asList(testUser2, testUser3));
            when(followRepository.countByFollowing_Id(any())).thenReturn(10L);
            when(followRepository.countByFollower_Id(any())).thenReturn(8L);
            when(postRepository.countByAuthor(any())).thenReturn(0L);

            List<UserDTO> following = userService.getFollowing(testUser1.getId());

            assertEquals(2, following.size());
            assertTrue(following.stream().anyMatch(u -> u.getUsername().equals("user2")));
            assertTrue(following.stream().anyMatch(u -> u.getUsername().equals("user3")));
        }

        @Test
        @DisplayName("Should return empty list when not following anyone")
        void shouldReturnEmptyListWhenNotFollowingAnyone() {
            when(followRepository.findFollowingByUserId(testUser1.getId()))
                    .thenReturn(Collections.emptyList());

            List<UserDTO> following = userService.getFollowing(testUser1.getId());

            assertTrue(following.isEmpty());
        }
    }

    @Nested
    @DisplayName("isFollowing")
    class IsFollowingTests {

        @Test
        @DisplayName("Should return true when following")
        void shouldReturnTrueWhenFollowing() {
            when(followRepository.existsByFollower_IdAndFollowing_Id(testUser1.getId(), testUser2.getId()))
                    .thenReturn(true);

            boolean result = userService.isFollowing(testUser1.getId(), testUser2.getId());

            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when not following")
        void shouldReturnFalseWhenNotFollowing() {
            when(followRepository.existsByFollower_IdAndFollowing_Id(testUser1.getId(), testUser2.getId()))
                    .thenReturn(false);

            boolean result = userService.isFollowing(testUser1.getId(), testUser2.getId());

            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("getFollowerCount")
    class GetFollowerCountTests {

        @Test
        @DisplayName("Should return correct follower count")
        void shouldReturnCorrectFollowerCount() {
            when(followRepository.countByFollowing_Id(testUser1.getId())).thenReturn(42L);

            long count = userService.getFollowerCount(testUser1.getId());

            assertEquals(42L, count);
        }

        @Test
        @DisplayName("Should return zero when no followers")
        void shouldReturnZeroWhenNoFollowers() {
            when(followRepository.countByFollowing_Id(testUser1.getId())).thenReturn(0L);

            long count = userService.getFollowerCount(testUser1.getId());

            assertEquals(0L, count);
        }
    }

    @Nested
    @DisplayName("getFollowingCount")
    class GetFollowingCountTests {

        @Test
        @DisplayName("Should return correct following count")
        void shouldReturnCorrectFollowingCount() {
            when(followRepository.countByFollower_Id(testUser1.getId())).thenReturn(25L);

            long count = userService.getFollowingCount(testUser1.getId());

            assertEquals(25L, count);
        }

        @Test
        @DisplayName("Should return zero when not following anyone")
        void shouldReturnZeroWhenNotFollowingAnyone() {
            when(followRepository.countByFollower_Id(testUser1.getId())).thenReturn(0L);

            long count = userService.getFollowingCount(testUser1.getId());

            assertEquals(0L, count);
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle user with maximum stats")
        void shouldHandleUserWithMaximumStats() {
            testUser1.setPostsVerified(1000);
            testUser1.setPostsFalse(500);
            testUser1.setDebatesWon(200);
            testUser1.setDebatesLost(100);
            testUser1.setTrustScore(new BigDecimal("100.00"));

            when(userRepository.findById(testUser1.getId())).thenReturn(Optional.of(testUser1));
            when(followRepository.countByFollowing_Id(any())).thenReturn(1_000_000L);
            when(followRepository.countByFollower_Id(any())).thenReturn(500_000L);
            when(postRepository.countByAuthor(any())).thenReturn(0L);

            Optional<UserDTO> result = userService.getUserById(testUser1.getId());

            assertTrue(result.isPresent());
            assertEquals(1_000_000L, result.get().getFollowerCount());
            assertEquals(500_000L, result.get().getFollowingCount());
            assertEquals(new BigDecimal("100.00"), result.get().getTrustScore());
        }

        @Test
        @DisplayName("Should handle new user with zero stats")
        void shouldHandleNewUserWithZeroStats() {
            User newUser = new User("newbie", "New User", "Just joined");
            newUser.setId(UUID.randomUUID());
            newUser.setTrustScore(new BigDecimal("50.00"));
            newUser.setPostsVerified(0);
            newUser.setPostsFalse(0);
            newUser.setDebatesWon(0);
            newUser.setDebatesLost(0);

            when(userRepository.findById(newUser.getId())).thenReturn(Optional.of(newUser));
            when(followRepository.countByFollowing_Id(any())).thenReturn(0L);
            when(followRepository.countByFollower_Id(any())).thenReturn(0L);
            when(postRepository.countByAuthor(any())).thenReturn(0L);

            Optional<UserDTO> result = userService.getUserById(newUser.getId());

            assertTrue(result.isPresent());
            assertEquals(0L, result.get().getFollowerCount());
            assertEquals(0L, result.get().getFollowingCount());
            assertEquals(0L, result.get().getPostCount());
            assertEquals(0, result.get().getPostsVerified());
            assertEquals(0, result.get().getPostsFalse());
        }

        @Test
        @DisplayName("Should handle null optional fields")
        void shouldHandleNullOptionalFields() {
            User minimalUser = new User("minimal", "Minimal", null);
            minimalUser.setId(UUID.randomUUID());
            minimalUser.setAvatarUrl(null);
            minimalUser.setBio(null);

            when(userRepository.findById(minimalUser.getId())).thenReturn(Optional.of(minimalUser));
            when(followRepository.countByFollowing_Id(any())).thenReturn(0L);
            when(followRepository.countByFollower_Id(any())).thenReturn(0L);
            when(postRepository.countByAuthor(any())).thenReturn(0L);

            Optional<UserDTO> result = userService.getUserById(minimalUser.getId());

            assertTrue(result.isPresent());
            assertNull(result.get().getBio());
            assertNull(result.get().getAvatarUrl());
        }
    }
}