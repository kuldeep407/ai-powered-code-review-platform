package com.example.codereview.controller;

import com.example.codereview.dto.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        return ResponseEntity.ok(ApiResponse.ok(
                "AI Code Review Platform Service is running healthy",
                Map.of(
                        "status", "UP",
                        "service", "codereview-platform",
                        "version", "1.0.0"
                )
        ));
    }
}
