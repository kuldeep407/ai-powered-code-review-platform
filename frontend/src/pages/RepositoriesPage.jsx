import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { repositoryApi } from '../services/repositoryApi';
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
  FiFolder,
  FiPlus,
  FiRefreshCw,
  FiSearch,
  FiGitPullRequest,
  FiExternalLink,
  FiLock,
  FiGlobe,
  FiArrowRight
} from 'react-icons/fi';

export const RepositoriesPage = () => {
  const [repositories, setRepositories] = useState([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [syncing, setSyncing] = useState(false);
  const [error, setError] = useState(null);

  // Add repo modal state
  const [addModalOpen, setAddModalOpen] = useState(false);
  const [repoName, setRepoName] = useState('');
  const [fullName, setFullName] = useState('');
  const [owner, setOwner] = useState('');
  const [description, setDescription] = useState('');
  const [defaultBranch, setDefaultBranch] = useState('main');
  const [isPrivate, setIsPrivate] = useState(false);
  const [savingRepo, setSavingRepo] = useState(false);

  const fetchRepositories = async () => {
    try {
      setLoading(true);
      setError(null);
      const res = await repositoryApi.getRepositories();
      setRepositories(res.data || []);
    } catch (err) {
      setError(err.message || 'Failed to load repositories');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRepositories();
  }, []);

  const handleSync = async () => {
    try {
      setSyncing(true);
      const res = await repositoryApi.syncGithub();
      toast.success(res.message || 'Repositories synced with GitHub');
      fetchRepositories();
    } catch (err) {
      toast.error(err.message || 'Failed to sync with GitHub');
    } finally {
      setSyncing(false);
    }
  };

  const handleAddRepo = async (e) => {
    e.preventDefault();
    if (!repoName || !fullName || !owner) return;

    try {
      setSavingRepo(true);
      await repositoryApi.addRepository({
        name: repoName,
        fullName: fullName,
        owner: owner,
        description: description,
        defaultBranch: defaultBranch || 'main',
        isPrivate: isPrivate,
      });

      toast.success('Repository added successfully');
      setAddModalOpen(false);
      setRepoName('');
      setFullName('');
      setOwner('');
      setDescription('');
      fetchRepositories();
    } catch (err) {
      toast.error(err.message || 'Failed to add repository');
    } finally {
      setSavingRepo(false);
    }
  };

  const filteredRepos = repositories.filter((r) =>
    r.name.toLowerCase().includes(search.toLowerCase()) ||
    r.fullName.toLowerCase().includes(search.toLowerCase()) ||
    (r.description && r.description.toLowerCase().includes(search.toLowerCase()))
  );

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 pb-2 border-b border-slate-200">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Connected Repositories</h1>
          <p className="text-sm text-slate-500 mt-1">
            Browse and manage GitHub repositories analyzed by AI review agents.
          </p>
        </div>
        <div className="flex items-center gap-2.5">
          <Button
            variant="secondary"
            size="sm"
            onClick={handleSync}
            loading={syncing}
            icon={FiRefreshCw}
          >
            Sync from GitHub
          </Button>
          <Button
            variant="primary"
            size="sm"
            onClick={() => setAddModalOpen(true)}
            icon={FiPlus}
          >
            Add Repository
          </Button>
        </div>
      </div>

      {/* Search Bar */}
      <div className="max-w-md">
        <Input
          placeholder="Filter repositories by name or description..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          icon={FiSearch}
        />
      </div>

      {/* Content Area */}
      {loading ? (
        <Loader text="Loading repositories..." />
      ) : error ? (
        <ErrorState message={error} onRetry={fetchRepositories} />
      ) : filteredRepos.length === 0 ? (
        <EmptyState
          title="No repositories found"
          description={
            search
              ? 'No repositories matched your search query.'
              : 'No repositories connected yet. Click Sync from GitHub or Add Repository to start reviewing.'
          }
          actionText={!search ? 'Sync from GitHub' : undefined}
          onAction={!search ? handleSync : undefined}
          actionIcon={FiRefreshCw}
        />
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {filteredRepos.map((repo) => (
            <Card key={repo.id} className="hover:border-indigo-200 transition-colors flex flex-col justify-between" padding="p-5">
              <div>
                <div className="flex items-start justify-between gap-2 mb-3">
                  <div className="flex items-center gap-2">
                    <div className="p-2 bg-indigo-50 text-indigo-600 rounded-lg">
                      <FiFolder className="h-5 w-5" />
                    </div>
                    <div>
                      <h3 className="text-sm font-bold text-slate-900 line-clamp-1">{repo.name}</h3>
                      <p className="text-xs text-slate-400">{repo.owner}</p>
                    </div>
                  </div>
                  <Badge variant={repo.private ? 'warning' : 'default'} size="xs">
                    {repo.private ? (
                      <span className="flex items-center gap-1">
                        <FiLock className="h-2.5 w-2.5" /> Private
                      </span>
                    ) : (
                      <span className="flex items-center gap-1">
                        <FiGlobe className="h-2.5 w-2.5" /> Public
                      </span>
                    )}
                  </Badge>
                </div>

                <p className="text-xs text-slate-500 line-clamp-2 min-h-8 mb-4">
                  {repo.description || 'No description provided for this repository.'}
                </p>
              </div>

              <div className="pt-4 border-t border-slate-100 flex items-center justify-between">
                <div className="flex items-center gap-3 text-xs text-slate-500">
                  <span className="flex items-center gap-1">
                    <FiGitPullRequest className="h-3.5 w-3.5 text-slate-400" />
                    <strong>{repo.totalPrCount}</strong> PRs
                  </span>
                  <span>•</span>
                  <span>Branch: <code className="text-indigo-600 font-semibold">{repo.defaultBranch}</code></span>
                </div>

                <Link
                  to={`/repositories/${repo.id}`}
                  className="inline-flex items-center gap-1 text-xs font-semibold text-indigo-600 hover:text-indigo-800"
                >
                  <span>Explore</span>
                  <FiArrowRight className="h-3.5 w-3.5" />
                </Link>
              </div>
            </Card>
          ))}
        </div>
      )}

      {/* Add Repository Modal */}
      <Modal
        isOpen={addModalOpen}
        onClose={() => setAddModalOpen(false)}
        title="Add New Repository"
        subtitle="Track and configure pull request analysis for a repository"
      >
        <form onSubmit={handleAddRepo} className="space-y-4">
          <Input
            label="Repository Name"
            id="repo-name"
            placeholder="e.g. backend-service"
            value={repoName}
            onChange={(e) => {
              setRepoName(e.target.value);
              if (owner) setFullName(`${owner}/${e.target.value}`);
            }}
            required
          />

          <Input
            label="Owner (GitHub Username / Org)"
            id="repo-owner"
            placeholder="e.g. your-org"
            value={owner}
            onChange={(e) => {
              setOwner(e.target.value);
              if (repoName) setFullName(`${e.target.value}/${repoName}`);
            }}
            required
          />

          <Input
            label="Full Name (owner/repo)"
            id="repo-fullname"
            placeholder="e.g. your-org/backend-service"
            value={fullName}
            onChange={(e) => setFullName(e.target.value)}
            required
          />

          <Input
            label="Default Branch"
            id="repo-branch"
            placeholder="main"
            value={defaultBranch}
            onChange={(e) => setDefaultBranch(e.target.value)}
          />

          <Input
            label="Description"
            id="repo-desc"
            placeholder="Brief description of the repository..."
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />

          <div className="flex items-center gap-2 pt-1">
            <input
              type="checkbox"
              id="is-private"
              checked={isPrivate}
              onChange={(e) => setIsPrivate(e.target.checked)}
              className="h-4 w-4 rounded border-slate-300 text-indigo-600 focus:ring-indigo-500 cursor-pointer"
            />
            <label htmlFor="is-private" className="text-xs font-medium text-slate-700 cursor-pointer">
              Mark as private repository
            </label>
          </div>

          <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-100">
            <Button variant="outline" size="sm" onClick={() => setAddModalOpen(false)}>
              Cancel
            </Button>
            <Button type="submit" size="sm" loading={savingRepo} icon={FiPlus}>
              Save Repository
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default RepositoriesPage;
