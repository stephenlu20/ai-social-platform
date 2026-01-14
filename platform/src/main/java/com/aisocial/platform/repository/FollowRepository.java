package com.aisocial.platform.repository;

import com.aisocial.platform.entity.Follow;
import com.aisocial.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FollowRepository extends JpaRepository<Follow, UUID> {

    boolean existsByFollower_IdAndFollowing_Id(UUID followerId, UUID followingId);

    Optional<Follow> findByFollower_IdAndFollowing_Id(UUID followerId, UUID followingId);

    long countByFollowing_Id(UUID userId);

    long countByFollower_Id(UUID userId);

    @Query("SELECT f.follower FROM Follow f WHERE f.following.id = :userId ORDER BY f.createdAt DESC")
    List<User> findFollowersByUserId(@Param("userId") UUID userId);

    @Query("SELECT f.following FROM Follow f WHERE f.follower.id = :userId ORDER BY f.createdAt DESC")
    List<User> findFollowingByUserId(@Param("userId") UUID userId);

    @Modifying
    void deleteByFollower_IdAndFollowing_Id(UUID followerId, UUID followingId);
}