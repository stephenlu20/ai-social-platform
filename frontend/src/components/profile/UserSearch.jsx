import React, { useState } from 'react';
import { useUser } from '../../context/UserContext';
import api from '../../services/api';
import FollowButton from './FollowButton';
import logo from '../../assets/CondorTransparent.png';
import { Shield } from 'lucide-react';

function UserSearch() {
  const { currentUser } = useUser();
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [hasSearched, setHasSearched] = useState(false);
  const [filters, setFilters] = useState({
    minTrustScore: '',
    maxTrustScore: ''
  });

  const handleSearch = async (e) => {
    e.preventDefault();
    
    if (!searchQuery.trim() && !filters.minTrustScore && !filters.maxTrustScore) {
      return;
    }

    try {
      setLoading(true);
      setHasSearched(true);
      
      const response = await api.post('/api/users/search', {
        username: searchQuery.trim() || null,
        displayName: searchQuery.trim() || null,
        minTrustScore: filters.minTrustScore ? parseFloat(filters.minTrustScore) : null,
        maxTrustScore: filters.maxTrustScore ? parseFloat(filters.maxTrustScore) : null,
        page: 0,
        size: 20
      });

      setSearchResults(response.data.content || []);
    } catch (error) {
      console.error('Error searching users:', error);
      alert('Failed to search users. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleFollowChange = (userId, newFollowStatus) => {
    setSearchResults(prevResults =>
      prevResults.map(user =>
        user.id === userId
          ? { ...user, isFollowing: newFollowStatus }
          : user
      )
    );
  };

  return (
    <div className="bg-white/[0.03] rounded-3xl overflow-hidden backdrop-blur-[10px] border border-white/10">
      {/* Header */}
      <div className="px-6 py-5 border-b border-white/10">
        <h2 className="text-2xl font-extrabold flex items-center gap-2">
          <span>Discover People</span>
        </h2>
      </div>

      {/* Search Form */}
      <form onSubmit={handleSearch} className="p-6 border-b border-white/10">
        {/* Main Search */}
        <div className="mb-4">
          <label className="block text-sm font-semibold mb-2 text-white/70">
            Search by username or display name
          </label>
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Enter username or name..."
            className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3
                     text-white placeholder:text-white/40 outline-none
                     focus:border-veritas-pink focus:bg-white/15 transition-all"
          />
        </div>

        {/* Filters */}
        <div className="grid grid-cols-2 gap-4 mb-4">
          <div>
            <label className="block text-sm font-semibold mb-2 text-white/70">
              Min Trust Score
            </label>
            <input
              type="number"
              min="0"
              max="100"
              value={filters.minTrustScore}
              onChange={(e) => setFilters({ ...filters, minTrustScore: e.target.value })}
              placeholder="0"
              className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3
                       text-white placeholder:text-white/40 outline-none
                       focus:border-veritas-pink focus:bg-white/15 transition-all"
            />
          </div>
          <div>
            <label className="block text-sm font-semibold mb-2 text-white/70">
              Max Trust Score
            </label>
            <input
              type="number"
              min="0"
              max="100"
              value={filters.maxTrustScore}
              onChange={(e) => setFilters({ ...filters, maxTrustScore: e.target.value })}
              placeholder="100"
              className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3
                       text-white placeholder:text-white/40 outline-none
                       focus:border-veritas-pink focus:bg-white/15 transition-all"
            />
          </div>
        </div>

        {/* Search Button */}
        <button
          type="submit"
          disabled={loading}
          className="w-full bg-gradient-to-br from-veritas-pink to-veritas-pink-dark
                   px-6 py-3 rounded-xl font-bold text-white
                   shadow-[0_4px_16px_rgba(255,107,157,0.3)]
                   transition-all duration-300
                   hover:-translate-y-0.5 hover:shadow-[0_6px_20px_rgba(255,107,157,0.4)]
                   disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {loading ? 'Searching...' : 'Search'}
        </button>
      </form>

      {/* Results */}
      <div>
        {loading && (
          <div className="p-20 text-center text-white/50">
            Searching...
          </div>
        )}

        {!loading && hasSearched && searchResults.length === 0 && (
          <div className="p-20 text-center text-white/50">
            No users found. Try different search terms.
          </div>
        )}

        {!loading && !hasSearched && (
          <div className="p-20 text-center text-white/50">
            Enter search terms to find users
          </div>
        )}

        {!loading && searchResults.length > 0 && (
          <div className="divide-y divide-white/10">
            {searchResults.map(user => (
              <UserSearchResult
                key={user.id}
                user={user}
                currentUserId={currentUser?.id}
                onFollowChange={handleFollowChange}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

function UserSearchResult({ user, currentUserId, onFollowChange }) {
  return (
    <div className="flex items-center gap-4 p-5 hover:bg-white/5 transition-all">
      {/* Avatar */}
      <div className="w-14 h-14 rounded-xl bg-gradient-to-br from-veritas-blue to-veritas-blue-dark
                      flex items-center justify-center text-2xl flex-shrink-0
                      shadow-[0_4px_12px_rgba(102,126,234,0.3)]">
        <img 
          src={logo} 
          alt="Candor Logo" 
          className="w-10 h-10 object-contain"
        />
      </div>

      {/* User Info */}
      <div className="flex-1 min-w-0">
        <div className="font-bold text-base">{user.displayName}</div>
        <div className="text-white/50 text-sm">@{user.username}</div>
        {user.bio && (
          <div className="text-white/70 text-sm mt-1 line-clamp-2">{user.bio}</div>
        )}
      </div>

      {/* Trust Score & Stats */}
      <div className="flex flex-col items-end gap-2 flex-shrink-0">
        <div className="flex items-center gap-2 bg-gradient-to-br from-green-600/30 to-green-700/30
                        border border-green-600/40 rounded-lg px-3 py-1.5">
          <span className="text-sm"><Shield className="w-3 h-3" /></span>
          <span className="text-sm font-bold">{Math.round(user.trustScore)}</span>
        </div>
        <div className="text-xs text-white/50">
          {user.followerCount || 0} followers
        </div>
      </div>

      {/* Follow Button */}
      {currentUserId && (
        <div className="flex-shrink-0">
          <FollowButton
            targetUser={user}
            currentUserId={currentUserId}
            initialIsFollowing={user.isFollowing || false}
            onFollowChange={(newStatus) => onFollowChange(user.id, newStatus)}
          />
        </div>
      )}
    </div>
  );
}

export default UserSearch;