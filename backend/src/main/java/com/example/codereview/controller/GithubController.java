package com.example.codereview.controller;

import com.example.codereview.dto.common.ApiResponse;
import com.example.codereview.dto.repository.ConnectGithubRequest;
import com.example.codereview.dto.repository.RepositoryResponse;
import com.example.codereview.security.CustomUserDetails;
import com.example.codereview.service.GithubService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/github")
public class GithubController {

    private final GithubService githubService;

    public GithubController(GithubService githubService) {
        this.githubService = githubService;
    }

    @PostMapping("/connect")
    public ResponseEntity<ApiResponse<String>> connectAccount(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                              @Valid @RequestBody ConnectGithubRequest request) {
        githubService.connectAccount(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("GitHub account connected and repositories synced successfully", "Connected"));
    }

    @DeleteMapping("/disconnect")
    public ResponseEntity<ApiResponse<Void>> disconnectAccount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        githubService.disconnectAccount(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok("GitHub account disconnected successfully", null));
    }

    @PostMapping("/sync")
    public ResponseEntity<ApiResponse<List<RepositoryResponse>>> syncRepositories(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<RepositoryResponse> repos = githubService.syncRepositories(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok("Repositories synced from GitHub successfully", repos));
    }
}
