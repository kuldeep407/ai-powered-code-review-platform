import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { reviewApi } from '../services/reviewApi';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import Badge from '../components/common/Badge';
import SeverityBadge from '../components/common/SeverityBadge';
import StatusBadge from '../components/common/StatusBadge';
import Loader from '../components/common/Loader';
import ErrorState from '../components/common/ErrorState';
import {
  FiArrowLeft,
  FiStar,
  FiCheckCircle,
  FiAlertCircle,
  FiShield,
  FiCode,
  FiGitPullRequest,
  FiCpu
} from 'react-icons/fi';

export const ReviewDetailPage = () => {
  const { id } = useParams();
  const [review, setReview] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchReview = async () => {
    try {
      setLoading(true);
      setError(null);
      const res = await reviewApi.getReviewById(id);
      setReview(res.data);
    } catch (err) {
      setError(err.message || 'Failed to load code review details');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReview();
  }, [id]);

  if (loading) {
    return <Loader text="Loading code review report..." />;
  }

  if (error) {
    return <ErrorState message={error} onRetry={fetchReview} />;
  }

  if (!review) {
    return <ErrorState title="Not Found" message="The requested review could not be found." />;
  }

  return (
    <div className="space-y-6">
      {/* Back button */}
      <div>
        <Link
          to="/reviews"
          className="inline-flex items-center gap-1.5 text-xs font-semibold text-slate-500 hover:text-slate-800 transition-colors"
        >
          <FiArrowLeft className="h-4 w-4" />
          <span>Back to reviews</span>
        </Link>
      </div>

      {/* Overview Card */}
      <Card padding="p-6">
        <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-4">
          <div>
            <div className="flex items-center gap-2 mb-2">
              <span className="text-xs font-bold text-indigo-700 bg-indigo-50 px-2.5 py-0.5 rounded-md border border-indigo-100">
                {review.repositoryName}
              </span>
              <span className="text-xs font-bold text-slate-400">#{review.prNumber}</span>
              <StatusBadge status={review.status} />
            </div>

            <h1 className="text-xl sm:text-2xl font-bold text-slate-900 tracking-tight">
              {review.prTitle || `Pull Request #${review.prNumber}`}
            </h1>

            <p className="text-xs text-slate-500 mt-2">
              Analyzed by AI on {review.createdAt ? new Date(review.createdAt).toLocaleDateString() : 'Recent'}
            </p>
          </div>

          <div className="flex sm:flex-col items-end gap-2 shrink-0">
            <div className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-indigo-50 border border-indigo-100 text-indigo-700 font-extrabold text-lg">
              <FiStar className="h-4 w-4 text-indigo-500" />
              <span>Score: {review.score}/10</span>
            </div>
            {review.pullRequestId && (
              <Link to={`/pull-requests/${review.pullRequestId}`}>
                <Button variant="outline" size="sm" icon={FiGitPullRequest}>
                  View PR
                </Button>
              </Link>
            )}
          </div>
        </div>
      </Card>

      {/* AI Disclaimer */}
      <div className="p-3.5 bg-amber-50/70 border border-amber-200 rounded-xl flex items-center justify-between text-xs text-amber-900">
        <div className="flex items-center gap-2">
          <FiShield className="h-4 w-4 text-amber-600 shrink-0" />
          <span>
            <strong>AI-generated analysis:</strong> Generated via Groq Llama 3.3. Verify code changes before deploying.
          </span>
        </div>
      </div>

      {/* Executive Summary */}
      <Card title="Executive AI Summary" subtitle="Architectural overview and risk assessment">
        <p className="text-sm text-slate-700 leading-relaxed">
          {review.summary}
        </p>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-6 pt-5 border-t border-slate-100">
          <div>
            <h4 className="text-xs font-bold text-emerald-800 uppercase tracking-wider mb-2 flex items-center gap-1.5">
              <FiCheckCircle className="h-3.5 w-3.5 text-emerald-600" />
              Key Strengths
            </h4>
            <ul className="space-y-1.5">
              {review.strengths?.map((str, idx) => (
                <li key={idx} className="text-xs text-slate-600 flex items-start gap-1.5">
                  <span className="text-emerald-500 font-bold">•</span>
                  <span>{str}</span>
                </li>
              ))}
            </ul>
          </div>

          <div>
            <h4 className="text-xs font-bold text-amber-800 uppercase tracking-wider mb-2 flex items-center gap-1.5">
              <FiAlertCircle className="h-3.5 w-3.5 text-amber-600" />
              Areas for Improvement
            </h4>
            <ul className="space-y-1.5">
              {review.improvements?.map((imp, idx) => (
                <li key={idx} className="text-xs text-slate-600 flex items-start gap-1.5">
                  <span className="text-amber-500 font-bold">•</span>
                  <span>{imp}</span>
                </li>
              ))}
            </ul>
          </div>
        </div>
      </Card>

      {/* Findings */}
      <div className="space-y-3">
        <h2 className="text-base font-bold text-slate-900">
          Findings & Action Items ({review.findings?.length || 0})
        </h2>

        {(!review.findings || review.findings.length === 0) ? (
          <Card padding="p-6">
            <div className="text-center py-4">
              <FiCheckCircle className="h-8 w-8 text-emerald-500 mx-auto mb-2" />
              <p className="text-sm font-semibold text-slate-800">Clean code review!</p>
              <p className="text-xs text-slate-500">No issues flagged by the AI engine.</p>
            </div>
          </Card>
        ) : (
          review.findings.map((f, idx) => (
            <Card key={f.id || idx} padding="p-5" className="hover:border-slate-300 transition-colors">
              <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-2 mb-2">
                <div className="flex flex-wrap items-center gap-2">
                  <Badge variant="primary" size="xs">
                    {f.type}
                  </Badge>
                  <SeverityBadge severity={f.severity} />
                  <h3 className="text-sm font-bold text-slate-900">{f.title}</h3>
                </div>

                {f.filePath && (
                  <div className="text-[11px] font-mono text-slate-500 bg-slate-100 px-2 py-0.5 rounded shrink-0">
                    {f.filePath}{f.lineNumber ? `:${f.lineNumber}` : ''}
                  </div>
                )}
              </div>

              <p className="text-xs text-slate-600 mt-2 leading-relaxed">
                {f.description}
              </p>

              {f.suggestion && (
                <div className="mt-3 p-3 bg-slate-50 border border-slate-200 rounded-lg">
                  <p className="text-[11px] font-semibold text-slate-700 mb-1 flex items-center gap-1.5">
                    <FiCode className="h-3.5 w-3.5 text-indigo-600" />
                    Recommended Remediation:
                  </p>
                  <p className="text-xs text-slate-800 font-mono whitespace-pre-wrap bg-white p-2 rounded border border-slate-200">
                    {f.suggestion}
                  </p>
                </div>
              )}
            </Card>
          ))
        )}
      </div>
    </div>
  );
};

export default ReviewDetailPage;
