
import React, { useState } from 'react';
import { useUser } from '../../context/UserContext';
import postService from '../../services/postService';
import factCheckService from '../../services/factcheckService';
import postAssistantService from '../../services/postAssistantService';
import { getStyleClasses } from './PostStyler';
import { Sparkles, Loader2 } from 'lucide-react';
import { CheckCircle, Circle, AlertTriangle, XCircle, HelpCircle, Dot, Edit } from 'lucide-react';
import { Send } from 'lucide-react';

// Compact style options
const QUICK_FONTS = [
  { id: 'default', label: 'Aa', title: 'Default', fontClass: "font-['Plus_Jakarta_Sans']" },
  { id: 'serif', label: 'Se', title: 'Serif', fontClass: "font-serif" },
  { id: 'mono', label: 'Mo', title: 'Mono', fontClass: "font-mono" },
  { id: 'handwritten', label: 'Sc', title: 'Script', fontClass: "font-['Caveat',cursive]" },
  { id: 'bold', label: 'Bo', title: 'Bold', fontClass: "font-black" },
  { id: 'condensed', label: 'Na', title: 'Narrow', fontClass: "font-['Roboto_Condensed',sans-serif]" },
];

const QUICK_SIZES = [
  { id: 'small', label: 'S', title: 'Small' },
  { id: 'default', label: 'M', title: 'Normal' },
  { id: 'large', label: 'L', title: 'Large' },
  { id: 'xlarge', label: 'XL', title: 'X-Large' },
];

const QUICK_COLORS = [
  { id: 'default', hex: '#e5e5e5', label: 'White' },
  { id: 'pink', hex: '#f472b6', label: 'Pink' },
  { id: 'purple', hex: '#c084fc', label: 'Purple' },
  { id: 'blue', hex: '#60a5fa', label: 'Blue' },
  { id: 'green', hex: '#4ade80', label: 'Green' },
  { id: 'orange', hex: '#fb923c', label: 'Orange' },
];

const QUICK_BACKGROUNDS = [
  { id: 'none', gradient: 'transparent', label: 'None' },
  { id: 'pink-purple', gradient: 'linear-gradient(135deg, #ec4899 0%, #8b5cf6 100%)', label: 'Pink-Purple' },
  { id: 'blue-purple', gradient: 'linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%)', label: 'Blue-Purple' },
  { id: 'green-blue', gradient: 'linear-gradient(135deg, #22c55e 0%, #3b82f6 100%)', label: 'Green-Blue' },
  { id: 'orange-pink', gradient: 'linear-gradient(135deg, #f97316 0%, #ec4899 100%)', label: 'Orange-Pink' },
  { id: 'sunset', gradient: 'linear-gradient(135deg, #f97316 0%, #ec4899 50%, #8b5cf6 100%)', label: 'Sunset' },
];

// #145 - FactCheckLoading Component
function FactCheckLoading() {
  return (
    <div className="p-4 bg-blue-500/10 border border-blue-500/30 rounded-xl mb-4">
      <div className="flex items-center gap-3">
        <div className="animate-spin w-5 h-5 border-2 border-blue-400 border-t-transparent rounded-full"></div>
        <span className="text-blue-300 font-semibold">Analyzing your post for factual accuracy...</span>
      </div>
    </div>
  );
}

