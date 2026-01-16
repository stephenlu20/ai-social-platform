import React from 'react';
import FollowButton from './FollowButton';

function UserCard({ user, currentUserId, onViewProfile, showFollowButton = true, onFollowChange }) {
  const handleViewProfile = () => {
    if (onViewProfile) {
      onViewProfile(user.id);
    }
  };

  return (
    <div className="p-4 border-b border-white/10 hover:bg-white/5 transition-all duration-300 flex items-start gap-4">
      {/* Avatar */}
      <div 
        className="w-12 h-12 rounded-xl bg-gradient-to-br from-veritas-blue to-veritas-blue-dark 
                   flex items-center justify-center text-xl shadow-[0_4px_12px_rgba(102,126,234,0.3)] 
                   flex-shrink-0 cursor-pointer"
        onClick={handleViewProfile}
      >
        🎨
      </div>

      {/* User Info */}
      <div className="flex-1 min-w-0">
        <div className="flex items-start justify-between gap-2">
          <div 
            className="flex-1 min-w-0 cursor-pointer" 
            onClick={handleViewProfile}
          >
            <div className="font-bold text-[15px] truncate hover:text-veritas-pink transition-colors">
              {user.displayName}
            </div>
            <div className="text-white/50 text-sm truncate">
              @{user.username}
            </div>
          </div>

          {/* Trust Score & Follow Button */}
          <div className="flex items-center gap-2 flex-shrink-0">
            <div className="bg-gradient-to-br from-[#10b981] to-[#059669] text-white 
                            px-2.5 py-1 rounded-lg text-xs font-bold flex items-center gap-1">
              <span>🛡️</span>
              <span>{Math.round(user.trustScore)}</span>
            </div>
            
            {showFollowButton && user.id !== currentUserId && (
              <FollowButton
                userId={user.id}
                currentUserId={currentUserId}
                initialIsFollowing={user.isFollowing}
                onFollowChange={onFollowChange}
              />
            )}
          </div>
        </div>

        {/* Bio */}
        {user.bio && (
          <div className="text-white/70 text-sm mt-2 line-clamp-2">
            {user.bio}
          </div>
        )}

        {/* Stats */}
        <div className="flex items-center gap-4 mt-2 text-xs text-white/50">
          <span>
            <strong className="text-white">{user.followerCount || 0}</strong> followers
          </span>
          <span>
            <strong className="text-white">{user.followingCount || 0}</strong> following
          </span>
          <span>
            <strong className="text-white">{user.postCount || 0}</strong> posts
          </span>
        </div>
      </div>
    </div>
  );
}

export default UserCard;