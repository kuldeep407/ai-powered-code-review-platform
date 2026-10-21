package com.example.codereview.dto.repository;

import jakarta.validation.constraints.NotBlank;

public class ConnectGithubRequest {

    @NotBlank(message = "GitHub Personal Access Token or OAuth Code is required")
    private String token;

    private String username;

    public ConnectGithubRequest() {
    }

    public ConnectGithubRequest(String token, String username) {
        this.token = token;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
