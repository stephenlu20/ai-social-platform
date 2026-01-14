

import React from 'react';

function Tweet(props) {
  const {
    id,
    author,
    handle,
    avatar,
    time,
    verified,
    trustScore,
    content,
    type,
    poll,
    debate,
    factCheck,
    likes,
    retweets,
    replies
  } = props;

  return (
    <div className="border-b border-white/[0.08] p-5 flex gap-3.5 cursor-pointer 
                    transition-all duration-300 hover:bg-veritas-pink/5">
      <div className="text-4xl flex-shrink-0 relative">{avatar}</div>
      <div className="flex-1">
        {/* Header */}
        <div className="flex items-center gap-2 mb-2 flex-wrap">
          <span className="font-bold text-[15px]">{author}</span>
          {verified && (
            <span className="w-[18px] h-[18px] bg-gradient-to-br from-veritas-pink to-veritas-coral 
                           rounded-full inline-flex items-center justify-center text-[10px]">
              ✓
            </span>
          )}
          <span className="text-white/50 text-sm">{handle}</span>
          <span className="text-white/50 text-sm">· {time}</span>
          {type === 'poll' && (
            <span className="px-2.5 py-1 rounded-lg text-[11px] font-bold inline-flex items-center gap-1 
                           uppercase tracking-wide bg-gradient-to-br from-blue-600/30 to-blue-700/30 
                           border border-blue-600/40 text-blue-300">
              📊 POLL
            </span>
          )}
          {type === 'debate' && (
            <span className="px-2.5 py-1 rounded-lg text-[11px] font-bold inline-flex items-center gap-1 
                           uppercase tracking-wide bg-gradient-to-br from-red-600/30 to-red-700/30 
                           border border-red-600/40 text-red-300">
              ⚔️ DEBATE
            </span>
          )}
          <div className="bg-gradient-to-br from-[#10b981] to-[#059669] text-white 
                          px-2.5 py-1 rounded-lg text-xs font-bold flex items-center gap-1 ml-auto">
            <span>🛡️</span>
            <span>{trustScore}</span>
          </div>
        </div>

        {/* Content */}
        <div className="text-white/90 leading-relaxed my-2 mb-4 whitespace-pre-line text-[15px]">
          {content}
        </div>

        {/* Poll */}
        {type === 'poll' && poll && (
          <div className="my-4 p-4 bg-white/5 rounded-2xl border border-white/10">
            {poll.options.map((option, index) => (
              <div key={index} className="p-3.5 mb-2.5 last:mb-0 bg-white/5 rounded-xl 
                                          cursor-pointer transition-all duration-300 relative overflow-hidden
                                          hover:bg-veritas-pink/10">
                <div className="absolute top-0 left-0 h-full bg-gradient-to-r from-veritas-pink/30 to-veritas-purple/30 
                                rounded-xl transition-all duration-500"
                     style={{ width: `${option.percentage}%` }}></div>
                <div className="relative z-[1] flex justify-between font-semibold">
                  <span>{option.text}</span>
                  <span>{option.percentage}%</span>
                </div>
              </div>
            ))}
            <div className="text-[13px] text-white/50 mt-3">
              {poll.totalVotes} votes · {poll.timeLeft}
            </div>
          </div>
        )}

        {/* Debate */}
        {type === 'debate' && debate && (
          <div className="my-4 p-4 bg-gradient-to-br from-red-600/10 to-red-700/10 
                          border-2 border-red-600/30 rounded-2xl">
            <div className="flex items-center gap-2.5 mb-3 font-bold text-red-300">
              <span>⚔️</span>
              <span>JOIN THE DEBATE - Pick Your Side!</span>
            </div>
            <div className="flex gap-3 mt-3">
              <div className="flex-1 p-2.5 rounded-xl font-bold text-center cursor-pointer 
                              transition-all duration-300 bg-green-600/20 border-2 border-green-600/40 
                              text-green-300 hover:-translate-y-0.5">
                <div>👍 I AGREE</div>
                <div className="text-xs mt-1">{debate.agree} people</div>
              </div>
              <div className="flex-1 p-2.5 rounded-xl font-bold text-center cursor-pointer 
                              transition-all duration-300 bg-red-600/20 border-2 border-red-600/40 
                              text-red-300 hover:-translate-y-0.5">
                <div>👎 I DISAGREE</div>
                <div className="text-xs mt-1">{debate.disagree} people</div>
              </div>
            </div>
          </div>
        )}

        {/* Fact Check */}
        {type === 'fact-checked' && factCheck && factCheck.status === 'verified' && (
          <div className="my-4 p-3.5 bg-green-600/10 border-2 border-green-600/40 rounded-[14px]">
            <div className="flex items-center gap-2.5 mb-2 font-bold text-green-300 text-sm">
              <span className="text-xl">✅</span>
              <span>AI FACT CHECK: VERIFIED</span>
            </div>
            <div className="text-[13px] text-white/80 leading-normal">
              ✓ Source verified: {factCheck.sources.join(', ')}<br/>
              ✓ Study confirmed: Published {factCheck.date}<br/>
              <button className="mt-2 px-3 py-1.5 bg-green-600/20 border border-green-600/40 
                                 rounded-lg text-green-300 font-semibold cursor-pointer text-xs 
                                 transition-all duration-300 hover:bg-green-600/30">
                View Sources
              </button>
            </div>
          </div>
        )}

        {/* Actions */}
        <div className="flex justify-between max-w-[500px] text-white/50 mt-3">
          <button className="flex items-center gap-2 cursor-pointer transition-all duration-300 
                             p-1.5 rounded-[10px] relative bg-transparent border-none 
                             text-inherit text-[13px] font-semibold
                             hover:text-veritas-pink hover:bg-veritas-pink/10">
            <span className="text-lg">💬</span>
            <span>{replies}</span>
          </button>
          <button className="flex items-center gap-2 cursor-pointer transition-all duration-300 
                             p-1.5 rounded-[10px] relative bg-transparent border-none 
                             text-inherit text-[13px] font-semibold
                             hover:text-veritas-pink hover:bg-veritas-pink/10">
            <span className="text-lg">🔁</span>
            <span>{retweets}</span>
          </button>
          <button className="flex items-center gap-2 cursor-pointer transition-all duration-300 
                             p-1.5 rounded-[10px] relative bg-transparent border-none 
                             text-inherit text-[13px] font-semibold
                             hover:text-veritas-pink hover:bg-veritas-pink/10">
            <span className="text-lg">❤️</span>
            <span>{likes}</span>
          </button>
          <button className="flex items-center gap-2 cursor-pointer transition-all duration-300 
                             p-1.5 rounded-[10px] relative bg-transparent border-none 
                             text-inherit text-[13px] font-semibold
                             hover:text-veritas-pink hover:bg-veritas-pink/10">
            <span className="text-lg">🔖</span>
          </button>
          <button className="flex items-center gap-2 cursor-pointer transition-all duration-300 
                             p-1.5 rounded-[10px] relative bg-transparent border-none 
                             text-inherit text-[13px] font-semibold
                             hover:text-veritas-pink hover:bg-veritas-pink/10">
            <span className="text-lg">🔗</span>
          </button>
        </div>
      </div>
    </div>
  );
}

export default Tweet;