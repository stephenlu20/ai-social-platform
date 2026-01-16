import React from 'react';

// #154 - ClaimCard component
function ClaimCard({ claim, index }) {
  if (!claim) return null;

  return (
    <div className="p-3 bg-white/5 rounded-lg border border-white/10">
      <div className="flex items-start gap-2">
        <span className="text-white/30 text-xs font-mono">{index + 1}.</span>
        <p className="text-white/80 text-sm flex-1">{claim}</p>
      </div>
    </div>
  );
}

export default ClaimCard;
