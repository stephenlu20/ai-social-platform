package com.aisocial.platform.repository;

import com.aisocial.platform.entity.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("Fact Check Repository Tests")
public class FactCheckRepositoryTest {

    @Autowired
    private FactCheckRepository factCheckRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private DebateRepository debateRepository;

    @Autowired
    private DebateArgumentRepository debateArgumentRepository;

    private User createUser(String username) {
        User user = new User(username, username + " display", username + " bio");
        return userRepository.save(user);
    }

    @Test
    void testSaveAndFindById() {
        User user = createUser("gina");
        Post post = postRepository.save(new Post(user, "Hello World"));

        FactCheck fc = new FactCheck();
        fc.setPost(post);
        fc.setRequestedBy(user);
        FactCheck saved = factCheckRepository.save(fc);

        Optional<FactCheck> found = factCheckRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getPost()).isEqualTo(post);
    }

    @Test
    void testFindByPost() {
        User user = createUser("henry");
        Post post = postRepository.save(new Post(user, "Post content"));

        FactCheck fc1 = new FactCheck();
        fc1.setPost(post);
        fc1.setRequestedBy(user);
        factCheckRepository.save(fc1);

        List<FactCheck> list = factCheckRepository.findByPost(post);
        assertThat(list).hasSize(1);
    }

    @Test
    void testFindByDebateArg() {
        // Persist a user first
        User user = new User("ivy", "Ivy", "Bio");
        user = userRepository.saveAndFlush(user);

        // Persist a debate
        Debate debate = new Debate("Debate Title", user, user);
        debate = debateRepository.saveAndFlush(debate);

        // Persist a debate argument
        DebateArgument arg = new DebateArgument(debate, user, 1, "Arg");
        arg = debateArgumentRepository.saveAndFlush(arg);

        // Persist a fact check
        FactCheck fc = new FactCheck();
        fc.setDebateArg(arg);
        fc.setRequestedBy(user);
        fc = factCheckRepository.saveAndFlush(fc);

        // Test repository method
        List<FactCheck> list = factCheckRepository.findByDebateArg(arg);

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getDebateArg().getId()).isEqualTo(arg.getId());
        assertThat(list.get(0).getRequestedBy().getId()).isEqualTo(user.getId());
    }

    @Test
    void testFindByStatus() {
        User user = createUser("jane");
        Post post = postRepository.save(new Post(user, "Another post"));

        FactCheck fc = new FactCheck();
        fc.setPost(post);
        fc.setRequestedBy(user);
        fc.setStatus(FactCheckStatus.VERIFIED);
        factCheckRepository.save(fc);

        List<FactCheck> list = factCheckRepository.findByStatus(FactCheckStatus.VERIFIED);
        assertThat(list).hasSize(1);
    }
}
