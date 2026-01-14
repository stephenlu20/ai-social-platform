package com.aisocial.platform.controller;

import com.aisocial.platform.dto.UserDTO;
import com.aisocial.platform.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class FollowController {

    private final UserService userService;

    public FollowController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<Void> followUser(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID currentUserId) {
        userService.followUser(currentUserId, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/follow")
    public ResponseEntity<Void> unfollowUser(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID currentUserId) {
        userService.unfollowUser(currentUserId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<List<UserDTO>> getFollowers(@PathVariable UUID id) {
        List<UserDTO> followers = userService.getFollowers(id);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{id}/following")
    public ResponseEntity<List<UserDTO>> getFollowing(@PathVariable UUID id) {
        List<UserDTO> following = userService.getFollowing(id);
        return ResponseEntity.ok(following);
    }

    @GetMapping("/{id}/is-following")
    public ResponseEntity<Boolean> isFollowing(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID currentUserId) {
        boolean following = userService.isFollowing(currentUserId, id);
        return ResponseEntity.ok(following);
    }
}
