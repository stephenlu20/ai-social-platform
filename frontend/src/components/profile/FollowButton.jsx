import React, { useState } from 'react';
import followService from '../../services/followService';

function FollowButton({ targetUser, currentUserId, initialIsFollowing = false, onFollowChange }) {
  const [isFollowing, setIsFollowing] = useState(initialIsFollowing);
  const [isLoading, setIsLoading] = useState(false);
  const [isHovering, setIsHovering] = useState(false);

  const handleFollowToggle = async (e) => {
    e.preventDefault();
    e.stopPropagation();
    
    if (isLoading) return;

    try {
      setIsLoading(true);
      
      // Use the toggle endpoint
      const response = await followService.toggleFollow(currentUserId, targetUser.id);
      
      setIsFollowing(response.following);

      if (onFollowChange) {
        onFollowChange(response.following);
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

  // Determine button text
  const getButtonText = () => {
    if (isLoading) return 'Loading...';
    if (isFollowing && isHovering) return 'Unfollow';
    if (isFollowing) return 'Following';
    return 'Follow';
  };

  return (
    <button
      onClick={handleFollowToggle}
      onMouseEnter={() => setIsHovering(true)}
      onMouseLeave={() => setIsHovering(false)}
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
      {getButtonText()}
    </button>
  );
}

export default FollowButton;