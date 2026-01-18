import React, { useState, useEffect } from 'react';
import userService from '../../services/userService';
import { Trophy, BarChart3, CheckCircle, XCircle } from 'lucide-react';

/**
 * Trust Score Breakdown - detailed breakdown panel
 */
function TrustScoreBreakdown({ userId, initialData = null }) {
  const [breakdown, setBreakdown] = useState(initialData);
  const [loading, setLoading] = useState(!initialData);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (initialData) return;

    const fetchBreakdown = async () => {
      if (!userId) return;
      try {
        setLoading(true);
        const data = await userService.getTrustBreakdown(userId);
        setBreakdown(data);
      } catch (err) {
        console.error('Error fetching trust breakdown:', err);
        setError('Failed to load trust score breakdown');
      } finally {
        setLoading(false);
      }
    };

    fetchBreakdown();
  }, [userId, initialData]);

  const getTierInfo = (score) => {
    const s = parseFloat(score) || 50;
    if (s >= 90) return { tier: 'TRUSTED', color: 'text-emerald-400', bg: 'bg-emerald-500/20', border: 'border-emerald-500/50' };
    if (s >= 75) return { tier: 'RELIABLE', color: 'text-green-400', bg: 'bg-green-500/20', border: 'border-green-500/50' };
    if (s >= 50) return { tier: 'NEUTRAL', color: 'text-blue-400', bg: 'bg-blue-500/20', border: 'border-blue-500/50' };
    if (s >= 25) return { tier: 'QUESTIONABLE', color: 'text-yellow-400', bg: 'bg-yellow-500/20', border: 'border-yellow-500/50' };
    return { tier: 'UNRELIABLE', color: 'text-red-400', bg: 'bg-red-500/20', border: 'border-red-500/50' };
  };

  if (loading) {
    return (
      <div className="p-6 bg-white/5 border border-white/10 rounded-2xl">
        <div className="animate-pulse space-y-4">
          <div className="h-8 bg-white/10 rounded w-1/3"></div>
          <div className="h-24 bg-white/10 rounded"></div>
          <div className="space-y-2">
            <div className="h-4 bg-white/10 rounded w-full"></div>
            <div className="h-4 bg-white/10 rounded w-full"></div>
            <div className="h-4 bg-white/10 rounded w-full"></div>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-6 bg-red-500/10 border border-red-500/30 rounded-2xl text-red-400 text-center">
        {error}
      </div>
    );
  }

  if (!breakdown) return null;

  const tierInfo = getTierInfo(breakdown.totalScore);
  const score = parseFloat(breakdown.totalScore) || 50;

  return (
    <div className="p-6 bg-white/5 border border-white/10 rounded-2xl">
      {/* Header */}
      <div className="flex items-center gap-3 mb-6">
        <span className="text-2xl"><Trophy className="w-6 h-6" /></span>
        <h3 className="text-lg font-bold text-white">Trust Score Breakdown</h3>
      </div>

      {/* Main Score Display */}
      <div className="text-center mb-6">
        <div className={`text-5xl font-black ${tierInfo.color} mb-2`}>
          {score.toFixed(0)}
        </div>
        <div className={`inline-block px-4 py-1.5 rounded-full font-bold text-sm ${tierInfo.bg} ${tierInfo.border} border ${tierInfo.color}`}>
          {tierInfo.tier}
        </div>
      </div>

      {/* Score Bar */}
      <div className="mb-6">
        <div className="h-3 bg-white/10 rounded-full overflow-hidden">
          <div
            className="h-full rounded-full transition-all duration-500 bg-gradient-to-r from-red-500 via-yellow-500 via-blue-500 to-emerald-500"
            style={{ width: `${score}%` }}
          />
        </div>
        <div className="flex justify-between text-xs text-white/50 mt-1">
          <span>0</span>
          <span>50</span>
          <span>100</span>
        </div>
      </div>

      {/* Breakdown Details */}
      <div className="space-y-4">
        <h4 className="text-white/70 text-xs font-semibold uppercase tracking-wider mb-3">Score Components</h4>

        {/* Base Score */}
        <div className="flex justify-between items-center p-3 bg-white/5 rounded-xl">
          <div className="flex items-center gap-2">
            <span className="text-lg"><BarChart3 className="w-5 h-5" /></span>
            <span className="text-white/80">Base Score</span>
          </div>
          <span className="text-white font-semibold">+{breakdown.baseScore?.toFixed(0) || 50}</span>
        </div>

        {/* Verified Posts */}
        <div className="flex justify-between items-center p-3 bg-green-500/10 border border-green-500/20 rounded-xl">
          <div className="flex items-center gap-2">
            <span className="text-lg">✓</span>
            <div>
              <span className="text-white/80">Verified Posts</span>
              <span className="text-white/50 text-xs ml-2">({breakdown.postsVerified} <CheckCircle className="w-5 h-5" /> 2, max +30)</span>
            </div>
          </div>
          <span className="text-green-400 font-semibold">+{breakdown.verifiedBonus?.toFixed(1)}</span>
        </div>

        {/* False Posts */}
        <div className="flex justify-between items-center p-3 bg-red-500/10 border border-red-500/20 rounded-xl">
          <div className="flex items-center gap-2">
            <span className="text-lg">✗</span>
            <div>
              <span className="text-white/80">False Posts</span>
              <span className="text-white/50 text-xs ml-2">({breakdown.postsFalse} <XCircle className="w-5 h-5" /> 5)</span>
            </div>
          </div>
          <span className="text-red-400 font-semibold">-{breakdown.falsePenalty?.toFixed(1)}</span>
        </div>
      </div>

      {/* Stats Summary */}
      <div className="mt-6 pt-4 border-t border-white/10">
        <div className="p-3 bg-white/5 rounded-xl text-center">
          <div className="text-2xl font-bold text-white">{breakdown.postsFactChecked}</div>
          <div className="text-xs text-white/50">Posts Fact-Checked</div>
        </div>
      </div>
    </div>
  );
}

export default TrustScoreBreakdown;
