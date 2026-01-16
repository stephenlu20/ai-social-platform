import React, { useState, useEffect } from 'react';
import postService from '../../services/postService';
import followService from '../../services/followService';

function Tweet({ post, currentUserId, onPostUpdated, onAuthorFollowChange, depth = 0 }) {
  const [isLiked, setIsLiked] = useState(post.isLikedByCurrentUser || false);
  const [isLiking, setIsLiking] = useState(false);
  const [localLikeCount, setLocalLikeCount] = useState(post.likeCount || 0);
  const [isFollowing, setIsFollowing] = useState(post.author?.isFollowing || false);
  const [isFollowLoading, setIsFollowLoading] = useState(false);
  const [isFollowHovering, setIsFollowHovering] = useState(false);
  const [showReplyModal, setShowReplyModal] = useState(false);
  
  // Reply state
  const [replies, setReplies] = useState([]);
  const [showReplies, setShowReplies] = useState(false);
  const [loadingReplies, setLoadingReplies] = useState(false);
  const [repliesLoaded, setRepliesLoaded] = useState(false);

  const author = post.author;
  const content = post.content;
  const createdAt = new Date(post.createdAt);
  const timeAgo = getTimeAgo(createdAt);
  const replyCount = post.replyCount || 0;

  function getTimeAgo(date) {
    const seconds = Math.floor((new Date() - date) / 1000);
    const intervals = {
      year: 31536000,
      month: 2592000,
      week: 604800,
      day: 86400,
      hour: 3600,
      minute: 60
    };

    for (const [unit, secondsInUnit] of Object.entries(intervals)) {
      const interval = Math.floor(seconds / secondsInUnit);
      if (interval >= 1) {
        return interval === 1 ? `1 ${unit} ago` : `${interval} ${unit}s ago`;
      }
    }
    return 'just now';
  }

  const handleLike = async () => {
    if (isLiking) return;
    
    try {
      setIsLiking(true);
      const response = await postService.likePost(currentUserId, post.id);
      
      setIsLiked(response.liked);
      setLocalLikeCount(response.likeCount);
      
      if (onPostUpdated) {
        onPostUpdated();
      }
    } catch (error) {
      console.error('Error toggling like:', error);
    } finally {
      setIsLiking(false);
    }
  };

  const handleRepost = async () => {
    try {
      await postService.repost(currentUserId, post.id);
      if (onPostUpdated) {
        onPostUpdated();
      }
    } catch (error) {
      console.error('Error reposting:', error);
    }
  };

  const handleFollowToggle = async (e) => {
    e.preventDefault();
    e.stopPropagation();
    
    if (isFollowLoading || !currentUserId || !author) return;
    
    if (currentUserId === author.id) return;

    try {
      setIsFollowLoading(true);
      
      const response = await followService.toggleFollow(currentUserId, author.id);
      setIsFollowing(response.following);

      if (onAuthorFollowChange) {
        onAuthorFollowChange(author.id, response.following);
      }
    } catch (error) {
      console.error('Error toggling follow:', error);
    } finally {
      setIsFollowLoading(false);
    }
  };

  const loadReplies = async () => {
    if (repliesLoaded) {
      // Just toggle visibility if already loaded
      setShowReplies(!showReplies);
      return;
    }

    try {
      setLoadingReplies(true);
      const repliesData = await postService.getReplies(post.id);
      setReplies(repliesData);
      setRepliesLoaded(true);
      setShowReplies(true);
    } catch (error) {
      console.error('Error loading replies:', error);
    } finally {
      setLoadingReplies(false);
    }
  };

  const handleToggleReplies = () => {
    if (replyCount === 0) return;
    
    if (repliesLoaded) {
      setShowReplies(!showReplies);
    } else {
      loadReplies();
    }
  };

  const handleOpenReplyModal = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setShowReplyModal(true);
  };

  const handleCloseReplyModal = () => {
    setShowReplyModal(false);
  };

  const isOwnPost = currentUserId === author?.id;

  // Indent for nested replies (max depth to prevent too much nesting)
  const maxDepth = 3;
  const effectiveDepth = Math.min(depth, maxDepth);
  const leftPadding = effectiveDepth * 40; // 40px per level

  return (
    <div>
      <div 
        className="border-b border-white/[0.08] p-5 flex gap-3.5 
                    transition-all duration-300 hover:bg-veritas-pink/5"
        style={{ paddingLeft: `${20 + leftPadding}px` }}
      >
        <div className="text-4xl flex-shrink-0 relative">🎨</div>
        <div className="flex-1">
          <div className="flex items-center gap-2 mb-2 flex-wrap">
            <span className="font-bold text-[15px]">{author.displayName}</span>
            <span className="text-white/50 text-sm">@{author.username}</span>
            <span className="text-white/50 text-sm">·</span>
            <span className="text-white/50 text-sm">{timeAgo}</span>
            
            {/* Small Follow Button on Post */}
            {!isOwnPost && currentUserId && (
              <button
                onClick={handleFollowToggle}
                onMouseEnter={() => setIsFollowHovering(true)}
                onMouseLeave={() => setIsFollowHovering(false)}
                disabled={isFollowLoading}
                className={`
                  ml-auto px-4 py-1.5 rounded-full font-bold text-xs transition-all duration-300
                  disabled:opacity-50 disabled:cursor-not-allowed
                  ${isFollowing 
                    ? 'bg-white/10 border border-white/20 text-white hover:bg-red-500/20 hover:border-red-500 hover:text-red-400' 
                    : 'bg-gradient-to-br from-veritas-pink to-veritas-pink-dark text-white border border-transparent hover:shadow-[0_4px_12px_rgba(255,107,157,0.3)]'
                  }
                `}
              >
                {isFollowLoading 
                  ? '...' 
                  : isFollowing && isFollowHovering 
                    ? 'Unfollow' 
                    : isFollowing 
                      ? 'Following' 
                      : 'Follow'}
              </button>
            )}
          </div>
          
          <div className="text-white/90 leading-relaxed mb-3 text-[15px]">{content}</div>
          
          <div className="flex gap-6 text-white/50">
            <button 
              onClick={handleToggleReplies}
              className={`flex items-center gap-2 cursor-pointer transition-all duration-300 
                         p-1.5 rounded-[10px] relative bg-transparent border-none 
                         text-inherit text-[13px] font-semibold
                         ${replyCount > 0 ? 'hover:text-blue-400 hover:bg-blue-400/10' : 'opacity-50 cursor-default'}`}
            >
              <span className="text-lg">💬</span>
              <span>{replyCount}</span>
              {replyCount > 0 && (
                <span className={`text-xs transition-transform duration-200 ${showReplies ? 'rotate-180' : ''}`}>
                  ▼
                </span>
              )}
            </button>
            <button 
              onClick={handleRepost}
              className="flex items-center gap-2 cursor-pointer transition-all duration-300 
                         p-1.5 rounded-[10px] relative bg-transparent border-none 
                         text-inherit text-[13px] font-semibold
                         hover:text-green-400 hover:bg-green-400/10">
              <span className="text-lg">🔁</span>
              <span>{post.repostCount || 0}</span>
            </button>
            <button 
              onClick={handleLike} 
              disabled={isLiking}
              className={`flex items-center gap-2 cursor-pointer transition-all duration-300 
                         p-1.5 rounded-[10px] relative bg-transparent border-none 
                         text-[13px] font-semibold
                         ${isLiked 
                          ? 'text-red-500 hover:text-red-600 hover:bg-red-500/10' 
                          : 'text-white/50 hover:text-veritas-pink hover:bg-veritas-pink/10'}
                        disabled:opacity-50`}>
              <span className="text-lg">{isLiked ? '❤️' : '🤍'}</span>
              <span>{localLikeCount}</span>
            </button>
            <button className="flex items-center gap-2 cursor-pointer transition-all duration-300 
                               p-1.5 rounded-[10px] relative bg-transparent border-none 
                               text-inherit text-[13px] font-semibold
                               hover:text-veritas-pink hover:bg-veritas-pink/10">
              <span className="text-lg">🔖</span>
            </button>
            <button className="flex items-center gap-2 cursor-pointer transition-all duration-300 
                               p-1.5 rounded-[10px] relative bg-transparent border-none 
                               text-inherit text-[13px] font-semibold
                               hover:text-veritas-pink hover:bg-veritas-pink/10">
              <span className="text-lg">🔗</span>
            </button>
            <button
              onClick={handleOpenReplyModal}
              className="ml-auto px-4 py-1.5 rounded-full font-bold text-xs transition-all duration-300
                        disabled:opacity-50 disabled:cursor-not-allowed
                        bg-gradient-to-br from-veritas-pink to-veritas-pink-dark text-white border border-transparent hover:shadow-[0_4px_12px_rgba(255,107,157,0.3)]"
            >
              <span className="text-lg">Reply</span>
            </button>
          </div>
        </div>
      </div>

      {/* Loading state */}
      {loadingReplies && (
        <div className="py-8 text-center text-white/50 text-sm" style={{ paddingLeft: `${20 + leftPadding + 40}px` }}>
          Loading replies...
        </div>
      )}

      {/* Replies Thread */}
      {showReplies && !loadingReplies && replies.length > 0 && (
        <div className="border-l-2 border-white/10">
          {replies.map(reply => (
            <Tweet
              key={reply.id}
              post={reply}
              currentUserId={currentUserId}
              onPostUpdated={onPostUpdated}
              onAuthorFollowChange={onAuthorFollowChange}
              depth={depth + 1}
            />
          ))}
        </div>
      )}

      {/* Empty state for replies */}
      {showReplies && !loadingReplies && replies.length === 0 && replyCount > 0 && (
        <div className="py-4 text-center text-white/30 text-sm italic" style={{ paddingLeft: `${20 + leftPadding + 40}px` }}>
          No replies yet
        </div>
      )}
    </div>
  );
}

export default Tweet;