import React, { useState, useEffect } from 'react';
import { useUser } from '../../context/UserContext';
import postService from '../../services/postService';
import userService from '../../services/userService';
import Tweet from './Tweet';
import SearchResults from './SearchResults';
import debateService from '../../services/debateService';
import DebateCard from '../debates/DebateCard';
import { Search } from 'lucide-react';

function MainFeed({ debateFilterRequest, onDebateUpdated }) {
  const { currentUser } = useUser();
  const [activeTab, setActiveTab] = useState('following');
  const [debateScope, setDebateScope] = useState('all'); // 'all' or 'mine'
  const [debateFilter, setDebateFilter] = useState('all'); // 'all', 'pending', 'active', 'voting', 'completed'
  const [posts, setPosts] = useState([]);
  const [debates, setDebates] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [pendingCount, setPendingCount] = useState(0);

  // Search state
  const [searchQuery, setSearchQuery] = useState('');
  const [searchActive, setSearchActive] = useState(false);
  const [searchType, setSearchType] = useState('all');
  const [searchLoading, setSearchLoading] = useState(false);
  const [userResults, setUserResults] = useState([]);
  const [postResults, setPostResults] = useState([]);
  const [userPage, setUserPage] = useState(0);
  const [postPage, setPostPage] = useState(0);
  const [hasMoreUsers, setHasMoreUsers] = useState(false);
  const [hasMorePosts, setHasMorePosts] = useState(false);
  
  // Trust score filters
  const [minTrustScore, setMinTrustScore] = useState('');
  const [maxTrustScore, setMaxTrustScore] = useState('');
  const [showFilters, setShowFilters] = useState(false);

  useEffect(() => {
    if (currentUser) {
      loadPosts();
      loadPendingCount();
    }
  }, [currentUser, activeTab, debateScope, debateFilter]);

  const loadPendingCount = async () => {
    try {
      const pending = await debateService.getPendingChallenges(currentUser.id);
      setPendingCount(pending.length);
    } catch (err) {
      console.error('Error loading pending count:', err);
    }
  };

  const handleDebateUpdated = () => {
    loadPosts();
    loadPendingCount();
    if (onDebateUpdated) {
      onDebateUpdated();
    }
  };

  // Handle navigation request from Sidebar (e.g., clicking "Challenges")
  useEffect(() => {
    if (debateFilterRequest) {
      setActiveTab('debates');
      setDebateScope('mine');
      setDebateFilter('pending');
    }
  }, [debateFilterRequest]);

  useEffect(() => {
    if (!searchQuery.trim()) {
      setSearchActive(false);
      setUserResults([]);
      setPostResults([]);
      return;
    }

    const timer = setTimeout(() => {
      performSearch();
    }, 300);

    return () => clearTimeout(timer);
  }, [searchQuery, searchType, minTrustScore, maxTrustScore]);

  const performSearch = async () => {
    if (!searchQuery.trim() || !currentUser) return;

    setSearchActive(true);
    setSearchLoading(true);
    setUserPage(0);
    setPostPage(0);

    try {
      const searchPromises = [];

      if (searchType === 'all' || searchType === 'users') {
        searchPromises.push(
          userService.searchUsers({
            query: searchQuery,
            minTrustScore: minTrustScore ? parseFloat(minTrustScore) : null,
            maxTrustScore: maxTrustScore ? parseFloat(maxTrustScore) : null,
            page: 0,
            size: 20
          })
        );
      } else {
        searchPromises.push(Promise.resolve({ content: [], last: true }));
      }

      if (searchType === 'all' || searchType === 'posts') {
        searchPromises.push(
          postService.searchPosts({
            query: searchQuery,
            viewerId: currentUser.id,
            page: 0,
            size: 20
          })
        );
      } else {
        searchPromises.push(Promise.resolve({ content: [], last: true }));
      }

      const [usersResponse, postsResponse] = await Promise.all(searchPromises);

      setUserResults(usersResponse.content || []);
      setPostResults(postsResponse.content || []);
      setHasMoreUsers(!usersResponse.last);
      setHasMorePosts(!postsResponse.last);
    } catch (err) {
      console.error('Search error:', err);
    } finally {
      setSearchLoading(false);
    }
  };

  const loadMoreUsers = async () => {
    if (!searchQuery.trim() || !currentUser) return;

    const nextPage = userPage + 1;
    try {
      const response = await userService.searchUsers({
        query: searchQuery,
        minTrustScore: minTrustScore ? parseFloat(minTrustScore) : null,
        maxTrustScore: maxTrustScore ? parseFloat(maxTrustScore) : null,
        page: nextPage,
        size: 20
      });

      setUserResults(prev => [...prev, ...(response.content || [])]);
      setUserPage(nextPage);
      setHasMoreUsers(!response.last);
    } catch (err) {
      console.error('Load more users error:', err);
    }
  };

  const loadMorePosts = async () => {
    if (!searchQuery.trim() || !currentUser) return;

    const nextPage = postPage + 1;
    try {
      const response = await postService.searchPosts({
        query: searchQuery,
        viewerId: currentUser.id,
        page: nextPage,
        size: 20
      });

      setPostResults(prev => [...prev, ...(response.content || [])]);
      setPostPage(nextPage);
      setHasMorePosts(!response.last);
    } catch (err) {
      console.error('Load more posts error:', err);
    }
  };

  const handleUserClick = (user) => {
    window.location.href = `/profile/${user.username}`;
  };

  const clearSearch = () => {
    setSearchQuery('');
    setSearchActive(false);
    setUserResults([]);
    setPostResults([]);
    setMinTrustScore('');
    setMaxTrustScore('');
    setShowFilters(false);
  };

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
          viewerId: currentUser.id,
          page: 0,
          size: 50
        });
        feed = feed.content || [];
        const sortedFeed = feed.sort((a, b) => 
          new Date(b.createdAt) - new Date(a.createdAt)
        );
        setPosts(sortedFeed);
      } else if (activeTab === 'debates') {
        let debatesToShow = [];
        
        if (debateScope === 'mine') {
          // My Debates - filter by status
          if (debateFilter === 'pending') {
            debatesToShow = await debateService.getPendingChallenges(currentUser.id);
          } else {
            // Get all user's debates, then filter
            const userDebates = await debateService.getDebatesByUser(currentUser.id);
            if (debateFilter === 'all') {
              debatesToShow = userDebates;
            } else if (debateFilter === 'active') {
              debatesToShow = userDebates.filter(d => d.status === 'ACTIVE');
            } else if (debateFilter === 'voting') {
              debatesToShow = userDebates.filter(d => d.status === 'VOTING');
            } else if (debateFilter === 'completed') {
              debatesToShow = userDebates.filter(d => d.status === 'COMPLETED');
            }
          }
        } else {
          // All Debates - filter by status
          if (debateFilter === 'active') {
            debatesToShow = await debateService.getActiveDebates();
          } else if (debateFilter === 'voting') {
            debatesToShow = await debateService.getVotingDebates();
          } else if (debateFilter === 'completed') {
            // For "all debates + completed", we'd need a new backend endpoint
            // For now, fall back to all active + voting
            const activeDebates = await debateService.getActiveDebates();
            const votingDebates = await debateService.getVotingDebates();
            debatesToShow = [...activeDebates, ...votingDebates];
          } else {
            // 'all' - Load all active and voting debates
            const activeDebates = await debateService.getActiveDebates();
            const votingDebates = await debateService.getVotingDebates();
            debatesToShow = [...activeDebates, ...votingDebates];
          }
        }
        
        const sortedDebates = debatesToShow.sort((a, b) =>
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

  const handlePostUpdated = (updatedPost) => {
    if (updatedPost) {
      setPosts(prevPosts => 
        prevPosts.map(post => 
          post.id === updatedPost.id ? updatedPost : post
        )
      );
    }
  };

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

  const handlePostDeleted = (postId) => {
    setPosts(prevPosts =>
      prevPosts.filter(post => post.id !== postId)
    );
  };

  // Reset debateFilter when switching scope
  const handleScopeChange = (newScope) => {
    setDebateScope(newScope);
    // Reset to 'all' when switching scope
    setDebateFilter('all');
  };

  if (!currentUser) {
    return (
      <div className="bg-white/[0.03] rounded-3xl overflow-hidden backdrop-blur-[10px] border border-white/10 
                      flex items-center justify-center p-20" style={{ minWidth: '550px' }}>
        <div className="text-white/50">Loading user...</div>
      </div>
    );
  }

  return (
    <div className="bg-white/[0.03] rounded-3xl overflow-hidden backdrop-blur-[10px] border border-white/10" style={{ minWidth: '700px' }}>
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
          className={`flex-1 p-3.5 text-center font-bold cursor-pointer
                     text-[15px] rounded-xl transition-all duration-300
                     flex items-center justify-center gap-2
                     ${activeTab === 'debates'
                       ? 'text-white bg-gradient-to-br from-veritas-pink/20 to-veritas-purple/20'
                       : 'text-white/50 hover:text-white/80 hover:bg-white/5'}`}
          onClick={() => setActiveTab('debates')}
        >
          Debates
          {pendingCount > 0 && (
            <span className="bg-veritas-pink text-white text-xs font-bold
                           px-1.5 py-0.5 rounded-full min-w-[20px]">
              {pendingCount}
            </span>
          )}
        </div>
      </div>

      {/* Search Bar */}
      <div className="sticky top-[60px] z-10 bg-[rgba(15,5,25,0.95)] backdrop-blur-[20px] border-b border-white/10">
        <div className="p-4">
          <div className="relative">
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search users and posts..."
              className="w-full bg-white/5 border border-white/20 rounded-xl px-4 py-3 pl-12
                         text-white placeholder:text-white/30
                         focus:outline-none focus:border-veritas-pink focus:bg-white/10
                         transition-all"
            />
            <span className="absolute left-4 top-1/2 -translate-y-1/2 text-white/50 text-xl">
              <Search/>
            </span>
            {searchQuery && (
              <button
                onClick={clearSearch}
                className="absolute right-4 top-1/2 -translate-y-1/2 text-white/50 
                           hover:text-white transition-colors"
              >
                ✕
              </button>
            )}
          </div>

          {searchActive && (
            <div className="mt-3 space-y-3">
              {/* Search Type Filters */}
              <div className="flex gap-2">
                <button
                  onClick={() => setSearchType('all')}
                  className={`px-4 py-2 rounded-lg text-xs font-semibold transition-all
                             ${searchType === 'all'
                               ? 'bg-veritas-pink text-white'
                               : 'bg-white/5 text-white/50 hover:text-white hover:bg-white/10'}`}
                >
                  All
                </button>
                <button
                  onClick={() => setSearchType('users')}
                  className={`px-4 py-2 rounded-lg text-xs font-semibold transition-all
                             ${searchType === 'users'
                               ? 'bg-veritas-pink text-white'
                               : 'bg-white/5 text-white/50 hover:text-white hover:bg-white/10'}`}
                >
                  Users
                </button>
                <button
                  onClick={() => setSearchType('posts')}
                  className={`px-4 py-2 rounded-lg text-xs font-semibold transition-all
                             ${searchType === 'posts'
                               ? 'bg-veritas-pink text-white'
                               : 'bg-white/5 text-white/50 hover:text-white hover:bg-white/10'}`}
                >
                  Posts
                </button>

                {/* Filters Toggle - Only show when searching users */}
                {(searchType === 'all' || searchType === 'users') && (
                  <button
                    onClick={() => setShowFilters(!showFilters)}
                    className={`ml-auto px-4 py-2 rounded-lg text-xs font-semibold transition-all flex items-center gap-2
                               ${showFilters || minTrustScore || maxTrustScore
                                 ? 'bg-veritas-purple/40 text-white border border-veritas-purple/50'
                                 : 'bg-white/5 text-white/50 hover:text-white hover:bg-white/10'}`}
                  >
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
                    </svg>
                    Filters
                    {(minTrustScore || maxTrustScore) && (
                      <span className="w-2 h-2 bg-veritas-pink rounded-full"></span>
                    )}
                  </button>
                )}
              </div>

              {/* Trust Score Filter Panel */}
              {showFilters && (searchType === 'all' || searchType === 'users') && (
                <div className="p-4 bg-white/5 border border-white/20 rounded-xl">
                  <div className="flex items-center justify-between mb-3">
                    <span className="text-xs font-bold text-white/70 uppercase tracking-wider">
                      Trust Score Range
                    </span>
                    {(minTrustScore || maxTrustScore) && (
                      <button
                        onClick={() => {
                          setMinTrustScore('');
                          setMaxTrustScore('');
                        }}
                        className="text-xs text-veritas-pink hover:text-veritas-coral transition-colors"
                      >
                        Clear
                      </button>
                    )}
                  </div>
                  
                  <div className="grid grid-cols-2 gap-3">
                    <div>
                      <label className="block text-xs text-white/50 mb-1">Min Score</label>
                      <input
                        type="number"
                        min="0"
                        max="100"
                        value={minTrustScore}
                        onChange={(e) => setMinTrustScore(e.target.value)}
                        placeholder="0"
                        className="w-full bg-white/10 border border-white/20 rounded-lg px-3 py-2
                                   text-white text-sm placeholder:text-white/30
                                   focus:outline-none focus:border-veritas-pink focus:bg-white/15
                                   transition-all"
                      />
                    </div>
                    <div>
                      <label className="block text-xs text-white/50 mb-1">Max Score</label>
                      <input
                        type="number"
                        min="0"
                        max="100"
                        value={maxTrustScore}
                        onChange={(e) => setMaxTrustScore(e.target.value)}
                        placeholder="100"
                        className="w-full bg-white/10 border border-white/20 rounded-lg px-3 py-2
                                   text-white text-sm placeholder:text-white/30
                                   focus:outline-none focus:border-veritas-pink focus:bg-white/15
                                   transition-all"
                      />
                    </div>
                  </div>
                  
                  {/* Visual trust score scale reference */}
                  <div className="mt-3 pt-3 border-t border-white/10">
                    <div className="text-[10px] text-white/40 mb-1">Reference:</div>
                    <div className="flex items-center justify-between text-[10px] text-white/50">
                      <span>0 (Unreliable)</span>
                      <span>50 (Neutral)</span>
                      <span>100 (Trusted)</span>
                    </div>
                    <div className="h-1.5 bg-gradient-to-r from-red-500 via-blue-500 to-emerald-500 rounded-full mt-1"></div>
                  </div>
                </div>
              )}
            </div>
          )}
        </div>
      </div>

      {/* Content Area */}
      {searchActive ? (
        <SearchResults
          searchType={searchType}
          userResults={userResults}
          postResults={postResults}
          loading={searchLoading}
          currentUserId={currentUser.id}
          onPostUpdated={handlePostUpdated}
          onAuthorFollowChange={handleAuthorFollowChange}
          onUserClick={handleUserClick}
          onLoadMoreUsers={loadMoreUsers}
          onLoadMorePosts={loadMorePosts}
          hasMoreUsers={hasMoreUsers}
          hasMorePosts={hasMorePosts}
        />
      ) : (
        <>
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

          {/* Two-Tier Debate Filters */}
          {activeTab === 'debates' && (
            <div className="border-b border-white/10">
              {/* Tier 1: Scope (All vs My) */}
              <div className="p-4 pb-2 flex gap-3">
                <button
                  onClick={() => handleScopeChange('all')}
                  className={`flex-1 px-5 py-3 rounded-xl text-sm font-bold transition-all
                             ${debateScope === 'all'
                               ? 'bg-gradient-to-br from-veritas-pink/30 to-veritas-purple/30 text-white border-2 border-veritas-pink/50'
                               : 'bg-white/5 text-white/50 hover:text-white hover:bg-white/10 border-2 border-white/10'}`}
                >
                  All Debates
                </button>
                <button
                  onClick={() => handleScopeChange('mine')}
                  className={`flex-1 px-5 py-3 rounded-xl text-sm font-bold transition-all
                             flex items-center justify-center gap-2
                             ${debateScope === 'mine'
                               ? 'bg-gradient-to-br from-veritas-pink/30 to-veritas-purple/30 text-white border-2 border-veritas-pink/50'
                               : 'bg-white/5 text-white/50 hover:text-white hover:bg-white/10 border-2 border-white/10'}`}
                >
                  My Debates
                  {pendingCount > 0 && debateScope === 'mine' && (
                    <span className="bg-veritas-pink text-white text-xs font-bold px-2 py-0.5 rounded-full">
                      {pendingCount}
                    </span>
                  )}
                </button>
              </div>

              {/* Tier 2: Status Filters */}
              <div className="px-4 pb-4 flex gap-2 flex-wrap">
                {/* Show "Pending" only for "My Debates" */}
                {debateScope === 'mine' && (
                  <button
                    onClick={() => setDebateFilter('pending')}
                    className={`px-4 py-2 rounded-lg text-sm font-semibold transition-all
                               ${debateFilter === 'pending'
                                 ? 'bg-veritas-pink text-white'
                                 : 'bg-white/5 text-white/50 hover:text-white hover:bg-white/10'}`}
                  >
                    Pending
                  </button>
                )}
                
                <button
                  onClick={() => setDebateFilter('all')}
                  className={`px-4 py-2 rounded-lg text-sm font-semibold transition-all
                             ${debateFilter === 'all'
                               ? 'bg-veritas-pink text-white'
                               : 'bg-white/5 text-white/50 hover:text-white hover:bg-white/10'}`}
                >
                  All
                </button>
                <button
                  onClick={() => setDebateFilter('active')}
                  className={`px-4 py-2 rounded-lg text-sm font-semibold transition-all
                             ${debateFilter === 'active'
                               ? 'bg-veritas-pink text-white'
                               : 'bg-white/5 text-white/50 hover:text-white hover:bg-white/10'}`}
                >
                  Active
                </button>
                <button
                  onClick={() => setDebateFilter('voting')}
                  className={`px-4 py-2 rounded-lg text-sm font-semibold transition-all
                             ${debateFilter === 'voting'
                               ? 'bg-veritas-pink text-white'
                               : 'bg-white/5 text-white/50 hover:text-white hover:bg-white/10'}`}
                >
                  Voting
                </button>
                <button
                  onClick={() => setDebateFilter('completed')}
                  className={`px-4 py-2 rounded-lg text-sm font-semibold transition-all
                             ${debateFilter === 'completed'
                               ? 'bg-veritas-pink text-white'
                               : 'bg-white/5 text-white/50 hover:text-white hover:bg-white/10'}`}
                >
                  Completed
                </button>
              </div>
            </div>
          )}

          {!loading && !error && activeTab === 'debates' && debates.length === 0 && (
            <div className="p-20 text-center text-white/50">
              {debateScope === 'mine' && debateFilter === 'pending' && 'No pending challenges. You have no debate invitations to respond to.'}
              {debateScope === 'mine' && debateFilter === 'active' && 'No active debates in progress.'}
              {debateScope === 'mine' && debateFilter === 'voting' && 'No debates currently in voting phase.'}
              {debateScope === 'mine' && debateFilter === 'completed' && 'No completed debates yet.'}
              {debateScope === 'mine' && debateFilter === 'all' && 'You have no debates yet. Create or accept a debate challenge to get started!'}
              {debateScope === 'all' && debateFilter === 'active' && 'No active debates found.'}
              {debateScope === 'all' && debateFilter === 'voting' && 'No debates in voting phase.'}
              {debateScope === 'all' && debateFilter === 'completed' && 'No completed debates found.'}
              {debateScope === 'all' && debateFilter === 'all' && 'No debates found. Create a debate challenge to get started!'}
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
                  canDelete={activeTab === 'yourPosts'}
                  onPostDeleted={handlePostDeleted}
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
                  onDebateUpdated={handleDebateUpdated}
                />
              ))}
            </div>
          )}
        </>
      )}
    </div>
  );
}

export default MainFeed;