// #146 - FactCheckPreview Component
function FactCheckPreview({ result, onPostAnyway, onEdit, isPosting }) {
  if (!result) return null;

  const getVerdictColor = (verdict) => {
    switch (verdict?.toUpperCase()) {
      case 'VERIFIED': return 'text-green-400 bg-green-500/20 border-green-500/50';
      case 'LIKELY_TRUE': return 'text-emerald-400 bg-emerald-500/20 border-emerald-500/50';
      case 'DISPUTED': return 'text-yellow-400 bg-yellow-500/20 border-yellow-500/50';
      case 'FALSE': return 'text-red-400 bg-red-500/20 border-red-500/50';
      case 'UNVERIFIABLE': return 'text-gray-400 bg-gray-500/20 border-gray-500/50';
      default: return 'text-gray-400 bg-gray-500/20 border-gray-500/50';
    }
  };

  const getVerdictIcon = (verdict) => {
    switch (verdict?.toUpperCase()) {
      case 'VERIFIED': return <CheckCircle/>;
      case 'LIKELY_TRUE': return <Circle/>;
      case 'DISPUTED': return <AlertTriangle/>;
      case 'FALSE': return <XCircle/>;
      case 'UNVERIFIABLE': return <HelpCircle/>;
      default: return <Dot/>;
    }
  };

  return (
    <div className="p-4 bg-white/5 border border-white/20 rounded-xl mb-4">
      <div className="flex items-center justify-between mb-3">
        <span className="text-white/70 font-semibold text-sm">Fact Check Result</span>
        <span className={`px-3 py-1 rounded-lg text-sm font-bold border ${getVerdictColor(result.verdict)}`}>
          {getVerdictIcon(result.verdict)} {result.verdict}
        </span>
      </div>

      {result.confidence && (
        <div className="mb-3">
          <div className="flex justify-between text-xs text-white/50 mb-1">
            <span>Confidence</span>
            <span>{result.confidence}%</span>
          </div>
          <div className="h-2 bg-white/10 rounded-full overflow-hidden">
            <div
              className="h-full bg-gradient-to-r from-veritas-pink to-veritas-purple rounded-full transition-all"
              style={{ width: `${result.confidence}%` }}
            ></div>
          </div>
        </div>
      )}

      {result.summary && (
        <p className="text-white/70 text-sm mb-4">{result.summary}</p>
      )}

      {/* #147 - Post Anyway and Edit Options */}
      <div className="flex gap-2 pt-3 border-t border-white/10">
        <button
          onClick={onEdit}
          disabled={isPosting}
          className="flex-1 px-4 py-2 rounded-lg font-semibold text-sm
                     bg-white/10 text-white/70 border border-white/20
                     hover:bg-white/20 transition-all
                     disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <Edit/> Edit Post
        </button>
        <button
          onClick={onPostAnyway}
          disabled={isPosting}
          className="flex-1 px-4 py-2 rounded-lg font-semibold text-sm
                     bg-gradient-to-r from-veritas-pink to-veritas-pink-dark text-white
                     hover:opacity-90 transition-all
                     disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {isPosting ? 'Posting...' : (
            <>
              <Send className="w-4 h-4" />
              Post Anyway
            </>
          )}
        </button>
      </div>
    </div>
  );
}

