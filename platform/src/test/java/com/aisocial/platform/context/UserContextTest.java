package com.aisocial.platform.context;

import com.aisocial.platform.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserContext Tests")
class UserContextTest {

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    @Test
    @DisplayName("Should set and get current user")
    void shouldSetAndGetCurrentUser() {
        User user = new User("testuser", "Test User", "Bio");

        UserContext.setCurrentUser(user);

        assertEquals(user, UserContext.getCurrentUser());
    }

    @Test
    @DisplayName("Should return null when no user set")
    void shouldReturnNullWhenNoUserSet() {
        assertNull(UserContext.getCurrentUser());
    }

    @Test
    @DisplayName("Should clear current user")
    void shouldClearCurrentUser() {
        User user = new User("testuser", "Test User", "Bio");
        UserContext.setCurrentUser(user);

        UserContext.clear();

        assertNull(UserContext.getCurrentUser());
    }

    @Test
    @DisplayName("Should return true when user is set")
    void shouldReturnTrueWhenUserIsSet() {
        User user = new User("testuser", "Test User", "Bio");
        UserContext.setCurrentUser(user);

        assertTrue(UserContext.hasUser());
    }

    @Test
    @DisplayName("Should return false when no user is set")
    void shouldReturnFalseWhenNoUserIsSet() {
        assertFalse(UserContext.hasUser());
    }

    @Test
    @DisplayName("Should return false after clearing user")
    void shouldReturnFalseAfterClearingUser() {
        User user = new User("testuser", "Test User", "Bio");
        UserContext.setCurrentUser(user);
        UserContext.clear();

        assertFalse(UserContext.hasUser());
    }

    @Test
    @DisplayName("Should overwrite existing user")
    void shouldOverwriteExistingUser() {
        User user1 = new User("user1", "User One", "Bio 1");
        User user2 = new User("user2", "User Two", "Bio 2");

        UserContext.setCurrentUser(user1);
        UserContext.setCurrentUser(user2);

        assertEquals(user2, UserContext.getCurrentUser());
    }
}