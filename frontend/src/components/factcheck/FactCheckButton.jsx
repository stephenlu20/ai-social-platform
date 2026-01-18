import React from 'react';
import { Search, Check, Loader2 } from 'lucide-react';

// #151 - FactCheckButton on PostCard
// #156 - Add fact-check button loading state
function FactCheckButton({ onClick, isLoading, isChecked, size = 'sm' }) {
  const sizeClasses = {
    xs: 'text-xs px-2 py-1 gap-1',
    sm: 'text-[13px] px-2.5 py-1.5 gap-1.5',
    md: 'text-sm px-3 py-2 gap-2'
  };

  return (
    <button
      onClick={onClick}
      disabled={isLoading}
      className={`
        flex items-center font-semibold rounded-lg
        transition-all duration-300
        disabled:opacity-50 disabled:cursor-not-allowed
        ${isChecked
          ? 'bg-blue-500/20 text-blue-400 border border-blue-500/30 hover:bg-blue-500/30'
          : 'bg-white/5 text-white/50 border border-white/10 hover:bg-white/10 hover:text-white/70'
        }
        ${sizeClasses[size]}
      `}
      title={isChecked ? 'View fact-check results' : 'Run fact-check on this post'}
    >
      {isLoading ? (
        <>
          <Loader2 className="w-4 h-4 animate-spin" />
          <span>Checking...</span>
        </>
      ) : (
        <>
          <span>{isChecked ? <Search/> : <Check/>}</span>
          <span>{isChecked ? 'View Check' : 'Fact Check'}</span>
        </>
      )}
    </button>
  );
}

export default FactCheckButton;
