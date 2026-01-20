import React, { useState, useEffect } from 'react';
import ReactDOM from 'react-dom';
import userService from '../../services/userService';

/**
 * Trust Score Tooltip - shows breakdown on hover
 * Uses portal and calculates position to stay within viewport
 */
function TrustScoreTooltip({ userId, score, tierInfo, triggerRect }) {
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

  // Calculate position immediately from triggerRect (no effect needed)
  const tooltipWidth = 256;
  const tooltipHeight = 200; // Approximate height
  const margin = 8;
  const arrowSize = 8;

  let top, left;
  let showArrowOnTop = false;

  // Vertical positioning - prefer below to avoid going off top of screen
  if (triggerRect.bottom + tooltipHeight + margin + arrowSize < window.innerHeight) {
    // Position below
    top = triggerRect.bottom + arrowSize;
    showArrowOnTop = true;
  } else if (triggerRect.top - tooltipHeight - margin - arrowSize > 0) {
    // Position above
    top = triggerRect.top - tooltipHeight - arrowSize;
    showArrowOnTop = false;
  } else {
    // Default to below if neither fits well
    top = triggerRect.bottom + arrowSize;
    showArrowOnTop = true;
  }

  // Horizontal positioning - center, but clamp to viewport
  const centerX = triggerRect.left + triggerRect.width / 2;
  left = centerX - tooltipWidth / 2;

  // Clamp horizontal position
  if (left < margin) {
    left = margin;
  } else if (left + tooltipWidth > window.innerWidth - margin) {
    left = window.innerWidth - margin - tooltipWidth;
  }

  // Calculate arrow position relative to tooltip
  const arrowLeft = Math.max(16, Math.min(tooltipWidth - 16, centerX - left));

  const style = {
    position: 'fixed',
    top: `${top}px`,
    left: `${left}px`,
    width: `${tooltipWidth}px`,
    zIndex: 9999,
  };

  const tooltipContent = (
    <div
      className="p-3 rounded-xl shadow-xl bg-[#1a1a2e] border border-white/20"
      style={style}
    >
      {/* Arrow */}
      <div
        className="absolute w-0 h-0 border-l-8 border-r-8 border-l-transparent border-r-transparent"
        style={{
          left: `${arrowLeft}px`,
          transform: 'translateX(-50%)',
          ...(showArrowOnTop
            ? { top: '-8px', borderBottom: '8px solid rgba(255,255,255,0.2)' }
            : { bottom: '-8px', borderTop: '8px solid rgba(255,255,255,0.2)' }
          ),
        }}
      />

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

  return ReactDOM.createPortal(tooltipContent, document.body);
}

export default TrustScoreTooltip;
