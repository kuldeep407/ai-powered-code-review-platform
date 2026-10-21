package com.example.codereview.controller;

import com.example.codereview.dto.common.ApiResponse;
import com.example.codereview.dto.team.AddMemberRequest;
import com.example.codereview.dto.team.TeamRequest;
import com.example.codereview.dto.team.TeamResponse;
import com.example.codereview.security.CustomUserDetails;
import com.example.codereview.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TeamResponse>>> getTeams(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<TeamResponse> teams = teamService.getUserTeams(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok("Teams fetched successfully", teams));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TeamResponse>> createTeam(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                @Valid @RequestBody TeamRequest request) {
        TeamResponse created = teamService.createTeam(userDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Team created successfully", created));
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<ApiResponse<TeamResponse>> addMember(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                               @PathVariable Long id,
                                                               @Valid @RequestBody AddMemberRequest request) {
        TeamResponse updated = teamService.addMember(id, userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Member added to team successfully", updated));
    }
}
