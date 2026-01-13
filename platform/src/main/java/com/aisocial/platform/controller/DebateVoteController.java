package com.aisocial.platform.controller;

import com.aisocial.platform.entity.DebateVote;
import com.aisocial.platform.entity.VoteType;
import com.aisocial.platform.service.DebateVoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/debates")
public class DebateVoteController {

    private final DebateVoteService voteService;

    public DebateVoteController(DebateVoteService voteService) {
        this.voteService = voteService;
    }

    /**
     * Cast or overwrite a vote for a debate.
     */
    @PostMapping("/{debateId}/vote")
    public ResponseEntity<DebateVote> castVote(
            @PathVariable UUID debateId,
            @RequestParam UUID userId,
            @RequestParam VoteType vote
    ) {
        DebateVote savedVote = voteService.vote(debateId, userId, vote);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVote);
    }

    /**
     * Count votes of a given type for a debate.
     */
    @GetMapping("/{debateId}/votes/count")
    public ResponseEntity<Long> countVotes(
            @PathVariable UUID debateId,
            @RequestParam VoteType vote
    ) {
        long count = voteService.countVotes(debateId, vote);
        return ResponseEntity.ok(count);
    }
}

