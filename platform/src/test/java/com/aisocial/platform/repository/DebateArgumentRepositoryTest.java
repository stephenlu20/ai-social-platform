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
@DisplayName("Debate Argument Repository Tests")
public class DebateArgumentRepositoryTest {

    @Autowired
    private DebateArgumentRepository debateArgumentRepository;

    @Autowired
    private DebateRepository debateRepository;

    @Autowired
    private UserRepository userRepository;

    private User createUser(String username) {
        User user = new User(username, username + " display", username + " bio");
        return userRepository.save(user);
    }

    @Test
    void testSaveAndFindById() {
        User user = createUser("alice");
        Debate debate = debateRepository.save(new Debate("Topic", user, user));

        DebateArgument arg = new DebateArgument(debate, user, 1, "Argument content");
        DebateArgument saved = debateArgumentRepository.save(arg);

        Optional<DebateArgument> found = debateArgumentRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getContent()).isEqualTo("Argument content");
    }

    @Test
    void testFindByDebate() {
        User user = createUser("bob");
        Debate debate = debateRepository.save(new Debate("Topic2", user, user));

        debateArgumentRepository.save(new DebateArgument(debate, user, 1, "Arg1"));
        debateArgumentRepository.save(new DebateArgument(debate, user, 2, "Arg2"));

        List<DebateArgument> list = debateArgumentRepository.findByDebateOrderByRoundNumberAsc(debate);
        assertThat(list).hasSize(2)
                        .extracting(DebateArgument::getContent)
                        .containsExactlyInAnyOrder("Arg1", "Arg2");
    }

    @Test
    void testUniqueConstraint() {
        User user = createUser("carol");
        Debate debate = debateRepository.save(new Debate("UniqueTopic", user, user));

        debateArgumentRepository.save(new DebateArgument(debate, user, 1, "First"));

        DebateArgument duplicate = new DebateArgument(debate, user, 1, "Duplicate");
        try {
            debateArgumentRepository.saveAndFlush(duplicate);
        } catch (Exception e) {
            assertThat(e).hasMessageContaining("constraint");
        }
    }
}
