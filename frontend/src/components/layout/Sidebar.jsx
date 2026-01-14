

import React from 'react';
import HelpBadge from '../common/HelpBadge';

function Sidebar() {
  const navItems = [
    { icon: '🏠', label: 'Home', badge: null },
  { icon: '🌍', label: 'Explore', badge: null },
  { icon: '💬', label: 'Messages', badge: null },
  { icon: '👥', label: 'Groups', badge: null },
  { icon: '⭐', label: 'Bookmarks', badge: null },
  { icon: '👤', label: 'Profile', badge: null },
  { icon: '🤝', label: 'Friends', badge: null },
  ];

  return (
    <div className="py-[30px] h-screen sticky top-0">
      {/* Logo */}
      <div className="flex items-center gap-3 mx-5 mb-[50px]">
        <div className="w-12 h-12 bg-gradient-to-br from-veritas-pink via-veritas-pink-dark to-veritas-coral 
                        rounded-2xl flex items-center justify-center text-[28px] 
                        shadow-[0_8px_24px_rgba(255,107,157,0.3)] -rotate-[5deg]">
          🎭
        </div>
        <div className="text-[28px] font-extrabold bg-gradient-to-br from-veritas-pink to-veritas-coral 
                        bg-clip-text text-transparent [-webkit-background-clip:text] [-webkit-text-fill-color:transparent] 
                        tracking-tight">
          Chirp
        </div>
      </div>

      {/* Section Label */}
      <div className="bg-veritas-pink/10 border-l-4 border-veritas-pink px-3 py-2 mx-3 my-2.5 
                      text-xs font-bold text-veritas-coral uppercase tracking-wider">
        📍 FEATURE #1 - USER PAGE
      </div>

      {/* Navigation */}
      <nav className="mb-5">
        {navItems.map((item, index) => (
          <div 
            key={index} 
            className="flex items-center gap-4 px-5 py-3.5 rounded-2xl mx-3 mb-2 
                       cursor-pointer text-[17px] font-semibold transition-all duration-300 
                       hover:bg-veritas-pink/15 hover:translate-x-1"
          >
            <div className="w-6 h-6 text-[22px]">{item.icon}</div>
            <div>{item.label}</div>
            {item.badge && (
              <HelpBadge 
                number={item.badge.number} 
                tooltip={item.badge.tooltip} 
              />
            )}
          </div>
        ))}

        {/* AI Studio */}
        <div className="flex items-center gap-4 px-5 py-3.5 rounded-2xl mx-3 mb-2 mt-5
                        cursor-pointer text-[17px] font-bold transition-all duration-300
                        bg-gradient-to-br from-veritas-purple/20 to-veritas-pink/20 
                        border-2 border-veritas-pink/30">
          <div className="w-6 h-6 text-[22px]">✨</div>
          <div>AI Studio</div>
        </div>
      </nav>

      {/* Info Banner */}
      <div className="bg-gradient-to-br from-veritas-pink/15 to-veritas-purple/15 
                      border-2 border-veritas-pink/30 rounded-2xl px-4 py-4 mx-3 my-2.5 
                      text-[13px] leading-relaxed">
        <strong className="text-veritas-coral block mb-2">💡 New User Tips:</strong>
        Hover over the numbered badges to learn what each feature does!
      </div>

      {/* Create Post Button */}
      <button className="bg-gradient-to-br from-veritas-pink to-veritas-pink-dark 
                         px-[18px] py-[18px] rounded-2xl text-center font-bold text-[17px] 
                         mx-3 my-[30px_12px_20px] cursor-pointer 
                         shadow-[0_8px_24px_rgba(255,107,157,0.4)] 
                         border-none text-white w-[calc(100%-24px)] 
                         transition-all duration-300 
                         hover:-translate-y-0.5 hover:shadow-[0_12px_32px_rgba(255,107,157,0.5)]">
        <span className="inline-flex items-center gap-1.5 px-2.5 py-1 
                         bg-gradient-to-br from-veritas-pink/20 to-veritas-purple/20 
                         border border-veritas-pink/40 rounded-lg 
                         text-[11px] font-bold uppercase tracking-wide text-veritas-coral mr-2">
          Feature #3
        </span>
        Create Post
      </button>

      {/* User Profile */}
      <div className="flex items-center gap-3 px-5 py-4 rounded-[20px] mx-3 mt-5 
                      bg-white/5 backdrop-blur-[10px] border border-white/10 
                      cursor-pointer transition-all duration-300 
                      hover:bg-white/10 hover:-translate-y-0.5">
        <div className="w-11 h-11 rounded-[14px] bg-gradient-to-br from-veritas-blue to-veritas-blue-dark 
                        flex items-center justify-center text-xl 
                        shadow-[0_4px_12px_rgba(102,126,234,0.3)]">
          🎨
        </div>
        <div className="flex-1">
          <div className="font-bold text-sm">Your Name</div>
          <div className="flex items-center gap-2 mt-1">
            <span className="text-[13px] text-white/50">@yourhandle</span>
            <div className="bg-gradient-to-br from-[#10b981] to-[#059669] text-white 
                            px-2.5 py-1 rounded-lg text-xs font-bold flex items-center gap-1">
              <span>🛡️</span>
              <span>92</span>
              <HelpBadge 
                number="12" 
                tooltip="Feature #12: Trust Score - Build reputation!" 
                bgColor="rgba(255,255,255,0.2)"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Sidebar;
