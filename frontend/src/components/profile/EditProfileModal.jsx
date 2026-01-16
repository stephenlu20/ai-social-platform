import React, { useState, useEffect } from 'react';
import ReactDOM from 'react-dom';
import { useUser } from '../../context/UserContext';
import userService from '../../services/userService';

function EditProfileModal({ isOpen, onClose }) {
  const { currentUser, updateCurrentUser } = useUser();

  const [displayName, setDisplayName] = useState('');
  const [bio, setBio] = useState('');
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (currentUser && isOpen) {
      setDisplayName(currentUser.displayName || '');
      setBio(currentUser.bio || '');
    }
  }, [currentUser, isOpen]);

  if (!isOpen) return null;

  const handleSave = async () => {
    if (!displayName.trim()) return;

    try {
      setSaving(true);

      const updatedUser = await userService.updateUser(
        currentUser.id,
        { displayName, bio }
      );

      updateCurrentUser(updatedUser);
      onClose();
    } catch (err) {
      console.error('Error updating profile:', err);
    } finally {
      setSaving(false);
    }
  };

  return ReactDOM.createPortal(
    <div className="fixed inset-0 z-[1000] flex items-center justify-center">
      {/* Backdrop */}
      <div
        className="absolute inset-0 bg-black/60 backdrop-blur-sm"
        onClick={onClose}
      />

      {/* Modal */}
      <div className="relative z-[1001] w-full max-w-md mx-4
                      bg-[#0f0f0f] rounded-2xl border border-white/10">
        <div className="px-5 py-4 border-b border-white/10 font-bold">
          Edit Profile
        </div>

        <div className="p-5 space-y-4">
          <div>
            <label className="text-xs text-white/50">Display Name</label>
            <input
              value={displayName}
              onChange={(e) => setDisplayName(e.target.value)}
              className="w-full mt-1 bg-transparent border border-white/20
                         rounded-lg px-3 py-2 text-white"
            />
          </div>

          <div>
            <label className="text-xs text-white/50">Bio</label>
            <textarea
              value={bio}
              onChange={(e) => setBio(e.target.value)}
              rows={3}
              className="w-full mt-1 bg-transparent border border-white/20
                         rounded-lg px-3 py-2 text-white resize-none"
            />
          </div>
        </div>

        <div className="px-5 py-4 border-t border-white/10 flex justify-end gap-3">
          <button
            onClick={onClose}
            className="text-white/50 hover:text-white"
          >
            Cancel
          </button>
          <button
            onClick={handleSave}
            disabled={saving}
            className="px-4 py-2 rounded-full font-bold
                       bg-gradient-to-br from-veritas-pink to-veritas-pink-dark
                       disabled:opacity-50"
          >
            {saving ? 'Saving…' : 'Save'}
          </button>
        </div>
      </div>
    </div>,
    document.body
  );
}

export default EditProfileModal;
