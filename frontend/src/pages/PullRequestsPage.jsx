import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { pullRequestApi } from '../services/pullRequestApi';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import Input from '../components/common/Input';
import StatusBadge from '../components/common/StatusBadge';
import Loader from '../components/common/Loader';
import EmptyState from '../components/common/EmptyState';
import ErrorState from '../components/common/ErrorState';
import {
  FiGitPullRequest,
  FiSearch,
  FiFilter,
  FiStar,
  FiCpu,
  FiClock,
  FiFolder,
  FiArrowRight
} from 'react-icons/fi';

export const PullRequestsPage = () => {
  const [pullRequests, setPullRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [error, setError] = useState(null);

  const fetchPullRequests = async () => {
    try {
      setLoading(true);
      setError(null);
      const res = await pullRequestApi.getAllPullRequests();
      setPullRequests(res.data || []);
    } catch (err) {
      setError(err.message || 'Failed to fetch pull requests');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPullRequests();
  }, []);

  const filteredPrs = pullRequests.filter((pr) => {
    const matchesSearch =
      pr.title.toLowerCase().includes(search.toLowerCase()) ||
      pr.author.toLowerCase().includes(search.toLowerCase()) ||
      (pr.repositoryName && pr.repositoryName.toLowerCase().includes(search.toLowerCase()));

    const matchesStatus =
      statusFilter === 'ALL' || pr.status === statusFilter;

    return matchesSearch && matchesStatus;
  });

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 pb-2 border-b border-slate-200">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Pull Requests</h1>
          <p className="text-sm text-slate-500 mt-1">
            Monitor incoming code changes and trigger instant AI architectural reviews.
          </p>
        </div>
        <Link to="/repositories">
          <Button variant="secondary" size="sm" icon={FiFolder}>
            Browse Repositories
          </Button>
        </Link>
      </div>

      {/* Filter and Search Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
        <div className="max-w-md w-full">
          <Input
            placeholder="Search pull requests by title, author, or repo..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            icon={FiSearch}
          />
        </div>

        <div className="flex items-center gap-1.5 p-1 bg-white border border-slate-200 rounded-lg shrink-0">
          {['ALL', 'OPEN', 'CLOSED'].map((st) => (
            <button
              key={st}
              onClick={() => setStatusFilter(st)}
              className={`px-3 py-1 rounded-md text-xs font-semibold transition-colors cursor-pointer ${
                statusFilter === st
                  ? 'bg-indigo-600 text-white'
                  : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
              }`}
            >
              {st}
            </button>
          ))}
        </div>
      </div>

      {/* PRs Table / List */}
      {loading ? (
        <Loader text="Loading pull requests..." />
      ) : error ? (
        <ErrorState message={error} onRetry={fetchPullRequests} />
      ) : filteredPrs.length === 0 ? (
        <EmptyState
          icon={FiGitPullRequest}
          title="No pull requests found"
          description={
            search || statusFilter !== 'ALL'
              ? 'No pull requests matched your filter parameters.'
              : 'Sync a repository or create pull requests to start reviewing code with AI.'
          }
        />
      ) : (
        <div className="bg-white border border-slate-200 rounded-xl overflow-hidden shadow-xs divide-y divide-slate-100">
          {filteredPrs.map((pr) => (
            <div
              key={pr.id}
              className="p-5 flex flex-col sm:flex-row sm:items-center justify-between gap-4 hover:bg-slate-50/60 transition-colors"
            >
              <div className="min-w-0 flex-1">
                <div className="flex flex-wrap items-center gap-2 mb-1.5">
                  <span className="text-xs font-bold text-indigo-700 bg-indigo-50 px-2 py-0.5 rounded border border-indigo-100">
                    {pr.repositoryName}
                  </span>
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

                <div className="flex flex-wrap items-center gap-3 mt-2 text-xs text-slate-500">
                  <span>Author: <strong className="text-slate-700">{pr.author}</strong></span>
                  <span>•</span>
                  <span>Branch: <code className="text-slate-700">{pr.sourceBranch}</code> → <code className="text-slate-700">{pr.targetBranch}</code></span>
                </div>
              </div>

              <div className="flex items-center gap-2 shrink-0">
                <Link to={`/pull-requests/${pr.id}`}>
                  <Button variant={pr.hasReview ? 'secondary' : 'primary'} size="sm" icon={FiCpu}>
                    {pr.hasReview ? 'Review Report' : 'Analyze PR'}
                  </Button>
                </Link>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default PullRequestsPage;
