
import api from './api';

const factCheckService = {
  getAll: async () => {
    const response = await api.get('/api/fact-checks');
    return response.data;
  },

  getById: async (factCheckId) => {
    const response = await api.get(`/api/fact-checks/${factCheckId}`);
    return response.data;
  },

  create: async (factCheck) => {
    const response = await api.post('/api/fact-checks', factCheck);
    return response.data;
  },

  update: async (factCheckId, status, overallScore, claims) => {
    const response = await api.put(`/api/fact-checks/${factCheckId}`, {
      status: status,
      overallScore: overallScore,
      claims: claims
    });
    return response.data;
  },

  delete: async (factCheckId) => {
    await api.delete(`/api/fact-checks/${factCheckId}`);
  },
};

export default factCheckService;
