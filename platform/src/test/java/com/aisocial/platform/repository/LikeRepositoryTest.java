package com.aisocial.platform.repository;

import com.aisocial.platform.entity.Like;
import com.aisocial.platform.entity.Post;
import com.aisocial.platform.entity.User;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("Like Repository Tests")
class LikeRepositoryTest {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    void shouldSaveAndFindLikeByUserAndPost() {
        User user = userRepository.save(new User("alice", "Alice", ""));
        Post post = postRepository.save(new Post(user, "Hello world"));

        Optional<Like> fetched = likeRepository.findByUserAndPost(user, post);

        assertThat(fetched).isPresent();
        assertThat(fetched.get().getUser()).isEqualTo(user);
        assertThat(fetched.get().getPost()).isEqualTo(post);
    }

    @Test
    void shouldCountLikesForPost() {
        User user = userRepository.save(new User("bob", "Bob", ""));
        Post post = postRepository.save(new Post(user, "Post"));

        likeRepository.save(new Like(user, post));

        long count = likeRepository.countByPost(post);
        assertThat(count).isEqualTo(1);
    }
}
