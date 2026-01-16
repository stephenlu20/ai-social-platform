import api from './api';

const postService = {
  getFeed: async (userId) => {
    const response = await api.get(`posts/feed/${userId}`);
    return response.data;
  },

  createPost: async (userId, content, factCheck = false) => {
    const response = await api.post('posts', {
      userId: userId,
      content: content,
      factCheck: factCheck
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
    const response = await api.post(`/posts/${postId}/like`, null, {
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
    // Backend returns List<Post>, we need to convert to proper format
    // The backend should ideally return PostResponseDTO, but if it returns Post entities,
    // we need to transform them
    return response.data.map(reply => ({
      id: reply.id,
      author: reply.author,
      content: reply.content,
      createdAt: reply.createdAt,
      likeCount: reply.likeCount || 0,
      replyCount: reply.replyCount || 0,
      repostCount: reply.repostCount || 0,
      replyToId: reply.replyTo?.id || reply.replyToId,
      isLikedByCurrentUser: reply.isLikedByCurrentUser || false,
      factCheckStatus: reply.factCheckStatus,
      factCheckScore: reply.factCheckScore
    }));
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