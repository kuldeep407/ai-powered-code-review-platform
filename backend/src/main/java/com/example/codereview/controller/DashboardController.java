package com.example.codereview.controller;

import com.example.codereview.dto.common.ApiResponse;
import com.example.codereview.dto.dashboard.DashboardStatsResponse;
import com.example.codereview.security.CustomUserDetails;
import com.example.codereview.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getStats(@AuthenticationPrincipal CustomUserDetails userDetails) {
        DashboardStatsResponse stats = dashboardService.getStats(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok("Dashboard statistics fetched successfully", stats));
    }
}
