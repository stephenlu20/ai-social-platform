import React from 'react';
import { FileText } from 'lucide-react';

// #155 - SourcesList component
function SourcesList({ sources }) {
  if (!sources || sources.length === 0) return null;

  return (
    <div className="space-y-2">
      <h4 className="text-white/50 text-xs font-semibold uppercase tracking-wider">Sources</h4>
      <div className="space-y-2">
        {sources.map((source, index) => (
          <div
            key={index}
            className="p-2 bg-white/5 rounded-lg border border-white/10 text-sm"
          >
            <div className="flex items-start gap-2">
              <span className="text-blue-400"><FileText className="w-4 h-4 text-blue-400" /></span>
              <div className="flex-1 min-w-0">
                {source.url ? (
                  <a
                    href={source.url}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="text-blue-400 hover:text-blue-300 font-medium truncate block"
                  >
                    {source.title || source.url}
                  </a>
                ) : (
                  <span className="text-white/70 font-medium">{source.title}</span>
                )}
                {source.relevance && (
                  <p className="text-white/50 text-xs mt-1">{source.relevance}</p>
                )}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default SourcesList;
