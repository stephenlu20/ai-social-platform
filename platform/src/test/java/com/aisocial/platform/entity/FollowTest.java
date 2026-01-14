package com.aisocial.platform.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Follow Entity Tests")
class FollowTest {

    private User follower;
    private User following;

    @BeforeEach
    void setUp() {
        follower = new User("follower", "Follower User", "I follow people");
        follower.setId(UUID.randomUUID());

        following = new User("following", "Following User", "People follow me");
        following.setId(UUID.randomUUID());
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create follow with default constructor")
        void shouldCreateFollowWithDefaultConstructor() {
            Follow follow = new Follow();

            assertNull(follow.getId());
            assertNull(follow.getFollower());
            assertNull(follow.getFollowing());
            assertNull(follow.getCreatedAt());
        }

        @Test
        @DisplayName("Should create follow with follower and following")
        void shouldCreateFollowWithFollowerAndFollowing() {
            Follow follow = new Follow(follower, following);

            assertEquals(follower, follow.getFollower());
            assertEquals(following, follow.getFollowing());
        }

        @Test
        @DisplayName("Should not set createdAt in constructor")
        void shouldNotSetCreatedAtInConstructor() {
            Follow follow = new Follow(follower, following);

            assertNull(follow.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id")
        void shouldSetAndGetId() {
            Follow follow = new Follow();
            UUID id = UUID.randomUUID();

            follow.setId(id);

            assertEquals(id, follow.getId());
        }

        @Test
        @DisplayName("Should set and get follower")
        void shouldSetAndGetFollower() {
            Follow follow = new Follow();

            follow.setFollower(follower);

            assertEquals(follower, follow.getFollower());
            assertEquals("follower", follow.getFollower().getUsername());
        }

        @Test
        @DisplayName("Should set and get following")
        void shouldSetAndGetFollowing() {
            Follow follow = new Follow();

            follow.setFollowing(following);

            assertEquals(following, follow.getFollowing());
            assertEquals("following", follow.getFollowing().getUsername());
        }

        @Test
        @DisplayName("Should set and get createdAt")
        void shouldSetAndGetCreatedAt() {
            Follow follow = new Follow();
            Instant now = Instant.now();

            follow.setCreatedAt(now);

            assertEquals(now, follow.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("PrePersist Tests")
    class PrePersistTests {

        @Test
        @DisplayName("Should set createdAt on persist")
        void shouldSetCreatedAtOnPersist() {
            Follow follow = new Follow(follower, following);
            assertNull(follow.getCreatedAt());

            follow.onCreate();

            assertNotNull(follow.getCreatedAt());
        }

        @Test
        @DisplayName("Should not overwrite existing createdAt")
        void shouldNotOverwriteExistingCreatedAt() {
            Follow follow = new Follow(follower, following);
            Instant originalTime = Instant.parse("2024-01-01T00:00:00Z");
            follow.setCreatedAt(originalTime);

            follow.onCreate();

            assertEquals(originalTime, follow.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("Relationship Tests")
    class RelationshipTests {

        @Test
        @DisplayName("Should allow changing follower")
        void shouldAllowChangingFollower() {
            Follow follow = new Follow(follower, following);
            User newFollower = new User("newfollower", "New Follower", "Bio");
            newFollower.setId(UUID.randomUUID());

            follow.setFollower(newFollower);

            assertEquals(newFollower, follow.getFollower());
            assertEquals("newfollower", follow.getFollower().getUsername());
        }

        @Test
        @DisplayName("Should allow changing following")
        void shouldAllowChangingFollowing() {
            Follow follow = new Follow(follower, following);
            User newFollowing = new User("newfollowing", "New Following", "Bio");
            newFollowing.setId(UUID.randomUUID());

            follow.setFollowing(newFollowing);

            assertEquals(newFollowing, follow.getFollowing());
            assertEquals("newfollowing", follow.getFollowing().getUsername());
        }

        @Test
        @DisplayName("Should handle same user as follower and following")
        void shouldHandleSameUserAsFollowerAndFollowing() {
            // Note: Business logic should prevent this, but entity allows it
            Follow follow = new Follow(follower, follower);

            assertEquals(follower, follow.getFollower());
            assertEquals(follower, follow.getFollowing());
            assertEquals(follow.getFollower().getId(), follow.getFollowing().getId());
        }

        @Test
        @DisplayName("Should maintain separate references for follower and following")
        void shouldMaintainSeparateReferences() {
            Follow follow = new Follow(follower, following);

            assertNotEquals(follow.getFollower(), follow.getFollowing());
            assertNotEquals(follow.getFollower().getId(), follow.getFollowing().getId());
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle null follower")
        void shouldHandleNullFollower() {
            Follow follow = new Follow();
            follow.setFollower(null);

            assertNull(follow.getFollower());
        }

        @Test
        @DisplayName("Should handle null following")
        void shouldHandleNullFollowing() {
            Follow follow = new Follow();
            follow.setFollowing(null);

            assertNull(follow.getFollowing());
        }

        @Test
        @DisplayName("Should create multiple follows from same follower")
        void shouldCreateMultipleFollowsFromSameFollower() {
            User anotherUser = new User("another", "Another User", "Bio");
            anotherUser.setId(UUID.randomUUID());

            Follow follow1 = new Follow(follower, following);
            Follow follow2 = new Follow(follower, anotherUser);

            assertEquals(follow1.getFollower(), follow2.getFollower());
            assertNotEquals(follow1.getFollowing(), follow2.getFollowing());
        }

        @Test
        @DisplayName("Should create multiple follows to same following")
        void shouldCreateMultipleFollowsToSameFollowing() {
            User anotherFollower = new User("another", "Another Follower", "Bio");
            anotherFollower.setId(UUID.randomUUID());

            Follow follow1 = new Follow(follower, following);
            Follow follow2 = new Follow(anotherFollower, following);

            assertNotEquals(follow1.getFollower(), follow2.getFollower());
            assertEquals(follow1.getFollowing(), follow2.getFollowing());
        }
    }
}