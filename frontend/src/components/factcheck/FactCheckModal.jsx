import React from 'react';
import ReactDOM from 'react-dom';
import FactCheckBadge from './FactCheckBadge';
import SourcesList from './SourcesList';
import { Search } from 'lucide-react';

// #153 - FactCheckModal (detailed results)
function FactCheckModal({ isOpen, onClose, result, postContent }) {
  if (!isOpen) return null;

  const hasError = result?.error;

  return ReactDOM.createPortal (
    <div
      className="fixed inset-0 z-50 overflow-y-auto"
      onClick={onClose}
    >
      {/* Backdrop */}
      <div className="fixed inset-0 bg-black/70 backdrop-blur-sm"></div>

      {/* Modal container - centers content */}
      <div className="min-h-full flex items-center justify-center p-4">
        {/* Modal */}
        <div
          className="relative bg-[#1a1a2e] border border-white/20 rounded-2xl w-full max-w-lg max-h-[80vh] overflow-hidden shadow-2xl"
          onClick={(e) => e.stopPropagation()}
        >
        {/* Header */}
        <div className="flex items-center justify-between p-4 border-b border-white/10">
          <h2 className="text-lg font-bold text-white flex items-center gap-2">
            <Search/>
            Fact Check Details
          </h2>
          <button
            onClick={onClose}
            className="w-8 h-8 rounded-full bg-white/10 hover:bg-white/20
                       flex items-center justify-center transition-colors"
          >
            <span className="text-white/70">✕</span>
          </button>
        </div>

        {/* Content */}
        <div className="p-4 overflow-y-auto max-h-[calc(80vh-60px)] space-y-4">
          {/* Original Post Content */}
          {postContent && (
            <div className="p-3 bg-white/5 rounded-lg border border-white/10">
              <p className="text-white/50 text-xs uppercase tracking-wider mb-2">Original Post</p>
              <p className="text-white/80 text-sm">{postContent}</p>
            </div>
          )}

          {/* Error State */}
          {hasError && (
            <div className="p-4 bg-red-500/10 border border-red-500/30 rounded-xl">
              <p className="text-red-400 font-semibold">Error</p>
              <p className="text-red-300 text-sm mt-1">{result.error}</p>
            </div>
          )}

          {/* Results */}
          {!hasError && result && (
            <>
              {/* Verdict */}
              <div className="flex items-center justify-between">
                <span className="text-white/50 text-sm">Verdict</span>
                <FactCheckBadge
                  status={result.verdict}
                  score={result.confidence ? result.confidence / 100 : null}
                  size="md"
                />
              </div>

              {/* Confidence Bar */}
              {result.confidence && (
                <div>
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

              {/* Summary */}
              {result.summary && (
                <div>
                  <p className="text-white/50 text-xs uppercase tracking-wider mb-2">Summary</p>
                  <p className="text-white/90 text-sm leading-relaxed">{result.summary}</p>
                </div>
              )}

              {/* Reasoning */}
              {result.reasoning && result.reasoning.length > 0 && (
                <div>
                  <p className="text-white/50 text-xs uppercase tracking-wider mb-2">Analysis</p>
                  <div className="space-y-2">
                    {result.reasoning.map((step, index) => (
                      <div
                        key={index}
                        className="flex items-start gap-2 p-2 bg-white/5 rounded-lg"
                      >
                        <span className="text-veritas-pink text-xs font-mono mt-0.5">
                          {index + 1}.
                        </span>
                        <p className="text-white/80 text-sm flex-1">{step}</p>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {/* Sources */}
              {result.sources && result.sources.length > 0 && (
                <SourcesList sources={result.sources} />
              )}
            </>
          )}
        </div>
        </div>
      </div>
    </div>,
    document.body
  );
}

export default FactCheckModal;
