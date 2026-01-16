import api from './api';

const followService = {
  // New toggle method - simpler API like the like button
  toggleFollow: async (currentUserId, targetUserId) => {
    const response = await api.post(`/api/users/${targetUserId}/follow`, null, {
      headers: { 'X-User-Id': currentUserId }
    });
    return response.data; // Returns { following: boolean, followerCount: number }
  },

  // Legacy methods kept for backwards compatibility
  followUser: async (currentUserId, targetUserId) => {
    const response = await api.post(`/api/users/${targetUserId}/follow`, null, {
      headers: { 'X-User-Id': currentUserId }
    });
    return response.data;
  },

  unfollowUser: async (currentUserId, targetUserId) => {
    await api.delete(`/api/users/${targetUserId}/follow`, {
      headers: { 'X-User-Id': currentUserId }
    });
  },

  getFollowers: async (userId) => {
    const response = await api.get(`/api/users/${userId}/followers`);
    return response.data;
  },

  getFollowing: async (userId) => {
    const response = await api.get(`/api/users/${userId}/following`);
    return response.data;
  },

  isFollowing: async (currentUserId, targetUserId) => {
    const response = await api.get(`/api/users/${targetUserId}/is-following`, {
      headers: { 'X-User-Id': currentUserId }
    });
    return response.data;
  },
};

export default followService;