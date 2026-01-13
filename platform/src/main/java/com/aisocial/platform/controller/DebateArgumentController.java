package com.aisocial.platform.controller;

import com.aisocial.platform.entity.Debate;
import com.aisocial.platform.entity.DebateArgument;
import com.aisocial.platform.entity.User;
import com.aisocial.platform.repository.DebateRepository;
import com.aisocial.platform.repository.UserRepository;
import com.aisocial.platform.service.DebateArgumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/debates/{debateId}/arguments")
public class DebateArgumentController {

    private final DebateArgumentService argumentService;
    private final DebateRepository debateRepository;
    private final UserRepository userRepository;

    public DebateArgumentController(
            DebateArgumentService argumentService,
            DebateRepository debateRepository,
            UserRepository userRepository
    ) {
        this.argumentService = argumentService;
        this.debateRepository = debateRepository;
        this.userRepository = userRepository;
    }

    /** Create a debate argument */
    @PostMapping
    public ResponseEntity<DebateArgument> createArgument(
            @PathVariable UUID debateId,
            @RequestParam UUID userId,
            @RequestBody DebateArgument argumentRequest
    ) {
        Optional<Debate> debateOpt = debateRepository.findById(debateId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (debateOpt.isEmpty() || userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        argumentRequest.setDebate(debateOpt.get());
        argumentRequest.setUser(userOpt.get());

        DebateArgument saved = argumentService.saveArgument(argumentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /** Get all arguments for a debate, ordered by round */
    @GetMapping
    public ResponseEntity<List<DebateArgument>> getArguments(@PathVariable UUID debateId) {
        Optional<Debate> debateOpt = debateRepository.findById(debateId);
        if (debateOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<DebateArgument> args = argumentService.getArgumentsByDebate(debateOpt.get());
        return ResponseEntity.ok(args);
    }

    /** Get a specific argument by debate, user, and round */
    @GetMapping("/{userId}/{round}")
    public ResponseEntity<DebateArgument> getArgument(
            @PathVariable UUID debateId,
            @PathVariable UUID userId,
            @PathVariable int round
    ) {
        Optional<Debate> debateOpt = debateRepository.findById(debateId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (debateOpt.isEmpty() || userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<DebateArgument> argumentOpt =
                argumentService.getArgumentByDebateUserRound(debateOpt.get(), userOpt.get(), round);

        return argumentOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
