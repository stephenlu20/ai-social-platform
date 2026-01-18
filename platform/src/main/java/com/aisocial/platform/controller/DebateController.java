package com.aisocial.platform.controller;

import com.aisocial.platform.dto.CreateDebateRequestDTO;
import com.aisocial.platform.dto.DebateDTO;
import com.aisocial.platform.dto.SubmitArgumentRequestDTO;
import com.aisocial.platform.entity.Debate;
import com.aisocial.platform.entity.DebateArgument;
import com.aisocial.platform.entity.DebateLike;
import com.aisocial.platform.entity.DebateStatus;
import com.aisocial.platform.entity.DebateVote;
import com.aisocial.platform.entity.User;
import com.aisocial.platform.entity.VoteType;
import com.aisocial.platform.repository.DebateLikeRepository;
import com.aisocial.platform.repository.DebateRepository;
import com.aisocial.platform.repository.DebateVoteRepository;
import com.aisocial.platform.repository.UserRepository;
import com.aisocial.platform.service.DebateService;
import com.aisocial.platform.service.DebateStateMachine;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/debates")
@CrossOrigin(origins = "*")
public class DebateController {

    private final DebateService debateService;
    private final DebateStateMachine debateStateMachine;
    private final DebateRepository debateRepository;
    private final UserRepository userRepository;
    private final DebateLikeRepository debateLikeRepository;
    private final DebateVoteRepository debateVoteRepository;

    public DebateController(DebateService debateService,
                            DebateStateMachine debateStateMachine,
                            DebateRepository debateRepository,
                            UserRepository userRepository,
                            DebateLikeRepository debateLikeRepository,
                            DebateVoteRepository debateVoteRepository) {
        this.debateService = debateService;
        this.debateStateMachine = debateStateMachine;
        this.debateRepository = debateRepository;
        this.userRepository = userRepository;
        this.debateLikeRepository = debateLikeRepository;
        this.debateVoteRepository = debateVoteRepository;
    }

    /**
     * Create a new debate challenge.
     * POST /api/debates
     */
    @PostMapping
    public ResponseEntity<DebateDTO> createChallenge(
            @RequestHeader("X-User-Id") UUID challengerId,
            @RequestBody CreateDebateRequestDTO request) {
        DebateDTO debate = debateService.createChallenge(
                challengerId,
                request.getDefenderId(),
                request.getTopic()
        );
        return new ResponseEntity<>(debate, HttpStatus.CREATED);
    }

    /**
     * Get a debate by ID.
     * GET /api/debates/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<DebateDTO> getDebateById(@PathVariable UUID id) {
        return debateService.getDebateById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * List all active debates (ACTIVE or VOTING status).
     * GET /api/debates
     */
    @GetMapping
    public ResponseEntity<List<DebateDTO>> getActiveDebates() {
        List<DebateDTO> debates = debateService.getActiveDebates();
        return ResponseEntity.ok(debates);
    }

    /**
     * List debates in voting phase.
     * GET /api/debates/voting
     */
    @GetMapping("/voting")
    public ResponseEntity<List<DebateDTO>> getVotingDebates() {
        List<DebateDTO> debates = debateService.getVotingDebates();
        return ResponseEntity.ok(debates);
    }

