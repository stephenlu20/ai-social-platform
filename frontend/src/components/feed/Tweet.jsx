

import React from 'react';
import './Tweet.css';

function Tweet(props) {
  const {
    id,
    author,
    handle,
    avatar,
    time,
    verified,
    trustScore,
    content,
    type,
    poll,
    debate,
    factCheck,
    likes,
    retweets,
    replies
  } = props;

  return (
    <div className="tweet">
      <div className="tweet-avatar">{avatar}</div>
      <div className="tweet-content">
        <div className="tweet-header">
          <span className="tweet-author">{author}</span>
          {verified && <span className="verified">✓</span>}
          <span className="tweet-handle">{handle}</span>
          <span className="tweet-time">· {time}</span>
          {type === 'poll' && <span className="tweet-badge poll-badge">📊 POLL</span>}
          {type === 'debate' && <span className="tweet-badge debate-badge">⚔️ DEBATE</span>}
          <div className="trust-score-small">
            <span>🛡️</span>
            <span>{trustScore}</span>
          </div>
        </div>

        <div className="tweet-text">{content}</div>

        {type === 'poll' && poll && (
          <div className="poll">
            {poll.options.map((option, index) => (
              <div key={index} className="poll-option">
                <div className="poll-bar" style={{ width: `${option.percentage}%` }}></div>
                <div className="poll-text">
                  <span>{option.text}</span>
                  <span>{option.percentage}%</span>
                </div>
              </div>
            ))}
            <div className="poll-info">
              {poll.totalVotes} votes · {poll.timeLeft}
            </div>
          </div>
        )}

        {type === 'debate' && debate && (
          <div className="debate-section">
            <div className="debate-header">
              <span>⚔️</span>
              <span>JOIN THE DEBATE - Pick Your Side!</span>
            </div>
            <div className="debate-stance">
              <div className="stance-btn stance-for">
                <div>👍 I AGREE</div>
                <div style={{ fontSize: '12px', marginTop: '4px' }}>{debate.agree} people</div>
              </div>
              <div className="stance-btn stance-against">
                <div>👎 I DISAGREE</div>
                <div style={{ fontSize: '12px', marginTop: '4px' }}>{debate.disagree} people</div>
              </div>
            </div>
          </div>
        )}

        {type === 'fact-checked' && factCheck && factCheck.status === 'verified' && (
          <div className="fact-check-verified">
            <div className="fact-check-header">
              <span style={{ fontSize: '20px' }}>✅</span>
              <span>AI FACT CHECK: VERIFIED</span>
            </div>
            <div className="fact-check-details">
              ✓ Source verified: {factCheck.sources.join(', ')}<br/>
              ✓ Study confirmed: Published {factCheck.date}<br/>
              <button className="view-sources-btn">View Sources</button>
            </div>
          </div>
        )}

        <div className="tweet-actions">
          <button className="tweet-action">
            <span className="tweet-action-icon">💬</span>
            <span>{replies}</span>
          </button>
          <button className="tweet-action">
            <span className="tweet-action-icon">🔁</span>
            <span>{retweets}</span>
          </button>
          <button className="tweet-action">
            <span className="tweet-action-icon">❤️</span>
            <span>{likes}</span>
          </button>
          <button className="tweet-action">
            <span className="tweet-action-icon">🔖</span>
          </button>
          <button className="tweet-action">
            <span className="tweet-action-icon">🔗</span>
          </button>
        </div>
      </div>
    </div>
  );
}

export default Tweet;