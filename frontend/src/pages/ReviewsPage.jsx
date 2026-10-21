import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { reviewApi } from '../services/reviewApi';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import Input from '../components/common/Input';
import StatusBadge from '../components/common/StatusBadge';
import Loader from '../components/common/Loader';
import EmptyState from '../components/common/EmptyState';
import ErrorState from '../components/common/ErrorState';
import {
  FiCheckSquare,
  FiSearch,
  FiStar,
  FiArrowRight,
  FiCpu,
  FiCalendar
} from 'react-icons/fi';

export const ReviewsPage = () => {
  const [reviews, setReviews] = useState([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchReviews = async () => {
    try {
      setLoading(true);
      setError(null);
      const res = await reviewApi.getRecentReviews();
      setReviews(res.data || []);
    } catch (err) {
      setError(err.message || 'Failed to fetch reviews');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReviews();
  }, []);

  const filteredReviews = reviews.filter((r) =>
    (r.prTitle && r.prTitle.toLowerCase().includes(search.toLowerCase())) ||
    (r.repositoryName && r.repositoryName.toLowerCase().includes(search.toLowerCase())) ||
    (r.summary && r.summary.toLowerCase().includes(search.toLowerCase()))
  );

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 pb-2 border-b border-slate-200">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">AI Code Review History</h1>
          <p className="text-sm text-slate-500 mt-1">
            Browse complete audit logs, quality scores, and recommendations.
          </p>
        </div>
        <Link to="/pull-requests">
          <Button variant="primary" size="sm" icon={FiCpu}>
            Review a Pull Request
          </Button>
        </Link>
      </div>

      {/* Search Input */}
      <div className="max-w-md">
        <Input
          placeholder="Filter reviews by PR title or repository..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          icon={FiSearch}
        />
      </div>

      {/* Content */}
      {loading ? (
        <Loader text="Loading code review logs..." />
      ) : error ? (
        <ErrorState message={error} onRetry={fetchReviews} />
      ) : filteredReviews.length === 0 ? (
        <EmptyState
          icon={FiCheckSquare}
          title="No reviews found"
          description={
            search
              ? 'No reviews match your search query.'
              : 'No AI code reviews have been performed yet. Analyze a pull request to create your first review.'
          }
          actionText={!search ? 'Browse Pull Requests' : undefined}
          onAction={!search ? () => window.location.href = '/pull-requests' : undefined}
        />
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {filteredReviews.map((rev) => (
            <Card
              key={rev.id}
              className="hover:border-indigo-200 transition-colors flex flex-col justify-between"
              padding="p-5"
            >
              <div>
                <div className="flex items-start justify-between gap-2 mb-2">
                  <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">
                    {rev.repositoryName}
                  </span>
                  <div className="flex items-center gap-1 px-2 py-0.5 rounded-lg bg-indigo-50 text-indigo-700 font-bold text-xs">
                    <FiStar className="h-3 w-3 text-indigo-500" />
                    <span>{rev.score}/10</span>
                  </div>
                </div>

                <Link
                  to={`/reviews/${rev.id}`}
                  className="text-sm font-bold text-slate-900 hover:text-indigo-600 transition-colors line-clamp-1"
                >
                  {rev.prTitle || `Pull Request #${rev.prNumber}`}
                </Link>

                <p className="text-xs text-slate-500 mt-2 line-clamp-3 leading-relaxed">
                  {rev.summary}
                </p>
              </div>

              <div className="mt-4 pt-3 border-t border-slate-100 flex items-center justify-between">
                <div className="text-[11px] text-slate-400">
                  <span>{rev.findings?.length || 0} findings</span>
                </div>

                <Link
                  to={`/reviews/${rev.id}`}
                  className="inline-flex items-center gap-1 text-xs font-semibold text-indigo-600 hover:text-indigo-800"
                >
                  <span>Read report</span>
                  <FiArrowRight className="h-3.5 w-3.5" />
                </Link>
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
};

export default ReviewsPage;
