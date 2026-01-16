import api from './api';

const userService = {
  getAllUsers: async () => {
    const response = await api.get('api/users');
    return response.data;
  },

  getCurrentUser: async (userId) => {
    const response = await api.get('api/users/me', {
      headers: { 'X-User-Id': userId }
    });
    return response.data;
  },

  getUserById: async (id, currentUserId = null) => {
    const headers = currentUserId ? { 'X-User-Id': currentUserId } : {};
    const response = await api.get(`api/users/${id}`, { headers });
    return response.data;
  },

  getUserByUsername: async (username, currentUserId = null) => {
    const headers = currentUserId ? { 'X-User-Id': currentUserId } : {};
    const response = await api.get(`api/users/username/${username}`, { headers });
    return response.data;
  },

  getTrustBreakdown: async (userId) => {
    const response = await api.get(`api/users/${userId}/trust-breakdown`);
    return response.data;
  },

  updateUser: async (userId, payload) => {
    const response = await api.put(`api/users/${userId}`, payload);
    return response.data;
  }
};

export default userService;
