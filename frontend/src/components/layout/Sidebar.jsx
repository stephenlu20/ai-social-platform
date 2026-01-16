import React, { useState } from 'react';
import { useUser } from '../../context/UserContext';
import EditProfileModal from '../profile/EditProfileModal';
import CreateDebateModal from '../debates/CreateDebateModal';

function Sidebar({ onNavigateToProfile, onNavigateToSearch }) {
  const { currentUser, loading } = useUser();

  const [isEditProfileOpen, setIsEditProfileOpen] = useState(false);
  const [isDebateModalOpen, setIsDebateModalOpen] = useState(false);

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

      {/* User Profile Card */}
      <div
        onClick={handleProfileClick}
        className="mx-3 mb-6 p-4 rounded-2xl cursor-pointer
                   bg-white/5 border border-white/10
                   hover:bg-white/10 transition-all"
      >
        <div className="flex items-center gap-3">
          <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-veritas-blue to-veritas-blue-dark 
                          flex items-center justify-center text-xl">
            🎨
          </div>

          <div className="flex-1">
            <div className="font-bold text-sm">{currentUser.displayName}</div>
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
          onClick={onNavigateToSearch}
          className="w-full px-4 py-3 rounded-xl text-left font-semibold
                     hover:bg-veritas-pink/15 transition"
        >
          🌍 Explore
        </button>

        <button
          onClick={() => setIsDebateModalOpen(true)}
          className="w-full px-4 py-3 rounded-xl text-left font-semibold
                     bg-gradient-to-br from-red-500/20 to-orange-500/20
                     border border-red-500/30 hover:bg-red-500/30 transition"
        >
          ⚔️ Create Debate
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
