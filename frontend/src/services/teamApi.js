import api from './api';

export const teamApi = {
  getTeams: () => api.get('/teams'),
  createTeam: (teamData) => api.post('/teams', teamData),
  addMember: (teamId, memberData) => api.post(`/teams/${teamId}/members`, memberData),
};
