import React, { useState } from 'react';
import postAssistantService from '../../services/postAssistantService';
import { Loader2 } from 'lucide-react';

function PostAssistantPanel({ currentText, onSelectSuggestion, onClose }) {
  const [mode, setMode] = useState(currentText ? 'improve' : 'generate');
  const [prompt, setPrompt] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [suggestions, setSuggestions] = useState([]);
  const [error, setError] = useState(null);

  const handleSubmit = async () => {
    setIsLoading(true);
    setError(null);
    setSuggestions([]);

    try {
      let result;
      if (mode === 'improve') {
        result = await postAssistantService.improve(currentText, prompt || null);
      } else {
        result = await postAssistantService.generate(prompt);
      }

      if (result.error) {
        setError(result.error);
      } else if (result.suggestions) {
        setSuggestions(result.suggestions);
      }
    } catch (err) {
      setError('Failed to get suggestions. Please try again.');
      console.error('Post assistant error:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSelectSuggestion = (text) => {
    onSelectSuggestion(text);
    onClose();
  };

  return (
    <div className="mb-4 p-4 bg-gradient-to-br from-veritas-purple/20 to-veritas-pink/10
                    border border-veritas-purple/30 rounded-xl">
      {/* Header */}
      <div className="flex items-center justify-between mb-3">
        <div className="flex items-center gap-2">
          <span className="text-lg"></span>
          <span className="font-bold text-white text-sm">Post Assistant</span>
        </div>
        <button
          onClick={onClose}
          className="w-6 h-6 rounded-full bg-white/10 hover:bg-white/20
                     flex items-center justify-center transition-colors text-xs text-white/70"
        >
          ✕
        </button>
      </div>

      {/* Mode Toggle */}
      <div className="flex gap-2 mb-3">
        <button
          onClick={() => setMode('improve')}
          disabled={!currentText}
          className={`flex-1 px-3 py-2 rounded-lg text-xs font-semibold transition-all
                     ${mode === 'improve'
                       ? 'bg-veritas-purple/50 text-white border border-veritas-purple'
                       : 'bg-white/5 text-white/50 border border-white/10 hover:bg-white/10'}
                     ${!currentText ? 'opacity-40 cursor-not-allowed' : ''}`}
        >
          Improve Current
        </button>
        <button
          onClick={() => setMode('generate')}
          className={`flex-1 px-3 py-2 rounded-lg text-xs font-semibold transition-all
                     ${mode === 'generate'
                       ? 'bg-veritas-purple/50 text-white border border-veritas-purple'
                       : 'bg-white/5 text-white/50 border border-white/10 hover:bg-white/10'}`}
        >
          Generate New
        </button>
      </div>

      {/* Current Text Preview (Improve Mode) */}
      {mode === 'improve' && currentText && (
        <div className="mb-3 p-2 bg-white/5 rounded-lg border border-white/10">
          <p className="text-white/50 text-xs mb-1">Current text:</p>
          <p className="text-white/80 text-sm">{currentText}</p>
        </div>
      )}

      {/* Input */}
      <div className="mb-3">
        <input
          type="text"
          value={prompt}
          onChange={(e) => setPrompt(e.target.value)}
          placeholder={mode === 'improve'
            ? 'Optional: specific instructions (e.g., "make it funnier")'
            : 'Describe what you want to post about...'}
          className="w-full px-3 py-2 bg-white/5 border border-white/20 rounded-lg
                     text-white text-sm placeholder:text-white/30
                     focus:outline-none focus:border-veritas-purple/50"
          onKeyDown={(e) => {
            if (e.key === 'Enter' && !isLoading) {
              if (mode === 'generate' && prompt.trim()) handleSubmit();
              else if (mode === 'improve' && currentText) handleSubmit();
            }
          }}
        />
      </div>

      {/* Submit Button */}
      <button
        onClick={handleSubmit}
        disabled={isLoading || (mode === 'generate' && !prompt.trim()) || (mode === 'improve' && !currentText)}
        className="w-full px-4 py-2 rounded-lg font-semibold text-sm
                   bg-gradient-to-r from-veritas-purple to-veritas-pink text-white
                   hover:opacity-90 transition-all
                   disabled:opacity-50 disabled:cursor-not-allowed
                   flex items-center justify-center gap-2"
      >
        {isLoading ? (
          <>
            <Loader2 className="w-4 h-4 animate-spin" />
            <span>Generating...</span>
          </>
        ) : (
          <>
            <span>{mode === 'improve' ? 'Get Suggestions' : 'Generate Posts'}</span>
          </>
        )}
      </button>

      {/* Error */}
      {error && (
        <div className="mt-3 p-2 bg-red-500/10 border border-red-500/30 rounded-lg">
          <p className="text-red-400 text-xs">{error}</p>
        </div>
      )}

      {/* Suggestions */}
      {suggestions.length > 0 && (
        <div className="mt-3 space-y-2">
          <p className="text-white/50 text-xs font-semibold">Click to use:</p>
          {suggestions.map((suggestion, index) => (
            <button
              key={index}
              onClick={() => handleSelectSuggestion(suggestion.text)}
              className="w-full p-3 bg-white/5 hover:bg-white/10 border border-white/10
                         hover:border-veritas-purple/50 rounded-lg text-left transition-all group"
            >
              <p className="text-white/90 text-sm mb-1">{suggestion.text}</p>
              <div className="flex items-center justify-between">
                <span className="text-white/40 text-xs">{suggestion.tone}</span>
                <span className="text-veritas-purple text-xs opacity-0 group-hover:opacity-100 transition-opacity">
                  Click to use →
                </span>
              </div>
            </button>
          ))}
        </div>
      )}
    </div>
  );
}

export default PostAssistantPanel;
