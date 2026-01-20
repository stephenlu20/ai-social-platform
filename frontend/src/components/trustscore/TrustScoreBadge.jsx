import React, { useState, useRef } from 'react';
import TrustScoreTooltip from './TrustScoreTooltip';
import { CheckCircle, Circle, CircleDot, AlertTriangle, XCircle } from 'lucide-react';

/**
 * Trust Score Badge - displays user's trust score with color coding and optional tooltip
 * Enhanced version with prominent hover effects
 */
function TrustScoreBadge({ score, size = 'md', showTooltip = true, userId = null }) {
  const [isHovered, setIsHovered] = useState(false);
  const [triggerRect, setTriggerRect] = useState(null);
  const badgeRef = useRef(null);

  const numScore = typeof score === 'number' ? score : parseFloat(score) || 50;

  // Determine tier and colors based on score
  const getTierInfo = (s) => {
    if (s >= 90) return { 
      tier: 'TRUSTED', 
      color: 'text-emerald-400', 
      bg: 'bg-emerald-500/20', 
      border: 'border-emerald-500/50',
      glow: 'shadow-emerald-500/50',
      hoverBg: 'hover:bg-emerald-500/30',
      hoverBorder: 'hover:border-emerald-400',
      icon: <CheckCircle className="w-3 h-3" /> 
    };
    if (s >= 75) return { 
      tier: 'RELIABLE', 
      color: 'text-green-400', 
      bg: 'bg-green-500/20', 
      border: 'border-green-500/50',
      glow: 'shadow-green-500/50',
      hoverBg: 'hover:bg-green-500/30',
      hoverBorder: 'hover:border-green-400',
      icon: <CircleDot className="w-3 h-3" /> 
    };
    if (s >= 50) return { 
      tier: 'NEUTRAL', 
      color: 'text-blue-400', 
      bg: 'bg-blue-500/20', 
      border: 'border-blue-500/50',
      glow: 'shadow-blue-500/50',
      hoverBg: 'hover:bg-blue-500/30',
      hoverBorder: 'hover:border-blue-400',
      icon: <Circle className="w-3 h-3" /> 
    };
    if (s >= 25) return { 
      tier: 'QUESTIONABLE', 
      color: 'text-yellow-400', 
      bg: 'bg-yellow-500/20', 
      border: 'border-yellow-500/50',
      glow: 'shadow-yellow-500/50',
      hoverBg: 'hover:bg-yellow-500/30',
      hoverBorder: 'hover:border-yellow-400',
      icon: <AlertTriangle className="w-3 h-3" /> 
    };
    return { 
      tier: 'UNRELIABLE', 
      color: 'text-red-400', 
      bg: 'bg-red-500/20', 
      border: 'border-red-500/50',
      glow: 'shadow-red-500/50',
      hoverBg: 'hover:bg-red-500/30',
      hoverBorder: 'hover:border-red-400',
      icon: <XCircle className="w-3 h-3" /> 
    };
  };
  const tierInfo = getTierInfo(numScore);

  // Get ring color based on tier
  const getRingColor = (s) => {
    if (s >= 90) return 'ring-emerald-400/60';
    if (s >= 75) return 'ring-green-400/60';
    if (s >= 50) return 'ring-blue-400/60';
    if (s >= 25) return 'ring-yellow-400/60';
    return 'ring-red-400/60';
  };
  const ringColor = getRingColor(numScore);

  // Updated size classes with larger options - increased base sizes
  const sizeClasses = {
    xs: 'text-[11px] px-2 py-0.5 gap-1',
    sm: 'text-[13px] px-3 py-1.5 gap-1.5',
    md: 'text-[15px] px-3.5 py-2 gap-2',
    lg: 'text-[17px] px-4.5 py-2.5 gap-2.5',
    xl: 'text-[19px] px-6 py-3 gap-3',
  };

  // Icon size scaling - increased to match larger text
  const iconSize = {
    xs: 'w-3 h-3',
    sm: 'w-3.5 h-3.5',
    md: 'w-4 h-4',
    lg: 'w-4.5 h-4.5',
    xl: 'w-5 h-5',
  };

  const handleMouseEnter = () => {
    if (badgeRef.current) {
      setTriggerRect(badgeRef.current.getBoundingClientRect());
    }
    setIsHovered(true);
  };

  const handleMouseLeave = () => {
    setIsHovered(false);
    setTriggerRect(null);
  };

  return (
    <div
      ref={badgeRef}
      className="relative inline-block"
      onMouseEnter={handleMouseEnter}
      onMouseLeave={handleMouseLeave}
    >
      <div
        className={`
          inline-flex items-center justify-center rounded-full font-semibold
          border cursor-default
          ${tierInfo.color} ${tierInfo.bg} ${tierInfo.border}
          ${tierInfo.hoverBg} ${tierInfo.hoverBorder}
          ${sizeClasses[size] || sizeClasses.md}
          transition-all duration-300 ease-out
          ${isHovered 
            ? `scale-125 brightness-125 shadow-lg ${tierInfo.glow} -translate-y-0.5 ring-2 ring-offset-2 ring-offset-[#0f0519] ${ringColor}` 
            : 'scale-100 brightness-100 shadow-none'
          }
        `}
        title={!showTooltip ? `Trust Score: ${numScore.toFixed(0)}` : undefined}
      >
        <span className={`inline-flex items-center justify-center ${iconSize[size] || iconSize.md}`}>
          {tierInfo.icon}
        </span>
        <span className="inline-flex items-center">{numScore.toFixed(0)}</span>
      </div>

      {/* Tooltip on hover */}
      {showTooltip && isHovered && userId && triggerRect && (
        <TrustScoreTooltip userId={userId} score={numScore} tierInfo={tierInfo} triggerRect={triggerRect} />
      )}
    </div>
  );
}

export default TrustScoreBadge;