package com.example.codereview.dto.team;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TeamResponse {
    private Long id;
    private String name;
    private String description;
    private Long createdById;
    private String createdByName;
    private List<MemberItem> members = new ArrayList<>();
    private LocalDateTime createdAt;

    public static class MemberItem {
        private Long id;
        private Long userId;
        private String name;
        private String email;
        private String roleInTeam;
        private LocalDateTime joinedAt;

        public MemberItem() {
        }

        public MemberItem(Long id, Long userId, String name, String email, String roleInTeam, LocalDateTime joinedAt) {
            this.id = id;
            this.userId = userId;
            this.name = name;
            this.email = email;
            this.roleInTeam = roleInTeam;
            this.joinedAt = joinedAt;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getRoleInTeam() {
            return roleInTeam;
        }

        public void setRoleInTeam(String roleInTeam) {
            this.roleInTeam = roleInTeam;
        }

        public LocalDateTime getJoinedAt() {
            return joinedAt;
        }

        public void setJoinedAt(LocalDateTime joinedAt) {
            this.joinedAt = joinedAt;
        }
    }

    public TeamResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public List<MemberItem> getMembers() {
        return members;
    }

    public void setMembers(List<MemberItem> members) {
        this.members = members;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
