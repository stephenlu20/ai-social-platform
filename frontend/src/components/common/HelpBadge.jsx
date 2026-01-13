

import React from 'react';
import './HelpBadge.css';

function HelpBadge({ number, tooltip, bgColor }) {
  return (
    <span 
      className="help-badge" 
      style={bgColor ? { background: bgColor } : {}}
    >
      {number}
      <div className="tooltip">{tooltip}</div>
    </span>
  );
}

export default HelpBadge;