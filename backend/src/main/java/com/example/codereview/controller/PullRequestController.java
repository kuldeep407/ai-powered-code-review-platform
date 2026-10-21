package com.example.codereview.controller;

import com.example.codereview.dto.common.ApiResponse;
import com.example.codereview.dto.pullrequest.PullRequestResponse;
import com.example.codereview.security.CustomUserDetails;
import com.example.codereview.service.PullRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PullRequestController {

    private final PullRequestService pullRequestService;

    public PullRequestController(PullRequestService pullRequestService) {
        this.pullRequestService = pullRequestService;
    }

    @GetMapping("/pull-requests")
    public ResponseEntity<ApiResponse<List<PullRequestResponse>>> getAllPullRequests(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<PullRequestResponse> list = pullRequestService.getAllPullRequestsForUser(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok("Pull requests fetched successfully", list));
    }

    @GetMapping("/pull-requests/{id}")
    public ResponseEntity<ApiResponse<PullRequestResponse>> getPullRequestById(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                               @PathVariable Long id) {
        PullRequestResponse pr = pullRequestService.getPullRequestById(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok("Pull request fetched successfully", pr));
    }

    @GetMapping("/repositories/{repoId}/pull-requests")
    public ResponseEntity<ApiResponse<List<PullRequestResponse>>> getPullRequestsByRepo(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                                        @PathVariable Long repoId) {
        List<PullRequestResponse> list = pullRequestService.getPullRequestsByRepo(repoId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok("Repository pull requests fetched successfully", list));
    }

    @PostMapping("/repositories/{repoId}/pull-requests/sync")
    public ResponseEntity<ApiResponse<List<PullRequestResponse>>> syncPullRequests(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                                   @PathVariable Long repoId) {
        List<PullRequestResponse> list = pullRequestService.syncPullRequestsForRepo(repoId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok("Pull requests synced successfully", list));
    }
}
