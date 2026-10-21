import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { dashboardApi } from '../services/dashboardApi';
import StatCard from '../components/common/StatCard';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import Loader from '../components/common/Loader';
import EmptyState from '../components/common/EmptyState';
import ErrorState from '../components/common/ErrorState';
import StatusBadge from '../components/common/StatusBadge';
import {
  FiFolder,
  FiGitPullRequest,
  FiAlertTriangle,
  FiStar,
  FiActivity,
  FiCheckCircle,
  FiArrowRight,
  FiCpu,
  FiShield
} from 'react-icons/fi';

export const DashboardPage = () => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchStats = async () => {
    try {
      setLoading(true);
      setError(null);
      const res = await dashboardApi.getStats();
      setStats(res.data);
    } catch (err) {
      setError(err.message || 'Failed to load dashboard metrics');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStats();
  }, []);

  if (loading) {
    return <Loader size="lg" text="Loading developer workspace metrics..." />;
  }

  if (error) {
    return <ErrorState message={error} onRetry={fetchStats} />;
  }

  return (
    <div className="space-y-6">
      {/* Top Banner / Hero Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 pb-2 border-b border-slate-200">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Engineering Productivity Dashboard</h1>
          <p className="text-sm text-slate-500 mt-1">
            Real-time AI code review insights, quality scores, and security findings.
          </p>
        </div>
        <div className="flex items-center gap-2.5">
          <Link to="/repositories">
            <Button variant="secondary" size="sm" icon={FiFolder}>
              Repositories
            </Button>
          </Link>
          <Link to="/pull-requests">
            <Button variant="primary" size="sm" icon={FiCpu}>
              Analyze Pull Request
            </Button>
          </Link>
        </div>
      </div>

      {/* KPI Stats Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Connected Repos"
          value={stats?.totalRepositories ?? 0}
          icon={FiFolder}
          subtitle="Monitored GitHub repositories"
          iconColor="text-indigo-600 bg-indigo-50 border border-indigo-100"
        />

        <StatCard
          title="Pull Requests"
          value={stats?.totalPullRequests ?? 0}
          icon={FiGitPullRequest}
          subtitle={`${stats?.totalReviews ?? 0} analyzed by AI`}
          iconColor="text-blue-600 bg-blue-50 border border-blue-100"
        />

        <StatCard
          title="Issues Detected"
          value={stats?.totalIssuesDetected ?? 0}
          icon={FiAlertTriangle}
          subtitle={`${stats?.criticalOrHighIssues ?? 0} critical or high severity`}
          iconColor="text-amber-600 bg-amber-50 border border-amber-100"
          trend={stats?.criticalOrHighIssues > 0 ? 'Requires attention' : 'Optimal'}
          trendPositive={stats?.criticalOrHighIssues === 0}
        />

        <StatCard
          title="Avg Quality Score"
          value={`${stats?.averageCodeQualityScore ?? 0} / 10`}
          icon={FiStar}
          subtitle="Evaluated across all PRs"
          iconColor="text-emerald-600 bg-emerald-50 border border-emerald-100"
          trend="+0.8 this sprint"
          trendPositive={true}
        />
      </div>

      {/* Main Content Grid: Recent AI Reviews + Recent Activity */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left Column: Recent AI Reviews (2 cols) */}
        <div className="lg:col-span-2">
          <Card
            title="Recent AI Code Reviews"
            subtitle="Detailed automated evaluations and pull request scores"
            action={
              <Link to="/reviews" className="text-xs font-semibold text-indigo-600 hover:text-indigo-800 flex items-center gap-1">
                <span>View all</span>
                <FiArrowRight className="h-3.5 w-3.5" />
              </Link>
            }
          >
            {(!stats?.recentReviews || stats.recentReviews.length === 0) ? (
              <EmptyState
                title="No AI reviews generated yet"
                description="Trigger an AI analysis on any open pull request to see findings here."
              />
            ) : (
              <div className="divide-y divide-slate-100">
                {stats.recentReviews.map((rev) => (
                  <div key={rev.id} className="py-4 first:pt-0 last:pb-0 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                    <div className="min-w-0 flex-1">
                      <div className="flex items-center gap-2 mb-1">
                        <span className="text-xs font-semibold text-slate-500 uppercase">
                          {rev.repositoryName}
                        </span>
                        <span className="text-xs font-semibold text-slate-700">
                          #{rev.prNumber}
                        </span>
                        <StatusBadge status={rev.status} />
                      </div>
                      <Link
                        to={`/reviews/${rev.id}`}
                        className="text-sm font-semibold text-slate-900 hover:text-indigo-600 transition-colors line-clamp-1"
                      >
                        {rev.prTitle || `Pull Request #${rev.prNumber}`}
                      </Link>
                      <p className="text-xs text-slate-500 mt-1 line-clamp-2">
                        {rev.summary}
                      </p>
                    </div>

                    <div className="flex sm:flex-col items-center sm:items-end justify-between shrink-0 gap-1">
                      <div className="flex items-center gap-1.5 px-2.5 py-1 rounded-lg bg-indigo-50 text-indigo-700 font-bold text-sm">
                        <FiStar className="h-3.5 w-3.5 text-indigo-500" />
                        <span>{rev.score}/10</span>
                      </div>
                      <span className="text-[11px] text-slate-400">
                        {rev.findings?.length || 0} findings
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </Card>
        </div>

        {/* Right Column: Platform Audit & Recent Activity */}
        <div className="lg:col-span-1">
          <Card
            title="Live Activity Stream"
            subtitle="Automated workspace and team audit trail"
          >
            {(!stats?.recentActivities || stats.recentActivities.length === 0) ? (
              <EmptyState
                icon={FiActivity}
                title="No activity recorded"
                description="Audit actions will appear as you interact with repos and reviews."
              />
            ) : (
              <div className="flow-root">
                <ul className="-mb-8">
                  {stats.recentActivities.map((act, idx) => (
                    <li key={act.id || idx}>
                      <div className="relative pb-8">
                        {idx !== stats.recentActivities.length - 1 && (
                          <span
                            className="absolute top-4 left-4 -ml-px h-full w-0.5 bg-slate-200"
                            aria-hidden="true"
                          />
                        )}
                        <div className="relative flex space-x-3">
                          <div>
                            <span className="h-8 w-8 rounded-full bg-slate-100 flex items-center justify-center ring-4 ring-white text-slate-600">
                              <FiActivity className="h-4 w-4" />
                            </span>
                          </div>
                          <div className="flex min-w-0 flex-1 justify-between space-x-4 pt-1.5">
                            <div>
                              <p className="text-xs font-semibold text-slate-900">
                                {act.action?.replace('_', ' ')}
                              </p>
                              <p className="text-xs text-slate-500 mt-0.5">
                                {act.details}
                              </p>
                            </div>
                          </div>
                        </div>
                      </div>
                    </li>
                  ))}
                </ul>
              </div>
            )}
          </Card>
        </div>
      </div>
    </div>
  );
};

export default DashboardPage;
