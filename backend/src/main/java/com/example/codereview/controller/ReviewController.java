package com.example.codereview.controller;

import com.example.codereview.dto.common.ApiResponse;
import com.example.codereview.dto.review.AnalyzePullRequestRequest;
import com.example.codereview.dto.review.ReviewResponse;
import com.example.codereview.security.CustomUserDetails;
import com.example.codereview.service.CodeReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReviewController {

    private final CodeReviewService codeReviewService;

    public ReviewController(CodeReviewService codeReviewService) {
        this.codeReviewService = codeReviewService;
    }

    @PostMapping("/pull-requests/{prId}/analyze")
    public ResponseEntity<ApiResponse<ReviewResponse>> analyzePullRequest(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                          @PathVariable Long prId,
                                                                          @RequestBody(required = false) AnalyzePullRequestRequest request) {
        ReviewResponse review = codeReviewService.analyzePullRequest(prId, userDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("AI code review completed successfully", review));
    }

    @GetMapping("/reviews/{id}")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReviewById(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                     @PathVariable Long id) {
        ReviewResponse review = codeReviewService.getReviewById(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok("Review fetched successfully", review));
    }

    @GetMapping("/pull-requests/{prId}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsForPullRequest(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                                      @PathVariable Long prId) {
        List<ReviewResponse> list = codeReviewService.getReviewsByPullRequestId(prId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok("Pull request reviews fetched successfully", list));
    }

    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getRecentReviews(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ReviewResponse> list = codeReviewService.getRecentReviews(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok("Recent reviews fetched successfully", list));
    }
}
