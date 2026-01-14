package com.aisocial.platform.controller;

import com.aisocial.platform.dto.CreateDebateRequestDTO;
import com.aisocial.platform.dto.DebateDTO;
import com.aisocial.platform.dto.SubmitArgumentRequestDTO;
import com.aisocial.platform.entity.Debate;
import com.aisocial.platform.entity.DebateArgument;
import com.aisocial.platform.entity.User;
import com.aisocial.platform.repository.DebateRepository;
import com.aisocial.platform.repository.UserRepository;
import com.aisocial.platform.service.DebateService;
import com.aisocial.platform.service.DebateStateMachine;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/debates")
@CrossOrigin(origins = "*")
public class DebateController {

    private final DebateService debateService;
    private final DebateStateMachine debateStateMachine;
    private final DebateRepository debateRepository;
    private final UserRepository userRepository;

    public DebateController(DebateService debateService,
                            DebateStateMachine debateStateMachine,
                            DebateRepository debateRepository,
                            UserRepository userRepository) {
        this.debateService = debateService;
        this.debateStateMachine = debateStateMachine;
        this.debateRepository = debateRepository;
        this.userRepository = userRepository;
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
}
