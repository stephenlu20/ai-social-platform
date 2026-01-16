
import React from 'react';
import { useUser } from '../../context/UserContext';
import debateService from '../../services/debateService';

function DebateCard({ debate, onDebateUpdated }) {
  const { currentUser } = useUser();

  const getStatusBadge = (status) => {
    const badges = {
      PENDING: { bg: 'bg-yellow-500/20', border: 'border-yellow-500/50', text: 'text-yellow-300', label: '⏳ Pending' },
      ACTIVE: { bg: 'bg-red-500/20', border: 'border-red-500/50', text: 'text-red-300', label: '⚔️ Active' },
      VOTING: { bg: 'bg-blue-500/20', border: 'border-blue-500/50', text: 'text-blue-300', label: '🗳️ Voting' },
      COMPLETED: { bg: 'bg-green-500/20', border: 'border-green-500/50', text: 'text-green-300', label: '✅ Completed' }
    };
    const badge = badges[status] || badges.PENDING;
    
    return (
      <span className={`px-3 py-1 rounded-lg text-xs font-bold ${badge.bg} border ${badge.border} ${badge.text}`}>
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

  const isDefender = currentUser?.id === debate.defender?.id;
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
          <span className="text-2xl">⚔️</span>
          <span className="font-bold text-lg">Debate Challenge</span>
        </div>
        {getStatusBadge(debate.status)}
      </div>

      <div className="mb-4">
        <div className="text-white/90 font-semibold mb-2">Topic:</div>
        <div className="text-white/80 leading-relaxed">{debate.topic}</div>
      </div>

      <div className="grid grid-cols-2 gap-4 mb-4">
        <div className="bg-blue-500/10 border border-blue-500/30 rounded-xl p-3">
          <div className="text-xs text-blue-300 font-bold mb-1">CHALLENGER</div>
          <div className="font-bold">{debate.challenger?.displayName}</div>
          <div className="text-sm text-white/50">@{debate.challenger?.username}</div>
        </div>
        <div className="bg-red-500/10 border border-red-500/30 rounded-xl p-3">
          <div className="text-xs text-red-300 font-bold mb-1">DEFENDER</div>
          <div className="font-bold">{debate.defender?.displayName}</div>
          <div className="text-sm text-white/50">@{debate.defender?.username}</div>
        </div>
      </div>

      {debate.status === 'ACTIVE' && (
        <div className="mb-4 p-3 bg-white/5 rounded-xl">
          <div className="text-sm">
            <span className="text-white/60">Round:</span>{' '}
            <span className="font-bold text-[#c9a35e]">{debate.currentRound}/3</span>
          </div>
        </div>
      )}

      {debate.status === 'VOTING' && (
        <div className="mb-4 space-y-2">
          <div className="text-sm font-bold text-white/80 mb-2">Current Votes:</div>
          
          <div className="relative">
            <div className="flex justify-between text-xs mb-1">
              <span>Challenger</span>
              <span>{percentages.challenger}%</span>
            </div>
            <div className="h-2 bg-white/10 rounded-full overflow-hidden">
              <div 
                className="h-full bg-blue-500 transition-all duration-500"
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
                className="h-full bg-red-500 transition-all duration-500"
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
                className="h-full bg-gray-500 transition-all duration-500"
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
        <div className="mb-4 p-3 bg-[#c9a35e]/20 border border-[#c9a35e]/50 rounded-xl">
          <div className="text-center">
            <div className="text-2xl mb-1">🏆</div>
            <div className="font-bold text-[#c9a35e]">
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
                       transition-all duration-300"
          >
            ✓ Accept Challenge
          </button>
          <button
            onClick={handleDecline}
            className="flex-1 px-4 py-2 bg-red-500/20 hover:bg-red-500/30 
                       border border-red-500/50 text-red-300 rounded-xl font-bold
                       transition-all duration-300"
          >
            ✗ Decline
          </button>
        </div>
      )}

      <button className="mt-3 w-full px-4 py-2 bg-white/5 hover:bg-white/10 rounded-xl
                         text-sm font-semibold transition-all duration-300">
        View Full Debate →
      </button>
    </div>
  );
}

export default DebateCard;