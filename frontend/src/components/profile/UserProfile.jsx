import React, { useState, useEffect } from 'react';
import { useUser } from '../../context/UserContext';
import userService from '../../services/userService';
import postService from '../../services/postService';
import FollowButton from './FollowButton';
import Tweet from '../feed/Tweet';

function UserProfile({ username }) {
  const { currentUser } = useUser();
  const [profileUser, setProfileUser] = useState(null);
  const [posts, setPosts] = useState([]);
  const [replies, setReplies] = useState([]);
  const [activeTab, setActiveTab] = useState('posts');
  const [loading, setLoading] = useState(true);
  const [showFollowersModal, setShowFollowersModal] = useState(false);
  const [showFollowingModal, setShowFollowingModal] = useState(false);

  useEffect(() => {
    if (username) {
      loadUserProfile();
    }
  }, [username, currentUser]);

  const loadUserProfile = async () => {
    try {
      setLoading(true);
      const user = await userService.getUserByUsername(username, currentUser?.id);
      setProfileUser(user);

      const userPosts = await postService.searchPosts({
        authorId: user.id,
        page: 0,
        size: 50
      });
      setPosts(userPosts.content || []);

      // Load replies separately if needed
      const userReplies = await postService.searchPosts({
        authorId: user.id,
        page: 0,
        size: 50
      });
      setReplies(userReplies.content?.filter(p => p.replyToId) || []);

    } catch (error) {
      console.error('Error loading profile:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleFollowChange = (newFollowStatus) => {
    setProfileUser(prev => ({
      ...prev,
      isFollowing: newFollowStatus,
      followerCount: newFollowStatus ? prev.followerCount + 1 : prev.followerCount - 1
    }));
  };

  // Update post state without reloading
  const handlePostUpdated = (updatedPost) => {
    if (updatedPost) {
      setPosts(prevPosts => 
        prevPosts.map(post => 
          post.id === updatedPost.id ? updatedPost : post
        )
      );
      setReplies(prevReplies => 
        prevReplies.map(post => 
          post.id === updatedPost.id ? updatedPost : post
        )
      );
    }
  };

  // This won't be called from UserProfile's own posts since they're all by the same author
  // But included for consistency with MainFeed
  const handleAuthorFollowChange = (authorId, isNowFollowing) => {
    // No-op for UserProfile since all posts are by the profile owner
  };

  if (loading) {
    return (
      <div className="bg-white/[0.03] rounded-3xl overflow-hidden backdrop-blur-[10px] border border-white/10 p-20 text-center">
        <div className="text-white/50">Loading profile...</div>
      </div>
    );
  }

  if (!profileUser) {
    return (
      <div className="bg-white/[0.03] rounded-3xl overflow-hidden backdrop-blur-[10px] border border-white/10 p-20 text-center">
        <div className="text-white/50">User not found</div>
      </div>
    );
  }

  return (
    <div className="bg-white/[0.03] rounded-3xl overflow-hidden backdrop-blur-[10px] border border-white/10">
      {/* Header Banner */}
      <div className="h-48 bg-gradient-to-br from-veritas-purple/30 to-veritas-pink/30" />
      
      {/* Profile Info */}
      <div className="px-6 pb-6">
        {/* Avatar & Follow Button Row */}
        <div className="flex justify-between items-start -mt-16 mb-4">
          <div className="w-32 h-32 rounded-[20px] bg-gradient-to-br from-veritas-blue to-veritas-blue-dark 
                          flex items-center justify-center text-5xl 
                          shadow-[0_8px_24px_rgba(102,126,234,0.4)] border-4 border-[#0f0519]">
            🎨
          </div>
          
          {currentUser && (
            <FollowButton 
              targetUser={profileUser}
              currentUserId={currentUser.id}
              initialIsFollowing={profileUser.isFollowing}
              onFollowChange={handleFollowChange}
            />
          )}
        </div>

        {/* Name & Username */}
        <div className="mb-4">
          <h1 className="text-3xl font-extrabold mb-1">{profileUser.displayName}</h1>
          <div className="text-white/50 text-lg">@{profileUser.username}</div>
        </div>

        {/* Bio */}
        {profileUser.bio && (
          <p className="text-white/90 mb-4 leading-relaxed">{profileUser.bio}</p>
        )}

        {/* Stats Row */}
        <div className="flex gap-6 mb-4 flex-wrap">
          <button 
            onClick={() => setShowFollowingModal(true)}
            className="hover:underline transition-all"
          >
            <span className="font-bold text-white">{profileUser.followingCount || 0}</span>
            <span className="text-white/50 ml-1">Following</span>
          </button>
          <button 
            onClick={() => setShowFollowersModal(true)}
            className="hover:underline transition-all"
          >
            <span className="font-bold text-white">{profileUser.followerCount || 0}</span>
            <span className="text-white/50 ml-1">Followers</span>
          </button>
          <div>
            <span className="font-bold text-white">{profileUser.postCount || 0}</span>
            <span className="text-white/50 ml-1">Posts</span>
          </div>
        </div>

        {/* Trust Score Badge */}
        <div className="inline-flex items-center gap-2 bg-gradient-to-br from-green-600/30 to-green-700/30 
                        border-2 border-green-600/40 rounded-xl px-4 py-2">
          <span className="text-2xl">🛡️</span>
          <div>
            <div className="text-xs text-green-300 font-semibold">Trust Score</div>
            <div className="text-xl font-bold text-white">{Math.round(profileUser.trustScore)}</div>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex border-b border-white/10 px-2">
        <button
          onClick={() => setActiveTab('posts')}
          className={`flex-1 py-4 text-center font-bold transition-all ${
            activeTab === 'posts'
              ? 'text-white border-b-4 border-veritas-pink'
              : 'text-white/50 hover:text-white/80'
          }`}
        >
          Posts
        </button>
        <button
          onClick={() => setActiveTab('replies')}
          className={`flex-1 py-4 text-center font-bold transition-all ${
            activeTab === 'replies'
              ? 'text-white border-b-4 border-veritas-pink'
              : 'text-white/50 hover:text-white/80'
          }`}
        >
          Replies
        </button>
      </div>

      {/* Content */}
      <div>
        {activeTab === 'posts' && (
          <>
            {posts.length === 0 ? (
              <div className="p-20 text-center text-white/50">
                No posts yet
              </div>
            ) : (
              posts.map(post => (
                <Tweet 
                  key={post.id}
                  post={post}
                  currentUserId={currentUser?.id}
                  onPostUpdated={handlePostUpdated}
                  onAuthorFollowChange={handleAuthorFollowChange}
                />
              ))
            )}
          </>
        )}

        {activeTab === 'replies' && (
          <>
            {replies.length === 0 ? (
              <div className="p-20 text-center text-white/50">
                No replies yet
              </div>
            ) : (
              replies.map(post => (
                <Tweet 
                  key={post.id}
                  post={post}
                  currentUserId={currentUser?.id}
                  onPostUpdated={handlePostUpdated}
                  onAuthorFollowChange={handleAuthorFollowChange}
                />
              ))
            )}
          </>
        )}
      </div>

      {/* Modals */}
      {showFollowersModal && (
        <FollowersModal 
          userId={profileUser.id}
          onClose={() => setShowFollowersModal(false)}
        />
      )}
      {showFollowingModal && (
        <FollowingModal 
          userId={profileUser.id}
          onClose={() => setShowFollowingModal(false)}
        />
      )}
    </div>
  );
}

// Placeholder modals - we'll implement these next
function FollowersModal({ userId, onClose }) {
  return (
    <div className="fixed inset-0 bg-black/80 flex items-center justify-center z-50" onClick={onClose}>
      <div className="bg-[#0f0519] border-2 border-white/10 rounded-3xl p-6 max-w-md w-full mx-4" onClick={e => e.stopPropagation()}>
        <h2 className="text-2xl font-bold mb-4">Followers</h2>
        <p className="text-white/50">Loading followers...</p>
      </div>
    </div>
  );
}

function FollowingModal({ userId, onClose }) {
  return (
    <div className="fixed inset-0 bg-black/80 flex items-center justify-center z-50" onClick={onClose}>
      <div className="bg-[#0f0519] border-2 border-white/10 rounded-3xl p-6 max-w-md w-full mx-4" onClick={e => e.stopPropagation()}>
        <h2 className="text-2xl font-bold mb-4">Following</h2>
        <p className="text-white/50">Loading following...</p>
      </div>
    </div>
  );
}

export default UserProfile;