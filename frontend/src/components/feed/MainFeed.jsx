
import React, { useState } from 'react';
import ComposeBox from './ComposeBox';
import Tweet from './Tweet';
import './MainFeed.css';

function MainFeed() {
  const [activeTab, setActiveTab] = useState('forYou');

  const tweets = [
    {
      id: 1,
      author: 'Emily Parker',
      handle: '@emilyparker',
      avatar: '👩‍💻',
      time: '1h',
      verified: true,
      trustScore: 87,
      content: 'Which programming language should I learn next? #WebDev #Coding',
      type: 'poll',
      poll: {
        options: [
          { text: 'Solidity', percentage: 45, votes: 562 },
          { text: 'Rust', percentage: 30, votes: 375 },
          { text: 'Go', percentage: 15, votes: 187 },
          { text: 'Python', percentage: 10, votes: 125 }
        ],
        totalVotes: 1247,
        timeLeft: '2 days left'
      },
      likes: 567,
      retweets: 234,
      replies: 89
    },
    {
      id: 2,
      author: 'Marcus Rivera',
      handle: '@marcusrivera',
      avatar: '🧑‍⚖️',
      time: '3h',
      verified: false,
      trustScore: 94,
      content: 'Remote work is more productive than office work. Change my mind.',
      type: 'debate',
      debate: {
        agree: 724,
        disagree: 456
      },
      likes: 156,
      retweets: 567,
      replies: 312
    },
    {
      id: 3,
      author: 'NewsDaily',
      handle: '@newsdaily',
      avatar: '📰',
      time: '2h',
      verified: true,
      trustScore: 98,
      content: 'Breaking: Scientists discover new renewable energy source that could power entire cities. Study published in Nature Journal. #Science #Energy',
      type: 'fact-checked',
      factCheck: {
        status: 'verified',
        sources: ['Nature Journal', 'MIT Research Team'],
        date: 'Jan 2026'
      },
      likes: 1500,
      retweets: 1200,
      replies: 234
    }
  ];

  return (
    <div className="main-feed">
      <div className="feed-header">
        <div 
          className={`tab ${activeTab === 'forYou' ? 'active' : ''}`}
          onClick={() => setActiveTab('forYou')}
        >
          For You
        </div>
        <div 
          className={`tab ${activeTab === 'following' ? 'active' : ''}`}
          onClick={() => setActiveTab('following')}
        >
          Following
        </div>
        <div 
          className={`tab ${activeTab === 'trending' ? 'active' : ''}`}
          onClick={() => setActiveTab('trending')}
        >
          Trending
        </div>
      </div>

      <ComposeBox />

      <div className="feed-info-banner">
        <div className="feed-info-title">📚 EXPLORE FEATURES BELOW:</div>
        <div className="feed-info-text">
          Scroll down to see examples of <strong>Polls</strong> (Feature #5), <strong>Debates</strong> (Feature #11), 
          <strong>Reactions</strong> (Feature #10), <strong>Edit</strong> (Feature #8), and more!
        </div>
      </div>

      <div className="tweet-list">
        {tweets.map(tweet => (
          <Tweet key={tweet.id} {...tweet} />
        ))}
      </div>
    </div>
  );
}

export default MainFeed;