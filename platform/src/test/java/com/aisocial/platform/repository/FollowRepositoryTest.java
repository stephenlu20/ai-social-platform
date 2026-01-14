package com.aisocial.platform.repository;

import com.aisocial.platform.entity.Follow;
import com.aisocial.platform.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("FollowRepository Tests")
class FollowRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FollowRepository followRepository;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        user1 = new User("user1", "User One", "Bio 1");
        user2 = new User("user2", "User Two", "Bio 2");
        user3 = new User("user3", "User Three", "Bio 3");
        
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        entityManager.persistAndFlush(user3);
    }

    @Test
    @DisplayName("Should create follow relationship")
    void shouldCreateFollowRelationship() {
        Follow follow = new Follow(user1, user2);
        Follow saved = followRepository.save(follow);

        assertNotNull(saved.getId());
        assertEquals(user1.getId(), saved.getFollower().getId());
        assertEquals(user2.getId(), saved.getFollowing().getId());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    @DisplayName("Should check if follow exists")
    void shouldCheckIfFollowExists() {
        Follow follow = new Follow(user1, user2);
        entityManager.persistAndFlush(follow);

        assertTrue(followRepository.existsByFollower_IdAndFollowing_Id(user1.getId(), user2.getId()));
        assertFalse(followRepository.existsByFollower_IdAndFollowing_Id(user2.getId(), user1.getId()));
        assertFalse(followRepository.existsByFollower_IdAndFollowing_Id(user1.getId(), user3.getId()));
    }

    @Test
    @DisplayName("Should find follow by follower and following IDs")
    void shouldFindFollowByIds() {
        Follow follow = new Follow(user1, user2);
        entityManager.persistAndFlush(follow);

        Optional<Follow> found = followRepository.findByFollower_IdAndFollowing_Id(user1.getId(), user2.getId());

        assertTrue(found.isPresent());
        assertEquals(user1.getId(), found.get().getFollower().getId());
        assertEquals(user2.getId(), found.get().getFollowing().getId());
    }

    @Test
    @DisplayName("Should return empty when follow not found")
    void shouldReturnEmptyWhenFollowNotFound() {
        Optional<Follow> found = followRepository.findByFollower_IdAndFollowing_Id(user1.getId(), user2.getId());

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should count followers correctly")
    void shouldCountFollowers() {
        // user1 and user3 follow user2
        entityManager.persistAndFlush(new Follow(user1, user2));
        entityManager.persistAndFlush(new Follow(user3, user2));

        long count = followRepository.countByFollowing_Id(user2.getId());

        assertEquals(2, count);
    }

    @Test
    @DisplayName("Should count following correctly")
    void shouldCountFollowing() {
        // user1 follows user2 and user3
        entityManager.persistAndFlush(new Follow(user1, user2));
        entityManager.persistAndFlush(new Follow(user1, user3));

        long count = followRepository.countByFollower_Id(user1.getId());

        assertEquals(2, count);
    }

    @Test
    @DisplayName("Should return zero count when no followers")
    void shouldReturnZeroWhenNoFollowers() {
        long count = followRepository.countByFollowing_Id(user1.getId());

        assertEquals(0, count);
    }

    @Test
    @DisplayName("Should get followers by user ID")
    void shouldGetFollowersByUserId() {
        // user1 and user3 follow user2
        entityManager.persistAndFlush(new Follow(user1, user2));
        entityManager.persistAndFlush(new Follow(user3, user2));

        List<User> followers = followRepository.findFollowersByUserId(user2.getId());

        assertEquals(2, followers.size());
        assertTrue(followers.stream().anyMatch(u -> u.getId().equals(user1.getId())));
        assertTrue(followers.stream().anyMatch(u -> u.getId().equals(user3.getId())));
    }

    @Test
    @DisplayName("Should get following by user ID")
    void shouldGetFollowingByUserId() {
        // user1 follows user2 and user3
        entityManager.persistAndFlush(new Follow(user1, user2));
        entityManager.persistAndFlush(new Follow(user1, user3));

        List<User> following = followRepository.findFollowingByUserId(user1.getId());

        assertEquals(2, following.size());
        assertTrue(following.stream().anyMatch(u -> u.getId().equals(user2.getId())));
        assertTrue(following.stream().anyMatch(u -> u.getId().equals(user3.getId())));
    }

    @Test
    @DisplayName("Should return empty list when no followers")
    void shouldReturnEmptyListWhenNoFollowers() {
        List<User> followers = followRepository.findFollowersByUserId(user1.getId());

        assertTrue(followers.isEmpty());
    }

    @Test
    @DisplayName("Should delete follow relationship")
    void shouldDeleteFollowRelationship() {
        Follow follow = new Follow(user1, user2);
        entityManager.persistAndFlush(follow);

        assertTrue(followRepository.existsByFollower_IdAndFollowing_Id(user1.getId(), user2.getId()));

        followRepository.deleteByFollower_IdAndFollowing_Id(user1.getId(), user2.getId());
        entityManager.flush();

        assertFalse(followRepository.existsByFollower_IdAndFollowing_Id(user1.getId(), user2.getId()));
    }

    @Test
    @DisplayName("Should be able to check for existing follow before creating duplicate")
    void shouldCheckForExistingFollowBeforeCreatingDuplicate() {
        // This tests the pattern used by UserService to prevent duplicates
        Follow follow1 = new Follow(user1, user2);
        followRepository.saveAndFlush(follow1);
        
        // Business logic should check this before saving
        boolean exists = followRepository.existsByFollower_IdAndFollowing_Id(user1.getId(), user2.getId());
        
        assertTrue(exists, "Should detect existing follow relationship");
    }

    @Test
    @DisplayName("Should allow same user to follow multiple users")
    void shouldAllowFollowingMultipleUsers() {
        Follow follow1 = new Follow(user1, user2);
        Follow follow2 = new Follow(user1, user3);
        
        entityManager.persistAndFlush(follow1);
        entityManager.persistAndFlush(follow2);

        assertEquals(2, followRepository.countByFollower_Id(user1.getId()));
    }

    @Test
    @DisplayName("Should allow user to have multiple followers")
    void shouldAllowMultipleFollowers() {
        Follow follow1 = new Follow(user1, user3);
        Follow follow2 = new Follow(user2, user3);
        
        entityManager.persistAndFlush(follow1);
        entityManager.persistAndFlush(follow2);

        assertEquals(2, followRepository.countByFollowing_Id(user3.getId()));
    }
}