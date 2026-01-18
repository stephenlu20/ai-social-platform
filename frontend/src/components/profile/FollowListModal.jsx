import React, { useState, useEffect } from 'react';
import { useUser } from '../../context/UserContext';
import followService from '../../services/followService';
import FollowButton from './FollowButton';
import logo from '../../assets/CondorTransparent.png';
import { X, Shield } from 'lucide-react';

function FollowListModal({ userId, type, onClose }) {
  const { currentUser } = useUser();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadUsers();
  }, [userId, type]);

  const loadUsers = async () => {
    try {
      setLoading(true);
      setError(null);
      
      let userList;
      if (type === 'followers') {
        userList = await followService.getFollowers(userId);
      } else {
        userList = await followService.getFollowing(userId);
      }
      
      setUsers(userList);
    } catch (err) {
      console.error('Error loading users:', err);
      setError('Failed to load users');
    } finally {
      setLoading(false);
    }
  };

  const handleFollowChange = (targetUserId, newFollowStatus) => {
    setUsers(prevUsers => 
      prevUsers.map(user => 
        user.id === targetUserId 
          ? { ...user, isFollowing: newFollowStatus }
          : user
      )
    );
  };

  return (
    <div 
      className="fixed inset-0 bg-black/80 flex items-center justify-center z-50 p-4"
      onClick={onClose}
    >
      <div 
        className="bg-[#0f0519] border-2 border-white/10 rounded-3xl max-w-lg w-full max-h-[80vh] overflow-hidden flex flex-col"
        onClick={e => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-white/10">
          <h2 className="text-2xl font-bold">
            {type === 'followers' ? 'Followers' : 'Following'}
          </h2>
          <button
            onClick={onClose}
            className="w-10 h-10 rounded-xl bg-white/5 hover:bg-white/10 transition-all flex items-center justify-center text-2xl"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content */}
        <div className="flex-1 overflow-y-auto">
          {loading && (
            <div className="p-20 text-center text-white/50">
              Loading...
            </div>
          )}

          {error && (
            <div className="p-20 text-center text-red-400">
              {error}
            </div>
          )}

          {!loading && !error && users.length === 0 && (
            <div className="p-20 text-center text-white/50">
              {type === 'followers' ? 'No followers yet' : 'Not following anyone yet'}
            </div>
          )}

          {!loading && !error && users.length > 0 && (
            <div className="divide-y divide-white/10">
              {users.map(user => (
                <UserListItem 
                  key={user.id}
                  user={user}
                  currentUserId={currentUser?.id}
                  onFollowChange={handleFollowChange}
                />
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

function UserListItem({ user, currentUserId, onFollowChange }) {
  return (
    <div className="flex items-center gap-3 p-4 hover:bg-white/5 transition-all">
      {/* Avatar */}
      <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-veritas-blue to-veritas-blue-dark 
                      flex items-center justify-center text-xl flex-shrink-0
                      shadow-[0_4px_12px_rgba(102,126,234,0.3)]">
        <img 
          src={logo} 
          alt="Candor Logo" 
          className="w-10 h-10 object-contain"
        />
      </div>

      {/* User Info */}
      <div className="flex-1 min-w-0">
        <div className="font-bold text-sm truncate">{user.displayName}</div>
        <div className="text-white/50 text-xs truncate">@{user.username}</div>
        {user.bio && (
          <div className="text-white/70 text-xs mt-1 line-clamp-2">{user.bio}</div>
        )}
      </div>

      {/* Trust Score */}
      <div className="flex items-center gap-1 bg-gradient-to-br from-green-600/30 to-green-700/30 
                      border border-green-600/40 rounded-lg px-2 py-1 flex-shrink-0">
        <span className="text-xs"><Shield className="w-3 h-3" /></span>
        <span className="text-xs font-bold">{Math.round(user.trustScore)}</span>
      </div>

      {/* Follow Button */}
      {currentUserId && (
        <div className="flex-shrink-0">
          <FollowButton
            targetUser={user}
            currentUserId={currentUserId}
            initialIsFollowing={user.isFollowing}
            onFollowChange={(newStatus) => onFollowChange(user.id, newStatus)}
          />
        </div>
      )}
    </div>
  );
}

export default FollowListModal;