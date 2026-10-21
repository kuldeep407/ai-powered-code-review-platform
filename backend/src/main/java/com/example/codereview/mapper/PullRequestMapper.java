package com.example.codereview.mapper;

import com.example.codereview.dto.pullrequest.PullRequestResponse;
import com.example.codereview.entity.PullRequest;
import org.springframework.stereotype.Component;

@Component
public class PullRequestMapper {

    public PullRequestResponse toResponse(PullRequest pr, boolean hasReview, Integer latestScore) {
        if (pr == null) {
            return null;
        }

        PullRequestResponse response = new PullRequestResponse();
        response.setId(pr.getId());
        if (pr.getRepository() != null) {
            response.setRepositoryId(pr.getRepository().getId());
            response.setRepositoryName(pr.getRepository().getName());
            response.setRepositoryFullName(pr.getRepository().getFullName());
        }
        response.setPrNumber(pr.getPrNumber());
        response.setTitle(pr.getTitle());
        response.setDescription(pr.getDescription());
        response.setAuthor(pr.getAuthor());
        response.setSourceBranch(pr.getSourceBranch());
        response.setTargetBranch(pr.getTargetBranch());
        response.setStatus(pr.getStatus());
        response.setPrUrl(pr.getPrUrl());
        response.setHasReview(hasReview);
        response.setLatestScore(latestScore);
        response.setGithubCreatedAt(pr.getGithubCreatedAt());
        response.setCreatedAt(pr.getCreatedAt());

        return response;
    }
}
