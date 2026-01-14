package com.aisocial.platform.controller;

import com.aisocial.platform.entity.FactCheck;
import com.aisocial.platform.service.FactCheckService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/fact-checks")
public class FactCheckController {

    private final FactCheckService factCheckService;

    public FactCheckController(FactCheckService factCheckService) {
        this.factCheckService = factCheckService;
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
