package com.example.codereview.mapper;

import com.example.codereview.dto.user.UserResponse;
import com.example.codereview.entity.GithubAccount;
import com.example.codereview.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user, GithubAccount githubAccount) {
        if (user == null) {
            return null;
        }

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());

        if (githubAccount != null) {
            response.setGithubConnected(true);
            response.setGithubUsername(githubAccount.getGithubUsername());
            response.setAvatarUrl(githubAccount.getAvatarUrl());
        } else {
            response.setGithubConnected(false);
        }

        return response;
    }
}
