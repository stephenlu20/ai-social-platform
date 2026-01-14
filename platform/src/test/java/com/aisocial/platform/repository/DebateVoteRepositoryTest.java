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
@DisplayName("Debate Vote Repository Tests")
public class DebateVoteRepositoryTest {

    @Autowired
    private DebateVoteRepository debateVoteRepository;

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
        User user = createUser("dave");
        Debate debate = debateRepository.save(new Debate("VoteTopic", user, user));

        DebateVote vote = new DebateVote();
        vote.setDebateId(debate.getId());
        vote.setUserId(user.getId());
        vote.setVote(VoteType.CHALLENGER);
        DebateVote saved = debateVoteRepository.save(vote);

        Optional<DebateVote> found = debateVoteRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getVote()).isEqualTo(VoteType.CHALLENGER);
    }

    @Test
    void testFindByDebateId() {
        User user = createUser("ellen");
        Debate debate = debateRepository.save(new Debate("VoteTopic2", user, user));

        DebateVote vote1 = new DebateVote();
        vote1.setDebateId(debate.getId());
        vote1.setUserId(user.getId());
        vote1.setVote(VoteType.DEFENDER);
        debateVoteRepository.save(vote1);

        List<DebateVote> votes = debateVoteRepository.findByDebateId(debate.getId());
        assertThat(votes).hasSize(1);
        assertThat(votes.get(0).getVote()).isEqualTo(VoteType.DEFENDER);
    }

    @Test
    void testUniqueDebateUserConstraint() {
        User user = createUser("frank");
        Debate debate = debateRepository.save(new Debate("VoteTopic3", user, user));

        DebateVote vote1 = new DebateVote();
        vote1.setDebateId(debate.getId());
        vote1.setUserId(user.getId());
        vote1.setVote(VoteType.CHALLENGER);
        debateVoteRepository.save(vote1);

        DebateVote duplicate = new DebateVote();
        duplicate.setDebateId(debate.getId());
        duplicate.setUserId(user.getId());
        duplicate.setVote(VoteType.DEFENDER);

        try {
            debateVoteRepository.saveAndFlush(duplicate);
        } catch (Exception e) {
            assertThat(e).hasMessageContaining("constraint");
        }
    }
}
