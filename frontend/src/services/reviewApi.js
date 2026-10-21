import api from './api';

export const reviewApi = {
  analyzePullRequest: (prId, customFocus) => api.post(`/pull-requests/${prId}/analyze`, { customFocus }),
  getReviewById: (id) => api.get(`/reviews/${id}`),
  getReviewsForPullRequest: (prId) => api.get(`/pull-requests/${prId}/reviews`),
  getRecentReviews: () => api.get('/reviews'),
};
