package com.example.codereview.mapper;

import com.example.codereview.dto.team.TeamResponse;
import com.example.codereview.entity.Team;
import com.example.codereview.entity.TeamMember;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TeamMapper {

    public TeamResponse toResponse(Team team, List<TeamMember> members) {
        if (team == null) {
            return null;
        }

        TeamResponse response = new TeamResponse();
        response.setId(team.getId());
        response.setName(team.getName());
        response.setDescription(team.getDescription());
        if (team.getCreatedBy() != null) {
            response.setCreatedById(team.getCreatedBy().getId());
            response.setCreatedByName(team.getCreatedBy().getName());
        }
        response.setCreatedAt(team.getCreatedAt());

        List<TeamResponse.MemberItem> memberItems = new ArrayList<>();
        if (members != null) {
            for (TeamMember m : members) {
                if (m.getUser() != null) {
                    memberItems.add(new TeamResponse.MemberItem(
                            m.getId(),
                            m.getUser().getId(),
                            m.getUser().getName(),
                            m.getUser().getEmail(),
                            m.getRoleInTeam(),
                            m.getJoinedAt()
                    ));
                }
            }
        }
        response.setMembers(memberItems);

        return response;
    }
}
