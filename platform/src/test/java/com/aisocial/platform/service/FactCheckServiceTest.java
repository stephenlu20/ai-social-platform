package com.aisocial.platform.service;

import com.aisocial.platform.entity.*;
import com.aisocial.platform.repository.DebateArgumentRepository;
import com.aisocial.platform.repository.FactCheckRepository;
import com.aisocial.platform.repository.DebateRepository;
import com.aisocial.platform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("FactCheckService Tests")
class FactCheckServiceTest {

    @Autowired
    private FactCheckRepository factCheckRepository;

    @Autowired
    private DebateArgumentRepository debateArgumentRepository;

    @Autowired
    private DebateRepository debateRepository;

    @Autowired
    private UserRepository userRepository;

    private FactCheckService service;

    private User user;
    private Debate debate;
    private DebateArgument argument;

    @BeforeEach
    void setup() {
        service = new FactCheckService(factCheckRepository);

        // Persist user first
        user = new User("bob", "Bob", "bio");
        user = userRepository.save(user);

        // Persist debate next
        debate = new Debate("FactCheck Debate", user, user);
        debate = debateRepository.save(debate);

        // Persist debate argument
        argument = new DebateArgument(debate, user, 1, "Argument 1");
        argument = debateArgumentRepository.save(argument);
    }

    @Test
    void testSaveAndFindByDebateArg() {
        FactCheck fc = new FactCheck();
        fc.setDebateArg(argument);
        fc.setRequestedBy(user);
        service.save(fc);

        List<FactCheck> list = service.findByDebateArg(argument);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getRequestedBy().getUsername()).isEqualTo("bob");
    }

    @Test
    void testFindById() {
        FactCheck fc = new FactCheck();
        fc.setDebateArg(argument);
        fc.setRequestedBy(user);
        fc = service.save(fc);

        Optional<FactCheck> fetched = service.findById(fc.getId());
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getDebateArg().getId()).isEqualTo(argument.getId());
    }

    @Test
    void testUpdateFactCheckStatus() {
        FactCheck fc = new FactCheck();
        fc.setDebateArg(argument);
        fc.setRequestedBy(user);
        fc = service.save(fc);

        fc.setStatus(FactCheckStatus.VERIFIED);
        service.save(fc);

        Optional<FactCheck> updated = service.findById(fc.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getStatus()).isEqualTo(FactCheckStatus.VERIFIED);
    }

    @Test
    void testDeleteFactCheck() {
        FactCheck fc = new FactCheck();
        fc.setDebateArg(argument);
        fc.setRequestedBy(user);
        fc = service.save(fc);

        service.delete(fc.getId());

        Optional<FactCheck> deleted = service.findById(fc.getId());
        assertThat(deleted).isEmpty();
    }
}
