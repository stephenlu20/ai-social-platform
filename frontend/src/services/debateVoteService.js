
import api from './api';

const debateVoteService = {
  getAll: async () => {
    const response = await api.get('/api/debate-votes');
    return response.data;
  },

  getById: async (voteId) => {
    const response = await api.get(`/api/debate-votes/${voteId}`);
    return response.data;
  },

  create: async (vote) => {
    const response = await api.post('/api/debate-votes', vote);
    return response.data;
  },

  update: async (voteId, voteValue) => {
    const response = await api.put(`/api/debate-votes/${voteId}`, {
      vote: voteValue
    });
    return response.data;
  },

  delete: async (voteId) => {
    await api.delete(`/api/debate-votes/${voteId}`);
  },
};

export default debateVoteService;