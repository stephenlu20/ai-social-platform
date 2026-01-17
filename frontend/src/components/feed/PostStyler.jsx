import React from 'react';

// Style options matching backend PostStyleDTO
const FONT_OPTIONS = [
  { id: 'default', label: 'Default', preview: 'Aa', fontClass: "font-['Plus_Jakarta_Sans']" },
  { id: 'serif', label: 'Serif', preview: 'Aa', fontClass: "font-['Georgia',serif]" },
  { id: 'mono', label: 'Mono', preview: 'Aa', fontClass: "font-['Fira_Code',monospace]" },
  { id: 'handwritten', label: 'Script', preview: 'Aa', fontClass: "font-['Caveat',cursive]" },
  { id: 'bold', label: 'Bold', preview: 'Aa', fontClass: "font-['Plus_Jakarta_Sans'] font-black" },
  { id: 'condensed', label: 'Narrow', preview: 'Aa', fontClass: "font-['Roboto_Condensed',sans-serif]" },
];

const COLOR_OPTIONS = [
  { id: 'default', label: 'White', colorClass: 'text-white/90', hex: '#e5e5e5' },
  { id: 'pink', label: 'Pink', colorClass: 'text-pink-400', hex: '#f472b6' },
  { id: 'purple', label: 'Purple', colorClass: 'text-purple-400', hex: '#c084fc' },
  { id: 'blue', label: 'Blue', colorClass: 'text-blue-400', hex: '#60a5fa' },
  { id: 'green', label: 'Green', colorClass: 'text-green-400', hex: '#4ade80' },
  { id: 'orange', label: 'Orange', colorClass: 'text-orange-400', hex: '#fb923c' },
];

const BACKGROUND_OPTIONS = [
  { id: 'none', label: 'None', gradient: 'transparent' },
  { id: 'pink-purple', label: 'Pink-Purple', gradient: 'linear-gradient(135deg, rgba(236,72,153,0.3) 0%, rgba(139,92,246,0.3) 100%)' },
  { id: 'blue-purple', label: 'Blue-Purple', gradient: 'linear-gradient(135deg, rgba(59,130,246,0.3) 0%, rgba(139,92,246,0.3) 100%)' },
  { id: 'green-blue', label: 'Green-Blue', gradient: 'linear-gradient(135deg, rgba(34,197,94,0.3) 0%, rgba(59,130,246,0.3) 100%)' },
  { id: 'orange-pink', label: 'Orange-Pink', gradient: 'linear-gradient(135deg, rgba(249,115,22,0.3) 0%, rgba(236,72,153,0.3) 100%)' },
  { id: 'dark', label: 'Dark', gradient: 'linear-gradient(135deg, rgba(30,30,50,0.8) 0%, rgba(50,30,60,0.8) 100%)' },
  { id: 'sunset', label: 'Sunset', gradient: 'linear-gradient(135deg, rgba(249,115,22,0.3) 0%, rgba(236,72,153,0.3) 50%, rgba(139,92,246,0.3) 100%)' },
];

const SIZE_OPTIONS = [
  { id: 'small', label: 'Small', sizeClass: 'text-sm', preview: 'A' },
  { id: 'default', label: 'Normal', sizeClass: 'text-[15px]', preview: 'A' },
  { id: 'large', label: 'Large', sizeClass: 'text-xl', preview: 'A' },
  { id: 'xlarge', label: 'X-Large', sizeClass: 'text-2xl', preview: 'A' },
];

// Export for use in Tweet component
export { FONT_OPTIONS, COLOR_OPTIONS, BACKGROUND_OPTIONS, SIZE_OPTIONS };

// Helper to get style classes
export function getStyleClasses(style) {
  if (!style) return { fontClass: '', colorClass: '', sizeClass: '', backgroundStyle: {} };

  const font = FONT_OPTIONS.find(f => f.id === style.font) || FONT_OPTIONS[0];
  const color = COLOR_OPTIONS.find(c => c.id === style.textColor) || COLOR_OPTIONS[0];
  const bg = BACKGROUND_OPTIONS.find(b => b.id === style.background) || BACKGROUND_OPTIONS[0];
  const size = SIZE_OPTIONS.find(s => s.id === style.size) || SIZE_OPTIONS.find(s => s.id === 'default');

  return {
    fontClass: font.fontClass,
    colorClass: color.colorClass,
    sizeClass: size.sizeClass,
    backgroundStyle: bg.gradient !== 'transparent' ? { background: bg.gradient } : {},
  };
}

