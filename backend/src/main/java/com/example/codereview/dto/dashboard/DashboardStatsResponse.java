package com.example.codereview.dto.dashboard;

import com.example.codereview.dto.review.ReviewResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DashboardStatsResponse {
    private long totalRepositories;
    private long totalPullRequests;
    private long totalReviews;
    private long totalIssuesDetected;
    private long criticalOrHighIssues;
    private double averageCodeQualityScore;
    private List<ReviewResponse> recentReviews = new ArrayList<>();
    private List<ActivityItem> recentActivities = new ArrayList<>();

    public static class ActivityItem {
        private Long id;
        private String action;
        private String details;
        private LocalDateTime timestamp;

        public ActivityItem() {
        }

        public ActivityItem(Long id, String action, String details, LocalDateTime timestamp) {
            this.id = id;
            this.action = action;
            this.details = details;
            this.timestamp = timestamp;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }
    }

    public DashboardStatsResponse() {
    }

    public long getTotalRepositories() {
        return totalRepositories;
    }

    public void setTotalRepositories(long totalRepositories) {
        this.totalRepositories = totalRepositories;
    }

    public long getTotalPullRequests() {
        return totalPullRequests;
    }

    public void setTotalPullRequests(long totalPullRequests) {
        this.totalPullRequests = totalPullRequests;
    }

    public long getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(long totalReviews) {
        this.totalReviews = totalReviews;
    }

    public long getTotalIssuesDetected() {
        return totalIssuesDetected;
    }

    public void setTotalIssuesDetected(long totalIssuesDetected) {
        this.totalIssuesDetected = totalIssuesDetected;
    }

    public long getCriticalOrHighIssues() {
        return criticalOrHighIssues;
    }

    public void setCriticalOrHighIssues(long criticalOrHighIssues) {
        this.criticalOrHighIssues = criticalOrHighIssues;
    }

    public double getAverageCodeQualityScore() {
        return averageCodeQualityScore;
    }

    public void setAverageCodeQualityScore(double averageCodeQualityScore) {
        this.averageCodeQualityScore = averageCodeQualityScore;
    }

    public List<ReviewResponse> getRecentReviews() {
        return recentReviews;
    }

    public void setRecentReviews(List<ReviewResponse> recentReviews) {
        this.recentReviews = recentReviews;
    }

    public List<ActivityItem> getRecentActivities() {
        return recentActivities;
    }

    public void setRecentActivities(List<ActivityItem> recentActivities) {
        this.recentActivities = recentActivities;
    }
}
