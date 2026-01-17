

import React from 'react';


function RightSidebar() {
  return (
    <div className="py-[30px]">
      {/* Search Box */}
      <div className="bg-white/8 backdrop-blur-[10px] border border-white/10 rounded-2xl 
                      px-[18px] py-3.5 flex items-center gap-3 mb-5 sticky top-[30px] 
                      transition-all duration-300
                      focus-within:bg-white/12 focus-within:border-veritas-pink 
                      focus-within:shadow-[0_0_20px_rgba(255,107,157,0.2)]">
        <div className="text-white/50 text-xl">🔍</div>
        <input 
          type="text" 
          placeholder="Search posts, people, hashtags..." 
          className="bg-transparent border-none outline-none text-white w-full 
                     font-['Plus_Jakarta_Sans'] text-[15px] placeholder:text-white/40"
        />
      </div>
    </div>
  );
}

export default RightSidebar;
