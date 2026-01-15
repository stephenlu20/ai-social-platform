

import React, { useState } from 'react';
import postService from '../../services/postService';

function Tweet({ post, currentUserId, onPostUpdated }) {
  const [isLiking, setIsLiking] = useState(false);
  const [localLikeCount, setLocalLikeCount] = useState(post.likeCount || 0);

  const author = post.author;
  const content = post.content;
  const createdAt = new Date(post.createdAt);
  const timeAgo = getTimeAgo(createdAt);

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
      await postService.likePost(currentUserId, post.id);
      setLocalLikeCount(prev => prev + 1);
      if (onPostUpdated) {
        onPostUpdated();
      }
    } catch (error) {
      console.error('Error liking post:', error);
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

  return (
    <div className="border-b border-white/[0.08] p-5 flex gap-3.5 cursor-pointer 
                    transition-all duration-300 hover:bg-veritas-pink/5">
      <div className="text-4xl flex-shrink-0 relative">🎨</div>
      <div className="flex-1">
        <div className="flex items-center gap-2 mb-2 flex-wrap">
          <span className="font-bold text-[15px]">{author.displayName}</span>
          <span className="text-white/50 text-sm">@{author.username}</span>
          <span className="text-white/50 text-sm">· {timeAgo}</span>
          
          {post.factCheckStatus && post.factCheckStatus !== 'UNCHECKED' && (
            <span className="px-2.5 py-1 rounded-lg text-[11px] font-bold inline-flex items-center gap-1 
                           uppercase tracking-wide bg-gradient-to-br from-green-600/30 to-green-700/30 
                           border border-green-600/40 text-green-300">
              ✅ VERIFIED
            </span>
          )}

          <div className="bg-gradient-to-br from-[#10b981] to-[#059669] text-white 
                          px-2.5 py-1 rounded-lg text-xs font-bold flex items-center gap-1 ml-auto">
            <span>🛡️</span>
            <span>{Math.round(author.trustScore)}</span>
          </div>
        </div>

        <div className="text-white/90 leading-relaxed my-2 mb-4 whitespace-pre-line text-[15px]">
          {content}
        </div>

        {post.repostOf && (
          <div className="my-4 p-4 bg-white/5 rounded-2xl border border-white/10">
            <div className="text-sm text-white/50 mb-2">🔁 Reposted from @{post.repostOf.author.username}</div>
            <div className="text-white/80">{post.repostOf.content}</div>
          </div>
        )}

        <div className="flex justify-between max-w-[500px] text-white/50 mt-3">
          <button className="flex items-center gap-2 cursor-pointer transition-all duration-300 
                             p-1.5 rounded-[10px] relative bg-transparent border-none 
                             text-inherit text-[13px] font-semibold
                             hover:text-veritas-pink hover:bg-veritas-pink/10">
            <span className="text-lg">💬</span>
            <span>{post.replyCount || 0}</span>
          </button>
          <button 
            onClick={handleRepost}
            className="flex items-center gap-2 cursor-pointer transition-all duration-300 
                       p-1.5 rounded-[10px] relative bg-transparent border-none 
                       text-inherit text-[13px] font-semibold
                       hover:text-veritas-pink hover:bg-veritas-pink/10">
            <span className="text-lg">🔁</span>
            <span>{post.repostCount || 0}</span>
          </button>
          <button 
            onClick={handleLike}
            disabled={isLiking}
            className="flex items-center gap-2 cursor-pointer transition-all duration-300 
                       p-1.5 rounded-[10px] relative bg-transparent border-none 
                       text-inherit text-[13px] font-semibold
                       hover:text-veritas-pink hover:bg-veritas-pink/10
                       disabled:opacity-50">
            <span className="text-lg">❤️</span>
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
        </div>
      </div>
    </div>
  );
}

export default Tweet;