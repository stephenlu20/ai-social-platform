import api from './api';

const postAssistantService = {
  // Improve existing post content
  improve: async (content, instruction = null) => {
    const response = await api.post('/api/ai/post-assistant/improve', {
      content,
      instruction
    });
    return response.data;
  },

  // Generate a new post from a prompt
  generate: async (prompt) => {
    const response = await api.post('/api/ai/post-assistant/generate', {
      prompt
    });
    return response.data;
  }
};

export default postAssistantService;
