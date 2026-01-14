package com.aisocial.platform.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Post Entity Tests")
class PostTest {

    private User author;
    private Post post;

    @BeforeEach
    void setUp() {
        author = new User("testuser", "Test User", "Bio");
        post = new Post(author, "Hello World!");
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create post with author and content")
        void shouldCreatePostWithAuthorAndContent() {
            assertEquals(author, post.getAuthor());
            assertEquals("Hello World!", post.getContent());
        }

        @Test
        @DisplayName("Should create post with default constructor")
        void shouldCreatePostWithDefaultConstructor() {
            Post emptyPost = new Post();
            assertNull(emptyPost.getAuthor());
            assertNull(emptyPost.getContent());
        }
    }

    @Nested
    @DisplayName("PrePersist Tests")
    class PrePersistTests {

        @Test
        @DisplayName("Should set createdAt on persist")
        void shouldSetCreatedAt() {
            assertNull(post.getCreatedAt());
            post.onCreate();
            assertNotNull(post.getCreatedAt());
        }

        @Test
        @DisplayName("Should initialize counts and flags")
        void shouldInitializeCountsAndFlags() {
            post.onCreate();
            assertEquals(0, post.getLikeCount());
            assertEquals(0, post.getReplyCount());
            assertEquals(0, post.getRepostCount());
            assertFalse(post.getWasCheckedBefore());
            assertEquals(FactCheckStatus.UNCHECKED, post.getFactCheckStatus());
        }
    }

    @Nested
    @DisplayName("Increment Method Tests")
    class IncrementMethodTests {

        @Test
        @DisplayName("Should increment like count")
        void shouldIncrementLikeCount() {
            post.incrementLikeCount();
            assertEquals(1, post.getLikeCount());
        }

        @Test
        @DisplayName("Should increment reply count")
        void shouldIncrementReplyCount() {
            post.incrementReplyCount();
            assertEquals(1, post.getReplyCount());
        }

        @Test
        @DisplayName("Should increment repost count")
        void shouldIncrementRepostCount() {
            post.incrementRepostCount();
            assertEquals(1, post.getRepostCount());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get all fields")
        void shouldSetAndGetAllFields() {
            User newAuthor = new User("newuser", "New User", "Bio");
            Post parentPost = new Post(author, "Parent");
            Post repostPost = new Post(author, "Repost");

            post.setAuthor(newAuthor);
            post.setContent("Updated Content");
            post.setReplyTo(parentPost);
            post.setRepostOf(repostPost);
            post.setStyle("{\"color\":\"red\"}");
            post.setFactCheckStatus(FactCheckStatus.VERIFIED);
            post.setFactCheckScore(0.95);
            post.setFactCheckData("{\"claim\":\"true\"}");
            post.setWasCheckedBefore(true);
            post.setLikeCount(5);
            post.setReplyCount(3);
            post.setRepostCount(2);
            Instant now = Instant.now();
            post.setCreatedAt(now);

            assertEquals(newAuthor, post.getAuthor());
            assertEquals("Updated Content", post.getContent());
            assertEquals(parentPost, post.getReplyTo());
            assertEquals(repostPost, post.getRepostOf());
            assertEquals("{\"color\":\"red\"}", post.getStyle());
            assertEquals(FactCheckStatus.VERIFIED, post.getFactCheckStatus());
            assertEquals(0.95, post.getFactCheckScore());
            assertEquals("{\"claim\":\"true\"}", post.getFactCheckData());
            assertTrue(post.getWasCheckedBefore());
            assertEquals(5, post.getLikeCount());
            assertEquals(3, post.getReplyCount());
            assertEquals(2, post.getRepostCount());
            assertEquals(now, post.getCreatedAt());
        }
    }
}
