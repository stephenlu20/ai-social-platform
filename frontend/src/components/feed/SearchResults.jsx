import React from 'react';
import Tweet from './Tweet';
import { TrustScoreBadge } from '../trustscore';
import logo from '../../assets/CondorTransparent.png';

function SearchResults({ 
  searchType, 
  userResults, 
  postResults, 
  loading, 
  currentUserId,
  onPostUpdated,
  onAuthorFollowChange,
  onUserClick,
  onLoadMoreUsers,
  onLoadMorePosts,
  hasMoreUsers,
  hasMorePosts
}) {
  
  if (loading) {
    return (
      <div className="p-20 text-center text-white/50" style={{ minWidth: '600px' }}>
        <div className="animate-pulse">Searching...</div>
      </div>
    );
  }

  const showUsers = searchType === 'all' || searchType === 'users';
  const showPosts = searchType === 'all' || searchType === 'posts';

  const hasUserResults = userResults && userResults.length > 0;
  const hasPostResults = postResults && postResults.length > 0;
  const hasAnyResults = hasUserResults || hasPostResults;

  if (!hasAnyResults) {
    return (
      <div className="p-20 text-center text-white/50" style={{ minWidth: '600px' }}>
        <div className="text-lg">No results found</div>
        <div className="text-sm text-white/30 mt-2">Try a different search term</div>
      </div>
    );
  }

  return (
    <div style={{ minWidth: '600px' }}>
      {/* User Results Section */}
      {showUsers && hasUserResults && (
        <div>
          <div className="px-5 py-3 border-b border-white/10 bg-white/5">
            <h3 className="text-white font-bold text-sm uppercase tracking-wider">
              Users ({userResults.length})
            </h3>
          </div>
          <div>
            {userResults.map(user => (
              <div
                key={user.id}
                className="border-b border-white/[0.08] p-5 hover:bg-veritas-pink/5 
                           transition-all duration-300 cursor-pointer"
                onClick={() => onUserClick && onUserClick(user)}
              >
                <div className="flex items-start gap-4">
                  <div className="w-12 h-12 rounded-xl bg-veritas-blue-dark 
                                  flex items-center justify-center text-xl">
                    <img 
                      src={logo} 
                      alt="Candor Logo" 
                      className="w-10 h-10 object-contain"
                    />
                    </div>
                  <div className="flex-1">
                    <div className="flex items-center gap-2 mb-1">
                      <span className="font-bold text-white text-[15px]">
                        {user.displayName}
                      </span>
                      <span className="text-white/50 text-sm">@{user.username}</span>
                      {user.trustScore != null && (
                        <TrustScoreBadge
                          score={user.trustScore}
                          size="xs"
                          showTooltip={true}
                          userId={user.id}
                        />
                      )}
                    </div>
                    {user.bio && (
                      <p className="text-white/70 text-sm">{user.bio}</p>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
          {hasMoreUsers && (
            <button
              onClick={onLoadMoreUsers}
              className="w-full py-3 text-center text-veritas-pink hover:bg-veritas-pink/10 
                         transition-colors border-b border-white/10 text-sm font-semibold"
            >
              Load more users
            </button>
          )}
        </div>
      )}

      {/* Post Results Section */}
      {showPosts && hasPostResults && (
        <div>
          {showUsers && hasUserResults && (
            <div className="px-5 py-3 border-b border-white/10 bg-white/5 mt-6">
              <h3 className="text-white font-bold text-sm uppercase tracking-wider">
                Posts ({postResults.length})
              </h3>
            </div>
          )}
          <div>
            {postResults.map(post => (
              <Tweet
                key={post.id}
                post={post}
                currentUserId={currentUserId}
                onPostUpdated={onPostUpdated}
                onAuthorFollowChange={onAuthorFollowChange}
              />
            ))}
          </div>
          {hasMorePosts && (
            <button
              onClick={onLoadMorePosts}
              className="w-full py-3 text-center text-veritas-pink hover:bg-veritas-pink/10 
                         transition-colors border-b border-white/10 text-sm font-semibold"
            >
              Load more posts
            </button>
          )}
        </div>
      )}
    </div>
  );
}

export default SearchResults;