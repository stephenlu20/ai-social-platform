import React from 'react';

// #152 - FactCheckBadge (status indicator)
function FactCheckBadge({ status, score, size = 'sm', onClick }) {
  if (!status || status === 'UNCHECKED') return null;

  const getStatusConfig = (status) => {
    switch (status?.toUpperCase()) {
      case 'VERIFIED':
        return {
          icon: '✓',
          label: 'Verified',
          colors: 'bg-green-500/20 text-green-400 border-green-500/50',
          dotColor: 'bg-green-400'
        };
      case 'LIKELY_TRUE':
        return {
          icon: '◐',
          label: 'Likely True',
          colors: 'bg-emerald-500/20 text-emerald-400 border-emerald-500/50',
          dotColor: 'bg-emerald-400'
        };
      case 'DISPUTED':
        return {
          icon: '⚠',
          label: 'Disputed',
          colors: 'bg-yellow-500/20 text-yellow-400 border-yellow-500/50',
          dotColor: 'bg-yellow-400'
        };
      case 'FALSE':
        return {
          icon: '✗',
          label: 'False',
          colors: 'bg-red-500/20 text-red-400 border-red-500/50',
          dotColor: 'bg-red-400'
        };
      case 'UNVERIFIABLE':
        return {
          icon: '?',
          label: 'Unverifiable',
          colors: 'bg-gray-500/20 text-gray-400 border-gray-500/50',
          dotColor: 'bg-gray-400'
        };
      default:
        return null;
    }
  };

  const config = getStatusConfig(status);
  if (!config) return null;

  const sizeClasses = {
    xs: 'text-[10px] px-1.5 py-0.5 gap-1',
    sm: 'text-xs px-2 py-1 gap-1.5',
    md: 'text-sm px-3 py-1.5 gap-2'
  };

  const Component = onClick ? 'button' : 'span';

  return (
    <Component
      onClick={onClick}
      className={`
        inline-flex items-center rounded-full border font-semibold
        ${config.colors}
        ${sizeClasses[size]}
        ${onClick ? 'cursor-pointer hover:opacity-80 transition-opacity' : ''}
      `}
      title={score ? `${config.label} (${Math.round(score * 100)}% confidence)` : config.label}
    >
      <span className={`w-1.5 h-1.5 rounded-full ${config.dotColor}`}></span>
      <span>{config.icon}</span>
      <span>{config.label}</span>
      {score && size !== 'xs' && (
        <span className="opacity-70">{Math.round(score * 100)}%</span>
      )}
    </Component>
  );
}

export default FactCheckBadge;
