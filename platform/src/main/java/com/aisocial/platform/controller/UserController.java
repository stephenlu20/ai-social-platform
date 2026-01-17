package com.aisocial.platform.controller;

import com.aisocial.platform.dto.TrustScoreBreakdownDTO;
import com.aisocial.platform.dto.UpdateUserRequestDTO;
import com.aisocial.platform.dto.UserDTO;
import com.aisocial.platform.service.TrustScoreService;
import com.aisocial.platform.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final TrustScoreService trustScoreService;

    public UserController(UserService userService, TrustScoreService trustScoreService) {
        this.userService = userService;
        this.trustScoreService = trustScoreService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@RequestHeader("X-User-Id") UUID userId) {
        return userService.getUserById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Id", required = false) UUID currentUserId) {
        return userService.getUserById(id, currentUserId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(
            @PathVariable String username,
            @RequestHeader(value = "X-User-Id", required = false) UUID currentUserId) {
        return userService.getUserByUsername(username, currentUserId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/trust-breakdown")
    public ResponseEntity<TrustScoreBreakdownDTO> getTrustBreakdown(@PathVariable UUID id) {
        return trustScoreService.getBreakdown(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable UUID id,
            @RequestBody UpdateUserRequestDTO request
    ) {
        UserDTO updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }
}