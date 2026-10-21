import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { pullRequestApi } from '../services/pullRequestApi';
import { reviewApi } from '../services/reviewApi';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import Input from '../components/common/Input';
import StatusBadge from '../components/common/StatusBadge';
import SeverityBadge from '../components/common/SeverityBadge';
import Badge from '../components/common/Badge';
import Loader from '../components/common/Loader';
import ErrorState from '../components/common/ErrorState';
import { toast } from 'react-toastify';
import {
  FiArrowLeft,
  FiGitPullRequest,
  FiCpu,
  FiStar,
  FiCheckCircle,
  FiAlertCircle,
  FiShield,
  FiClock,
  FiExternalLink,
  FiFileText,
  FiCode
} from 'react-icons/fi';

export const PullRequestDetailPage = () => {
  const { id } = useParams();
  const [pullRequest, setPullRequest] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [activeReview, setActiveReview] = useState(null);
  const [loading, setLoading] = useState(true);
  const [analyzing, setAnalyzing] = useState(false);
  const [customFocus, setCustomFocus] = useState('');
  const [error, setError] = useState(null);

  const fetchData = async () => {
    try {
      setLoading(true);
      setError(null);
      const [prRes, reviewsRes] = await Promise.all([
        pullRequestApi.getPullRequestById(id),
        reviewApi.getReviewsForPullRequest(id),
      ]);
      setPullRequest(prRes.data);
      const revs = reviewsRes.data || [];
      setReviews(revs);
      if (revs.length > 0) {
        setActiveReview(revs[0]);
      }
    } catch (err) {
      setError(err.message || 'Failed to load pull request details');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [id]);

  const handleAnalyze = async () => {
    try {
      setAnalyzing(true);
      toast.info('Groq AI code review analysis started...');
      const res = await reviewApi.analyzePullRequest(id, customFocus);
      toast.success('AI analysis completed successfully!');
      const newReview = res.data;
      setReviews((prev) => [newReview, ...prev]);
      setActiveReview(newReview);
      setCustomFocus('');
    } catch (err) {
      toast.error(err.message || 'AI analysis failed');
    } finally {
      setAnalyzing(false);
    }
  };

  if (loading) {
    return <Loader text="Loading pull request details and analysis..." />;
  }

  if (error) {
    return <ErrorState message={error} onRetry={fetchData} />;
  }

  return (
    <div className="space-y-6">
      {/* Back button */}
      <div>
        <Link
          to="/pull-requests"
          className="inline-flex items-center gap-1.5 text-xs font-semibold text-slate-500 hover:text-slate-800 transition-colors"
        >
          <FiArrowLeft className="h-4 w-4" />
          <span>Back to all pull requests</span>
        </Link>
      </div>

      {/* PR Header Card */}
      <Card padding="p-6">
        <div className="flex flex-col lg:flex-row lg:items-start justify-between gap-6">
          <div className="min-w-0 flex-1">
            <div className="flex flex-wrap items-center gap-2 mb-2">
              <span className="text-xs font-bold text-indigo-700 bg-indigo-50 px-2.5 py-1 rounded-md border border-indigo-100">
                {pullRequest?.repositoryName}
              </span>
              <span className="text-xs font-bold text-slate-400">#{pullRequest?.prNumber}</span>
              <StatusBadge status={pullRequest?.status} />
              {activeReview && (
                <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-md text-xs font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
                  <FiStar className="h-3.5 w-3.5" />
                  <span>Quality Score: {activeReview.score}/10</span>
                </span>
              )}
            </div>

            <h1 className="text-xl sm:text-2xl font-bold text-slate-900 tracking-tight">
              {pullRequest?.title}
            </h1>

            <div className="flex flex-wrap items-center gap-4 mt-3 text-xs text-slate-500">
              <span>Author: <strong className="text-slate-800">{pullRequest?.author}</strong></span>
              <span>•</span>
              <span>Branch: <code>{pullRequest?.sourceBranch}</code> into <code>{pullRequest?.targetBranch}</code></span>
              {pullRequest?.prUrl && (
                <>
                  <span>•</span>
                  <a
                    href={pullRequest.prUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="inline-flex items-center gap-1 text-indigo-600 hover:text-indigo-800 font-medium"
                  >
                    <span>GitHub PR</span>
                    <FiExternalLink className="h-3 w-3" />
                  </a>
                </>
              )}
            </div>

            {pullRequest?.description && (
              <p className="mt-3 text-xs sm:text-sm text-slate-600 bg-slate-50 p-3 rounded-lg border border-slate-100">
                {pullRequest.description}
              </p>
            )}
          </div>

          {/* Trigger AI Analysis Box */}
          <div className="w-full lg:w-80 shrink-0 bg-slate-50/70 border border-slate-200 rounded-xl p-4 flex flex-col justify-between">
            <div>
              <div className="flex items-center gap-2 mb-2 text-xs font-semibold text-slate-700">
                <FiCpu className="h-4 w-4 text-indigo-600" />
                <span>AI Review Engine (Groq Llama 3.3)</span>
              </div>
              <Input
                placeholder="Optional focus (e.g. security, edge cases)"
                value={customFocus}
                onChange={(e) => setCustomFocus(e.target.value)}
                className="text-xs py-1.5"
              />
            </div>

            <div className="mt-3">
              <Button
                variant="primary"
                size="sm"
                onClick={handleAnalyze}
                loading={analyzing}
                className="w-full"
                icon={FiCpu}
              >
                {activeReview ? 'Re-Analyze with AI' : 'Analyze Pull Request'}
              </Button>
            </div>
          </div>
        </div>
      </Card>

      {/* Review History Tabs if multiple reviews */}
      {reviews.length > 1 && (
        <div className="flex items-center gap-2 overflow-x-auto pb-1">
          <span className="text-xs font-medium text-slate-500 mr-2 shrink-0">Review Runs:</span>
          {reviews.map((rev, idx) => (
            <button
              key={rev.id}
              onClick={() => setActiveReview(rev)}
              className={`px-3 py-1.5 rounded-lg text-xs font-medium whitespace-nowrap transition-colors cursor-pointer ${
                activeReview?.id === rev.id
                  ? 'bg-indigo-600 text-white font-semibold'
                  : 'bg-white text-slate-600 border border-slate-200 hover:bg-slate-50'
              }`}
            >
              Run #{reviews.length - idx} • Score {rev.score}/10
            </button>
          ))}
        </div>
      )}

      {/* AI Review Report Display */}
      {!activeReview ? (
        <Card className="text-center py-10">
          <div className="max-w-md mx-auto">
            <div className="p-3 bg-indigo-50 text-indigo-600 rounded-full w-12 h-12 flex items-center justify-center mx-auto mb-3">
              <FiCpu className="h-6 w-6" />
            </div>
            <h3 className="text-base font-semibold text-slate-900">No AI review performed yet</h3>
            <p className="text-xs text-slate-500 mt-1 mb-5">
              Run the AI analysis to automatically inspect this pull request for bugs, security risks, performance issues, and maintainability concerns.
            </p>
            <Button
              variant="primary"
              size="md"
              onClick={handleAnalyze}
              loading={analyzing}
              icon={FiCpu}
            >
              Start AI Review Now
            </Button>
          </div>
        </Card>
      ) : (
        <div className="space-y-6">
          {/* AI Banner Disclaimer */}
          <div className="p-3.5 bg-amber-50/70 border border-amber-200 rounded-xl flex items-center justify-between text-xs text-amber-900">
            <div className="flex items-center gap-2">
              <FiShield className="h-4 w-4 text-amber-600 shrink-0" />
              <span>
                <strong>AI-generated analysis:</strong> Automated evaluation by Groq Llama 3.3 model. Verify critical suggestions before merging.
              </span>
            </div>
            <span className="text-[11px] text-amber-700 font-medium hidden sm:inline">
              Reviewed by {activeReview.reviewerName || 'System'}
            </span>
          </div>

          {/* Executive Summary & Score Card */}
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <div className="lg:col-span-2">
              <Card title="Executive AI Summary" subtitle="Overview of architectural impact and code changes">
                <p className="text-sm text-slate-700 leading-relaxed">
                  {activeReview.summary}
                </p>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-5 pt-5 border-t border-slate-100">
                  {/* Strengths */}
                  <div>
                    <h4 className="text-xs font-bold text-emerald-800 uppercase tracking-wider mb-2 flex items-center gap-1.5">
                      <FiCheckCircle className="h-3.5 w-3.5 text-emerald-600" />
                      Key Strengths
                    </h4>
                    <ul className="space-y-1.5">
                      {activeReview.strengths?.map((str, idx) => (
                        <li key={idx} className="text-xs text-slate-600 flex items-start gap-1.5">
                          <span className="text-emerald-500 font-bold">•</span>
                          <span>{str}</span>
                        </li>
                      ))}
                    </ul>
                  </div>

                  {/* Areas for Improvement */}
                  <div>
                    <h4 className="text-xs font-bold text-amber-800 uppercase tracking-wider mb-2 flex items-center gap-1.5">
                      <FiAlertCircle className="h-3.5 w-3.5 text-amber-600" />
                      Suggested Improvements
                    </h4>
                    <ul className="space-y-1.5">
                      {activeReview.improvements?.map((imp, idx) => (
                        <li key={idx} className="text-xs text-slate-600 flex items-start gap-1.5">
                          <span className="text-amber-500 font-bold">•</span>
                          <span>{imp}</span>
                        </li>
                      ))}
                    </ul>
                  </div>
                </div>
              </Card>
            </div>

            {/* Score & Health Card */}
            <div className="lg:col-span-1">
              <Card title="Quality Scorecard" subtitle="Overall pull request health">
                <div className="text-center py-4">
                  <div className="inline-flex items-center justify-center w-24 h-24 rounded-full border-4 border-indigo-100 bg-indigo-50/50 mb-2">
                    <span className="text-3xl font-extrabold text-indigo-700">{activeReview.score}</span>
                    <span className="text-sm text-slate-400 font-medium">/10</span>
                  </div>
                  <p className="text-xs font-semibold text-slate-800">
                    {activeReview.score >= 8 ? 'High Code Quality' : activeReview.score >= 5 ? 'Moderate Code Quality' : 'Needs Significant Revision'}
                  </p>
                  <p className="text-[11px] text-slate-400 mt-1">
                    Based on security, bug density, and maintainability.
                  </p>
                </div>

                <div className="pt-4 border-t border-slate-100 space-y-2 text-xs">
                  <div className="flex justify-between text-slate-600">
                    <span>Total Findings:</span>
                    <strong className="text-slate-900">{activeReview.findings?.length || 0}</strong>
                  </div>
                  <div className="flex justify-between text-slate-600">
                    <span>Analysis Status:</span>
                    <strong className="text-emerald-600 font-semibold">{activeReview.status}</strong>
                  </div>
                </div>
              </Card>
            </div>
          </div>

          {/* Structured Findings List */}
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <h2 className="text-base font-bold text-slate-900">
                Detailed Findings & Recommendations ({activeReview.findings?.length || 0})
              </h2>
            </div>

            {(!activeReview.findings || activeReview.findings.length === 0) ? (
              <Card padding="p-6">
                <div className="text-center py-4">
                  <FiCheckCircle className="h-8 w-8 text-emerald-500 mx-auto mb-2" />
                  <p className="text-sm font-semibold text-slate-800">No issues found!</p>
                  <p className="text-xs text-slate-500 mt-0.5">The pull request passed all AI inspection rules.</p>
                </div>
              </Card>
            ) : (
              <div className="space-y-3">
                {activeReview.findings.map((f, idx) => (
                  <Card key={f.id || idx} padding="p-5" className="hover:border-slate-300 transition-colors">
                    <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-3 mb-2">
                      <div className="flex flex-wrap items-center gap-2">
                        <Badge variant="primary" size="xs">
                          {f.type}
                        </Badge>
                        <SeverityBadge severity={f.severity} />
                        <h3 className="text-sm font-bold text-slate-900">
                          {f.title}
                        </h3>
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
                          Recommended Fix / Suggestion:
                        </p>
                        <p className="text-xs text-slate-800 font-mono whitespace-pre-wrap bg-white p-2 rounded border border-slate-200">
                          {f.suggestion}
                        </p>
                      </div>
                    )}
                  </Card>
                ))}
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default PullRequestDetailPage;
