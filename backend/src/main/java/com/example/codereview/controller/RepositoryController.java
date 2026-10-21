package com.example.codereview.controller;

import com.example.codereview.dto.common.ApiResponse;
import com.example.codereview.dto.repository.AddRepositoryRequest;
import com.example.codereview.dto.repository.RepositoryResponse;
import com.example.codereview.security.CustomUserDetails;
import com.example.codereview.service.RepositoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repositories")
public class RepositoryController {

    private final RepositoryService repositoryService;

    public RepositoryController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RepositoryResponse>>> getRepositories(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<RepositoryResponse> repos = repositoryService.getUserRepositories(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok("Repositories fetched successfully", repos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RepositoryResponse>> getRepository(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                         @PathVariable Long id) {
        RepositoryResponse repo = repositoryService.getRepositoryById(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok("Repository fetched successfully", repo));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RepositoryResponse>> addRepository(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                         @Valid @RequestBody AddRepositoryRequest request) {
        RepositoryResponse created = repositoryService.addRepository(userDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Repository added successfully", created));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRepository(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                              @PathVariable Long id) {
        repositoryService.deleteRepository(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok("Repository removed successfully", null));
    }
}
