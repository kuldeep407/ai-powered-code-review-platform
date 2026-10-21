package com.example.codereview.service;

import com.example.codereview.dto.dashboard.DashboardStatsResponse;
import com.example.codereview.dto.review.ReviewResponse;
import com.example.codereview.entity.Activity;
import com.example.codereview.entity.CodeReview;
import com.example.codereview.enums.FindingSeverity;
import com.example.codereview.mapper.ReviewMapper;
import com.example.codereview.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DashboardService {

    private final RepositoryEntityRepository repositoryEntityRepository;
    private final PullRequestRepository pullRequestRepository;
    private final CodeReviewRepository codeReviewRepository;
    private final ReviewFindingRepository reviewFindingRepository;
    private final ActivityRepository activityRepository;
    private final ReviewMapper reviewMapper;

    public DashboardService(RepositoryEntityRepository repositoryEntityRepository,
                            PullRequestRepository pullRequestRepository,
                            CodeReviewRepository codeReviewRepository,
                            ReviewFindingRepository reviewFindingRepository,
                            ActivityRepository activityRepository,
                            ReviewMapper reviewMapper) {
        this.repositoryEntityRepository = repositoryEntityRepository;
        this.pullRequestRepository = pullRequestRepository;
        this.codeReviewRepository = codeReviewRepository;
        this.reviewFindingRepository = reviewFindingRepository;
        this.activityRepository = activityRepository;
        this.reviewMapper = reviewMapper;
    }

    @Transactional(readOnly = true)
    public DashboardStatsResponse getStats(Long userId) {
        DashboardStatsResponse stats = new DashboardStatsResponse();

        long reposCount = repositoryEntityRepository.findByUserId(userId).size();
        long prCount = pullRequestRepository.countByRepository_UserId(userId);
        long reviewCount = codeReviewRepository.countByReviewerId(userId);
        long issuesCount = reviewFindingRepository.countTotalIssuesByUserId(userId);

        long criticalIssues = reviewFindingRepository.countByUserIdAndSeverity(userId, FindingSeverity.CRITICAL);
        long highIssues = reviewFindingRepository.countByUserIdAndSeverity(userId, FindingSeverity.HIGH);

        Double avgScore = codeReviewRepository.getAverageScoreByUserId(userId);

        stats.setTotalRepositories(reposCount);
        stats.setTotalPullRequests(prCount);
        stats.setTotalReviews(reviewCount);
        stats.setTotalIssuesDetected(issuesCount);
        stats.setCriticalOrHighIssues(criticalIssues + highIssues);
        stats.setAverageCodeQualityScore(avgScore != null ? Math.round(avgScore * 10.0) / 10.0 : 0.0);

        List<CodeReview> recentReviews = codeReviewRepository.findByReviewerIdOrderByCreatedAtDesc(userId);
        List<ReviewResponse> mappedReviews = recentReviews.stream()
                .limit(5)
                .map(reviewMapper::toResponse)
                .toList();
        stats.setRecentReviews(mappedReviews);

        List<Activity> activities = activityRepository.findTop10ByUserIdOrderByTimestampDesc(userId);
        List<DashboardStatsResponse.ActivityItem> mappedActivities = activities.stream()
                .map(a -> new DashboardStatsResponse.ActivityItem(a.getId(), a.getAction(), a.getDetails(), a.getTimestamp()))
                .toList();
        stats.setRecentActivities(mappedActivities);

        return stats;
    }
}
