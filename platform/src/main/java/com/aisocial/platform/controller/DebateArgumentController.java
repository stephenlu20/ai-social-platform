package com.aisocial.platform.controller;

import com.aisocial.platform.dto.DebateArgumentDTO;
import com.aisocial.platform.dto.DebateArgumentUpdateDTO;
import com.aisocial.platform.entity.DebateArgument;
import com.aisocial.platform.service.DebateArgumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/debate-arguments")
public class DebateArgumentController {

    private final DebateArgumentService debateArgumentService;

    public DebateArgumentController(DebateArgumentService debateArgumentService) {
        this.debateArgumentService = debateArgumentService;
    }

    @GetMapping
    public ResponseEntity<List<DebateArgument>> getAll() {
        return ResponseEntity.ok(debateArgumentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DebateArgument> getById(@PathVariable UUID id) {
        return debateArgumentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DebateArgument> create(@RequestBody DebateArgument argument) {
        DebateArgument saved = debateArgumentService.save(argument);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DebateArgument> update(
            @PathVariable UUID id,
            @RequestBody DebateArgumentUpdateDTO dto) {

        DebateArgument updated = debateArgumentService.update(id, dto.getContent(), dto.getRoundNumber());
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (debateArgumentService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/debate/{debateId}")
    public ResponseEntity<List<DebateArgumentDTO>> getArgumentsByDebateId(@PathVariable UUID debateId) {
        List<DebateArgumentDTO> arguments = debateArgumentService.findByDebateId(debateId)
                .stream()
                .map(DebateArgumentDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(arguments);
    }
}