function PostStyler({ style, onStyleChange, onClose }) {
  const handleFontChange = (fontId) => {
    onStyleChange({ ...style, font: fontId });
  };

  const handleColorChange = (colorId) => {
    onStyleChange({ ...style, textColor: colorId });
  };

  const handleBackgroundChange = (bgId) => {
    onStyleChange({ ...style, background: bgId });
  };

  const handleSizeChange = (sizeId) => {
    onStyleChange({ ...style, size: sizeId });
  };

  return (
    <div className="mb-4 p-4 bg-gradient-to-br from-veritas-purple/20 to-veritas-pink/10
                    border border-veritas-purple/30 rounded-xl">
      {/* Header */}
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-2">
          <span className="text-lg">🎨</span>
          <span className="font-bold text-white text-sm">Style Your Post</span>
        </div>
        <button
          onClick={onClose}
          className="w-6 h-6 rounded-full bg-white/10 hover:bg-white/20
                     flex items-center justify-center transition-colors text-xs text-white/70"
        >
          ✕
        </button>
      </div>

      {/* Font Picker (#71) */}
      <div className="mb-4">
        <p className="text-white/50 text-xs font-semibold mb-2 uppercase tracking-wider">Font</p>
        <div className="flex flex-wrap gap-2">
          {FONT_OPTIONS.map((font) => (
            <button
              key={font.id}
              onClick={() => handleFontChange(font.id)}
              className={`px-3 py-2 rounded-lg text-sm transition-all
                         ${style.font === font.id
                           ? 'bg-veritas-purple/50 border-veritas-purple text-white'
                           : 'bg-white/5 border-white/10 text-white/70 hover:bg-white/10'}
                         border ${font.fontClass}`}
            >
              <span className="text-base">{font.preview}</span>
              <span className="ml-1 text-xs opacity-70">{font.label}</span>
            </button>
          ))}
        </div>
      </div>

      {/* Size Picker */}
      <div className="mb-4">
        <p className="text-white/50 text-xs font-semibold mb-2 uppercase tracking-wider">Size</p>
        <div className="flex flex-wrap gap-2">
          {SIZE_OPTIONS.map((size) => (
            <button
              key={size.id}
              onClick={() => handleSizeChange(size.id)}
              className={`px-3 py-2 rounded-lg transition-all flex items-center gap-1
                         ${style.size === size.id || (!style.size && size.id === 'default')
                           ? 'bg-veritas-purple/50 border-veritas-purple text-white'
                           : 'bg-white/5 border-white/10 text-white/70 hover:bg-white/10'}
                         border`}
            >
              <span className={size.sizeClass}>{size.preview}</span>
              <span className="ml-1 text-xs opacity-70">{size.label}</span>
            </button>
          ))}
        </div>
      </div>

      {/* Color Picker (#72) */}
      <div className="mb-4">
        <p className="text-white/50 text-xs font-semibold mb-2 uppercase tracking-wider">Text Color</p>
        <div className="flex flex-wrap gap-2">
          {COLOR_OPTIONS.map((color) => (
            <button
              key={color.id}
              onClick={() => handleColorChange(color.id)}
              className={`w-10 h-10 rounded-lg transition-all flex items-center justify-center
                         ${style.textColor === color.id
                           ? 'ring-2 ring-veritas-purple ring-offset-2 ring-offset-[#1a1a2e]'
                           : 'hover:scale-110'}
                         border border-white/20`}
              style={{ backgroundColor: color.hex + '33' }}
              title={color.label}
            >
              <span style={{ color: color.hex }} className="text-lg font-bold">A</span>
            </button>
          ))}
        </div>
      </div>

      {/* Background Picker (#73) */}
      <div>
        <p className="text-white/50 text-xs font-semibold mb-2 uppercase tracking-wider">Background</p>
        <div className="flex flex-wrap gap-2">
          {BACKGROUND_OPTIONS.map((bg) => (
            <button
              key={bg.id}
              onClick={() => handleBackgroundChange(bg.id)}
              className={`w-12 h-8 rounded-lg transition-all
                         ${style.background === bg.id
                           ? 'ring-2 ring-veritas-purple ring-offset-2 ring-offset-[#1a1a2e]'
                           : 'hover:scale-110'}
                         border border-white/20`}
              style={{ background: bg.gradient === 'transparent' ? '#1a1a2e' : bg.gradient }}
              title={bg.label}
            >
              {bg.id === 'none' && <span className="text-white/30 text-xs">None</span>}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}

export default PostStyler;
