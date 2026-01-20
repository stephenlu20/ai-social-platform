import React from 'react';

function ArgumentThread({ debate, arguments: args }) {
  // Group arguments by round
  const rounds = [1, 2, 3];

  const getArgumentForRound = (round, role) => {
    // Match argument by round number and by comparing user ID with challenger/defender ID
    const targetUserId = role === 'CHALLENGER'
      ? debate.challenger?.id
      : debate.defender?.id;

    return args.find(
      (arg) => arg.roundNumber === round &&
               String(arg.user?.id) === String(targetUserId)
    );
  };

  // Determine whose turn it is based on whoseTurnId
  const isWaitingForChallenger = debate.whoseTurnId &&
    String(debate.whoseTurnId) === String(debate.challenger?.id);
  const isWaitingForDefender = debate.whoseTurnId &&
    String(debate.whoseTurnId) === String(debate.defender?.id);

  const formatTime = (timestamp) => {
    if (!timestamp) return '';
    const date = new Date(timestamp);
    return date.toLocaleString('en-US', {
      month: 'short',
      day: 'numeric',
      hour: 'numeric',
      minute: '2-digit',
    });
  };

  return (
    <div className="space-y-4">
      {rounds.map((round) => {
        const challengerArg = getArgumentForRound(round, 'CHALLENGER');
        const defenderArg = getArgumentForRound(round, 'DEFENDER');

        // Skip rounds that haven't started yet
        if (!challengerArg && !defenderArg && round > debate.currentRound) {
          return null;
        }

        return (
          <div key={round} className="border border-veritas-pink/20 rounded-xl overflow-hidden">
            <div className="bg-gradient-to-r from-veritas-purple/10 to-veritas-pink/10 px-4 py-2 border-b border-veritas-pink/20">
              <span className="text-sm font-bold text-white/70">Round {round}</span>
            </div>

            <div className="grid grid-cols-2 divide-x divide-veritas-pink/20">
              {/* Challenger Column */}
              <div className="p-4">
                <div className="text-xs font-bold text-veritas-coral mb-2 uppercase tracking-wider">
                  Challenger
                </div>
                {challengerArg ? (
                  <div>
                    <p className="text-white/90 text-sm leading-relaxed">
                      {challengerArg.content}
                    </p>
                    <p className="text-white/40 text-xs mt-2">
                      {formatTime(challengerArg.createdAt)}
                    </p>
                  </div>
                ) : (
                  debate.currentRound === round && isWaitingForChallenger ? (
                    <div className="text-white/30 text-sm italic">
                      Waiting for argument...
                    </div>
                  ) : (
                    <div className="text-white/20 text-sm">-</div>
                  )
                )}
              </div>

              {/* Defender Column */}
              <div className="p-4">
                <div className="text-xs font-bold text-veritas-coral mb-2 uppercase tracking-wider">
                  Defender
                </div>
                {defenderArg ? (
                  <div>
                    <p className="text-white/90 text-sm leading-relaxed">
                      {defenderArg.content}
                    </p>
                    <p className="text-white/40 text-xs mt-2">
                      {formatTime(defenderArg.createdAt)}
                    </p>
                  </div>
                ) : (
                  debate.currentRound === round && isWaitingForDefender ? (
                    <div className="text-white/30 text-sm italic">
                      Waiting for argument...
                    </div>
                  ) : challengerArg ? (
                    <div className="text-white/30 text-sm italic">
                      Waiting for argument...
                    </div>
                  ) : (
                    <div className="text-white/20 text-sm">-</div>
                  )
                )}
              </div>
            </div>
          </div>
        );
      })}

      {args.length === 0 && debate.status === 'ACTIVE' && (
        <div className="text-center py-8 text-white/40">
          No arguments yet. The debate is just getting started!
        </div>
      )}
    </div>
  );
}

export default ArgumentThread;
