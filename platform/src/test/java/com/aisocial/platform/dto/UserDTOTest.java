package com.aisocial.platform.dto;

import com.aisocial.platform.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserDTO Tests")
class UserDTOTest {

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "Test User", "This is a test bio");
        testUser.setId(UUID.randomUUID());
        testUser.setAvatarUrl("https://example.com/avatar.png");
        testUser.setTrustScore(new BigDecimal("75.50"));
        testUser.setPostsFactChecked(10);
        testUser.setPostsVerified(8);
        testUser.setPostsFalse(2);
        testUser.setDebatesWon(5);
        testUser.setDebatesLost(3);
        testUser.setCreatedAt(Instant.now());
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create DTO with default constructor")
        void shouldCreateDTOWithDefaultConstructor() {
            UserDTO dto = new UserDTO();
            
            assertNull(dto.getId());
            assertNull(dto.getUsername());
            assertNull(dto.getDisplayName());
            assertNull(dto.getBio());
            assertNull(dto.getAvatarUrl());
            assertNull(dto.getTrustScore());
            assertNull(dto.getPostsFactChecked());
            assertNull(dto.getPostsVerified());
            assertNull(dto.getPostsFalse());
            assertNull(dto.getDebatesWon());
            assertNull(dto.getDebatesLost());
            assertNull(dto.getCreatedAt());
            assertNull(dto.getFollowerCount());
            assertNull(dto.getFollowingCount());
            assertNull(dto.getPostCount());
            assertNull(dto.getIsFollowing());
        }

        @Test
        @DisplayName("Should create DTO from User entity")
        void shouldCreateDTOFromUserEntity() {
            UserDTO dto = new UserDTO(testUser);

            assertEquals(testUser.getId(), dto.getId());
            assertEquals("testuser", dto.getUsername());
            assertEquals("Test User", dto.getDisplayName());
            assertEquals("This is a test bio", dto.getBio());
            assertEquals("https://example.com/avatar.png", dto.getAvatarUrl());
            assertEquals(new BigDecimal("75.50"), dto.getTrustScore());
            assertEquals(10, dto.getPostsFactChecked());
            assertEquals(8, dto.getPostsVerified());
            assertEquals(2, dto.getPostsFalse());
            assertEquals(5, dto.getDebatesWon());
            assertEquals(3, dto.getDebatesLost());
            assertEquals(testUser.getCreatedAt(), dto.getCreatedAt());
        }

        @Test
        @DisplayName("Should create DTO from entity with null optional fields")
        void shouldCreateDTOFromEntityWithNullFields() {
            User minimalUser = new User("minimal", "Minimal User", null);
            minimalUser.setId(UUID.randomUUID());
            
            UserDTO dto = new UserDTO(minimalUser);

            assertEquals("minimal", dto.getUsername());
            assertEquals("Minimal User", dto.getDisplayName());
            assertNull(dto.getBio());
            assertNull(dto.getAvatarUrl());
        }
    }

    @Nested
    @DisplayName("Static Factory Method Tests")
    class StaticFactoryTests {

        @Test
        @DisplayName("Should create DTO using fromEntity factory method")
        void shouldCreateDTOUsingFromEntity() {
            UserDTO dto = UserDTO.fromEntity(testUser);

            assertEquals(testUser.getId(), dto.getId());
            assertEquals("testuser", dto.getUsername());
            assertEquals("Test User", dto.getDisplayName());
            assertEquals(new BigDecimal("75.50"), dto.getTrustScore());
        }

        @Test
        @DisplayName("fromEntity should produce same result as constructor")
        void fromEntityShouldMatchConstructor() {
            UserDTO fromConstructor = new UserDTO(testUser);
            UserDTO fromFactory = UserDTO.fromEntity(testUser);

            assertEquals(fromConstructor.getId(), fromFactory.getId());
            assertEquals(fromConstructor.getUsername(), fromFactory.getUsername());
            assertEquals(fromConstructor.getDisplayName(), fromFactory.getDisplayName());
            assertEquals(fromConstructor.getBio(), fromFactory.getBio());
            assertEquals(fromConstructor.getAvatarUrl(), fromFactory.getAvatarUrl());
            assertEquals(fromConstructor.getTrustScore(), fromFactory.getTrustScore());
            assertEquals(fromConstructor.getPostsFactChecked(), fromFactory.getPostsFactChecked());
            assertEquals(fromConstructor.getPostsVerified(), fromFactory.getPostsVerified());
            assertEquals(fromConstructor.getPostsFalse(), fromFactory.getPostsFalse());
            assertEquals(fromConstructor.getDebatesWon(), fromFactory.getDebatesWon());
            assertEquals(fromConstructor.getDebatesLost(), fromFactory.getDebatesLost());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id")
        void shouldSetAndGetId() {
            UserDTO dto = new UserDTO();
            UUID id = UUID.randomUUID();
            
            dto.setId(id);
            
            assertEquals(id, dto.getId());
        }

        @Test
        @DisplayName("Should set and get username")
        void shouldSetAndGetUsername() {
            UserDTO dto = new UserDTO();
            
            dto.setUsername("newuser");
            
            assertEquals("newuser", dto.getUsername());
        }

        @Test
        @DisplayName("Should set and get displayName")
        void shouldSetAndGetDisplayName() {
            UserDTO dto = new UserDTO();
            
            dto.setDisplayName("New Display Name");
            
            assertEquals("New Display Name", dto.getDisplayName());
        }

        @Test
        @DisplayName("Should set and get bio")
        void shouldSetAndGetBio() {
            UserDTO dto = new UserDTO();
            
            dto.setBio("New bio text");
            
            assertEquals("New bio text", dto.getBio());
        }

        @Test
        @DisplayName("Should set and get avatarUrl")
        void shouldSetAndGetAvatarUrl() {
            UserDTO dto = new UserDTO();
            
            dto.setAvatarUrl("https://new-url.com/avatar.png");
            
            assertEquals("https://new-url.com/avatar.png", dto.getAvatarUrl());
        }

        @Test
        @DisplayName("Should set and get trustScore")
        void shouldSetAndGetTrustScore() {
            UserDTO dto = new UserDTO();
            
            dto.setTrustScore(new BigDecimal("92.50"));
            
            assertEquals(new BigDecimal("92.50"), dto.getTrustScore());
        }

        @Test
        @DisplayName("Should set and get postsFactChecked")
        void shouldSetAndGetPostsFactChecked() {
            UserDTO dto = new UserDTO();
            
            dto.setPostsFactChecked(25);
            
            assertEquals(25, dto.getPostsFactChecked());
        }

        @Test
        @DisplayName("Should set and get postsVerified")
        void shouldSetAndGetPostsVerified() {
            UserDTO dto = new UserDTO();
            
            dto.setPostsVerified(20);
            
            assertEquals(20, dto.getPostsVerified());
        }

        @Test
        @DisplayName("Should set and get postsFalse")
        void shouldSetAndGetPostsFalse() {
            UserDTO dto = new UserDTO();
            
            dto.setPostsFalse(5);
            
            assertEquals(5, dto.getPostsFalse());
        }

        @Test
        @DisplayName("Should set and get debatesWon")
        void shouldSetAndGetDebatesWon() {
            UserDTO dto = new UserDTO();
            
            dto.setDebatesWon(10);
            
            assertEquals(10, dto.getDebatesWon());
        }

        @Test
        @DisplayName("Should set and get debatesLost")
        void shouldSetAndGetDebatesLost() {
            UserDTO dto = new UserDTO();
            
            dto.setDebatesLost(3);
            
            assertEquals(3, dto.getDebatesLost());
        }

        @Test
        @DisplayName("Should set and get createdAt")
        void shouldSetAndGetCreatedAt() {
            UserDTO dto = new UserDTO();
            Instant now = Instant.now();
            
            dto.setCreatedAt(now);
            
            assertEquals(now, dto.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("Additional Profile Fields Tests")
    class AdditionalFieldsTests {

        @Test
        @DisplayName("Should set and get followerCount")
        void shouldSetAndGetFollowerCount() {
            UserDTO dto = new UserDTO();
            
            dto.setFollowerCount(1000L);
            
            assertEquals(1000L, dto.getFollowerCount());
        }

        @Test
        @DisplayName("Should set and get followingCount")
        void shouldSetAndGetFollowingCount() {
            UserDTO dto = new UserDTO();
            
            dto.setFollowingCount(500L);
            
            assertEquals(500L, dto.getFollowingCount());
        }

        @Test
        @DisplayName("Should set and get postCount")
        void shouldSetAndGetPostCount() {
            UserDTO dto = new UserDTO();
            
            dto.setPostCount(250L);
            
            assertEquals(250L, dto.getPostCount());
        }

        @Test
        @DisplayName("Should set and get isFollowing true")
        void shouldSetAndGetIsFollowingTrue() {
            UserDTO dto = new UserDTO();
            
            dto.setIsFollowing(true);
            
            assertTrue(dto.getIsFollowing());
        }

        @Test
        @DisplayName("Should set and get isFollowing false")
        void shouldSetAndGetIsFollowingFalse() {
            UserDTO dto = new UserDTO();
            
            dto.setIsFollowing(false);
            
            assertFalse(dto.getIsFollowing());
        }

        @Test
        @DisplayName("Additional fields should be null when created from entity")
        void additionalFieldsShouldBeNullFromEntity() {
            UserDTO dto = UserDTO.fromEntity(testUser);

            assertNull(dto.getFollowerCount());
            assertNull(dto.getFollowingCount());
            assertNull(dto.getPostCount());
            assertNull(dto.getIsFollowing());
        }

        @Test
        @DisplayName("Should be able to enrich DTO with additional fields after creation")
        void shouldEnrichDTOWithAdditionalFields() {
            UserDTO dto = UserDTO.fromEntity(testUser);
            
            dto.setFollowerCount(150L);
            dto.setFollowingCount(75L);
            dto.setPostCount(42L);
            dto.setIsFollowing(true);

            assertEquals(150L, dto.getFollowerCount());
            assertEquals(75L, dto.getFollowingCount());
            assertEquals(42L, dto.getPostCount());
            assertTrue(dto.getIsFollowing());
            
            assertEquals(testUser.getUsername(), dto.getUsername());
            assertEquals(testUser.getTrustScore(), dto.getTrustScore());
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle zero values")
        void shouldHandleZeroValues() {
            User newUser = new User("newbie", "New User", "Just joined");
            newUser.setId(UUID.randomUUID());
            newUser.setTrustScore(new BigDecimal("50.00"));
            newUser.setPostsFactChecked(0);
            newUser.setPostsVerified(0);
            newUser.setPostsFalse(0);
            newUser.setDebatesWon(0);
            newUser.setDebatesLost(0);

            UserDTO dto = UserDTO.fromEntity(newUser);

            assertEquals(0, dto.getPostsFactChecked());
            assertEquals(0, dto.getPostsVerified());
            assertEquals(0, dto.getPostsFalse());
            assertEquals(0, dto.getDebatesWon());
            assertEquals(0, dto.getDebatesLost());
        }

        @Test
        @DisplayName("Should handle maximum trust score")
        void shouldHandleMaxTrustScore() {
            testUser.setTrustScore(new BigDecimal("100.00"));
            
            UserDTO dto = UserDTO.fromEntity(testUser);

            assertEquals(new BigDecimal("100.00"), dto.getTrustScore());
        }

        @Test
        @DisplayName("Should handle minimum trust score")
        void shouldHandleMinTrustScore() {
            testUser.setTrustScore(new BigDecimal("0.00"));
            
            UserDTO dto = UserDTO.fromEntity(testUser);

            assertEquals(new BigDecimal("0.00"), dto.getTrustScore());
        }

        @Test
        @DisplayName("Should handle empty string bio")
        void shouldHandleEmptyStringBio() {
            testUser.setBio("");
            
            UserDTO dto = UserDTO.fromEntity(testUser);

            assertEquals("", dto.getBio());
        }

        @Test
        @DisplayName("Should handle maximum length username")
        void shouldHandleMaxLengthUsername() {
            testUser.setUsername("abunchofcharacters");
            
            UserDTO dto = UserDTO.fromEntity(testUser);

            assertEquals("abunchofcharacters", dto.getUsername());
        }

        @Test
        @DisplayName("Should handle large follower counts")
        void shouldHandleLargeFollowerCounts() {
            UserDTO dto = new UserDTO();
            
            dto.setFollowerCount(1_000_000_000L);
            dto.setFollowingCount(999_999_999L);
            
            assertEquals(1_000_000_000L, dto.getFollowerCount());
            assertEquals(999_999_999L, dto.getFollowingCount());
        }
    }
}