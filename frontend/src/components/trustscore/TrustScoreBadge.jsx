import React, { useState } from 'react';
import TrustScoreTooltip from './TrustScoreTooltip';
import { CheckCircle, Circle, CircleDot, AlertTriangle, XCircle } from 'lucide-react';

/**
 * Trust Score Badge - displays user's trust score with color coding and optional tooltip
 */
function TrustScoreBadge({ score, size = 'md', showTooltip = true, userId = null }) {
  const [isHovered, setIsHovered] = useState(false);

  const numScore = typeof score === 'number' ? score : parseFloat(score) || 50;

  // Determine tier and colors based on score
  const getTierInfo = (s) => {
    if (s >= 90) return { tier: 'TRUSTED', color: 'text-emerald-400', bg: 'bg-emerald-500/20', border: 'border-emerald-500/50', icon: <CheckCircle className="w-3 h-3" /> };
    if (s >= 75) return { tier: 'RELIABLE', color: 'text-green-400', bg: 'bg-green-500/20', border: 'border-green-500/50', icon: <CircleDot className="w-3 h-3" /> };
    if (s >= 50) return { tier: 'NEUTRAL', color: 'text-blue-400', bg: 'bg-blue-500/20', border: 'border-blue-500/50', icon: <Circle className="w-3 h-3" /> };
    if (s >= 25) return { tier: 'QUESTIONABLE', color: 'text-yellow-400', bg: 'bg-yellow-500/20', border: 'border-yellow-500/50', icon: <AlertTriangle className="w-3 h-3" /> };
    return { tier: 'UNRELIABLE', color: 'text-red-400', bg: 'bg-red-500/20', border: 'border-red-500/50', icon: <XCircle className="w-3 h-3" /> };
  };
  const tierInfo = getTierInfo(numScore);

  // Size classes
  const sizeClasses = {
    xs: 'text-[10px] px-1.5 py-0.5',
    sm: 'text-xs px-2 py-1',
    md: 'text-sm px-2.5 py-1',
    lg: 'text-base px-3 py-1.5',
  };

  return (
    <div
      className="relative inline-block"
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      <div
        className={`
          inline-flex items-center gap-1 rounded-full font-semibold
          border cursor-default transition-all duration-200
          ${tierInfo.color} ${tierInfo.bg} ${tierInfo.border}
          ${sizeClasses[size] || sizeClasses.md}
          ${isHovered ? 'scale-105' : ''}
        `}
        title={!showTooltip ? `Trust Score: ${numScore.toFixed(0)}` : undefined}
      >
        <span>{tierInfo.icon}</span>
        <span>{numScore.toFixed(0)}</span>
      </div>

      {/* Tooltip on hover */}
      {showTooltip && isHovered && userId && (
        <TrustScoreTooltip userId={userId} score={numScore} tierInfo={tierInfo} />
      )}
    </div>
  );
}

export default TrustScoreBadge;
