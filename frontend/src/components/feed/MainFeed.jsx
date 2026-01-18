import React, { useState, useEffect } from 'react';
import { useUser } from '../../context/UserContext';
import postService from '../../services/postService';
import userService from '../../services/userService';
import Tweet from './Tweet';
import SearchResults from './SearchResults';
import debateService from '../../services/debateService';
import DebateCard from '../debates/DebateCard';
import { Search } from 'lucide-react';

function MainFeed() {
  const { currentUser } = useUser();
  const [activeTab, setActiveTab] = useState('following');
  const [posts, setPosts] = useState([]);
  const [debates, setDebates] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

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

  useEffect(() => {
    if (currentUser) {
      loadPosts();
    }
  }, [currentUser, activeTab]);

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
  }, [searchQuery, searchType]);

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
        const activeDebates = await debateService.getActiveDebates();
        const allDebates = [...activeDebates];
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
            <div className="flex gap-2 mt-3">
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
                  onDebateUpdated={loadPosts}
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