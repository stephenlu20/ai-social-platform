

import api from './api';

const postService = {
  getFeed: async (userId) => {
    const response = await api.get(`posts/feed/${userId}`);
    return response.data;
  },

  createPost: async (userId, content) => {
    const response = await api.post('posts', {
      userId: userId,
      content: content
    });
    return response.data;
  },

  replyToPost: async (userId, postId, content) => {
    const response = await api.post(`posts/${postId}/reply`, {
      userId: userId,
      content: content
    });
    return response.data;
  },

  repost: async (userId, postId) => {
    const response = await api.post(`posts/${postId}/repost`, {
      userId: userId
    });
    return response.data;
  },

  likePost: async (userId, postId) => {
    const response = await api.post(`posts/${postId}/like`, null, {
      params: { userId }
    });
    return response.data;
  },

  unlikePost: async (userId, postId) => {
    await api.delete(`posts/${postId}/like`, {
      params: { userId }
    });
  },

  getLikeCount: async (postId) => {
    const response = await api.get(`posts/${postId}/likes/count`);
    return response.data;
  },

  getReplies: async (postId) => {
    const response = await api.get(`posts/${postId}/replies`);
    return response.data;
  },

  deletePost: async (userId, postId) => {
    await api.delete(`posts/${postId}`, {
      params: { userId }
    });
  },

  searchPosts: async (searchRequest) => {
    const response = await api.post('posts/search', searchRequest);
    return response.data;
  },
};

export default postService;