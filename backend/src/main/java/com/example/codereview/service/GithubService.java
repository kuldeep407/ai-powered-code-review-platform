package com.example.codereview.service;

import com.example.codereview.dto.repository.ConnectGithubRequest;
import com.example.codereview.dto.repository.RepositoryResponse;
import com.example.codereview.entity.GithubAccount;
import com.example.codereview.entity.RepositoryEntity;
import com.example.codereview.entity.User;
import com.example.codereview.exception.ResourceNotFoundException;
import com.example.codereview.integration.github.GitHubClient;
import com.example.codereview.mapper.RepositoryMapper;
import com.example.codereview.repository.GithubAccountRepository;
import com.example.codereview.repository.PullRequestRepository;
import com.example.codereview.repository.RepositoryEntityRepository;
import com.example.codereview.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GithubService {

    private final GithubAccountRepository githubAccountRepository;
    private final UserRepository userRepository;
    private final RepositoryEntityRepository repositoryEntityRepository;
    private final PullRequestRepository pullRequestRepository;
    private final GitHubClient gitHubClient;
    private final RepositoryMapper repositoryMapper;
    private final ActivityService activityService;

    public GithubService(GithubAccountRepository githubAccountRepository,
                         UserRepository userRepository,
                         RepositoryEntityRepository repositoryEntityRepository,
                         PullRequestRepository pullRequestRepository,
                         GitHubClient gitHubClient,
                         RepositoryMapper repositoryMapper,
                         ActivityService activityService) {
        this.githubAccountRepository = githubAccountRepository;
        this.userRepository = userRepository;
        this.repositoryEntityRepository = repositoryEntityRepository;
        this.pullRequestRepository = pullRequestRepository;
        this.gitHubClient = gitHubClient;
        this.repositoryMapper = repositoryMapper;
        this.activityService = activityService;
    }

    @Transactional
    public GithubAccount connectAccount(Long userId, ConnectGithubRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Map<String, Object> profile = gitHubClient.getUserProfile(request.getToken());

        String username = request.getUsername();
        if (username == null || username.isBlank()) {
            username = (String) profile.getOrDefault("login", "developer");
        }

        String avatar = (String) profile.getOrDefault("avatar_url", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150");
        String githubId = String.valueOf(profile.getOrDefault("id", "12345678"));

        GithubAccount account = githubAccountRepository.findByUserId(userId)
                .orElse(new GithubAccount());

        account.setUser(user);
        account.setAccessToken(request.getToken());
        account.setGithubUsername(username);
        account.setAvatarUrl(avatar);
        account.setGithubId(githubId);

        GithubAccount saved = githubAccountRepository.save(account);

        activityService.record(user, "GITHUB_CONNECTED", "Connected GitHub account: @" + username);

        // Auto-sync initial repos
        syncRepositories(userId);

        return saved;
    }

    @Transactional
    public void disconnectAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        githubAccountRepository.deleteByUserId(userId);
        activityService.record(user, "GITHUB_DISCONNECTED", "Disconnected GitHub integration");
    }

    @Transactional
    public List<RepositoryResponse> syncRepositories(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        GithubAccount account = githubAccountRepository.findByUserId(userId).orElse(null);
        String token = account != null ? account.getAccessToken() : null;

        List<Map<String, Object>> externalRepos = gitHubClient.getUserRepositories(token);
        List<RepositoryResponse> responses = new ArrayList<>();

        for (Map<String, Object> repoData : externalRepos) {
            String fullName = (String) repoData.get("full_name");
            String name = (String) repoData.get("name");
            String description = (String) repoData.get("description");
            String defaultBranch = (String) repoData.getOrDefault("default_branch", "main");
            String cloneUrl = (String) repoData.get("clone_url");
            boolean isPrivate = Boolean.TRUE.equals(repoData.get("private"));

            Map<String, Object> ownerMap = (Map<String, Object>) repoData.get("owner");
            String owner = ownerMap != null ? (String) ownerMap.get("login") : "octocat";

            RepositoryEntity entity = repositoryEntityRepository
                    .findByUserIdAndFullName(userId, fullName)
                    .orElse(new RepositoryEntity());

            entity.setUser(user);
            entity.setName(name);
            entity.setFullName(fullName);
            entity.setOwner(owner);
            entity.setDescription(description);
            entity.setDefaultBranch(defaultBranch);
            entity.setCloneUrl(cloneUrl);
            entity.setPrivate(isPrivate);

            RepositoryEntity saved = repositoryEntityRepository.save(entity);

            long totalPr = pullRequestRepository.findByRepositoryId(saved.getId()).size();
            responses.add(repositoryMapper.toResponse(saved, 0, totalPr));
        }

        activityService.record(user, "REPOS_SYNCED", "Synchronized " + responses.size() + " repositories from GitHub");
        return responses;
    }
}
