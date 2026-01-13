package com.aisocial.platform.controller;

import com.aisocial.platform.entity.DebateArgument;
import com.aisocial.platform.entity.FactCheck;
import com.aisocial.platform.entity.Post;
import com.aisocial.platform.entity.User;
import com.aisocial.platform.repository.DebateArgumentRepository;
import com.aisocial.platform.repository.PostRepository;
import com.aisocial.platform.repository.UserRepository;
import com.aisocial.platform.service.FactCheckService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/fact-checks")
public class FactCheckController {

    private final FactCheckService factCheckService;
    private final PostRepository postRepository;
    private final DebateArgumentRepository debateArgumentRepository;
    private final UserRepository userRepository;

    public FactCheckController(
            FactCheckService factCheckService,
            PostRepository postRepository,
            DebateArgumentRepository debateArgumentRepository,
            UserRepository userRepository
    ) {
        this.factCheckService = factCheckService;
        this.postRepository = postRepository;
        this.debateArgumentRepository = debateArgumentRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a new fact check (AI-triggered).
     * Either postId OR debateArgumentId must be provided.
     */
    @PostMapping
    public ResponseEntity<FactCheck> createFactCheck(
            @RequestParam(required = false) UUID postId,
            @RequestParam(required = false) UUID debateArgumentId,
            @RequestParam UUID requestedBy
    ) {
        if (postId == null && debateArgumentId == null) {
            return ResponseEntity.badRequest().build();
        }

        Optional<User> userOpt = userRepository.findById(requestedBy);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        FactCheck factCheck = new FactCheck();
        factCheck.setRequestedBy(userOpt.get());

        if (postId != null) {
            Optional<Post> postOpt = postRepository.findById(postId);
            if (postOpt.isEmpty()) return ResponseEntity.notFound().build();
            factCheck.setPost(postOpt.get());
        }

        if (debateArgumentId != null) {
            Optional<DebateArgument> argOpt = debateArgumentRepository.findById(debateArgumentId);
            if (argOpt.isEmpty()) return ResponseEntity.notFound().build();
            factCheck.setDebateArg(argOpt.get());
        }

        FactCheck saved = factCheckService.save(factCheck);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Get a single fact check by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FactCheck> getById(@PathVariable UUID id) {
        return factCheckService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get fact checks for a post.
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<FactCheck>> getForPost(@PathVariable UUID postId) {
        return postRepository.findById(postId)
                .map(post -> ResponseEntity.ok(factCheckService.findByPost(post)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get fact checks for a debate argument.
     */
    @GetMapping("/debate-argument/{argId}")
    public ResponseEntity<List<FactCheck>> getForDebateArgument(@PathVariable UUID argId) {
        return debateArgumentRepository.findById(argId)
                .map(arg -> ResponseEntity.ok(factCheckService.findByDebateArg(arg)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get fact checks requested by a user.
     */
    @GetMapping("/requested-by/{userId}")
    public ResponseEntity<List<FactCheck>> getRequestedBy(@PathVariable UUID userId) {
        return userRepository.findById(userId)
                .map(user -> ResponseEntity.ok(factCheckService.findByRequestedBy(user)))
                .orElse(ResponseEntity.notFound().build());
    }
}