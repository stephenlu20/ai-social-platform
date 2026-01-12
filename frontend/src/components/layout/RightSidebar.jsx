

import React from 'react';
import './RightSidebar.css';

function RightSidebar() {
  return (
    <div className="right-sidebar">
      <div className="search-box">
        <div style={{ color: 'rgba(255,255,255,0.5)', fontSize: '20px' }}>🔍</div>
        <input type="text" placeholder="Search posts, people, hashtags..." />
      </div>
      
      <div className="widget">
        <div className="widget-header">
          <span>🤖</span>
          <span>AI Tools</span>
        </div>
        <div className="widget-content">
          <p style={{ color: 'rgba(255,255,255,0.7)', fontSize: '14px' }}>
            AI widgets coming soon...
          </p>
        </div>
      </div>

      <div className="widget">
        <div className="widget-header">
          <span>🎯</span>
          <span>Filters & Sort</span>
        </div>
        <div className="widget-content">
          <p style={{ color: 'rgba(255,255,255,0.7)', fontSize: '14px' }}>
            Filter options coming soon...
          </p>
        </div>
      </div>
    </div>
  );
}

export default RightSidebar;
