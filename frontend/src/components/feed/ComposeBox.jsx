

import React, { useState } from 'react';
import HelpBadge from '../common/HelpBadge';
import './ComposeBox.css';

function ComposeBox() {
  const [postText, setPostText] = useState('');
  const maxChars = 280;

  const getCharCountClass = () => {
    const remaining = maxChars - postText.length;
    if (remaining < 20) return 'danger';
    if (remaining < 50) return 'warning';
    return 'normal';
  };

  const handlePost = () => {
    if (postText.trim().length === 0) return;
    console.log('Posting:', postText);
    setPostText('');
  };

  return (
    <div className="compose-box">
      <div className="avatar">🎨</div>
      <div className="compose-area">
        <div className="section-label">🎯 CREATE A POST (Feature #3)</div>

        <div className="ai-tools-panel">
          <div className="ai-tools-header">
            <span style={{ fontSize: '20px' }}>✨</span>
            <span className="ai-tools-title">AI WRITING TOOLS</span>
          </div>
          <div className="ai-tools-buttons">
            <button className="ai-tool-btn ai-tool-assistant">
              <span>🤖</span>
              <span>Post Assistant</span>
            </button>
            <button className="ai-tool-btn ai-tool-factcheck">
              <span>✅</span>
              <span>Fact Check</span>
              <label className="auto-check-toggle">
                <input type="checkbox" />
                <span>Auto</span>
              </label>
            </button>
            <button className="ai-tool-btn ai-tool-crawler">
              <span>🔍</span>
              <span>Fact Crawler</span>
            </button>
          </div>
        </div>

        <textarea
          className="compose-input"
          placeholder="What's on your mind?"
          value={postText}
          onChange={(e) => setPostText(e.target.value)}
          maxLength={maxChars}
        />

        <div className="compose-actions">
          <div className="action-icons">
            <button className="action-icon" title="Add image">
              🖼️
              <HelpBadge number="18" tooltip="Feature #18: Add Multimedia" />
            </button>
            <button className="action-icon" title="Add video">
              🎬
              <HelpBadge number="18" tooltip="Feature #18: Add Video" />
            </button>
            <button className="action-icon" title="Create poll">
              📊
              <HelpBadge number="5" tooltip="Feature #5: Create Poll" />
            </button>
            <button className="action-icon" title="Add emoji">
              😊
              <HelpBadge number="7" tooltip="Feature #7: Emoji Support" />
            </button>
            <button className="action-icon" title="Add hashtag">
              #
              <HelpBadge number="6" tooltip="Feature #6: Add Hashtag" />
            </button>
            <button className="action-icon" title="Schedule post">
              ⏰
              <HelpBadge number="9" tooltip="Feature #9: Timed Messages" />
            </button>
          </div>
          <div className="compose-submit">
            <span className={`char-counter ${getCharCountClass()}`}>
              {postText.length}/{maxChars}
              <HelpBadge number="4" tooltip="Feature #4: Character Limit" bgColor="rgba(255,255,255,0.2)" />
            </span>
            <button 
              className="post-btn-small" 
              onClick={handlePost}
              disabled={postText.trim().length === 0}
            >
              Share
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ComposeBox;
