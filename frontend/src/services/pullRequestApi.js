import api from './api';

export const pullRequestApi = {
  getAllPullRequests: () => api.get('/pull-requests'),
  getPullRequestById: (id) => api.get(`/pull-requests/${id}`),
  getPullRequestsByRepo: (repoId) => api.get(`/repositories/${repoId}/pull-requests`),
  syncPullRequests: (repoId) => api.post(`/repositories/${repoId}/pull-requests/sync`),
};
