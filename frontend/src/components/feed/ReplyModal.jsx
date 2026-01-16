import React, { useState, useEffect, useRef } from 'react';
import postService from '../../services/postService';

function ReplyModal({ post, currentUserId, onClose, onReplyCreated }) {
  const [content, setContent] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const textareaRef = useRef(null);

  // Auto-focus when modal opens
  useEffect(() => {
    textareaRef.current?.focus();
  }, []);

  const handleSubmit = async () => {
    if (!content.trim() || submitting) return;

    try {
      setSubmitting(true);
      await postService.replyToPost(currentUserId, post.id, content.trim());

      if (onReplyCreated) {
        onReplyCreated();
      }
    } catch (error) {
      console.error('Error creating reply:', error);
    } finally {
      setSubmitting(false);
    }
  };

  const handleKeyDown = (e) => {
    // Cmd+Enter / Ctrl+Enter to submit
    if ((e.metaKey || e.ctrlKey) && e.key === 'Enter') {
      handleSubmit();
    }

    // Escape to close
    if (e.key === 'Escape') {
      onClose();
    }
  };

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/60"
      onKeyDown={handleKeyDown}
    >
      <div className="w-full max-w-lg mx-4 bg-[#0f0f0f] border border-white/10 rounded-2xl shadow-xl">
        {/* Header */}
        <div className="flex items-center justify-between px-5 py-4 border-b border-white/10">
          <span className="text-white font-semibold text-sm">Reply</span>
          <button
            onClick={onClose}
            className="text-white/50 hover:text-white transition"
          >
            ✕
          </button>
        </div>

        {/* Original Post Context */}
        <div className="px-5 pt-4 pb-3 text-sm text-white/70">
          <span className="text-white font-semibold">
            {post.author.displayName}
          </span>{' '}
          <span className="text-white/50">@{post.author.username}</span>
          <div className="mt-2 text-white/80 leading-relaxed">
            {post.content}
          </div>
        </div>

        {/* Reply Input */}
        <div className="px-5 pb-5">
          <textarea
            ref={textareaRef}
            value={content}
            onChange={(e) => setContent(e.target.value)}
            placeholder="Post your reply"
            rows={4}
            className="
              w-full resize-none bg-transparent text-white
              border border-white/20 rounded-xl p-3
              focus:outline-none focus:border-veritas-pink
              placeholder:text-white/30
            "
          />

          {/* Actions */}
          <div className="flex justify-end gap-3 mt-4">
            <button
              onClick={onClose}
              className="px-4 py-2 text-sm text-white/50 hover:text-white transition"
            >
              Cancel
            </button>
            <button
              onClick={handleSubmit}
              disabled={submitting || !content.trim()}
              className="
                px-5 py-2 rounded-full text-sm font-bold text-white
                bg-gradient-to-br from-veritas-pink to-veritas-pink-dark
                disabled:opacity-50 disabled:cursor-not-allowed
                hover:shadow-[0_4px_12px_rgba(255,107,157,0.35)]
                transition-all
              "
            >
              {submitting ? 'Replying…' : 'Reply'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ReplyModal;
