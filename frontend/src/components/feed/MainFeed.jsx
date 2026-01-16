import React, { useState, useEffect } from 'react';
import { useUser } from '../../context/UserContext';
import postService from '../../services/postService';
import ComposeBox from './ComposeBox';
import Tweet from './Tweet';
import debateService from '../../services/debateService';
import DebateCard from '../debates/DebateCard';

function MainFeed() {
  const { currentUser } = useUser();
  const [activeTab, setActiveTab] = useState('following');
  const [posts, setPosts] = useState([]);
  const [debates, setDebates] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (currentUser) {
      loadPosts();
    }
  }, [currentUser, activeTab]);

  const loadPosts = async () => {
  try {
    setLoading(true);
    setError(null);
    
    let feed;
    if (activeTab === 'following') {
      feed = await postService.getFeed(currentUser.id);
      const sortedFeed = feed.sort((a, b) => 
        new Date(b.createdAt) - new Date(a.createdAt)
      );
      setPosts(sortedFeed);
    } else if (activeTab === 'yourPosts') {
      feed = await postService.searchPosts({
        authorId: currentUser.id,
        page: 0,
        size: 50
      });
      feed = feed.content || [];
      const sortedFeed = feed.sort((a, b) => 
        new Date(b.createdAt) - new Date(a.createdAt)
      );
      setPosts(sortedFeed);
    } else if (activeTab === 'debates') {
      const activeDebates = await debateService.getActiveDebates();
      const votingDebates = await debateService.getVotingDebates();
      const allDebates = [...activeDebates, ...votingDebates];
      const sortedDebates = allDebates.sort((a, b) => 
        new Date(b.createdAt) - new Date(a.createdAt)
      );
      setDebates(sortedDebates);
    }
  } catch (err) {
    console.error('Error loading posts:', err);
    setError('Failed to load content');
  } finally {
    setLoading(false);
  }
};

  const handlePostCreated = () => {
    loadPosts();
  };

  // Update local post state without reloading from server
  const handlePostUpdated = (updatedPost) => {
    if (updatedPost) {
      setPosts(prevPosts => 
        prevPosts.map(post => 
          post.id === updatedPost.id ? updatedPost : post
        )
      );
    }
  };

  // Update author follow state across all posts by that author
  const handleAuthorFollowChange = (authorId, isNowFollowing) => {
    setPosts(prevPosts =>
      prevPosts.map(post => {
        if (post.author.id === authorId) {
          return {
            ...post,
            author: {
              ...post.author,
              isFollowing: isNowFollowing
            }
          };
        }
        return post;
      })
    );
  };

  if (!currentUser) {
    return (
      <div className="bg-white/[0.03] rounded-3xl overflow-hidden backdrop-blur-[10px] border border-white/10 
                      flex items-center justify-center p-20">
        <div className="text-white/50">Loading user...</div>
      </div>
    );
  }

  return (
    <div className="bg-white/[0.03] rounded-3xl overflow-hidden backdrop-blur-[10px] border border-white/10">
      {/* Tabs */}
      <div className="flex sticky top-0 bg-[rgba(15,5,25,0.95)] backdrop-blur-[20px] z-10 
                      p-2 border-b border-white/10">
        <div 
          className={`flex-1 p-3.5 text-center font-bold cursor-pointer relative 
                     text-[15px] rounded-xl transition-all duration-300
                     ${activeTab === 'following' 
                       ? 'text-white bg-gradient-to-br from-veritas-pink/20 to-veritas-purple/20' 
                       : 'text-white/50 hover:text-white/80 hover:bg-white/5'}`}
          
          onClick={() => setActiveTab('following')}
        >
          Following
        </div>
        <div 
          className={`flex-1 p-3.5 text-center font-bold cursor-pointer relative 
                     text-[15px] rounded-xl transition-all duration-300
                     ${activeTab === 'yourPosts' 
                       ? 'text-white bg-gradient-to-br from-veritas-pink/20 to-veritas-purple/20' 
                       : 'text-white/50 hover:text-white/80 hover:bg-white/5'}`}
          onClick={() => setActiveTab('yourPosts')}
        >
          Your Posts
        </div>
        <div 
          className={`flex-1 p-3.5 text-center font-bold cursor-pointer relative 
                     text-[15px] rounded-xl transition-all duration-300
                     ${activeTab === 'debates' 
                       ? 'text-white bg-gradient-to-br from-veritas-pink/20 to-veritas-purple/20' 
                       : 'text-white/50 hover:text-white/80 hover:bg-white/5'}`}
          onClick={() => setActiveTab('debates')}
        >
          ⚔️ Debates
        </div>
      </div>

      <ComposeBox onPostCreated={handlePostCreated} />

      {loading && (
        <div className="p-20 text-center text-white/50">
          Loading posts...
        </div>
      )}

      {error && (
        <div className="p-20 text-center text-red-400">
          {error}
        </div>
      )}

      {!loading && !error && activeTab === 'debates' && debates.length === 0 && (
        <div className="p-20 text-center text-white/50">
          No active debates. Create a debate challenge to get started!
        </div>
      )}

      {!loading && !error && activeTab !== 'debates' && posts.length > 0 && (
        <div>
          {posts.map(post => (
            <Tweet 
              key={post.id} 
              post={post}
              currentUserId={currentUser.id}
              onPostUpdated={handlePostUpdated}
              onAuthorFollowChange={handleAuthorFollowChange}
            />
          ))}
        </div>
      )}

      {!loading && !error && activeTab === 'debates' && debates.length > 0 && (
        <div>
          {debates.map(debate => (
            <DebateCard 
              key={debate.id} 
              debate={debate}
              onDebateUpdated={loadPosts}
            />
          ))}
        </div>
      )}
  </div>
)}

export default MainFeed;