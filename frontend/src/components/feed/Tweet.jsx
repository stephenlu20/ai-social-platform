import React, { useState, useEffect } from 'react';
import postService from '../../services/postService';
import followService from '../../services/followService';
import ReplyModal from './ReplyModal';
import factCheckService from '../../services/factcheckService';
import { FactCheckBadge, FactCheckButton, FactCheckModal } from '../factcheck';
import { getStyleClasses } from './PostStyler'; // #75
import { TrustScoreBadge } from '../trustscore';
import CreateDebateModal from '../debates/CreateDebateModal';

function Tweet({ post, currentUserId, onPostUpdated, onAuthorFollowChange, onPostDeleted, canDelete = false, depth = 0 }) {
  const [isLiked, setIsLiked] = useState(post.isLikedByCurrentUser || false);
  const [isLiking, setIsLiking] = useState(false);
  const [localLikeCount, setLocalLikeCount] = useState(post.likeCount || 0);
  const [isFollowing, setIsFollowing] = useState(post.author?.isFollowing || false);
  const [isFollowLoading, setIsFollowLoading] = useState(false);
  const [isFollowHovering, setIsFollowHovering] = useState(false);
  const [showReplyModal, setShowReplyModal] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [clickY, setClickY] = useState(0);
  
  // Reply state
  const [replies, setReplies] = useState([]);
  const [showReplies, setShowReplies] = useState(false);
  const [loadingReplies, setLoadingReplies] = useState(false);
  const [repliesLoaded, setRepliesLoaded] = useState(false);

  // Fact-check state
  const [isFactChecking, setIsFactChecking] = useState(false);
  const [factCheckResult, setFactCheckResult] = useState(null);
  const [showFactCheckModal, setShowFactCheckModal] = useState(false);
  const [factCheckStatus, setFactCheckStatus] = useState(post.factCheckStatus);
  const [factCheckScore, setFactCheckScore] = useState(post.factCheckScore);

  // Repost state
  const [isReposted, setIsReposted] = useState(post.isRepostedByCurrentUser || false);
  const [localRepostCount, setLocalRepostCount] = useState(post.repostCount || 0);
  const [isReposting, setIsReposting] = useState(false);

  // Challenge modal state
  const [showChallengeModal, setShowChallengeModal] = useState(false);

  // Check if this is a repost - if so, display original post content
  const isRepost = post.repostOf != null;
  const displayPost = isRepost ? post.repostOf : post;
  const originalAuthor = isRepost ? post.repostOf.author : null;
  const reposter = isRepost ? post.author : null;

  const author = displayPost.author || post.author;
  const content = displayPost.content || post.content;
  const postStyle = displayPost.style || post.style; // #75 - Get style from post
  const styleClasses = getStyleClasses(postStyle); // #75 - Get CSS classes for style
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
    if (isReposting || isReposted) return; // Prevent double repost

    try {
      setIsReposting(true);
      // Optimistic UI update
      setIsReposted(true);
      setLocalRepostCount(prev => prev + 1);

      await postService.repost(currentUserId, post.id);

      if (onPostUpdated) {
        onPostUpdated();
      }
    } catch (error) {
      // Revert optimistic update on error
      setIsReposted(false);
      setLocalRepostCount(prev => prev - 1);
      console.error('Error reposting:', error.response?.data || error.message || error);
    } finally {
      setIsReposting(false);
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

  // Fact-check handlers
  const handleFactCheck = async (e) => {
    console.log('Click Y:', e.clientY);
    console.log('Window height:', window.innerHeight);
    setClickY(e.clientY);
    // If already checked, just show the modal
    if (factCheckStatus && factCheckStatus !== 'UNCHECKED') {
      setShowFactCheckModal(true);
      return;
    }

    try {
      setIsFactChecking(true);
      const result = await factCheckService.checkPost(post.id, currentUserId);
      setFactCheckResult(result);

      // Update local state with new status
      if (result && !result.error) {
        setFactCheckStatus(result.verdict);
        setFactCheckScore(result.confidence ? result.confidence / 100 : null);
      }

      setShowFactCheckModal(true);

      if (onPostUpdated) {
        onPostUpdated();
      }
    } catch (error) {
      console.error('Error fact-checking post:', error);
      setFactCheckResult({ error: 'Failed to fact-check this post. Please try again.' });
      setShowFactCheckModal(true);
    } finally {
      setIsFactChecking(false);
    }
  };

  const handleViewFactCheck = (e) => {
    console.log('Badge click Y:', e.clientY);
    setClickY(e.clientY);
    setShowFactCheckModal(true);
  };

  const handleDelete = async (e) => {
    e.preventDefault();
    e.stopPropagation();

    if (isDeleting) return;

    const confirmed = window.confirm('Delete this post? This cannot be undone.');
    if (!confirmed) return;

    try {
      setIsDeleting(true);
      await postService.deletePost(currentUserId, post.id);

      if (onPostDeleted) {
        onPostDeleted(post.id);
      }
    } catch (error) {
      console.error('Error deleting post:', error);
    } finally {
      setIsDeleting(false);
    }
  };

  // For reposts, check against the displayed author (original post author)
  const isOwnPost = currentUserId === author?.id;

  // Indent for nested replies (max depth to prevent too much nesting)
  const maxDepth = 3;
  const effectiveDepth = Math.min(depth, maxDepth);
  const leftPadding = effectiveDepth * 40; // 40px per level

  return (
    <div>
      {/* Repost Attribution Header */}
      {isRepost && reposter && (
        <div
          className="flex items-center gap-2 text-white/50 text-xs pt-3 pb-1"
          style={{ paddingLeft: `${20 + leftPadding + 54}px` }}
        >
          <span className="text-green-400">🔁</span>
          <span className="font-semibold text-white/70">{reposter.displayName}</span>
          <span>reposted</span>
        </div>
      )}
      <div
        className={`border-b border-white/[0.08] p-5 flex gap-3.5
                    transition-all duration-300 hover:bg-veritas-pink/5
                    ${isRepost ? 'pt-2' : ''}`}
        style={{ paddingLeft: `${20 + leftPadding}px` }}
      >
        <div className="text-4xl flex-shrink-0 relative">🎨</div>
        <div className="flex-1">
          <div className="flex items-center gap-2 mb-2 flex-wrap">
            <span className="font-bold text-[15px] text-veritas-coral">{author.displayName}</span>
            <span className="text-white/50 text-sm">@{author.username}</span>
            {/* Trust Score Badge */}
            {author.trustScore != null && (
              <TrustScoreBadge
                score={author.trustScore}
                size="xs"
                showTooltip={true}
                userId={author.id}
              />
            )}
            <span className="text-white/50 text-sm">·</span>
            <span className="text-white/50 text-sm">{timeAgo}</span>

            {/* Fact Check Badge */}
            {factCheckStatus && factCheckStatus !== 'UNCHECKED' && (
              <FactCheckBadge
                status={factCheckStatus}
                score={factCheckScore}
                size="xs"
                onClick={handleViewFactCheck}
              />
            )}

            {canDelete && isOwnPost && (
              <button
                onClick={handleDelete}
                disabled={isDeleting}
                className="
                  ml-2 px-3 py-1.5 rounded-full text-xs font-bold
                  text-red-400 border border-red-500/40
                  hover:bg-red-500/20 hover:border-red-500
                  transition-all
                  disabled:opacity-50 disabled:cursor-not-allowed
                "
              >
                {isDeleting ? 'Deleting…' : 'Delete'}
              </button>
            )}

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
          
          {/* #75 - Apply post styles */}
          <div
            className={`leading-relaxed mb-3 ${styleClasses.sizeClass || 'text-[15px]'} ${styleClasses.fontClass} ${styleClasses.colorClass || 'text-white/90'}
                       ${Object.keys(styleClasses.backgroundStyle).length > 0 ? 'p-3 rounded-xl' : ''}`}
            style={styleClasses.backgroundStyle}
          >
            {content}
          </div>
          
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
              disabled={isReposting || isReposted}
              className={`flex items-center gap-2 cursor-pointer transition-all duration-300
                         p-1.5 rounded-[10px] relative bg-transparent border-none
                         text-[13px] font-semibold
                         ${isReposted
                           ? 'text-green-500 cursor-default'
                           : 'text-white/50 hover:text-green-400 hover:bg-green-400/10'}
                         disabled:opacity-70`}>
              <span className="text-lg">🔁</span>
              <span>{localRepostCount}</span>
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
            {/* Challenge to Debate - only show on other users' posts */}
            {!isOwnPost && currentUserId && (
              <button
                onClick={() => setShowChallengeModal(true)}
                className="flex items-center gap-2 cursor-pointer transition-all duration-300
                           p-1.5 rounded-[10px] relative bg-transparent border-none
                           text-inherit text-[13px] font-semibold
                           hover:text-orange-400 hover:bg-orange-400/10"
                title="Challenge to debate"
              >
                <span className="text-lg">⚔️</span>
              </button>
            )}
            <FactCheckButton
              onClick={handleFactCheck}
              isLoading={isFactChecking}
              isChecked={factCheckStatus && factCheckStatus !== 'UNCHECKED'}
              size="sm"
            />
            <button
              onClick={handleOpenReplyModal}
              className="ml-auto px-3 py-1.5 rounded-full font-bold text-xs transition-all duration-300
                        disabled:opacity-50 disabled:cursor-not-allowed
                        bg-gradient-to-br from-veritas-pink to-veritas-pink-dark text-white border border-transparent hover:shadow-[0_4px_12px_rgba(255,107,157,0.3)]"
            >
              Reply
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
      {showReplyModal && (
        <ReplyModal
          post={post}
          currentUserId={currentUserId}
          onClose={handleCloseReplyModal}
          onReplyCreated={() => {
            handleCloseReplyModal();
            if (onPostUpdated) onPostUpdated();
          }}
        />
      )}
      {/* Fact Check Modal */}
      <FactCheckModal
        isOpen={showFactCheckModal}
        onClose={() => setShowFactCheckModal(false)}
        result={factCheckResult}
        postContent={content}
        clickY={clickY}
      />

      {/* Challenge to Debate Modal */}
      {showChallengeModal && (
        <CreateDebateModal
          isOpen={showChallengeModal}
          onClose={() => setShowChallengeModal(false)}
          prefilledDefender={author}
          prefilledTopic={content.length > 200 ? content.substring(0, 200) + '...' : content}
        />
      )}
    </div>
  );
}

export default Tweet;