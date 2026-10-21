package com.example.codereview.service;

import com.example.codereview.dto.team.AddMemberRequest;
import com.example.codereview.dto.team.TeamRequest;
import com.example.codereview.dto.team.TeamResponse;
import com.example.codereview.entity.Team;
import com.example.codereview.entity.TeamMember;
import com.example.codereview.entity.User;
import com.example.codereview.exception.BadRequestException;
import com.example.codereview.exception.ResourceNotFoundException;
import com.example.codereview.mapper.TeamMapper;
import com.example.codereview.repository.TeamMemberRepository;
import com.example.codereview.repository.TeamRepository;
import com.example.codereview.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final TeamMapper teamMapper;
    private final ActivityService activityService;

    public TeamService(TeamRepository teamRepository,
                       TeamMemberRepository teamMemberRepository,
                       UserRepository userRepository,
                       TeamMapper teamMapper,
                       ActivityService activityService) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.userRepository = userRepository;
        this.teamMapper = teamMapper;
        this.activityService = activityService;
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> getUserTeams(Long userId) {
        List<Team> createdTeams = teamRepository.findByCreatedById(userId);
        List<TeamResponse> list = new ArrayList<>();

        for (Team t : createdTeams) {
            List<TeamMember> members = teamMemberRepository.findByTeamId(t.getId());
            list.add(teamMapper.toResponse(t, members));
        }

        return list;
    }

    @Transactional
    public TeamResponse createTeam(Long userId, TeamRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Team team = new Team(request.getName(), request.getDescription(), user);
        Team savedTeam = teamRepository.save(team);

        TeamMember ownerMember = new TeamMember(savedTeam, user, "OWNER");
        teamMemberRepository.save(ownerMember);

        activityService.record(user, "TEAM_CREATED", "Created team: " + savedTeam.getName());

        return teamMapper.toResponse(savedTeam, List.of(ownerMember));
    }

    @Transactional
    public TeamResponse addMember(Long teamId, Long currentUserId, AddMemberRequest request) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + teamId));

        User newMember = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No registered user found with email: " + request.getEmail()));

        if (teamMemberRepository.existsByTeamIdAndUserId(teamId, newMember.getId())) {
            throw new BadRequestException("User is already a member of this team");
        }

        TeamMember member = new TeamMember(team, newMember, request.getRoleInTeam() != null ? request.getRoleInTeam() : "MEMBER");
        teamMemberRepository.save(member);

        User currentUser = userRepository.findById(currentUserId).orElse(null);
        activityService.record(currentUser, "TEAM_MEMBER_ADDED", "Added " + newMember.getEmail() + " to team " + team.getName());

        List<TeamMember> allMembers = teamMemberRepository.findByTeamId(teamId);
        return teamMapper.toResponse(team, allMembers);
    }
}
