import React, { useState } from 'react';
import followService from '../../services/followService';

function FollowButton({ targetUser, currentUserId, initialIsFollowing = false, onFollowChange }) {
  const [isFollowing, setIsFollowing] = useState(initialIsFollowing);
  const [isLoading, setIsLoading] = useState(false);

  const handleFollowToggle = async (e) => {
    e.preventDefault();
    e.stopPropagation();
    
    if (isLoading) return;

    try {
      setIsLoading(true);
      
      if (isFollowing) {
        await followService.unfollowUser(currentUserId, targetUser.id);
        setIsFollowing(false);
      } else {
        await followService.followUser(currentUserId, targetUser.id);
        setIsFollowing(true);
      }

      if (onFollowChange) {
        onFollowChange(!isFollowing);
      }
    } catch (error) {
      console.error('Error toggling follow:', error);
      alert('Failed to update follow status. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  // Don't show button if viewing own profile
  if (currentUserId === targetUser.id) {
    return null;
  }

  return (
    <button
      onClick={handleFollowToggle}
      disabled={isLoading}
      className={`
        px-6 py-2.5 rounded-xl font-bold text-sm transition-all duration-300
        disabled:opacity-50 disabled:cursor-not-allowed
        ${isFollowing 
          ? 'bg-white/10 border-2 border-white/20 text-white hover:bg-red-500/20 hover:border-red-500 hover:text-red-400' 
          : 'bg-gradient-to-br from-veritas-pink to-veritas-pink-dark text-white border-2 border-transparent hover:shadow-[0_4px_16px_rgba(255,107,157,0.4)] hover:-translate-y-0.5'
        }
      `}
    >
      {isLoading ? 'Loading...' : (isFollowing ? 'Following' : 'Follow')}
    </button>
  );
}

export default FollowButton;