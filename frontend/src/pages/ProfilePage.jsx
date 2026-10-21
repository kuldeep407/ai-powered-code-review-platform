import React from 'react';
import { useAuth } from '../context/AuthContext';
import { repositoryApi } from '../services/repositoryApi';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import Badge from '../components/common/Badge';
import { toast } from 'react-toastify';
import {
  FiUser,
  FiMail,
  FiShield,
  FiGithub,
  FiCalendar,
  FiCheckCircle,
  FiAlertCircle
} from 'react-icons/fi';

export const ProfilePage = () => {
  const { user, updateUserData } = useAuth();

  const handleDisconnectGithub = async () => {
    if (!window.confirm('Are you sure you want to disconnect your GitHub integration?')) {
      return;
    }
    try {
      await repositoryApi.disconnectGithub();
      toast.info('GitHub account disconnected');
      updateUserData({ ...user, githubConnected: false, githubUsername: null, avatarUrl: null });
    } catch (err) {
      toast.error(err.message || 'Failed to disconnect GitHub');
    }
  };

  return (
    <div className="space-y-6 max-w-4xl">
      {/* Header */}
      <div className="pb-2 border-b border-slate-200">
        <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Developer Profile</h1>
        <p className="text-sm text-slate-500 mt-1">
          Personal credentials, role permissions, and linked third-party providers.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* User Card */}
        <Card className="md:col-span-1 text-center" padding="p-6">
          <div className="w-20 h-20 rounded-full bg-indigo-100 text-indigo-700 font-bold text-2xl flex items-center justify-center mx-auto border-2 border-indigo-200 overflow-hidden shadow-xs mb-3">
            {user?.avatarUrl ? (
              <img src={user.avatarUrl} alt={user.name} className="w-full h-full object-cover" />
            ) : (
              (user?.name?.charAt(0) || 'U').toUpperCase()
            )}
          </div>

          <h3 className="text-base font-bold text-slate-900">{user?.name}</h3>
          <p className="text-xs text-slate-500 mt-0.5">{user?.email}</p>

          <div className="mt-3">
            <Badge variant="primary" size="md">
              {user?.role || 'DEVELOPER'}
            </Badge>
          </div>

          <div className="mt-6 pt-4 border-t border-slate-100 text-xs text-slate-500 flex items-center justify-center gap-1.5">
            <FiCalendar className="h-3.5 w-3.5 text-slate-400" />
            <span>Member since {user?.createdAt ? new Date(user.createdAt).toLocaleDateString() : '2026'}</span>
          </div>
        </Card>

        {/* Details & Integrations */}
        <div className="md:col-span-2 space-y-5">
          <Card title="Account Information" subtitle="System role and authentication details">
            <div className="space-y-3 text-sm">
              <div className="flex justify-between py-2 border-b border-slate-100">
                <span className="text-slate-500">Full Name</span>
                <strong className="text-slate-800">{user?.name}</strong>
              </div>
              <div className="flex justify-between py-2 border-b border-slate-100">
                <span className="text-slate-500">Email Address</span>
                <strong className="text-slate-800">{user?.email}</strong>
              </div>
              <div className="flex justify-between py-2 border-b border-slate-100">
                <span className="text-slate-500">Platform Role</span>
                <strong className="text-indigo-600 font-semibold">{user?.role}</strong>
              </div>
              <div className="flex justify-between py-2">
                <span className="text-slate-500">Auth Method</span>
                <strong className="text-slate-800">JWT Bearer (Stateless BCrypt)</strong>
              </div>
            </div>
          </Card>

          <Card title="GitHub Integration" subtitle="Repository synchronization and webhooks">
            {user?.githubConnected ? (
              <div className="flex items-center justify-between p-4 bg-slate-50 border border-slate-200 rounded-xl">
                <div className="flex items-center gap-3">
                  <div className="p-2.5 bg-slate-900 text-white rounded-lg">
                    <FiGithub className="h-5 w-5" />
                  </div>
                  <div>
                    <h4 className="text-xs font-bold text-slate-900">Connected as @{user.githubUsername}</h4>
                    <p className="text-[11px] text-emerald-600 font-medium flex items-center gap-1 mt-0.5">
                      <FiCheckCircle className="h-3 w-3" />
                      Token active and synchronized
                    </p>
                  </div>
                </div>

                <Button
                  variant="outline"
                  size="sm"
                  onClick={handleDisconnectGithub}
                  className="text-rose-600 border-rose-200 hover:bg-rose-50"
                >
                  Disconnect
                </Button>
              </div>
            ) : (
              <div className="flex items-center justify-between p-4 bg-slate-50 border border-slate-200 rounded-xl">
                <div className="flex items-center gap-3">
                  <div className="p-2.5 bg-slate-200 text-slate-600 rounded-lg">
                    <FiGithub className="h-5 w-5" />
                  </div>
                  <div>
                    <h4 className="text-xs font-bold text-slate-800">GitHub Not Connected</h4>
                    <p className="text-[11px] text-slate-500 mt-0.5">Link your account to auto-sync pull requests.</p>
                  </div>
                </div>

                <Button
                  variant="secondary"
                  size="sm"
                  onClick={() => window.location.reload()}
                >
                  Connect in Topbar
                </Button>
              </div>
            )}
          </Card>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;
