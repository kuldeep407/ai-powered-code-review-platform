package com.example.codereview.dto.team;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AddMemberRequest {

    @NotBlank(message = "User email is required")
    @Email(message = "Must be a valid email")
    private String email;

    private String roleInTeam = "MEMBER";

    public AddMemberRequest() {
    }

    public AddMemberRequest(String email, String roleInTeam) {
        this.email = email;
        this.roleInTeam = roleInTeam;
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
}
