package com.aisocial.platform.service;

import com.aisocial.platform.entity.Debate;
import com.aisocial.platform.entity.DebateStatus;
import com.aisocial.platform.entity.DebateVote;
import com.aisocial.platform.entity.User;
import com.aisocial.platform.entity.VoteType;
import com.aisocial.platform.repository.DebateRepository;
import com.aisocial.platform.repository.DebateVoteRepository;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("DebateVoteService Tests")
class DebateVoteServiceTest {

    @Autowired
    private DebateVoteRepository debateVoteRepository;

    @Autowired
    private DebateRepository debateRepository;

    @Autowired
    private UserRepository userRepository;

    private DebateVoteService service;
    private Debate debate;
    private User voter;

    @BeforeEach
    void setUp() {
        service = new DebateVoteService(debateVoteRepository, debateRepository);

        // Create users for the debate
        User challenger = new User("challenger_" + UUID.randomUUID(), "Challenger", "bio");
        User defender = new User("defender_" + UUID.randomUUID(), "Defender", "bio");
        voter = new User("voter_" + UUID.randomUUID(), "Voter", "bio");

        userRepository.save(challenger);
        userRepository.save(defender);
        userRepository.save(voter);

        // Create a debate in VOTING status
        debate = new Debate("Test Topic", challenger, defender);
        debate.setStatus(DebateStatus.VOTING);
        debate = debateRepository.save(debate);
    }

    @Test
    void testSaveAndFind() {
        DebateVote vote = new DebateVote();
        vote.setDebateId(debate.getId());
        vote.setUserId(voter.getId());
        vote.setVote(VoteType.CHALLENGER);

        service.save(vote);

        List<DebateVote> byDebate = service.findByDebateId(debate.getId());
        assertThat(byDebate).hasSize(1);

        List<DebateVote> byUser = service.findByUserId(voter.getId());
        assertThat(byUser).hasSize(1);

        Optional<DebateVote> fetched = service.findById(vote.getId());
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getVote()).isEqualTo(VoteType.CHALLENGER);

        // Verify vote count was updated on the debate
        Debate updatedDebate = debateRepository.findById(debate.getId()).orElseThrow();
        assertThat(updatedDebate.getVotesChallenger()).isEqualTo(1);
    }

    @Test
    void testDelete() {
        DebateVote vote = new DebateVote();
        vote.setDebateId(debate.getId());
        vote.setUserId(voter.getId());
        vote.setVote(VoteType.DEFENDER);

        service.save(vote);

        // Verify vote count was incremented
        Debate afterSave = debateRepository.findById(debate.getId()).orElseThrow();
        assertThat(afterSave.getVotesDefender()).isEqualTo(1);

        service.delete(vote.getId());

        List<DebateVote> votes = service.findByDebateId(debate.getId());
        assertThat(votes).isEmpty();

        // Verify vote count was decremented
        Debate afterDelete = debateRepository.findById(debate.getId()).orElseThrow();
        assertThat(afterDelete.getVotesDefender()).isEqualTo(0);
    }
}
