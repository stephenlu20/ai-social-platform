package com.aisocial.platform.repository;

import com.aisocial.platform.entity.Debate;
import com.aisocial.platform.entity.DebateStatus;
import com.aisocial.platform.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("Debate Repository Tests")
class DebateRepositoryTest {

    @Autowired
    private DebateRepository debateRepository;

    @Autowired
    private UserRepository userRepository;

    private User alice;
    private User bob;
    private User carol;

    @BeforeEach
    void setUp() {
        alice = userRepository.save(new User("alice", "Alice", "Bio"));
        bob = userRepository.save(new User("bob", "Bob", "Bio"));
        carol = userRepository.save(new User("carol", "Carol", "Bio"));
    }

    @Test
    @DisplayName("Should save and retrieve debate by ID")
    void shouldSaveAndRetrieveDebate() {
        Debate debate = new Debate("Is Java better than Python?", alice, bob);
        Debate saved = debateRepository.save(debate);

        assertThat(debateRepository.findById(saved.getId()))
            .isPresent()
            .hasValueSatisfying(d -> {
                assertThat(d.getTopic()).isEqualTo("Is Java better than Python?");
                assertThat(d.getChallenger().getId()).isEqualTo(alice.getId());
                assertThat(d.getDefender().getId()).isEqualTo(bob.getId());
            });
    }

    @Test
    @DisplayName("Should find debates by status")
    void shouldFindByStatus() {
        Debate pending = new Debate("Topic 1", alice, bob);
        pending.setStatus(DebateStatus.PENDING);
        debateRepository.save(pending);

        Debate active = new Debate("Topic 2", alice, carol);
        active.setStatus(DebateStatus.ACTIVE);
        debateRepository.save(active);

        List<Debate> pendingDebates = debateRepository.findByStatus(DebateStatus.PENDING);
        List<Debate> activeDebates = debateRepository.findByStatus(DebateStatus.ACTIVE);

        assertThat(pendingDebates).hasSize(1);
        assertThat(activeDebates).hasSize(1);
        assertThat(pendingDebates.get(0).getTopic()).isEqualTo("Topic 1");
        assertThat(activeDebates.get(0).getTopic()).isEqualTo("Topic 2");
    }

    @Test
    @DisplayName("Should find debates by challenger")
    void shouldFindByChallenger() {
        debateRepository.save(new Debate("Topic 1", alice, bob));
        debateRepository.save(new Debate("Topic 2", alice, carol));
        debateRepository.save(new Debate("Topic 3", bob, carol));

        List<Debate> aliceDebates = debateRepository.findByChallenger(alice);

        assertThat(aliceDebates).hasSize(2);
        assertThat(aliceDebates).allMatch(d -> d.getChallenger().getId().equals(alice.getId()));
    }

    @Test
    @DisplayName("Should find debates by defender")
    void shouldFindByDefender() {
        debateRepository.save(new Debate("Topic 1", alice, bob));
        debateRepository.save(new Debate("Topic 2", carol, bob));

        List<Debate> bobDefending = debateRepository.findByDefender(bob);

        assertThat(bobDefending).hasSize(2);
        assertThat(bobDefending).allMatch(d -> d.getDefender().getId().equals(bob.getId()));
    }

    @Test
    @DisplayName("Should find debates by participant (challenger or defender)")
    void shouldFindByParticipant() {
        debateRepository.save(new Debate("Alice challenges Bob", alice, bob));
        debateRepository.save(new Debate("Carol challenges Alice", carol, alice));
        debateRepository.save(new Debate("Bob challenges Carol", bob, carol));

        List<Debate> aliceDebates = debateRepository.findByParticipant(alice);

        assertThat(aliceDebates).hasSize(2);
    }

    @Test
    @DisplayName("Should find debates by participant and status")
    void shouldFindByParticipantAndStatus() {
        Debate pending = new Debate("Pending debate", alice, bob);
        pending.setStatus(DebateStatus.PENDING);
        debateRepository.save(pending);

        Debate active = new Debate("Active debate", alice, carol);
        active.setStatus(DebateStatus.ACTIVE);
        debateRepository.save(active);

        List<Debate> aliceActiveDebates = debateRepository.findByParticipantAndStatus(alice, DebateStatus.ACTIVE);

        assertThat(aliceActiveDebates).hasSize(1);
        assertThat(aliceActiveDebates.get(0).getStatus()).isEqualTo(DebateStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should find pending challenges for user")
    void shouldFindPendingChallengesForUser() {
        Debate pending1 = new Debate("Challenge 1", alice, bob);
        pending1.setStatus(DebateStatus.PENDING);
        debateRepository.save(pending1);

        Debate pending2 = new Debate("Challenge 2", carol, bob);
        pending2.setStatus(DebateStatus.PENDING);
        debateRepository.save(pending2);

        Debate active = new Debate("Active", alice, bob);
        active.setStatus(DebateStatus.ACTIVE);
        debateRepository.save(active);

        List<Debate> bobsPendingChallenges = debateRepository.findPendingChallengesForUser(bob);

        assertThat(bobsPendingChallenges).hasSize(2);
        assertThat(bobsPendingChallenges).allMatch(d -> d.getStatus() == DebateStatus.PENDING);
    }

    @Test
    @DisplayName("Should find debates by multiple statuses")
    void shouldFindByStatusIn() {
        Debate pending = new Debate("Pending", alice, bob);
        pending.setStatus(DebateStatus.PENDING);
        debateRepository.save(pending);

        Debate active = new Debate("Active", alice, carol);
        active.setStatus(DebateStatus.ACTIVE);
        debateRepository.save(active);

        Debate voting = new Debate("Voting", bob, carol);
        voting.setStatus(DebateStatus.VOTING);
        debateRepository.save(voting);

        Debate completed = new Debate("Completed", carol, alice);
        completed.setStatus(DebateStatus.COMPLETED);
        debateRepository.save(completed);

        List<Debate> activeOrVoting = debateRepository.findByStatusIn(
            List.of(DebateStatus.ACTIVE, DebateStatus.VOTING)
        );

        assertThat(activeOrVoting).hasSize(2);
        assertThat(activeOrVoting).allMatch(d ->
            d.getStatus() == DebateStatus.ACTIVE || d.getStatus() == DebateStatus.VOTING
        );
    }

    @Test
    @DisplayName("Should check if user has debates as challenger")
    void shouldCheckExistsByChallenger() {
        debateRepository.save(new Debate("Topic", alice, bob));

        assertThat(debateRepository.existsByChallenger(alice)).isTrue();
        assertThat(debateRepository.existsByChallenger(carol)).isFalse();
    }

    @Test
    @DisplayName("Should check if user has debates as defender")
    void shouldCheckExistsByDefender() {
        debateRepository.save(new Debate("Topic", alice, bob));

        assertThat(debateRepository.existsByDefender(bob)).isTrue();
        assertThat(debateRepository.existsByDefender(carol)).isFalse();
    }
}
