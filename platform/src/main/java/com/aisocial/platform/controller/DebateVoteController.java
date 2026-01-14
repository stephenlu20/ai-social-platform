package com.aisocial.platform.controller;

import com.aisocial.platform.entity.DebateVote;
import com.aisocial.platform.service.DebateVoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/debate-votes")
public class DebateVoteController {

    private final DebateVoteService debateVoteService;

    public DebateVoteController(DebateVoteService debateVoteService) {
        this.debateVoteService = debateVoteService;
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    @GetMapping
    public ResponseEntity<List<DebateVote>> getAll() {
        return ResponseEntity.ok(debateVoteService.findAll());
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @GetMapping("/{id}")
    public ResponseEntity<DebateVote> getById(@PathVariable UUID id) {
        return debateVoteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // -----------------------------
    // CREATE
    // -----------------------------
    @PostMapping
    public ResponseEntity<DebateVote> create(@RequestBody DebateVote vote) {
        try {
            DebateVote saved = debateVoteService.save(vote);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).build();
        }
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @PutMapping("/{id}")
    public ResponseEntity<DebateVote> update(
            @PathVariable UUID id,
            @RequestBody DebateVote vote) {

        DebateVote updated = debateVoteService.update(id, vote.getVote());
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    // -----------------------------
    // DELETE
    // -----------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (debateVoteService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
