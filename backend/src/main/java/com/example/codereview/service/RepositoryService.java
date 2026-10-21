package com.example.codereview.service;

import com.example.codereview.dto.repository.AddRepositoryRequest;
import com.example.codereview.dto.repository.RepositoryResponse;
import com.example.codereview.entity.PullRequest;
import com.example.codereview.entity.RepositoryEntity;
import com.example.codereview.entity.User;
import com.example.codereview.enums.PullRequestStatus;
import com.example.codereview.exception.ResourceNotFoundException;
import com.example.codereview.mapper.RepositoryMapper;
import com.example.codereview.repository.PullRequestRepository;
import com.example.codereview.repository.RepositoryEntityRepository;
import com.example.codereview.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RepositoryService {

    private final RepositoryEntityRepository repositoryEntityRepository;
    private final PullRequestRepository pullRequestRepository;
    private final UserRepository userRepository;
    private final RepositoryMapper repositoryMapper;
    private final ActivityService activityService;

    public RepositoryService(RepositoryEntityRepository repositoryEntityRepository,
                             PullRequestRepository pullRequestRepository,
                             UserRepository userRepository,
                             RepositoryMapper repositoryMapper,
                             ActivityService activityService) {
        this.repositoryEntityRepository = repositoryEntityRepository;
        this.pullRequestRepository = pullRequestRepository;
        this.userRepository = userRepository;
        this.repositoryMapper = repositoryMapper;
        this.activityService = activityService;
    }

    @Transactional(readOnly = true)
    public List<RepositoryResponse> getUserRepositories(Long userId) {
        List<RepositoryEntity> repos = repositoryEntityRepository.findByUserId(userId);
        List<RepositoryResponse> results = new ArrayList<>();

        for (RepositoryEntity repo : repos) {
            List<PullRequest> prs = pullRequestRepository.findByRepositoryId(repo.getId());
            long openCount = prs.stream().filter(p -> p.getStatus() == PullRequestStatus.OPEN).count();
            results.add(repositoryMapper.toResponse(repo, openCount, prs.size()));
        }

        return results;
    }

    @Transactional(readOnly = true)
    public RepositoryResponse getRepositoryById(Long repoId, Long userId) {
        RepositoryEntity repo = repositoryEntityRepository.findById(repoId)
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found with id: " + repoId));

        List<PullRequest> prs = pullRequestRepository.findByRepositoryId(repo.getId());
        long openCount = prs.stream().filter(p -> p.getStatus() == PullRequestStatus.OPEN).count();

        return repositoryMapper.toResponse(repo, openCount, prs.size());
    }

    @Transactional
    public RepositoryResponse addRepository(Long userId, AddRepositoryRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        RepositoryEntity repo = new RepositoryEntity();
        repo.setUser(user);
        repo.setName(request.getName());
        repo.setFullName(request.getFullName());
        repo.setOwner(request.getOwner());
        repo.setDescription(request.getDescription());
        repo.setDefaultBranch(request.getDefaultBranch() != null ? request.getDefaultBranch() : "main");
        repo.setCloneUrl(request.getCloneUrl());
        repo.setPrivate(request.isPrivate());

        RepositoryEntity saved = repositoryEntityRepository.save(repo);

        activityService.record(user, "REPO_ADDED", "Added repository: " + saved.getFullName());

        return repositoryMapper.toResponse(saved, 0, 0);
    }

    @Transactional
    public void deleteRepository(Long repoId, Long userId) {
        RepositoryEntity repo = repositoryEntityRepository.findById(repoId)
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found with id: " + repoId));

        repositoryEntityRepository.delete(repo);
    }
}
