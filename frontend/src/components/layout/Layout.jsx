import React, { useState } from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import Navbar from './Navbar';
import Modal from '../common/Modal';
import Input from '../common/Input';
import Button from '../common/Button';
import { repositoryApi } from '../../services/repositoryApi';
import { useAuth } from '../../context/AuthContext';
import { toast } from 'react-toastify';
import { FiGithub, FiKey, FiUser, FiInfo } from 'react-icons/fi';

export const Layout = () => {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [githubModalOpen, setGithubModalOpen] = useState(false);
  const [tokenInput, setTokenInput] = useState('');
  const [usernameInput, setUsernameInput] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { user, updateUserData } = useAuth();

  const handleConnectGithub = async (e) => {
    e.preventDefault();
    if (!tokenInput.trim()) {
      toast.error('Please enter a GitHub Personal Access Token or demo token');
      return;
    }

    try {
      setIsSubmitting(true);
      await repositoryApi.connectGithub({
        token: tokenInput.trim(),
        username: usernameInput.trim() || undefined,
      });

      toast.success('GitHub connected successfully! Repositories synchronized.');
      updateUserData({
        ...user,
        githubConnected: true,
        githubUsername: usernameInput.trim() || 'developer-user',
      });
      setGithubModalOpen(false);
      setTokenInput('');
      setUsernameInput('');
      // Trigger reload to refresh page data
      window.location.reload();
    } catch (err) {
      toast.error(err.message || 'Failed to connect GitHub account');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col md:flex-row antialiased text-slate-900">
      <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />

      <div className="flex-1 md:pl-64 flex flex-col min-w-0">
        <Navbar
          onOpenSidebar={() => setSidebarOpen(true)}
          onOpenGithubModal={() => setGithubModalOpen(true)}
        />

        <main className="flex-1 p-4 sm:p-6 lg:p-8 max-w-7xl w-full mx-auto">
          <Outlet />
        </main>
      </div>

      {/* Connect GitHub Modal */}
      <Modal
        isOpen={githubModalOpen}
        onClose={() => setGithubModalOpen(false)}
        title="Connect GitHub Account"
        subtitle="Authorize repositories and pull requests for AI code review"
      >
        <form onSubmit={handleConnectGithub} className="space-y-4">
          <div className="p-3 bg-indigo-50/70 border border-indigo-100 rounded-lg flex items-start gap-2.5 text-xs text-indigo-900">
            <FiInfo className="h-4 w-4 shrink-0 text-indigo-600 mt-0.5" />
            <span>
              Enter a GitHub Personal Access Token (classic or fine-grained with <code>repo</code> scope) or click <strong>Use Demo Token</strong> to simulate an enterprise GitHub sync immediately.
            </span>
          </div>

          <Input
            label="GitHub Personal Access Token"
            id="gh-token"
            type="password"
            placeholder="ghp_xxxxxxxxxxxx"
            value={tokenInput}
            onChange={(e) => setTokenInput(e.target.value)}
            required
            icon={FiKey}
            helperText="Stored securely and used strictly for repository read operations."
          />

          <Input
            label="GitHub Username (Optional)"
            id="gh-username"
            placeholder="e.g. alex-developer"
            value={usernameInput}
            onChange={(e) => setUsernameInput(e.target.value)}
            icon={FiUser}
          />

          <div className="flex items-center justify-between pt-3 border-t border-slate-100">
            <button
              type="button"
              onClick={() => {
                setTokenInput('ghp_demo_enterprise_token_sample');
                setUsernameInput('alex-dev');
              }}
              className="text-xs text-indigo-600 hover:text-indigo-800 font-medium cursor-pointer"
            >
              Fill Demo Credentials
            </button>
            <div className="flex gap-2">
              <Button variant="outline" size="sm" onClick={() => setGithubModalOpen(false)}>
                Cancel
              </Button>
              <Button type="submit" size="sm" loading={isSubmitting} icon={FiGithub}>
                Connect Account
              </Button>
            </div>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default Layout;