function ComposeBox({ onPostCreated }) {
  const { currentUser } = useUser();
  const [postText, setPostText] = useState('');
  const [isPosting, setIsPosting] = useState(false);
  const [factCheckEnabled, setFactCheckEnabled] = useState(false);
  const [isFactChecking, setIsFactChecking] = useState(false);
  const [factCheckResult, setFactCheckResult] = useState(null);
  const [postStyle, setPostStyle] = useState({ font: 'default', textColor: 'default', background: 'none', size: 'default' });
  // AI Assistant state
  const [aiPrompt, setAiPrompt] = useState('');
  const [isGenerating, setIsGenerating] = useState(false);
  const [aiSuggestions, setAiSuggestions] = useState([]);
  const maxChars = 280;

  const getCharCountClass = () => {
    const remaining = maxChars - postText.length;
    if (remaining < 20) return 'text-red-500';
    if (remaining < 50) return 'text-yellow-400';
    return 'text-white/50';
  };

  const handlePost = async () => {
    if (postText.trim().length === 0 || !currentUser) return;

    try {
      // If fact-check enabled, preview first (don't create post yet)
      if (factCheckEnabled) {
        setIsFactChecking(true);
        setFactCheckResult(null);

        const result = await factCheckService.preview(postText);
        setIsFactChecking(false);
        setFactCheckResult(result);
        return; // Show preview, let user decide
      }

      // No fact-check - just create post (with optional style)
      setIsPosting(true);
      const hasCustomStyle = postStyle.font !== 'default' || postStyle.textColor !== 'default' || postStyle.background !== 'none' || postStyle.size !== 'default';
      await postService.createPost(currentUser.id, postText, false, hasCustomStyle ? postStyle : null);
      setPostText('');
      setPostStyle({ font: 'default', textColor: 'default', background: 'none', size: 'default' }); // Reset style
      if (onPostCreated) {
        onPostCreated();
      }
    } catch (error) {
      console.error('Error:', error);
      alert('Something went wrong. Please try again.');
    } finally {
      setIsPosting(false);
      setIsFactChecking(false);
    }
  };

  // #147 - Post anyway after seeing fact-check result
  const handlePostAnyway = async () => {
    try {
      setIsPosting(true);
      // Create post with fact-check data already included (and optional style)
      const hasCustomStyle = postStyle.font !== 'default' || postStyle.textColor !== 'default' || postStyle.background !== 'none' || postStyle.size !== 'default';
      await postService.createPost(currentUser.id, postText, true, hasCustomStyle ? postStyle : null);
      setPostText('');
      setPostStyle({ font: 'default', textColor: 'default', background: 'none', size: 'default' }); // Reset style
      setFactCheckResult(null);
      if (onPostCreated) {
        onPostCreated();
      }
    } catch (error) {
      console.error('Error creating post:', error);
      alert('Failed to create post. Please try again.');
    } finally {
      setIsPosting(false);
    }
  };

  // #147 - Edit post after seeing fact-check result
  const handleEdit = () => {
    setFactCheckResult(null);
    // Keep postText so user can edit
  };

  // Handle AI generation
  const handleAiGenerate = async () => {
    setIsGenerating(true);
    setAiSuggestions([]);
    try {
      let result;
      if (postText) {
        result = await postAssistantService.improve(postText, aiPrompt || null);
      } else {
        result = await postAssistantService.generate(aiPrompt);
      }
      if (result.suggestions) {
        setAiSuggestions(result.suggestions);
      }
    } catch (err) {
      console.error('AI generation error:', err);
    } finally {
      setIsGenerating(false);
      setAiPrompt('');
    }
  };

  if (!currentUser) {
    return null;
  }

  const hasCustomStyle = postStyle.font !== 'default' || postStyle.textColor !== 'default' || postStyle.background !== 'none' || postStyle.size !== 'default';

  return (
    <div className="p-4 bg-gradient-to-br from-veritas-purple/20 to-veritas-pink/20
                    border-2 border-veritas-pink/30 rounded-2xl">
      {/* Header */}
      <label className="block text-xs font-bold text-veritas-coral mb-3 uppercase tracking-wider">
        Create Post
      </label>

      {/* AI Assistant - Always Visible */}
      <div className="mb-3">
        <div className="flex items-center gap-2 mb-2">
          <span className="text-xs font-semibold text-white/70">AI Assistant</span>
        </div>
        <div className="flex gap-2">
          <input
            type="text"
            value={aiPrompt}
            onChange={(e) => setAiPrompt(e.target.value)}
            placeholder={postText ? "How to improve? (or leave empty)" : "What do you want to post about?"}
            className="flex-1 px-3 py-2 bg-white/10 border border-white/20 rounded-lg
                       text-white text-xs placeholder:text-white/30
                       focus:outline-none focus:border-veritas-purple/50 transition-all"
            onKeyDown={(e) => {
              if (e.key === 'Enter' && !isGenerating) {
                handleAiGenerate();
              }
            }}
          />
          <button
            onClick={handleAiGenerate}
            disabled={isGenerating || (!postText && !aiPrompt.trim())}
            className="px-3 py-2 rounded-lg text-xs font-semibold
                       bg-veritas-purple/40 text-white border border-veritas-purple/50
                       hover:bg-veritas-purple/60 transition-all
                       disabled:opacity-40 disabled:cursor-not-allowed
                       flex items-center gap-1.5"
          >
            {isGenerating ? (
              <Loader2 className="animate-spin" size={16} />
            ) : (
              <Sparkles size={16} />
            )}
            <span>{isGenerating ? '...' : postText ? 'Improve' : 'Generate'}</span>
          </button>
        </div>
        {/* AI Suggestions */}
        {aiSuggestions.length > 0 && (
          <div className="mt-2 space-y-1.5">
            {aiSuggestions.map((suggestion, index) => (
              <button
                key={index}
                onClick={() => {
                  setPostText(suggestion.text);
                  setAiSuggestions([]);
                }}
                className="w-full p-2 bg-white/5 hover:bg-white/10 border border-white/10
                           hover:border-veritas-purple/50 rounded-lg text-left transition-all text-xs"
              >
                <p className="text-white/90">{suggestion.text}</p>
                <span className="text-white/40 text-[10px]">{suggestion.tone}</span>
              </button>
            ))}
          </div>
        )}
      </div>

      {/* Style Picker - 4 rows */}
      <div className="mb-3 p-3 bg-white/5 rounded-xl border border-white/10 space-y-2">
        <div className="flex items-center gap-2 mb-1">
          <span className="text-xs font-semibold text-white/70">Style</span>
        </div>

        {/* Row 1: Font */}
        <div className="flex items-center gap-2">
          <span className="text-[11px] text-white/50 font-medium w-10">Font</span>
          <div className="flex gap-1.5">
            {QUICK_FONTS.map((font) => (
              <button
                key={font.id}
                onClick={() => setPostStyle({ ...postStyle, font: font.id })}
                className={`w-9 h-7 rounded transition-all text-[11px]
                           ${postStyle.font === font.id
                             ? 'bg-veritas-pink/30 border-veritas-pink text-white'
                             : 'bg-white/5 border-white/10 text-white/60 hover:bg-white/10 hover:text-white'}
                           border ${font.fontClass}`}
                title={font.title}
              >
                {font.label}
              </button>
            ))}
          </div>
        </div>

        {/* Row 2: Size */}
        <div className="flex items-center gap-2">
          <span className="text-[11px] text-white/50 font-medium w-10">Size</span>
          <div className="flex gap-1.5">
            {QUICK_SIZES.map((size) => (
              <button
                key={size.id}
                onClick={() => setPostStyle({ ...postStyle, size: size.id })}
                className={`w-9 h-7 rounded transition-all text-[11px] font-medium
                           ${postStyle.size === size.id
                             ? 'bg-veritas-pink/30 border-veritas-pink text-white'
                             : 'bg-white/5 border-white/10 text-white/60 hover:bg-white/10 hover:text-white'}
                           border`}
                title={size.title}
              >
                {size.label}
              </button>
            ))}
          </div>
        </div>

        {/* Row 3: Color */}
        <div className="flex items-center gap-2">
          <span className="text-[11px] text-white/50 font-medium w-10">Color</span>
          <div className="flex gap-1.5">
            {QUICK_COLORS.map((color) => (
              <button
                key={color.id}
                onClick={() => setPostStyle({ ...postStyle, textColor: color.id })}
                className={`w-7 h-7 rounded-full transition-all border-2
                           ${postStyle.textColor === color.id
                             ? 'border-white scale-110'
                             : 'border-white/20 hover:scale-110 hover:border-white/50'}`}
                style={{ backgroundColor: color.hex }}
                title={color.label}
              />
            ))}
          </div>
        </div>

        {/* Row 4: Background */}
        <div className="flex items-center gap-2">
          <span className="text-[11px] text-white/50 font-medium w-10">BG</span>
          <div className="flex gap-1.5">
            {QUICK_BACKGROUNDS.map((bg) => (
              <button
                key={bg.id}
                onClick={() => setPostStyle({ ...postStyle, background: bg.id })}
                className={`w-7 h-7 rounded-full transition-all border-2
                           ${postStyle.background === bg.id
                             ? 'border-white scale-110'
                             : 'border-white/20 hover:scale-110 hover:border-white/50'}`}
                style={{ background: bg.gradient === 'transparent' ? '#333' : bg.gradient }}
                title={bg.label}
              />
            ))}
          </div>
        </div>
      </div>

      {/* Textarea */}
      <textarea
        className="bg-white/10 border border-white/20 rounded-xl px-3 py-3 text-white text-sm w-full min-h-[70px]
                   resize-none outline-none font-['Plus_Jakarta_Sans'] placeholder:text-white/40
                   focus:border-veritas-pink focus:bg-white/15 transition-all duration-300"
        placeholder="What's on your mind?"
        value={postText}
        onChange={(e) => setPostText(e.target.value)}
        maxLength={maxChars}
        disabled={isPosting || isFactChecking}
      />

      {/* Style Preview - only when custom style is applied */}
      {postText.length > 0 && hasCustomStyle && (
        <div className="mt-3 p-3 rounded-xl border border-white/20"
             style={getStyleClasses(postStyle).backgroundStyle}>
          <div className="text-white/40 text-[10px] font-semibold mb-1 uppercase tracking-wider">Preview</div>
          <p className={`${getStyleClasses(postStyle).sizeClass || 'text-sm'} ${getStyleClasses(postStyle).fontClass} ${getStyleClasses(postStyle).colorClass}`}>
            {postText}
          </p>
        </div>
      )}

      {/* Fact Check Loading */}
      {isFactChecking && <FactCheckLoading />}

      {/* Fact Check Preview */}
      {factCheckResult && (
        <FactCheckPreview
          result={factCheckResult}
          onPostAnyway={handlePostAnyway}
          onEdit={handleEdit}
          isPosting={isPosting}
        />
      )}

      {/* Toolbar */}
      <div className="flex items-center justify-between mt-3 pt-3 border-t border-white/10">
        {/* Fact Check Toggle */}
        <label className="flex items-center gap-2 cursor-pointer">
          <span className={`text-xs font-medium transition-colors ${factCheckEnabled ? 'text-veritas-coral' : 'text-white/50'}`}>
            Fact Check
          </span>
          <div
            onClick={() => setFactCheckEnabled(!factCheckEnabled)}
            className={`relative w-9 h-5 rounded-full transition-all duration-200 cursor-pointer
                       ${factCheckEnabled
                         ? 'bg-gradient-to-r from-veritas-pink to-veritas-coral'
                         : 'bg-white/20'}`}
          >
            <div
              className={`absolute top-0.5 w-4 h-4 rounded-full shadow transition-transform duration-200
                         ${factCheckEnabled
                           ? 'translate-x-4 bg-white'
                           : 'translate-x-0.5 bg-white/80'}`}
            />
          </div>
        </label>

        {/* Reset Style Button */}
        <button
          onClick={() => setPostStyle({ font: 'default', textColor: 'default', background: 'none', size: 'default' })}
          disabled={!hasCustomStyle}
          className={`px-2 py-1 rounded text-[11px] font-medium transition-all
                     ${hasCustomStyle
                       ? 'bg-veritas-coral/20 text-veritas-coral border border-veritas-coral/30 hover:bg-veritas-coral/30'
                       : 'bg-white/5 text-white/30 border border-white/10 cursor-default'}`}
        >
          Reset Style
        </button>

        {/* Character Count */}
        <span className={`text-xs font-medium ${getCharCountClass()}`}>
          {postText.length}/{maxChars}
        </span>

        {/* Post Button */}
        <button
          className="bg-gradient-to-r from-veritas-pink to-veritas-pink-dark
                     px-4 py-1.5 rounded-lg font-semibold text-sm
                     border-none text-white transition-all duration-200
                     hover:opacity-90
                     disabled:opacity-50 disabled:cursor-not-allowed"
            onClick={handlePost}
            disabled={postText.trim().length === 0 || isPosting || isFactChecking || factCheckResult}
          >
            {isFactChecking ? '...' : isPosting ? '...' : 'Post'}
          </button>
        </div>
    </div>
  );
}

export default ComposeBox;