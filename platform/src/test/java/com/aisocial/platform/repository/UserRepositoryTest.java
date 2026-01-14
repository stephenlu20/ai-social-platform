package com.aisocial.platform.repository;

import com.aisocial.platform.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "Test User", "Test bio");
        testUser.setAvatarUrl("https://example.com/avatar.png");
        testUser.setTrustScore(new BigDecimal("75.00"));
    }

    @Test
    @DisplayName("Should save and retrieve user by ID")
    void shouldSaveAndRetrieveUserById() {
        User savedUser = entityManager.persistAndFlush(testUser);

        Optional<User> found = userRepository.findById(savedUser.getId());

        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
        assertEquals("Test User", found.get().getDisplayName());
    }

    @Test
    @DisplayName("Should find user by username")
    void shouldFindUserByUsername() {
        entityManager.persistAndFlush(testUser);

        Optional<User> found = userRepository.findByUsername("testuser");

        assertTrue(found.isPresent());
        assertEquals("Test User", found.get().getDisplayName());
    }

    @Test
    @DisplayName("Should return empty when username not found")
    void shouldReturnEmptyWhenUsernameNotFound() {
        Optional<User> found = userRepository.findByUsername("nonexistent");

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should check if username exists")
    void shouldCheckIfUsernameExists() {
        entityManager.persistAndFlush(testUser);

        assertTrue(userRepository.existsByUsername("testuser"));
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    @DisplayName("Should find all users")
    void shouldFindAllUsers() {
        User user1 = new User("user1", "User One", "Bio 1");
        User user2 = new User("user2", "User Two", "Bio 2");
        User user3 = new User("user3", "User Three", "Bio 3");

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        entityManager.persistAndFlush(user3);

        List<User> users = userRepository.findAll();

        assertEquals(3, users.size());
    }

    @Test
    @DisplayName("Should update user")
    void shouldUpdateUser() {
        User savedUser = entityManager.persistAndFlush(testUser);

        savedUser.setDisplayName("Updated Name");
        savedUser.setBio("Updated bio");
        userRepository.save(savedUser);
        entityManager.flush();
        entityManager.clear();

        Optional<User> found = userRepository.findById(savedUser.getId());

        assertTrue(found.isPresent());
        assertEquals("Updated Name", found.get().getDisplayName());
        assertEquals("Updated bio", found.get().getBio());
    }

    @Test
    @DisplayName("Should delete user")
    void shouldDeleteUser() {
        User savedUser = entityManager.persistAndFlush(testUser);

        userRepository.delete(savedUser);
        entityManager.flush();

        Optional<User> found = userRepository.findById(savedUser.getId());

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should persist default values")
    void shouldPersistDefaultValues() {
        User newUser = new User("newuser", "New User", "New bio");
        User savedUser = entityManager.persistAndFlush(newUser);

        entityManager.clear();
        Optional<User> found = userRepository.findById(savedUser.getId());

        assertTrue(found.isPresent());
        assertEquals(0, found.get().getTrustScore().compareTo(new BigDecimal("50.00")));
        assertEquals(0, found.get().getPostsFactChecked());
        assertEquals(0, found.get().getPostsVerified());
        assertEquals(0, found.get().getPostsFalse());
        assertEquals(0, found.get().getDebatesWon());
        assertEquals(0, found.get().getDebatesLost());
        assertNotNull(found.get().getCreatedAt());
    }

    @Test
    @DisplayName("Should enforce unique username constraint")
    void shouldEnforceUniqueUsernameConstraint() {
        entityManager.persistAndFlush(testUser);

        User duplicateUser = new User("testuser", "Duplicate User", "Duplicate bio");

        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(duplicateUser);
        });
    }

    @Test
    @DisplayName("Should persist trust score with correct precision")
    void shouldPersistTrustScoreWithCorrectPrecision() {
        testUser.setTrustScore(new BigDecimal("87.65"));
        User savedUser = entityManager.persistAndFlush(testUser);

        entityManager.clear();
        Optional<User> found = userRepository.findById(savedUser.getId());

        assertTrue(found.isPresent());
        assertEquals(0, found.get().getTrustScore().compareTo(new BigDecimal("87.65")));
    }
}