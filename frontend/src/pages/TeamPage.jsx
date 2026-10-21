import React, { useState, useEffect } from 'react';
import { teamApi } from '../services/teamApi';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import Input from '../components/common/Input';
import Modal from '../components/common/Modal';
import Badge from '../components/common/Badge';
import Loader from '../components/common/Loader';
import EmptyState from '../components/common/EmptyState';
import ErrorState from '../components/common/ErrorState';
import { toast } from 'react-toastify';
import {
  FiUsers,
  FiPlus,
  FiUserPlus,
  FiMail,
  FiShield,
  FiUser
} from 'react-icons/fi';

export const TeamPage = () => {
  const [teams, setTeams] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Create team modal
  const [createModalOpen, setCreateModalOpen] = useState(false);
  const [teamName, setTeamName] = useState('');
  const [teamDesc, setTeamDesc] = useState('');
  const [savingTeam, setSavingTeam] = useState(false);

  // Add member modal
  const [memberModalOpen, setMemberModalOpen] = useState(false);
  const [activeTeamId, setActiveTeamId] = useState(null);
  const [memberEmail, setMemberEmail] = useState('');
  const [memberRole, setMemberRole] = useState('MEMBER');
  const [savingMember, setSavingMember] = useState(false);

  const fetchTeams = async () => {
    try {
      setLoading(true);
      setError(null);
      const res = await teamApi.getTeams();
      setTeams(res.data || []);
    } catch (err) {
      setError(err.message || 'Failed to load teams');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTeams();
  }, []);

  const handleCreateTeam = async (e) => {
    e.preventDefault();
    if (!teamName.trim()) return;

    try {
      setSavingTeam(true);
      await teamApi.createTeam({ name: teamName.trim(), description: teamDesc.trim() });
      toast.success('Team created successfully!');
      setCreateModalOpen(false);
      setTeamName('');
      setTeamDesc('');
      fetchTeams();
    } catch (err) {
      toast.error(err.message || 'Failed to create team');
    } finally {
      setSavingTeam(false);
    }
  };

  const handleAddMember = async (e) => {
    e.preventDefault();
    if (!memberEmail.trim() || !activeTeamId) return;

    try {
      setSavingMember(true);
      await teamApi.addMember(activeTeamId, {
        email: memberEmail.trim(),
        roleInTeam: memberRole,
      });
      toast.success('Member invited to team!');
      setMemberModalOpen(false);
      setMemberEmail('');
      fetchTeams();
    } catch (err) {
      toast.error(err.message || 'Failed to add member to team');
    } finally {
      setSavingMember(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 pb-2 border-b border-slate-200">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Team Management</h1>
          <p className="text-sm text-slate-500 mt-1">
            Organize engineering squads, review reviewers, and collaboration permissions.
          </p>
        </div>
        <Button
          variant="primary"
          size="sm"
          onClick={() => setCreateModalOpen(true)}
          icon={FiPlus}
        >
          Create New Team
        </Button>
      </div>

      {/* Content */}
      {loading ? (
        <Loader text="Loading engineering teams..." />
      ) : error ? (
        <ErrorState message={error} onRetry={fetchTeams} />
      ) : teams.length === 0 ? (
        <EmptyState
          icon={FiUsers}
          title="No teams established"
          description="Create your first team to collaborate on pull request reviews and set shared repository rules."
          actionText="Create Team"
          onAction={() => setCreateModalOpen(true)}
          actionIcon={FiPlus}
        />
      ) : (
        <div className="space-y-6">
          {teams.map((t) => (
            <Card
              key={t.id}
              title={t.name}
              subtitle={t.description || 'General engineering team'}
              action={
                <Button
                  variant="secondary"
                  size="sm"
                  onClick={() => {
                    setActiveTeamId(t.id);
                    setMemberModalOpen(true);
                  }}
                  icon={FiUserPlus}
                >
                  Add Member
                </Button>
              }
              padding="p-6"
            >
              <div className="space-y-4">
                <div className="flex items-center justify-between text-xs text-slate-500 pb-2 border-b border-slate-100">
                  <span>Squad Members ({t.members?.length || 0})</span>
                  <span>Owner: <strong className="text-slate-800">{t.createdByName || 'You'}</strong></span>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-3">
                  {t.members?.map((m) => (
                    <div
                      key={m.id}
                      className="p-3 bg-slate-50/70 border border-slate-200 rounded-lg flex items-center justify-between gap-3"
                    >
                      <div className="flex items-center gap-2.5 min-w-0">
                        <div className="w-8 h-8 rounded-full bg-indigo-100 text-indigo-700 font-bold flex items-center justify-center text-xs shrink-0">
                          {(m.name?.charAt(0) || 'U').toUpperCase()}
                        </div>
                        <div className="min-w-0">
                          <p className="text-xs font-semibold text-slate-900 truncate">{m.name}</p>
                          <p className="text-[11px] text-slate-500 truncate">{m.email}</p>
                        </div>
                      </div>

                      <Badge variant={m.roleInTeam === 'OWNER' || m.roleInTeam === 'LEAD' ? 'primary' : 'default'} size="xs">
                        {m.roleInTeam}
                      </Badge>
                    </div>
                  ))}
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}

      {/* Create Team Modal */}
      <Modal
        isOpen={createModalOpen}
        onClose={() => setCreateModalOpen(false)}
        title="Create Engineering Team"
        subtitle="Group developers and assign shared code review metrics"
      >
        <form onSubmit={handleCreateTeam} className="space-y-4">
          <Input
            label="Team Name"
            id="t-name"
            placeholder="e.g. Core Infrastructure"
            value={teamName}
            onChange={(e) => setTeamName(e.target.value)}
            required
          />

          <Input
            label="Description"
            id="t-desc"
            placeholder="What does this team build and review?"
            value={teamDesc}
            onChange={(e) => setTeamDesc(e.target.value)}
          />

          <div className="flex justify-end gap-3 pt-3 border-t border-slate-100">
            <Button variant="outline" size="sm" onClick={() => setCreateModalOpen(false)}>
              Cancel
            </Button>
            <Button type="submit" size="sm" loading={savingTeam} icon={FiPlus}>
              Create Team
            </Button>
          </div>
        </form>
      </Modal>

      {/* Add Member Modal */}
      <Modal
        isOpen={memberModalOpen}
        onClose={() => setMemberModalOpen(false)}
        title="Add Team Member"
        subtitle="Invite an existing platform user by email"
      >
        <form onSubmit={handleAddMember} className="space-y-4">
          <Input
            label="User Email"
            id="m-email"
            type="email"
            placeholder="colleague@codereview.ai"
            value={memberEmail}
            onChange={(e) => setMemberEmail(e.target.value)}
            required
            icon={FiMail}
            helperText="The user must already be registered on the platform."
          />

          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">
              Role in Team
            </label>
            <select
              value={memberRole}
              onChange={(e) => setMemberRole(e.target.value)}
              className="block w-full rounded-lg border border-slate-300 px-3 py-2 text-sm text-slate-900 bg-white focus:outline-none focus:ring-1 focus:ring-indigo-500"
            >
              <option value="MEMBER">Member (Standard Contributor)</option>
              <option value="REVIEWER">Reviewer (Lead Reviewer)</option>
              <option value="LEAD">Tech Lead</option>
            </select>
          </div>

          <div className="flex justify-end gap-3 pt-3 border-t border-slate-100">
            <Button variant="outline" size="sm" onClick={() => setMemberModalOpen(false)}>
              Cancel
            </Button>
            <Button type="submit" size="sm" loading={savingMember} icon={FiUserPlus}>
              Add to Team
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default TeamPage;
