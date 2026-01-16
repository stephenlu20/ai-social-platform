package com.aisocial.platform.controller;

import com.aisocial.platform.dto.FactCheckResultDTO;
import com.aisocial.platform.entity.FactCheck;
import com.aisocial.platform.service.AIFactCheckService;
import com.aisocial.platform.service.FactCheckService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/fact-checks")
@CrossOrigin(origins = "*")
public class FactCheckController {

    private final FactCheckService factCheckService;
    private final AIFactCheckService aiFactCheckService;

    public FactCheckController(FactCheckService factCheckService,
                               AIFactCheckService aiFactCheckService) {
        this.factCheckService = factCheckService;
        this.aiFactCheckService = aiFactCheckService;
    }

    // ----------------------------
    // AI-Powered Fact Check - Preview (before posting)
    // ----------------------------
    @PostMapping("/preview")
    public ResponseEntity<FactCheckResultDTO> previewFactCheck(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(FactCheckResultDTO.error("Content is required"));
        }
        FactCheckResultDTO result = aiFactCheckService.previewFactCheck(content);
        return ResponseEntity.ok(result);
    }

    // ----------------------------
    // AI-Powered Fact Check - Check existing post
    // ----------------------------
    @PostMapping("/post/{postId}")
    public ResponseEntity<FactCheckResultDTO> factCheckPost(
            @PathVariable UUID postId,
            @RequestHeader(value = "X-User-Id", required = false) UUID userId) {
        try {
            FactCheckResultDTO result = aiFactCheckService.factCheckPost(postId, userId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ----------------------------
    // GET All
    // ----------------------------
    @GetMapping
    public ResponseEntity<List<FactCheck>> getAll() {
        return ResponseEntity.ok(factCheckService.findAll());
    }

    // ----------------------------
    // GET by ID
    // ----------------------------
    @GetMapping("/{id}")
    public ResponseEntity<FactCheck> getById(@PathVariable UUID id) {
        return factCheckService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ----------------------------
    // POST / Create
    // ----------------------------
    @PostMapping
    public ResponseEntity<FactCheck> create(@RequestBody FactCheck factCheck) {
        FactCheck saved = factCheckService.save(factCheck);
        return ResponseEntity.ok(saved);
    }

    // ----------------------------
    // PUT / Update
    // ----------------------------
    @PutMapping("/{id}")
    public ResponseEntity<FactCheck> update(
            @PathVariable UUID id,
            @RequestBody FactCheck factCheck) {

        FactCheck updated = factCheckService.update(
                id,
                factCheck.getStatus(),
                factCheck.getOverallScore(),
                factCheck.getClaims()
        );

        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    // ----------------------------
    // DELETE
    // ----------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (factCheckService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
