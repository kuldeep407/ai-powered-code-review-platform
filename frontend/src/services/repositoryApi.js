import api from './api';

export const repositoryApi = {
  getRepositories: () => api.get('/repositories'),
  getRepositoryById: (id) => api.get(`/repositories/${id}`),
  addRepository: (repoData) => api.post('/repositories', repoData),
  deleteRepository: (id) => api.delete(`/repositories/${id}`),
  connectGithub: (data) => api.post('/github/connect', data),
  disconnectGithub: () => api.delete('/github/disconnect'),
  syncGithub: () => api.post('/github/sync'),
};
