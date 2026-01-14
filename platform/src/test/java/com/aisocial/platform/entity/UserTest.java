package com.aisocial.platform.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Entity Tests")
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "Test User", "This is a test bio");
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create user with provided values")
        void shouldCreateUserWithProvidedValues() {
            assertEquals("testuser", user.getUsername());
            assertEquals("Test User", user.getDisplayName());
            assertEquals("This is a test bio", user.getBio());
        }

        @Test
        @DisplayName("Should create user with default constructor")
        void shouldCreateUserWithDefaultConstructor() {
            User emptyUser = new User();
            assertNull(emptyUser.getUsername());
            assertNull(emptyUser.getDisplayName());
        }
    }

    @Nested
    @DisplayName("Trust Score Calculation Tests")
    class TrustScoreCalculationTests {

        @Test
        @DisplayName("Should return base score of 50 for new user")
        void shouldReturnBaseScoreForNewUser() {
            user.setPostsFactChecked(0);
            user.setPostsVerified(0);
            user.setPostsFalse(0);
            user.setDebatesWon(0);
            user.setDebatesLost(0);

            BigDecimal score = user.calculateTrustScore();

            assertEquals(new BigDecimal("50.00"), score);
        }

        @Test
        @DisplayName("Should add 2 points per verified post")
        void shouldAddPointsForVerifiedPosts() {
            user.setPostsVerified(5);
            user.setPostsFalse(0);
            user.setDebatesWon(0);
            user.setDebatesLost(0);

            BigDecimal score = user.calculateTrustScore();

            // 50 + (5 * 2) = 60
            assertEquals(new BigDecimal("60.00"), score);
        }

        @Test
        @DisplayName("Should cap verified post bonus at 30")
        void shouldCapVerifiedPostBonus() {
            user.setPostsVerified(20); // Would be 40 points, but capped at 30
            user.setPostsFalse(0);
            user.setDebatesWon(0);
            user.setDebatesLost(0);

            BigDecimal score = user.calculateTrustScore();

            // 50 + 30 (capped) = 80
            assertEquals(new BigDecimal("80.00"), score);
        }

        @Test
        @DisplayName("Should subtract 5 points per false post")
        void shouldSubtractPointsForFalsePosts() {
            user.setPostsVerified(0);
            user.setPostsFalse(3);
            user.setDebatesWon(0);
            user.setDebatesLost(0);

            BigDecimal score = user.calculateTrustScore();

            // 50 - (3 * 5) = 35
            assertEquals(new BigDecimal("35.00"), score);
        }

        @Test
        @DisplayName("Should not affect trust score for debates won")
        void shouldNotAffectScoreForDebatesWon() {
            user.setPostsVerified(0);
            user.setPostsFalse(0);
            user.setDebatesWon(10);
            user.setDebatesLost(0);

            BigDecimal score = user.calculateTrustScore();

            // Debates don't affect trust score - still 50
            assertEquals(new BigDecimal("50.00"), score);
        }

        @Test
        @DisplayName("Should not affect trust score for debates lost")
        void shouldNotAffectScoreForDebatesLost() {
            user.setPostsVerified(0);
            user.setPostsFalse(0);
            user.setDebatesWon(0);
            user.setDebatesLost(10);

            BigDecimal score = user.calculateTrustScore();

            // Debates don't affect trust score - still 50
            assertEquals(new BigDecimal("50.00"), score);
        }

        @Test
        @DisplayName("Should clamp score to minimum of 0")
        void shouldClampScoreToMinimum() {
            user.setPostsVerified(0);
            user.setPostsFalse(20); // -100 points

            BigDecimal score = user.calculateTrustScore();

            assertEquals(new BigDecimal("0.00"), score);
        }

        @Test
        @DisplayName("Should clamp score to maximum of 100")
        void shouldClampScoreToMaximum() {
            user.setPostsVerified(50); // +30 (capped)
            user.setPostsFalse(0);

            BigDecimal score = user.calculateTrustScore();

            // 50 + 30 = 80
            assertEquals(new BigDecimal("80.00"), score);
        }

        @Test
        @DisplayName("Should calculate complex score correctly")
        void shouldCalculateComplexScore() {
            user.setPostsVerified(10); // +20
            user.setPostsFalse(2);     // -10
            user.setDebatesWon(3);     // no effect
            user.setDebatesLost(1);    // no effect

            BigDecimal score = user.calculateTrustScore();

            // 50 + 20 - 10 = 60
            assertEquals(new BigDecimal("60.00"), score);
        }
    }

    @Nested
    @DisplayName("Helper Method Tests")
    class HelperMethodTests {

        @Test
        @DisplayName("Should increment posts fact checked")
        void shouldIncrementPostsFactChecked() {
            user.setPostsFactChecked(5);
            user.incrementPostsFactChecked();
            assertEquals(6, user.getPostsFactChecked());
        }

        @Test
        @DisplayName("Should increment posts verified and recalculate score")
        void shouldIncrementPostsVerifiedAndRecalculate() {
            user.setPostsVerified(0);
            user.setPostsFalse(0);
            user.setDebatesWon(0);
            user.setDebatesLost(0);
            user.setTrustScore(new BigDecimal("50.00"));

            user.incrementPostsVerified();

            assertEquals(1, user.getPostsVerified());
            assertEquals(new BigDecimal("52.00"), user.getTrustScore());
        }

        @Test
        @DisplayName("Should increment posts false and recalculate score")
        void shouldIncrementPostsFalseAndRecalculate() {
            user.setPostsVerified(0);
            user.setPostsFalse(0);
            user.setDebatesWon(0);
            user.setDebatesLost(0);
            user.setTrustScore(new BigDecimal("50.00"));

            user.incrementPostsFalse();

            assertEquals(1, user.getPostsFalse());
            assertEquals(new BigDecimal("45.00"), user.getTrustScore());
        }

        @Test
        @DisplayName("Should increment debates won without recalculating score")
        void shouldIncrementDebatesWonWithoutRecalculate() {
            user.setDebatesWon(0);
            user.setTrustScore(new BigDecimal("50.00"));

            user.incrementDebatesWon();

            assertEquals(1, user.getDebatesWon());
            // Trust score unchanged - debates don't affect it
            assertEquals(new BigDecimal("50.00"), user.getTrustScore());
        }

        @Test
        @DisplayName("Should increment debates lost without recalculating score")
        void shouldIncrementDebatesLostWithoutRecalculate() {
            user.setDebatesLost(0);
            user.setTrustScore(new BigDecimal("50.00"));

            user.incrementDebatesLost();

            assertEquals(1, user.getDebatesLost());
            // Trust score unchanged - debates don't affect it
            assertEquals(new BigDecimal("50.00"), user.getTrustScore());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get all fields")
        void shouldSetAndGetAllFields() {
            user.setUsername("newuser");
            user.setDisplayName("New User");
            user.setBio("New bio");
            user.setAvatarUrl("https://example.com/avatar.png");
            user.setTrustScore(new BigDecimal("75.50"));
            user.setPostsFactChecked(10);
            user.setPostsVerified(8);
            user.setPostsFalse(2);
            user.setDebatesWon(5);
            user.setDebatesLost(3);

            assertEquals("newuser", user.getUsername());
            assertEquals("New User", user.getDisplayName());
            assertEquals("New bio", user.getBio());
            assertEquals("https://example.com/avatar.png", user.getAvatarUrl());
            assertEquals(new BigDecimal("75.50"), user.getTrustScore());
            assertEquals(10, user.getPostsFactChecked());
            assertEquals(8, user.getPostsVerified());
            assertEquals(2, user.getPostsFalse());
            assertEquals(5, user.getDebatesWon());
            assertEquals(3, user.getDebatesLost());
        }
    }
}