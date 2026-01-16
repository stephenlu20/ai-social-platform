
import React, { useState } from 'react';
import { createPortal } from 'react-dom';
import { useUser } from '../../context/UserContext';
import debateService from '../../services/debateService';

function CreateDebateModal({ isOpen, onClose, onDebateCreated }) {
  const { currentUser, allUsers } = useUser();
  const [topic, setTopic] = useState('');
  const [defenderId, setDefenderId] = useState('');
  const [isCreating, setIsCreating] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!topic.trim() || !defenderId) {
      setError('Please fill in all fields');
      return;
    }

    if (defenderId === currentUser.id) {
      setError('You cannot challenge yourself');
      return;
    }

    try {
      setIsCreating(true);
      setError('');
      
      await debateService.createChallenge(currentUser.id, defenderId, topic);
      
      setTopic('');
      setDefenderId('');
      onClose();
      
      if (onDebateCreated) {
        onDebateCreated();
      }
    } catch (err) {
      console.error('Error creating debate:', err);
      setError('Failed to create debate challenge');
    } finally {
      setIsCreating(false);
    }
  };

  if (!isOpen) return null;

  const availableOpponents = allUsers.filter(u => u.id !== currentUser.id);

  const modalContent = (
    <div className="fixed inset-0 bg-black/60 backdrop-blur-sm z-[9999] flex items-center justify-center p-4">
      <div className="bg-gradient-to-br from-[#1a3a52] to-[#234562] border-2 border-white/20 
                      rounded-3xl max-w-2xl w-full p-8 shadow-2xl">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-2xl font-bold flex items-center gap-3">
            <span className="text-3xl">⚔️</span>
            <span>Create Debate Challenge</span>
          </h2>
          <button 
            onClick={onClose}
            className="text-white/50 hover:text-white text-3xl leading-none"
          >
            ×
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="mb-6">
            <label className="block text-sm font-bold text-white/80 mb-2">
              Debate Topic
            </label>
            <textarea
              value={topic}
              onChange={(e) => setTopic(e.target.value)}
              placeholder="What do you want to debate about?"
              className="w-full bg-white/10 border border-white/20 rounded-xl p-4 text-white
                         placeholder:text-white/30 focus:outline-none focus:border-[#c9a35e]
                         min-h-[100px] resize-none"
              maxLength={280}
            />
            <div className="text-xs text-white/50 mt-1 text-right">
              {topic.length}/280
            </div>
          </div>

          <div className="mb-6">
            <label className="block text-sm font-bold text-white/80 mb-2">
              Challenge User
            </label>
            <select
              value={defenderId}
              onChange={(e) => setDefenderId(e.target.value)}
              className="w-full bg-white/10 border border-white/20 rounded-xl p-4 text-white
                         focus:outline-none focus:border-[#c9a35e] cursor-pointer"
            >
              <option value="" className="bg-[#1a3a52]">Select opponent...</option>
              {availableOpponents.map(user => (
                <option key={user.id} value={user.id} className="bg-[#1a3a52]">
                  {user.displayName} (@{user.username})
                </option>
              ))}
            </select>
          </div>

          {error && (
            <div className="mb-4 p-4 bg-red-500/20 border border-red-500/50 rounded-xl text-red-200">
              {error}
            </div>
          )}

          <div className="flex gap-3">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 px-6 py-3 bg-white/10 hover:bg-white/20 rounded-xl font-bold
                         transition-all duration-300"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isCreating || !topic.trim() || !defenderId}
              className="flex-1 px-6 py-3 bg-gradient-to-r from-[#c9a35e] to-[#a68847] 
                         hover:shadow-lg rounded-xl font-bold transition-all duration-300
                         disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isCreating ? 'Creating...' : '⚔️ Issue Challenge'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );

  return createPortal(modalContent, document.body);
}

export default CreateDebateModal;
