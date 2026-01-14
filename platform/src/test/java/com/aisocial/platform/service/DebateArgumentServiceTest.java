package com.aisocial.platform.service;

import com.aisocial.platform.entity.Debate;
import com.aisocial.platform.entity.DebateArgument;
import com.aisocial.platform.entity.User;
import com.aisocial.platform.repository.DebateArgumentRepository;
import com.aisocial.platform.repository.DebateRepository;
import com.aisocial.platform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("DebateArgumentService Tests")
class DebateArgumentServiceTest {

    @Autowired
    private DebateArgumentRepository debateArgumentRepository;

    @Autowired
    private DebateRepository debateRepository;

    @Autowired
    private UserRepository userRepository;

    private DebateArgumentService service;

    private User user;
    private Debate debate;

    @BeforeEach
    void setup() {
        service = new DebateArgumentService(debateArgumentRepository);

        // Persist user first
        user = new User("alice", "Alice", "bio");
        user = userRepository.save(user);

        // Persist debate next
        debate = new Debate("Sample Debate", user, user);
        debate = debateRepository.save(debate);
    }

    @Test
    void testSaveAndFindByDebate() {
        DebateArgument arg = new DebateArgument(debate, user, 1, "Argument 1");
        service.save(arg);

        List<DebateArgument> args = service.findByDebate(debate);
        assertThat(args).hasSize(1);
        assertThat(args.get(0).getContent()).isEqualTo("Argument 1");
    }

    @Test
    void testFindById() {
        DebateArgument arg = new DebateArgument(debate, user, 2, "Argument 2");
        service.save(arg);

        Optional<DebateArgument> fetched = service.findById(arg.getId());
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getRoundNumber()).isEqualTo(2);
    }

    @Test
    void testUpdateArgumentContent() {
        DebateArgument arg = new DebateArgument(debate, user, 3, "Old Content");
        service.save(arg);

        arg.setContent("Updated Content");
        service.save(arg);

        Optional<DebateArgument> updated = service.findById(arg.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getContent()).isEqualTo("Updated Content");
    }

    @Test
    void testDeleteArgument() {
        DebateArgument arg = new DebateArgument(debate, user, 4, "To be deleted");
        service.save(arg);

        service.delete(arg.getId());

        Optional<DebateArgument> deleted = service.findById(arg.getId());
        assertThat(deleted).isEmpty();
    }
}
