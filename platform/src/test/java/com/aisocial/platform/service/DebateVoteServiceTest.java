package com.aisocial.platform.service;

import com.aisocial.platform.entity.DebateVote;
import com.aisocial.platform.entity.VoteType;
import com.aisocial.platform.repository.DebateRepository;
import com.aisocial.platform.repository.DebateVoteRepository;
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

    private DebateVoteService service;

    @Test
    void testSaveAndFind() {
        service = new DebateVoteService(debateVoteRepository, debateRepository);

        UUID debateId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        DebateVote vote = new DebateVote();
        vote.setDebateId(debateId);
        vote.setUserId(userId);
        vote.setVote(VoteType.CHALLENGER);

        service.save(vote);

        List<DebateVote> byDebate = service.findByDebateId(debateId);
        assertThat(byDebate).hasSize(1);

        List<DebateVote> byUser = service.findByUserId(userId);
        assertThat(byUser).hasSize(1);

        Optional<DebateVote> fetched = service.findById(vote.getId());
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getVote()).isEqualTo(VoteType.CHALLENGER);
    }

    @Test
    void testDelete() {
        service = new DebateVoteService(debateVoteRepository, debateRepository);

        UUID debateId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        DebateVote vote = new DebateVote();
        vote.setDebateId(debateId);
        vote.setUserId(userId);
        vote.setVote(VoteType.DEFENDER);

        service.save(vote);
        service.delete(vote.getId());

        List<DebateVote> votes = service.findByDebateId(debateId);
        assertThat(votes).isEmpty();
    }
}
