package com.aisocial.platform.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LikeTest {

    @Test
    void shouldCreateLikeAndSetCreatedAt() {
        User user = new User("user", "User", "");
        Post post = new Post(user, "Hello");

        Like like = new Like(user, post);
        like.onCreate(); // simulate @PrePersist

        assertThat(like.getUser()).isEqualTo(user);
        assertThat(like.getPost()).isEqualTo(post);
        assertThat(like.getCreatedAt()).isNotNull();
    }
}
