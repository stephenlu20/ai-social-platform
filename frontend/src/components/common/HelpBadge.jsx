

import React from 'react';

function HelpBadge({ number, tooltip, bgColor }) {
  return (
    <span 
      className="inline-flex items-center justify-center w-5 h-5 rounded-full text-xs font-extrabold ml-1.5 cursor-help relative group"
      style={bgColor ? { background: bgColor } : { background: 'linear-gradient(135deg, #ff6b9d, #c44569)' }}
    >
      {number}
      <div className="absolute bottom-full left-1/2 -translate-x-1/2 -translate-y-[-5px] 
                      bg-black/95 border-2 border-veritas-pink/50 px-4 py-3 rounded-xl 
                      whitespace-nowrap text-[13px] font-semibold z-[1000] 
                      opacity-0 invisible group-hover:opacity-100 group-hover:visible 
                      group-hover:translate-y-0 transition-all duration-300 pointer-events-none 
                      shadow-[0_8px_24px_rgba(0,0,0,0.5)]
                      after:content-[''] after:absolute after:top-full after:left-1/2 
                      after:-translate-x-1/2 after:border-8 after:border-transparent 
                      after:border-t-veritas-pink/50">
        {tooltip}
      </div>
    </span>
  );
}

export default HelpBadge;
