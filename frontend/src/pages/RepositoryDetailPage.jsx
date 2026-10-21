import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { repositoryApi } from '../services/repositoryApi';
import { pullRequestApi } from '../services/pullRequestApi';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import Badge from '../components/common/Badge';
import StatusBadge from '../components/common/StatusBadge';
import Loader from '../components/common/Loader';
import EmptyState from '../components/common/EmptyState';
import ErrorState from '../components/common/ErrorState';
import { toast } from 'react-toastify';
import {
  FiFolder,
  FiGitPullRequest,
  FiRefreshCw,
  FiTrash2,
  FiArrowLeft,
  FiLock,
  FiGlobe,
  FiCpu,
  FiStar,
  FiArrowRight
} from 'react-icons/fi';

export const RepositoryDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [repository, setRepository] = useState(null);
  const [pullRequests, setPullRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [syncingPrs, setSyncingPrs] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [error, setError] = useState(null);

  const fetchData = async () => {
    try {
      setLoading(true);
      setError(null);
      const [repoRes, prsRes] = await Promise.all([
        repositoryApi.getRepositoryById(id),
        pullRequestApi.getPullRequestsByRepo(id),
      ]);
      setRepository(repoRes.data);
      setPullRequests(prsRes.data || []);
    } catch (err) {
      setError(err.message || 'Failed to load repository details');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [id]);

  const handleSyncPrs = async () => {
    try {
      setSyncingPrs(true);
      const res = await pullRequestApi.syncPullRequests(id);
      toast.success(res.message || 'Pull requests synchronized with GitHub');
      setPullRequests(res.data || []);
    } catch (err) {
      toast.error(err.message || 'Failed to sync pull requests');
    } finally {
      setSyncingPrs(false);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm(`Are you sure you want to remove repository ${repository?.name}?`)) {
      return;
    }
    try {
      setDeleting(true);
      await repositoryApi.deleteRepository(id);
      toast.success('Repository removed successfully');
      navigate('/repositories');
    } catch (err) {
      toast.error(err.message || 'Failed to delete repository');
      setDeleting(false);
    }
  };

  if (loading) {
    return <Loader text="Loading repository and pull requests..." />;
  }

  if (error) {
    return <ErrorState message={error} onRetry={fetchData} />;
  }

  return (
    <div className="space-y-6">
      {/* Back button & Breadcrumb */}
      <div>
        <Link
          to="/repositories"
          className="inline-flex items-center gap-1.5 text-xs font-semibold text-slate-500 hover:text-slate-800 transition-colors"
        >
          <FiArrowLeft className="h-4 w-4" />
          <span>Back to repositories</span>
        </Link>
      </div>

      {/* Repo Overview Header Card */}
      <Card padding="p-6">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="flex items-start gap-4">
            <div className="p-3 bg-indigo-50 text-indigo-600 rounded-xl shrink-0 mt-0.5">
              <FiFolder className="h-7 w-7" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h1 className="text-xl font-bold text-slate-900 tracking-tight">{repository?.name}</h1>
                <Badge variant={repository?.private ? 'warning' : 'default'} size="xs">
                  {repository?.private ? 'Private' : 'Public'}
                </Badge>
              </div>
              <p className="text-xs font-medium text-slate-400 mt-0.5">{repository?.fullName}</p>
              <p className="text-sm text-slate-600 mt-2 max-w-2xl">{repository?.description || 'No description provided.'}</p>
            </div>
          </div>

          <div className="flex sm:flex-col items-end gap-2 shrink-0">
            <Button
              variant="secondary"
              size="sm"
              onClick={handleSyncPrs}
              loading={syncingPrs}
              icon={FiRefreshCw}
            >
              Sync PRs
            </Button>
            <Button
              variant="outline"
              size="sm"
              onClick={handleDelete}
              loading={deleting}
              className="text-rose-600 border-rose-200 hover:bg-rose-50 hover:text-rose-700"
              icon={FiTrash2}
            >
              Delete Repo
            </Button>
          </div>
        </div>

        <div className="mt-6 pt-4 border-t border-slate-100 flex flex-wrap items-center gap-6 text-xs text-slate-500">
          <div>Default Branch: <code className="font-semibold text-slate-800 bg-slate-100 px-1.5 py-0.5 rounded">{repository?.defaultBranch}</code></div>
          <div>Total Pull Requests: <strong className="text-slate-800">{pullRequests.length}</strong></div>
          {repository?.cloneUrl && (
            <div className="truncate max-w-sm">Clone: <span className="text-slate-600">{repository.cloneUrl}</span></div>
          )}
        </div>
      </Card>

      {/* Pull Requests List */}
      <div className="space-y-4">
        <div className="flex items-center justify-between">
          <h2 className="text-base font-bold text-slate-900">Pull Requests in this Repository</h2>
          <span className="text-xs text-slate-500">{pullRequests.length} total pull requests</span>
        </div>

        {pullRequests.length === 0 ? (
          <EmptyState
            icon={FiGitPullRequest}
            title="No pull requests found"
            description="Sync pull requests from GitHub or create one to start automated code review."
            actionText="Sync PRs from GitHub"
            onAction={handleSyncPrs}
            actionIcon={FiRefreshCw}
          />
        ) : (
          <div className="bg-white border border-slate-200 rounded-xl overflow-hidden shadow-xs divide-y divide-slate-100">
            {pullRequests.map((pr) => (
              <div key={pr.id} className="p-4 sm:p-5 flex flex-col sm:flex-row sm:items-center justify-between gap-4 hover:bg-slate-50/60 transition-colors">
                <div className="min-w-0 flex-1">
                  <div className="flex items-center gap-2 mb-1.5">
                    <span className="text-xs font-bold text-slate-400">#{pr.prNumber}</span>
                    <StatusBadge status={pr.status} />
                    {pr.hasReview && (
                      <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded text-[11px] font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
                        <FiStar className="h-3 w-3" />
                        <span>Score: {pr.latestScore}/10</span>
                      </span>
                    )}
                  </div>

                  <Link
                    to={`/pull-requests/${pr.id}`}
                    className="text-sm font-semibold text-slate-900 hover:text-indigo-600 transition-colors line-clamp-1"
                  >
                    {pr.title}
                  </Link>

                  <div className="flex items-center gap-3 mt-2 text-xs text-slate-500">
                    <span>By: <strong className="text-slate-700">{pr.author}</strong></span>
                    <span>•</span>
                    <span><code>{pr.sourceBranch}</code> → <code>{pr.targetBranch}</code></span>
                  </div>
                </div>

                <div className="flex items-center gap-2 shrink-0">
                  <Link to={`/pull-requests/${pr.id}`}>
                    <Button variant="primary" size="sm" icon={FiCpu}>
                      {pr.hasReview ? 'View AI Review' : 'Run AI Analysis'}
                    </Button>
                  </Link>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default RepositoryDetailPage;
