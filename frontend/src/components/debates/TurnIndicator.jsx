import React from 'react';
import { Clock } from 'lucide-react';

function TurnIndicator({ debate, currentUserId }) {
  if (debate.status !== 'ACTIVE') {
    return null;
  }

  // Use String() to handle potential type mismatches
  const isChallenger = currentUserId && debate.challenger?.id &&
    String(currentUserId) === String(debate.challenger.id);
  const isDefender = currentUserId && debate.defender?.id &&
    String(currentUserId) === String(debate.defender.id);
  const isParticipant = isChallenger || isDefender;

  // Determine whose turn it is based on whoseTurnId
  const whoseTurnIsChallenger = debate.whoseTurnId &&
    String(debate.whoseTurnId) === String(debate.challenger?.id);
  const whoseTurnName = whoseTurnIsChallenger
    ? debate.challenger?.displayName
    : debate.defender?.displayName;

  if (!isParticipant) {
    return (
      <div className="px-4 py-3 bg-white/5 border border-white/10 rounded-xl mb-4">
        <div className="flex items-center gap-2 text-white/70">
          <Clock className="w-4 h-4" />
          <span className="text-sm">
            Waiting for{' '}
            <span className="font-semibold">{whoseTurnName}</span>
            ...
          </span>
        </div>
      </div>
    );
  }

  // Check if it's my turn using whoseTurnId
  const isMyTurn = debate.whoseTurnId &&
    String(currentUserId) === String(debate.whoseTurnId);

  if (isMyTurn) {
    return (
      <div className="px-4 py-3 bg-gradient-to-br from-veritas-purple/20 to-veritas-pink/20 border border-veritas-pink/30 rounded-xl mb-4">
        <div className="flex items-center gap-2">
          <span className="text-2xl">⚔️</span>
          <span className="text-veritas-coral font-bold">Your Turn!</span>
          <span className="text-white/60 text-sm ml-2">
            Round {debate.currentRound} of 3
          </span>
        </div>
      </div>
    );
  }

  return (
    <div className="px-4 py-3 bg-white/5 border border-white/10 rounded-xl mb-4">
      <div className="flex items-center gap-2 text-white/70">
        <Clock className="w-4 h-4" />
        <span className="text-sm">
          Waiting for <span className="font-semibold">{whoseTurnName}</span>...
        </span>
        <span className="text-white/40 text-sm ml-2">
          Round {debate.currentRound} of 3
        </span>
      </div>
    </div>
  );
}

export default TurnIndicator;