    /**
     * Accept a debate challenge.
     * POST /api/debates/{id}/accept
     */
    @PostMapping("/{id}/accept")
    public ResponseEntity<DebateDTO> acceptChallenge(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {
        DebateDTO debate = debateService.acceptChallenge(id, userId);
        return ResponseEntity.ok(debate);
    }

    /**
     * Decline a debate challenge.
     * POST /api/debates/{id}/decline
     */
    @PostMapping("/{id}/decline")
    public ResponseEntity<Void> declineChallenge(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {
        debateService.declineChallenge(id, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all debates for a specific user.
     * GET /api/users/{userId}/debates
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DebateDTO>> getDebatesByUser(@PathVariable UUID userId) {
        List<DebateDTO> debates = debateService.getDebatesByUser(userId);
        return ResponseEntity.ok(debates);
    }

    /**
     * Get pending challenges for the current user.
     * GET /api/debates/pending
     */
    @GetMapping("/pending")
    public ResponseEntity<List<DebateDTO>> getPendingChallenges(
            @RequestHeader("X-User-Id") UUID userId) {
        List<DebateDTO> debates = debateService.getPendingChallengesForUser(userId);
        return ResponseEntity.ok(debates);
    }

    /**
     * Submit an argument for a debate.
     * Validates that it's the user's turn before accepting.
     * POST /api/debates/{id}/arguments
     */
    @PostMapping("/{id}/arguments")
    @Transactional
    public ResponseEntity<DebateArgument> submitArgument(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody SubmitArgumentRequestDTO request) {

        Debate debate = debateRepository.findById(id).orElse(null);
        if (debate == null) {
            return ResponseEntity.notFound().build();
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            DebateArgument argument = debateStateMachine.submitArgument(debate, user, request.getContent());
            return new ResponseEntity<>(argument, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Toggle like on a debate.
     * Non-participants only.
     * POST /api/debates/{id}/like
     */
    @PostMapping("/{id}/like")
    @Transactional
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {

        Debate debate = debateRepository.findById(id).orElse(null);
        if (debate == null) {
            return ResponseEntity.notFound().build();
        }

        // Check if user is a participant (can't like own debate)
        if (debate.getChallenger().getId().equals(userId) ||
            debate.getDefender().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Cannot like your own debate"));
        }

        Optional<DebateLike> existingLike = debateLikeRepository.findByDebateIdAndUserId(id, userId);

        boolean liked;
        if (existingLike.isPresent()) {
            // Unlike
            debateLikeRepository.delete(existingLike.get());
            debate.decrementLikeCount();
            liked = false;
        } else {
            // Like
            DebateLike newLike = new DebateLike(id, userId);
            debateLikeRepository.save(newLike);
            debate.incrementLikeCount();
            liked = true;
        }

        debateRepository.save(debate);

        return ResponseEntity.ok(Map.of(
                "liked", liked,
                "likeCount", debate.getLikeCount()
        ));
    }

    /**
     * Get current user's vote on a debate.
     * GET /api/debates/{id}/vote
     */
    @GetMapping("/{id}/vote")
    public ResponseEntity<Map<String, Object>> getUserVote(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {

        Optional<DebateVote> vote = debateVoteRepository.findByDebateIdAndUserId(id, userId);
        if (vote.isPresent()) {
            return ResponseEntity.ok(Map.of(
                    "hasVoted", true,
                    "voteId", vote.get().getId(),
                    "vote", vote.get().getVote().name()
            ));
        } else {
            return ResponseEntity.ok(Map.of("hasVoted", false));
        }
    }

    /**
     * Submit or update a vote on a debate.
     * POST /api/debates/{id}/vote
     */
    @PostMapping("/{id}/vote")
    @Transactional
    public ResponseEntity<Map<String, Object>> submitVote(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody Map<String, String> request) {

        Debate debate = debateRepository.findById(id).orElse(null);
        if (debate == null) {
            return ResponseEntity.notFound().build();
        }

        // Only allow voting during VOTING phase
        if (debate.getStatus() != DebateStatus.VOTING) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Voting is not open for this debate"));
        }

        // Participants cannot vote
        if (debate.getChallenger().getId().equals(userId) ||
            debate.getDefender().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Debate participants cannot vote"));
        }

        VoteType voteType;
        try {
            voteType = VoteType.valueOf(request.get("vote"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid vote type. Use CHALLENGER, DEFENDER, or TIE"));
        }

        Optional<DebateVote> existingVote = debateVoteRepository.findByDebateIdAndUserId(id, userId);

        if (existingVote.isPresent()) {
            // Update existing vote
            DebateVote vote = existingVote.get();
            VoteType oldVote = vote.getVote();

            if (oldVote != voteType) {
                // Update vote counts
                decrementVoteCount(debate, oldVote);
                incrementVoteCount(debate, voteType);
                vote.setVote(voteType);
                debateVoteRepository.save(vote);
                debateRepository.save(debate);
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "voteId", vote.getId(),
                    "vote", voteType.name(),
                    "updated", true
            ));
        } else {
            // Create new vote
            DebateVote newVote = new DebateVote();
            newVote.setDebateId(id);
            newVote.setUserId(userId);
            newVote.setVote(voteType);
            debateVoteRepository.save(newVote);

            incrementVoteCount(debate, voteType);
            debateRepository.save(debate);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "voteId", newVote.getId(),
                    "vote", voteType.name(),
                    "updated", false
            ));
        }
    }

    private void incrementVoteCount(Debate debate, VoteType voteType) {
        switch (voteType) {
            case CHALLENGER -> debate.incrementVotesChallenger();
            case DEFENDER -> debate.incrementVotesDefender();
            case TIE -> debate.incrementVotesTie();
        }
    }

    private void decrementVoteCount(Debate debate, VoteType voteType) {
        switch (voteType) {
            case CHALLENGER -> debate.decrementVotesChallenger();
            case DEFENDER -> debate.decrementVotesDefender();
            case TIE -> debate.decrementVotesTie();
        }
    }
}

