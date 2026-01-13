package com.aisocial.platform.service;

import com.aisocial.platform.dto.UserDTO;
import com.aisocial.platform.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    List<UserDTO> getAllUsers();

    Optional<UserDTO> getUserById(UUID id);

    Optional<UserDTO> getUserById(UUID id, UUID viewerId);

    Optional<UserDTO> getUserByUsername(String username);

    Optional<UserDTO> getUserByUsername(String username, UUID viewerId);

    void followUser(UUID followerId, UUID followingId);

    void unfollowUser(UUID followerId, UUID followingId);

    List<UserDTO> getFollowers(UUID userId);

    List<UserDTO> getFollowing(UUID userId);

    boolean isFollowing(UUID followerId, UUID followingId);

    long getFollowerCount(UUID userId);

    long getFollowingCount(UUID userId);
}