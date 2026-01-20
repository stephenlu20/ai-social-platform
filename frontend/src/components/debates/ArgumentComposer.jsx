import React, { useState } from 'react';
import { Send } from 'lucide-react';
import debateService from '../../services/debateService';

function ArgumentComposer({ debate, currentUserId, onArgumentSubmitted }) {
  const [content, setContent] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState('');

  const maxChars = 1000;

  // Use String() to handle potential type mismatches (string vs UUID)
  const isChallenger = currentUserId && debate.challenger?.id &&
    String(currentUserId) === String(debate.challenger.id);
  const isDefender = currentUserId && debate.defender?.id &&
    String(currentUserId) === String(debate.defender.id);
  const isParticipant = isChallenger || isDefender;

  // Check if it's the current user's turn using whoseTurnId from backend
  const isMyTurn =
    debate.status === 'ACTIVE' &&
    debate.whoseTurnId &&
    String(currentUserId) === String(debate.whoseTurnId);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!content.trim() || !isMyTurn) return;

    try {
      setIsSubmitting(true);
      setError('');

      await debateService.submitArgument(debate.id, currentUserId, content.trim());

      setContent('');
      if (onArgumentSubmitted) {
        onArgumentSubmitted();
      }
    } catch (err) {
      console.error('Error submitting argument:', err);
      setError('Failed to submit argument. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  if (!isParticipant || debate.status !== 'ACTIVE') {
    return null;
  }

  const getCharCountClass = () => {
    const remaining = maxChars - content.length;
    if (remaining < 50) return 'text-red-500';
    if (remaining < 100) return 'text-yellow-400';
    return 'text-white/50';
  };

  return (
    <div className="mt-4 pt-4 border-t border-veritas-pink/20">
      <form onSubmit={handleSubmit}>
        <label className="block text-sm font-bold text-veritas-coral mb-2">
          {isMyTurn ? 'Your Argument' : 'Waiting for opponent...'}
        </label>

        <textarea
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder={
            isMyTurn
              ? 'Make your argument... Be persuasive!'
              : "It's not your turn yet"
          }
          disabled={!isMyTurn || isSubmitting}
          maxLength={maxChars}
          className="w-full bg-white/10 border border-veritas-pink/30 rounded-xl p-4 text-white
                     placeholder:text-white/30 focus:outline-none focus:border-veritas-pink focus:bg-white/15
                     min-h-[120px] resize-none transition-all
                     disabled:opacity-50 disabled:cursor-not-allowed"
        />

        <div className="flex items-center justify-between mt-2">
          <span className={`text-xs ${getCharCountClass()}`}>
            {content.length}/{maxChars}
          </span>

          <button
            type="submit"
            disabled={!isMyTurn || !content.trim() || isSubmitting}
            className="px-6 py-2 bg-gradient-to-r from-veritas-pink to-veritas-pink-dark
                       hover:shadow-lg hover:shadow-veritas-pink/25 rounded-xl font-bold text-sm flex items-center gap-2
                       transition-all duration-300
                       disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <Send className="w-4 h-4" />
            {isSubmitting ? 'Submitting...' : 'Submit Argument'}
          </button>
        </div>

        {error && (
          <div className="mt-2 p-3 bg-red-500/20 border border-red-500/50 rounded-xl text-red-200 text-sm">
            {error}
          </div>
        )}
      </form>
    </div>
  );
}

export default ArgumentComposer;
