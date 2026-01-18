import React, { useState, useEffect } from 'react';
import HelpBadge from '../common/HelpBadge';
import { useUser } from '../../context/UserContext';
import EditProfileModal from '../profile/EditProfileModal';
import CreateDebateModal from '../debates/CreateDebateModal';
import debateService from '../../services/debateService';


function Sidebar({ onNavigateToProfile, onNavigateToSearch, onNavigateToDebates }) {
  const { currentUser, allUsers, loading, switchUser } = useUser();

  const [isEditProfileOpen, setIsEditProfileOpen] = useState(false);
  const [isDebateModalOpen, setIsDebateModalOpen] = useState(false);
  const [pendingChallenges, setPendingChallenges] = useState([]);

  useEffect(() => {
    if (currentUser) {
      loadPendingChallenges();
    }
  }, [currentUser]);

  const loadPendingChallenges = async () => {
    try {
      const challenges = await debateService.getPendingChallenges(currentUser.id);
      setPendingChallenges(challenges);
    } catch (err) {
      console.error('Error loading pending challenges:', err);
    }
  };

  const handleProfileClick = () => {
    if (currentUser && onNavigateToProfile) {
      onNavigateToProfile(currentUser.id);
    }
  };

  if (loading || !currentUser) {
    return (
      <div className="py-[30px] h-screen sticky top-0 flex items-center justify-center">
        <div className="text-white/50">Loading...</div>
      </div>
    );
  }

  return (
    <div className="py-[30px] h-screen sticky top-0 flex flex-col">
      {/* Logo */}
      <div className="flex items-center gap-3 mx-5 mb-6">
        <div className="w-12 h-12 bg-gradient-to-br from-veritas-pink to-veritas-coral 
                        rounded-2xl flex items-center justify-center text-[28px]">
          🎭
        </div>
        <div className="text-[26px] font-extrabold bg-gradient-to-br from-veritas-pink to-veritas-coral 
                        bg-clip-text text-transparent">
          Chirp
        </div>
      </div>

      {/* User Selector Dropdown */}
      <div className="mx-3 mb-4 p-3 bg-gradient-to-br from-veritas-purple/20 to-veritas-pink/20 
                      border-2 border-veritas-pink/30 rounded-2xl">
        <label className="block text-xs font-bold text-veritas-coral mb-2 uppercase tracking-wider">
          Demo Mode - Select User:
        </label>
        <select 
          value={currentUser?.id || ''} 
          onChange={(e) => switchUser(e.target.value)}
          className="w-full bg-white/10 border border-white/20 rounded-xl px-3 py-2 
                     text-white font-semibold cursor-pointer
                     focus:outline-none focus:border-veritas-pink focus:bg-white/15
                     transition-all duration-300"
        >
          {allUsers.map(user => (
            <option key={user.id} value={user.id} className="bg-gray-900">
              {user.displayName} (@{user.username})
            </option>
          ))}
        </select>
      </div>

      {/* User Profile Card */}
      <div
        onClick={handleProfileClick}
        className="mx-3 mb-6 p-4 rounded-2xl cursor-pointer
                   bg-gradient-to-br from-veritas-purple/20 to-veritas-pink/20
                   border-2 border-veritas-pink/30
                   hover:border-veritas-pink/50 transition-all"
      >
        <div className="flex items-center gap-3">
          <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-veritas-blue to-veritas-blue-dark 
                          flex items-center justify-center text-xl">
            🎨
          </div>

          <div className="flex-1">
            <div className="font-bold text-sm text-veritas-coral">{currentUser.displayName}</div>
            <div className="text-xs text-white/50">@{currentUser.username}</div>
          </div>

          <button
            onClick={(e) => {
              e.stopPropagation();
              setIsEditProfileOpen(true);
            }}
            className="p-2 rounded-lg text-white/50 hover:text-white hover:bg-white/10"
          >
            ✏️
          </button>
        </div>

        {currentUser.bio && (
          <div className="mt-3 text-xs text-white/70 leading-relaxed">
            {currentUser.bio}
          </div>
        )}
      </div>

      {/* Primary Actions */}
      <div className="mx-3 space-y-2">
        <button
          onClick={() => setIsDebateModalOpen(true)}
          className="w-full px-4 py-3 rounded-xl text-left font-semibold
                     bg-gradient-to-br from-red-500/20 to-orange-500/20
                     border border-red-500/30 hover:bg-red-500/30 transition"
        >
          ⚔️ Create Debate
        </button>

        <button
          onClick={() => onNavigateToDebates && onNavigateToDebates('invitations')}
          className="w-full px-4 py-3 rounded-xl text-left font-semibold
                     transition flex items-center justify-between
                     bg-gradient-to-br from-purple-500/20 to-pink-500/20
                     border border-purple-500/30 hover:bg-purple-500/30"
        >
          <span>📬 Challenges</span>
          {pendingChallenges.length > 0 && (
            <span className="bg-orange-500 text-white text-xs font-bold px-2 py-0.5 rounded-full">
              {pendingChallenges.length}
            </span>
          )}
        </button>
      </div>

      {/* Spacer */}
      <div className="flex-1" />

      {/* Modals */}
      <EditProfileModal
        isOpen={isEditProfileOpen}
        onClose={() => setIsEditProfileOpen(false)}
      />

      <CreateDebateModal
        isOpen={isDebateModalOpen}
        onClose={() => setIsDebateModalOpen(false)}
        onDebateCreated={() => {}}
      />
    </div>
  );
}

export default Sidebar;
