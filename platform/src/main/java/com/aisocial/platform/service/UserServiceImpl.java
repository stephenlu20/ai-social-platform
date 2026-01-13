package com.aisocial.platform.service;

import com.aisocial.platform.dto.UserDTO;
import com.aisocial.platform.entity.Follow;
import com.aisocial.platform.entity.User;
import com.aisocial.platform.repository.FollowRepository;
import com.aisocial.platform.repository.PostRepository;
import com.aisocial.platform.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final PostRepository postRepository;

    public UserServiceImpl(UserRepository userRepository,
                           FollowRepository followRepository,
                           PostRepository postRepository) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.postRepository = postRepository;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toEnrichedDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDTO> getUserById(UUID id) {
        return userRepository.findById(id)
                .map(this::toEnrichedDTO);
    }

    @Override
    public Optional<UserDTO> getUserById(UUID id, UUID viewerId) {
        return userRepository.findById(id)
                .map(user -> toEnrichedDTO(user, viewerId));
    }

    @Override
    public Optional<UserDTO> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::toEnrichedDTO);
    }

    @Override
    public Optional<UserDTO> getUserByUsername(String username, UUID viewerId) {
        return userRepository.findByUsername(username)
                .map(user -> toEnrichedDTO(user, viewerId));
    }

    @Override
    @Transactional
    public void followUser(UUID followerId, UUID followingId) {
        if (followerId.equals(followingId)) {
            throw new IllegalStateException("Users cannot follow themselves");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("Follower user not found"));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new IllegalArgumentException("User to follow not found"));

        if (followRepository.existsByFollower_IdAndFollowing_Id(followerId, followingId)) {
            throw new IllegalStateException("Already following this user");
        }

        Follow follow = new Follow(follower, following);
        followRepository.save(follow);
    }

    @Override
    @Transactional
    public void unfollowUser(UUID followerId, UUID followingId) {
        if (!followRepository.existsByFollower_IdAndFollowing_Id(followerId, followingId)) {
            throw new IllegalArgumentException("Follow relationship does not exist");
        }

        followRepository.deleteByFollower_IdAndFollowing_Id(followerId, followingId);
    }

    @Override
    public List<UserDTO> getFollowers(UUID userId) {
        return followRepository.findFollowersByUserId(userId)
                .stream()
                .map(this::toEnrichedDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getFollowing(UUID userId) {
        return followRepository.findFollowingByUserId(userId)
                .stream()
                .map(this::toEnrichedDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isFollowing(UUID followerId, UUID followingId) {
        return followRepository.existsByFollower_IdAndFollowing_Id(followerId, followingId);
    }

    @Override
    public long getFollowerCount(UUID userId) {
        return followRepository.countByFollowing_Id(userId);
    }

    @Override
    public long getFollowingCount(UUID userId) {
        return followRepository.countByFollower_Id(userId);
    }

    private UserDTO toEnrichedDTO(User user) {
        return toEnrichedDTO(user, null);
    }

    private UserDTO toEnrichedDTO(User user, UUID viewerId) {
        UserDTO dto = UserDTO.fromEntity(user);

        dto.setFollowerCount(followRepository.countByFollowing_Id(user.getId()));
        dto.setFollowingCount(followRepository.countByFollower_Id(user.getId()));
        dto.setPostCount((long) postRepository.findByAuthor(user).size());

        if (viewerId != null && !viewerId.equals(user.getId())) {
            dto.setIsFollowing(followRepository.existsByFollower_IdAndFollowing_Id(viewerId, user.getId()));
        } else {
            dto.setIsFollowing(false);
        }

        return dto;
    }
}