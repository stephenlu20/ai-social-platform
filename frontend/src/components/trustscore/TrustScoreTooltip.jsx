import React, { useState, useEffect } from 'react';
import userService from '../../services/userService';

/**
 * Trust Score Tooltip - shows breakdown on hover
 */
function TrustScoreTooltip({ userId, score, tierInfo }) {
  const [breakdown, setBreakdown] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchBreakdown = async () => {
      if (!userId) return;
      try {
        const data = await userService.getTrustBreakdown(userId);
        setBreakdown(data);
      } catch (error) {
        console.error('Error fetching trust breakdown:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchBreakdown();
  }, [userId]);

  return (
    <div
      className="absolute z-50 bottom-full left-1/2 -translate-x-1/2 mb-2
                 w-64 p-3 rounded-xl shadow-xl
                 bg-[#1a1a2e] border border-white/20
                 animate-fadeIn"
    >
      {/* Arrow */}
      <div className="absolute -bottom-2 left-1/2 -translate-x-1/2
                      w-0 h-0 border-l-8 border-r-8 border-t-8
                      border-l-transparent border-r-transparent border-t-white/20" />

      {/* Header */}
      <div className="flex items-center justify-between mb-3 pb-2 border-b border-white/10">
        <span className="text-white/70 text-xs font-semibold uppercase tracking-wider">Trust Score</span>
        <span className={`text-lg font-bold ${tierInfo.color}`}>
          {score.toFixed(0)}
        </span>
      </div>

      {/* Tier */}
      <div className={`text-center py-2 mb-3 rounded-lg ${tierInfo.bg} ${tierInfo.border} border`}>
        <span className={`font-bold ${tierInfo.color}`}>{tierInfo.tier}</span>
      </div>

      {loading ? (
        <div className="text-center text-white/50 text-xs py-2">Loading breakdown...</div>
      ) : breakdown ? (
        <div className="space-y-2 text-xs">
          {/* Verified Posts */}
          <div className="flex justify-between items-center">
            <span className="text-white/60">Verified Posts</span>
            <span className="text-green-400 font-semibold">
              {breakdown.postsVerified} (+{breakdown.verifiedBonus?.toFixed(1)})
            </span>
          </div>

          {/* False Posts */}
          <div className="flex justify-between items-center">
            <span className="text-white/60">False Posts</span>
            <span className="text-red-400 font-semibold">
              {breakdown.postsFalse} (-{breakdown.falsePenalty?.toFixed(1)})
            </span>
          </div>

          {/* Total Fact-Checked */}
          <div className="pt-2 mt-2 border-t border-white/10 flex justify-between items-center">
            <span className="text-white/60">Total Fact-Checked</span>
            <span className="text-white/80 font-semibold">{breakdown.postsFactChecked}</span>
          </div>
        </div>
      ) : (
        <div className="text-center text-white/50 text-xs py-2">Unable to load breakdown</div>
      )}
    </div>
  );
}

export default TrustScoreTooltip;
