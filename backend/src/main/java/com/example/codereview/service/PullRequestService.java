package com.example.codereview.service;

import com.example.codereview.dto.pullrequest.PullRequestResponse;
import com.example.codereview.entity.CodeReview;
import com.example.codereview.entity.GithubAccount;
import com.example.codereview.entity.PullRequest;
import com.example.codereview.entity.RepositoryEntity;
import com.example.codereview.enums.PullRequestStatus;
import com.example.codereview.exception.ResourceNotFoundException;
import com.example.codereview.integration.github.GitHubClient;
import com.example.codereview.mapper.PullRequestMapper;
import com.example.codereview.repository.CodeReviewRepository;
import com.example.codereview.repository.GithubAccountRepository;
import com.example.codereview.repository.PullRequestRepository;
import com.example.codereview.repository.RepositoryEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PullRequestService {

    private final PullRequestRepository pullRequestRepository;
    private final RepositoryEntityRepository repositoryEntityRepository;
    private final CodeReviewRepository codeReviewRepository;
    private final GithubAccountRepository githubAccountRepository;
    private final GitHubClient gitHubClient;
    private final PullRequestMapper pullRequestMapper;
    private final ActivityService activityService;

    public PullRequestService(PullRequestRepository pullRequestRepository,
                              RepositoryEntityRepository repositoryEntityRepository,
                              CodeReviewRepository codeReviewRepository,
                              GithubAccountRepository githubAccountRepository,
                              GitHubClient gitHubClient,
                              PullRequestMapper pullRequestMapper,
                              ActivityService activityService) {
        this.pullRequestRepository = pullRequestRepository;
        this.repositoryEntityRepository = repositoryEntityRepository;
        this.codeReviewRepository = codeReviewRepository;
        this.githubAccountRepository = githubAccountRepository;
        this.gitHubClient = gitHubClient;
        this.pullRequestMapper = pullRequestMapper;
        this.activityService = activityService;
    }

    @Transactional(readOnly = true)
    public List<PullRequestResponse> getPullRequestsByRepo(Long repoId, Long userId) {
        RepositoryEntity repo = repositoryEntityRepository.findById(repoId)
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found with id: " + repoId));

        List<PullRequest> prs = pullRequestRepository.findByRepositoryId(repoId);
        return mapToResponses(prs);
    }

    @Transactional(readOnly = true)
    public List<PullRequestResponse> getAllPullRequestsForUser(Long userId) {
        List<RepositoryEntity> userRepos = repositoryEntityRepository.findByUserId(userId);
        List<PullRequest> allPrs = new ArrayList<>();
        for (RepositoryEntity repo : userRepos) {
            allPrs.addAll(pullRequestRepository.findByRepositoryId(repo.getId()));
        }
        return mapToResponses(allPrs);
    }

    @Transactional(readOnly = true)
    public PullRequestResponse getPullRequestById(Long prId, Long userId) {
        PullRequest pr = pullRequestRepository.findById(prId)
                .orElseThrow(() -> new ResourceNotFoundException("Pull Request not found with id: " + prId));

        Optional<CodeReview> latestReview = codeReviewRepository.findFirstByPullRequestIdOrderByCreatedAtDesc(pr.getId());
        boolean hasReview = latestReview.isPresent();
        Integer score = latestReview.map(CodeReview::getScore).orElse(null);

        return pullRequestMapper.toResponse(pr, hasReview, score);
    }

    @Transactional
    public List<PullRequestResponse> syncPullRequestsForRepo(Long repoId, Long userId) {
        RepositoryEntity repo = repositoryEntityRepository.findById(repoId)
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found with id: " + repoId));

        GithubAccount account = githubAccountRepository.findByUserId(userId).orElse(null);
        String token = account != null ? account.getAccessToken() : null;

        List<Map<String, Object>> externalPrs = gitHubClient.getPullRequests(repo.getOwner(), repo.getName(), token);

        for (Map<String, Object> prData : externalPrs) {
            Integer prNumber = (Integer) prData.get("number");
            String title = (String) prData.get("title");
            String description = (String) prData.get("body");
            String htmlUrl = (String) prData.get("html_url");
            String stateStr = (String) prData.get("state");

            Map<String, Object> userMap = (Map<String, Object>) prData.get("user");
            String author = userMap != null ? (String) userMap.get("login") : "contributor";

            Map<String, Object> headMap = (Map<String, Object>) prData.get("head");
            String sourceBranch = headMap != null ? (String) headMap.get("ref") : "feature";

            Map<String, Object> baseMap = (Map<String, Object>) prData.get("base");
            String targetBranch = baseMap != null ? (String) baseMap.get("ref") : "main";

            PullRequest pr = pullRequestRepository.findByRepositoryIdAndPrNumber(repoId, prNumber)
                    .orElse(new PullRequest());

            pr.setRepository(repo);
            pr.setPrNumber(prNumber);
            pr.setTitle(title);
            pr.setDescription(description);
            pr.setAuthor(author);
            pr.setSourceBranch(sourceBranch);
            pr.setTargetBranch(targetBranch);
            pr.setPrUrl(htmlUrl);
            pr.setGithubCreatedAt(LocalDateTime.now().minusDays(2));

            if ("closed".equalsIgnoreCase(stateStr)) {
                pr.setStatus(PullRequestStatus.CLOSED);
            } else {
                pr.setStatus(PullRequestStatus.OPEN);
            }

            pullRequestRepository.save(pr);
        }

        activityService.record(repo.getUser(), "PRS_SYNCED", "Synced pull requests for repository: " + repo.getFullName());

        List<PullRequest> updatedPrs = pullRequestRepository.findByRepositoryId(repoId);
        return mapToResponses(updatedPrs);
    }

    private List<PullRequestResponse> mapToResponses(List<PullRequest> prs) {
        List<PullRequestResponse> list = new ArrayList<>();
        for (PullRequest pr : prs) {
            Optional<CodeReview> review = codeReviewRepository.findFirstByPullRequestIdOrderByCreatedAtDesc(pr.getId());
            boolean hasReview = review.isPresent();
            Integer score = review.map(CodeReview::getScore).orElse(null);
            list.add(pullRequestMapper.toResponse(pr, hasReview, score));
        }
        return list;
    }
}
