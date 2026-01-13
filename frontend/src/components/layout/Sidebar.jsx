

import React from 'react';
import HelpBadge from '../common/HelpBadge';
import './Sidebar.css';

function Sidebar() {
  const navItems = [
    { icon: '🏠', label: 'Home', badge: null },
  { icon: '🌍', label: 'Explore', badge: null },
  { icon: '💬', label: 'Messages', badge: null },
  { icon: '👥', label: 'Groups', badge: null },
  { icon: '⭐', label: 'Bookmarks', badge: null },
  { icon: '👤', label: 'Profile', badge: null },
  { icon: '🤝', label: 'Friends', badge: null },
  ];

  return (
    <div className="sidebar">
      <div className="logo">
        <div className="logo-icon">🎭</div>
        <div className="logo-text">Chirp</div>
      </div>

      <div className="section-label">📍 FEATURE #1 - USER PAGE</div>

      <nav className="nav">
        {navItems.map((item, index) => (
          <div key={index} className="nav-item">
            <div className="nav-icon">{item.icon}</div>
            <div>{item.label}</div>
            {item.badge && (
              <HelpBadge 
                number={item.badge.number} 
                tooltip={item.badge.tooltip} 
              />
            )}
          </div>
        ))}

        <div className="nav-item ai-nav-item">
          <div className="nav-icon">✨</div>
          <div style={{ fontWeight: 700 }}>AI Studio</div>
        </div>
      </nav>

      <div className="info-banner">
        <strong>💡 New User Tips:</strong>
        Hover over the numbered badges to learn what each feature does!
      </div>

      <button className="post-btn">
        <span className="feature-label">Feature #3</span>
        Create Post
      </button>

      <div className="user-profile">
        <div className="avatar">🎨</div>
        <div style={{ flex: 1 }}>
          <div style={{ fontWeight: 700, fontSize: '14px' }}>Your Name</div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginTop: '4px' }}>
            <span style={{ fontSize: '13px', color: 'rgba(255,255,255,0.5)' }}>@yourhandle</span>
            <div className="trust-score">
              <span>🛡️</span>
              <span>92</span>
              <HelpBadge 
                number="12" 
                tooltip="Feature #12: Trust Score - Build reputation!" 
                bgColor="rgba(255,255,255,0.2)"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Sidebar;