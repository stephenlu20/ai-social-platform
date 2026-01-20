
import React, { useState } from 'react';
import { useUser } from '../../context/UserContext';
import debateService from '../../services/debateService';
import { Clock, Swords, Vote, CheckCircle, Trophy } from 'lucide-react';
import DebateDetailModal from './DebateDetailModal';

function DebateCard({ debate, onDebateUpdated }) {
  const { currentUser } = useUser();
  const [showDetailModal, setShowDetailModal] = useState(false);

  const getStatusBadge = (status) => {
    const badges = {
      PENDING: {
        icon: <Clock className="w-3 h-3" />,
        bg: 'bg-yellow-500/20',
        border: 'border-yellow-500/50',
        text: 'text-yellow-300',
        label: 'Pending'
      },
      ACTIVE: {
        icon: <Swords className="w-3 h-3" />,
        bg: 'bg-veritas-pink/20',
        border: 'border-veritas-pink/30',
        text: 'text-veritas-coral',
        label: 'Active'
      },
      VOTING: {
        icon: <Vote className="w-3 h-3" />,
        bg: 'bg-veritas-purple/20',
        border: 'border-veritas-purple/30',
        text: 'text-veritas-purple-light',
        label: 'Voting'
      },
      COMPLETED: {
        icon: <CheckCircle className="w-3 h-3" />,
        bg: 'bg-green-500/20',
        border: 'border-green-500/30',
        text: 'text-green-300',
        label: 'Completed'
      }
    };
    const badge = badges[status] || badges.PENDING;
    
    return (
      <span className={`px-3 py-1 rounded-lg text-xs font-bold ${badge.bg} border ${badge.border} ${badge.text} flex items-center gap-1.5 w-fit`}>
        {badge.icon}
        {badge.label}
      </span>
    );
  };

  const handleAccept = async () => {
    try {
      await debateService.acceptChallenge(debate.id, currentUser.id);
      if (onDebateUpdated) onDebateUpdated();
    } catch (error) {
      console.error('Error accepting debate:', error);
      alert('Failed to accept debate');
    }
  };

  const handleDecline = async () => {
    try {
      await debateService.declineChallenge(debate.id, currentUser.id);
      if (onDebateUpdated) onDebateUpdated();
    } catch (error) {
      console.error('Error declining debate:', error);
      alert('Failed to decline debate');
    }
  };

  // Use String() to handle potential type mismatches (string vs number/UUID)
  const isDefender = currentUser?.id && debate.defender?.id &&
    String(currentUser.id) === String(debate.defender.id);
  const isPending = debate.status === 'PENDING';
  const canRespond = isPending && isDefender;

  const getVotePercentages = () => {
    if (!debate.totalVotes || debate.totalVotes === 0) return { challenger: 0, defender: 0, tie: 0 };
    return {
      challenger: Math.round((debate.votesChallenger / debate.totalVotes) * 100),
      defender: Math.round((debate.votesDefender / debate.totalVotes) * 100),
      tie: Math.round((debate.votesTie / debate.totalVotes) * 100)
    };
  };

  const percentages = getVotePercentages();

  return (
    <div className="border-b border-white/10 p-5 transition-all duration-300 hover:bg-white/5">
      <div className="flex items-start justify-between mb-3">
        <div className="flex items-center gap-2">
          <Swords className="w-6 h-6" />
          <span className="font-bold text-lg">Debate Challenge</span>
        </div>
        {getStatusBadge(debate.status)}
      </div>

      <div className="mb-4">
        <div className="text-xs text-veritas-coral font-bold mb-2 uppercase tracking-wider">Topic</div>
        <div className="text-white/90 leading-relaxed">{debate.topic}</div>
      </div>

      <div className="grid grid-cols-2 gap-4 mb-4">
        <div className="bg-gradient-to-br from-veritas-purple/20 to-veritas-pink/10 border border-veritas-pink/30 rounded-xl p-3">
          <div className="text-xs text-veritas-coral font-bold mb-1 uppercase tracking-wider">Challenger</div>
          <div className="font-bold">{debate.challenger?.displayName}</div>
          <div className="text-sm text-white/50">@{debate.challenger?.username}</div>
        </div>
        <div className="bg-gradient-to-br from-veritas-purple/20 to-veritas-pink/10 border border-veritas-pink/30 rounded-xl p-3">
          <div className="text-xs text-veritas-coral font-bold mb-1 uppercase tracking-wider">Defender</div>
          <div className="font-bold">{debate.defender?.displayName}</div>
          <div className="text-sm text-white/50">@{debate.defender?.username}</div>
        </div>
      </div>

      {debate.status === 'ACTIVE' && (
        <div className="mb-4 p-3 bg-gradient-to-br from-veritas-purple/10 to-veritas-pink/10 border border-veritas-pink/20 rounded-xl">
          <div className="text-sm">
            <span className="text-white/60">Round:</span>{' '}
            <span className="font-bold text-veritas-coral">{debate.currentRound}/3</span>
          </div>
        </div>
      )}

      {debate.status === 'VOTING' && (
        <div className="mb-4 space-y-2">
          <div className="text-sm font-bold text-veritas-coral mb-2">Current Votes:</div>

          <div className="relative">
            <div className="flex justify-between text-xs mb-1">
              <span>Challenger</span>
              <span>{percentages.challenger}%</span>
            </div>
            <div className="h-2 bg-white/10 rounded-full overflow-hidden">
              <div
                className="h-full bg-gradient-to-r from-veritas-pink to-veritas-coral transition-all duration-500"
                style={{ width: `${percentages.challenger}%` }}
              />
            </div>
          </div>

          <div className="relative">
            <div className="flex justify-between text-xs mb-1">
              <span>Defender</span>
              <span>{percentages.defender}%</span>
            </div>
            <div className="h-2 bg-white/10 rounded-full overflow-hidden">
              <div
                className="h-full bg-gradient-to-r from-veritas-purple to-veritas-pink transition-all duration-500"
                style={{ width: `${percentages.defender}%` }}
              />
            </div>
          </div>

          <div className="relative">
            <div className="flex justify-between text-xs mb-1">
              <span>Tie</span>
              <span>{percentages.tie}%</span>
            </div>
            <div className="h-2 bg-white/10 rounded-full overflow-hidden">
              <div
                className="h-full bg-white/30 transition-all duration-500"
                style={{ width: `${percentages.tie}%` }}
              />
            </div>
          </div>

          <div className="text-xs text-white/50 text-center mt-2">
            {debate.totalVotes} total votes
          </div>
        </div>
      )}

      {debate.status === 'COMPLETED' && debate.winnerId && (
        <div className="mb-4 p-3 bg-gradient-to-br from-veritas-purple/20 to-veritas-pink/20 border border-veritas-pink/30 rounded-xl">
          <div className="text-center">
            <Trophy className="w-8 h-8 mx-auto mb-2 text-veritas-coral" />
            <div className="font-bold text-veritas-coral">
              Winner: {debate.winnerId === debate.challenger?.id
                ? debate.challenger?.displayName
                : debate.defender?.displayName}
            </div>
          </div>
        </div>
      )}

      {canRespond && (
        <div className="flex gap-2">
          <button
            onClick={handleAccept}
            className="flex-1 px-4 py-2 bg-green-500/20 hover:bg-green-500/30 
                       border border-green-500/50 text-green-300 rounded-xl font-bold
                       transition-all duration-300 flex items-center justify-center gap-2"
          >
            <Check className="w-4 h-4" />
            Accept Challenge
          </button>
          <button
            onClick={handleDecline}
            className="flex-1 px-4 py-2 bg-red-500/20 hover:bg-red-500/30 
                       border border-red-500/50 text-red-300 rounded-xl font-bold
                       transition-all duration-300 flex items-center justify-center gap-2"
          >
            <X className="w-4 h-4" />
            Decline
          </button>
        </div>
      )}

      <button
        onClick={() => setShowDetailModal(true)}
        className="mt-3 w-full px-4 py-2 bg-white/5 hover:bg-white/10 rounded-xl
                   text-sm font-semibold transition-all duration-300"
      >
        View Full Debate
      </button>

      <DebateDetailModal
        isOpen={showDetailModal}
        onClose={() => setShowDetailModal(false)}
        debate={debate}
        currentUserId={currentUser?.id}
        onDebateUpdated={onDebateUpdated}
      />
    </div>
  );
}

export default DebateCard;