package com.aisocial.platform.service;

import com.aisocial.platform.dto.TrustScoreBreakdownDTO;
import com.aisocial.platform.entity.FactCheckStatus;
import com.aisocial.platform.entity.User;
import com.aisocial.platform.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for calculating and managing user trust scores.
 *
 * Trust Score Formula:
 * - Base score: 50
 * - Verified posts: +2 each (max +30)
 * - False posts: -5 each (no cap)
 * - Final score clamped to 0-100
 */
@Service
public class TrustScoreService {

    private static final Logger log = LoggerFactory.getLogger(TrustScoreService.class);

    private static final double BASE_SCORE = 50.0;
    private static final double VERIFIED_BONUS = 2.0;
    private static final double VERIFIED_BONUS_CAP = 30.0;
    private static final double FALSE_PENALTY = 5.0;

    private final UserRepository userRepository;

    public TrustScoreService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Calculate trust score based on user statistics.
     */
    public BigDecimal calculateScore(User user) {
        double score = BASE_SCORE;

        // Verified posts bonus (capped)
        double verifiedBonus = Math.min(user.getPostsVerified() * VERIFIED_BONUS, VERIFIED_BONUS_CAP);
        score += verifiedBonus;

        // False posts penalty (no cap)
        score -= user.getPostsFalse() * FALSE_PENALTY;

        // Clamp to 0-100
        score = Math.max(0.0, Math.min(100.0, score));

        return BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Recalculate and save user's trust score.
     */
    @Transactional
    public BigDecimal recalculateAndSave(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        BigDecimal newScore = calculateScore(user);
        user.setTrustScore(newScore);
        userRepository.save(user);

        log.info("Recalculated trust score for user {}: {}", userId, newScore);
        return newScore;
    }

    /**
     * Update user stats based on fact-check result and recalculate score.
     */
    @Transactional
    public BigDecimal updateOnFactCheck(UUID userId, FactCheckStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        user.incrementPostsFactChecked();

        switch (status) {
            case VERIFIED, LIKELY_TRUE -> user.incrementPostsVerified();
            case FALSE -> user.incrementPostsFalse();
            default -> { /* DISPUTED, UNVERIFIABLE, UNCHECKED - no score change */ }
        }

        BigDecimal newScore = calculateScore(user);
        user.setTrustScore(newScore);
        userRepository.save(user);

        log.info("Updated trust score for user {} after fact-check ({}): {}", userId, status, newScore);
        return newScore;
    }

    /**
     * Get detailed trust score breakdown for a user.
     */
    public Optional<TrustScoreBreakdownDTO> getBreakdown(UUID userId) {
        return userRepository.findById(userId)
                .map(this::buildBreakdown);
    }

    private TrustScoreBreakdownDTO buildBreakdown(User user) {
        TrustScoreBreakdownDTO dto = new TrustScoreBreakdownDTO();
        dto.setUserId(user.getId());
        dto.setTotalScore(user.getTrustScore());
        dto.setBaseScore(BASE_SCORE);

        // Fact-check stats
        dto.setPostsFactChecked(user.getPostsFactChecked());
        dto.setPostsVerified(user.getPostsVerified());
        dto.setPostsFalse(user.getPostsFalse());

        // Debate stats (for display only, not used in score calculation)
        dto.setDebatesWon(user.getDebatesWon());
        dto.setDebatesLost(user.getDebatesLost());

        // Calculated bonuses/penalties
        dto.setVerifiedBonus(Math.min(user.getPostsVerified() * VERIFIED_BONUS, VERIFIED_BONUS_CAP));
        dto.setFalsePenalty(user.getPostsFalse() * FALSE_PENALTY);

        // Score tier
        dto.setTier(calculateTier(user.getTrustScore()));

        return dto;
    }

    private String calculateTier(BigDecimal score) {
        if (score == null) return "NEWCOMER";
        double s = score.doubleValue();
        if (s >= 90) return "TRUSTED";
        if (s >= 75) return "RELIABLE";
        if (s >= 50) return "NEUTRAL";
        if (s >= 25) return "QUESTIONABLE";
        return "UNRELIABLE";
    }
}
