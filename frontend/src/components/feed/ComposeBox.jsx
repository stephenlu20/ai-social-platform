
import React, { useState } from 'react';
import { useUser } from '../../context/UserContext';
import postService from '../../services/postService';
import factCheckService from '../../services/factcheckService';

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
      case 'VERIFIED': return '✓';
      case 'LIKELY_TRUE': return '◐';
      case 'DISPUTED': return '⚠';
      case 'FALSE': return '✗';
      case 'UNVERIFIABLE': return '?';
      default: return '•';
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
          ✏️ Edit Post
        </button>
        <button
          onClick={onPostAnyway}
          disabled={isPosting}
          className="flex-1 px-4 py-2 rounded-lg font-semibold text-sm
                     bg-gradient-to-r from-veritas-pink to-veritas-pink-dark text-white
                     hover:opacity-90 transition-all
                     disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {isPosting ? 'Posting...' : '📤 Post Anyway'}
        </button>
      </div>
    </div>
  );
}

function ComposeBox({ onPostCreated }) {
  const { currentUser } = useUser();
  const [postText, setPostText] = useState('');
  const [isPosting, setIsPosting] = useState(false);
  const [factCheckEnabled, setFactCheckEnabled] = useState(false); // #144
  const [isFactChecking, setIsFactChecking] = useState(false);
  const [factCheckResult, setFactCheckResult] = useState(null);
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

      // No fact-check - just create post
      setIsPosting(true);
      await postService.createPost(currentUser.id, postText, false);
      setPostText('');
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
      // Create post with fact-check data already included
      await postService.createPost(currentUser.id, postText, true);
      setPostText('');
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

  if (!currentUser) {
    return null;
  }

  return (
    <div className="p-5 flex gap-3.5 border-b border-white/10 relative">
      <div className="w-11 h-11 rounded-[14px] bg-gradient-to-br from-veritas-blue to-veritas-blue-dark 
                      flex items-center justify-center text-xl 
                      shadow-[0_4px_12px_rgba(102,126,234,0.3)] flex-shrink-0">
        🎨
      </div>
      <div className="flex-1">
        <div className="bg-veritas-pink/10 border-l-4 border-veritas-pink px-3 py-2 mb-4
                        text-xs font-bold text-veritas-coral uppercase tracking-wider">
          🎯 CREATE A POST (Feature #3)
        </div>

        <div className="mb-4 p-4 bg-gradient-to-br from-veritas-purple/10 to-veritas-pink/10 
                        border-2 border-veritas-pink/30 rounded-2xl">
          <div className="flex items-center gap-2.5 mb-3">
            <span className="text-xl">✨</span>
            <span className="font-bold text-veritas-coral text-sm">AI WRITING TOOLS</span>
          </div>
          <div className="flex flex-wrap gap-2">
            <button className="px-3.5 py-2 rounded-[10px] font-semibold cursor-pointer text-[13px] 
                               flex items-center gap-1.5 border-2 transition-all duration-300
                               bg-veritas-purple/30 border-veritas-purple/50 text-veritas-purple-light
                               hover:-translate-y-0.5">
              <span>🤖</span>
              <span>Post Assistant</span>
            </button>
            <button
              onClick={() => setFactCheckEnabled(!factCheckEnabled)}
              className={`px-3.5 py-2 rounded-[10px] font-semibold cursor-pointer text-[13px]
                         flex items-center gap-1.5 border-2 transition-all duration-300
                         hover:-translate-y-0.5
                         ${factCheckEnabled
                           ? 'bg-blue-600/50 border-blue-400 text-blue-200 shadow-[0_0_12px_rgba(59,130,246,0.3)]'
                           : 'bg-blue-600/30 border-blue-600/50 text-blue-300'}`}
            >
              <span>✅</span>
              <span>Fact Check</span>
              <span className={`ml-1 px-1.5 py-0.5 rounded text-[10px] font-bold uppercase
                              ${factCheckEnabled ? 'bg-blue-400 text-blue-900' : 'bg-blue-600/50 text-blue-300'}`}>
                {factCheckEnabled ? 'ON' : 'OFF'}
              </span>
            </button>
            <button className="px-3.5 py-2 rounded-[10px] font-semibold cursor-pointer text-[13px] 
                               flex items-center gap-1.5 border-2 transition-all duration-300
                               bg-green-600/30 border-green-600/50 text-green-300
                               hover:-translate-y-0.5">
              <span>🔍</span>
              <span>Fact Crawler</span>
            </button>
          </div>
        </div>

        <textarea
          className="bg-transparent border-none text-white text-lg w-full min-h-[80px]
                     resize-none outline-none font-['Plus_Jakarta_Sans'] placeholder:text-white/30"
          placeholder="What's on your mind?"
          value={postText}
          onChange={(e) => setPostText(e.target.value)}
          maxLength={maxChars}
          disabled={isPosting || isFactChecking}
        />

        {/* #145 - Fact Check Loading */}
        {isFactChecking && <FactCheckLoading />}

        {/* #146 - Fact Check Preview with #147 Post Anyway / Edit options */}
        {factCheckResult && (
          <FactCheckPreview
            result={factCheckResult}
            onPostAnyway={handlePostAnyway}
            onEdit={handleEdit}
            isPosting={isPosting}
          />
        )}

        <div className="flex justify-between items-center mt-4 pt-4 border-t border-white/10">
          <div className="flex gap-1 flex-wrap">
            <button className="w-[38px] h-[38px] rounded-xl flex items-center justify-center 
                               cursor-pointer transition-all duration-300 text-xl relative
                               bg-transparent border-none hover:bg-veritas-pink/15 hover:scale-110"
                    title="Add image">
              🖼️
            </button>
            <button className="w-[38px] h-[38px] rounded-xl flex items-center justify-center 
                               cursor-pointer transition-all duration-300 text-xl relative
                               bg-transparent border-none hover:bg-veritas-pink/15 hover:scale-110"
                    title="Add video">
              🎬
            </button>
            <button className="w-[38px] h-[38px] rounded-xl flex items-center justify-center 
                               cursor-pointer transition-all duration-300 text-xl relative
                               bg-transparent border-none hover:bg-veritas-pink/15 hover:scale-110"
                    title="Create poll">
              📊
            </button>
            <button className="w-[38px] h-[38px] rounded-xl flex items-center justify-center 
                               cursor-pointer transition-all duration-300 text-xl relative
                               bg-transparent border-none hover:bg-veritas-pink/15 hover:scale-110"
                    title="Add emoji">
              😊
            </button>
            <button className="w-[38px] h-[38px] rounded-xl flex items-center justify-center 
                               cursor-pointer transition-all duration-300 text-xl relative
                               bg-transparent border-none hover:bg-veritas-pink/15 hover:scale-110"
                    title="Add hashtag">
              #
            </button>
            <button className="w-[38px] h-[38px] rounded-xl flex items-center justify-center 
                               cursor-pointer transition-all duration-300 text-xl relative
                               bg-transparent border-none hover:bg-veritas-pink/15 hover:scale-110"
                    title="Schedule post">
              ⏰
            </button>
          </div>
          <div className="flex items-center gap-3">
            <span className={`text-sm font-semibold flex items-center gap-1 ${getCharCountClass()}`}>
              {postText.length}/{maxChars}
            </span>
            <button
              className="bg-gradient-to-br from-veritas-pink to-veritas-pink-dark
                         px-7 py-2.5 rounded-xl font-bold cursor-pointer
                         shadow-[0_4px_16px_rgba(255,107,157,0.3)] text-[15px]
                         border-none text-white transition-all duration-300
                         hover:-translate-y-0.5 hover:shadow-[0_6px_20px_rgba(255,107,157,0.4)]
                         disabled:opacity-50 disabled:cursor-not-allowed"
              onClick={handlePost}
              disabled={postText.trim().length === 0 || isPosting || isFactChecking || factCheckResult}
            >
              {isFactChecking ? 'Checking...' : isPosting ? 'Posting...' : factCheckEnabled ? '✓ Share' : 'Share'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ComposeBox;