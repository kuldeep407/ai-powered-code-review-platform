package com.example.codereview.service;

import com.example.codereview.dto.review.AiReviewPayload;
import com.example.codereview.dto.review.AnalyzePullRequestRequest;
import com.example.codereview.dto.review.ReviewResponse;
import com.example.codereview.entity.*;
import com.example.codereview.enums.ReviewStatus;
import com.example.codereview.exception.ResourceNotFoundException;
import com.example.codereview.integration.github.GitHubClient;
import com.example.codereview.integration.groq.GroqClient;
import com.example.codereview.integration.groq.GroqPromptBuilder;
import com.example.codereview.mapper.ReviewMapper;
import com.example.codereview.repository.CodeReviewRepository;
import com.example.codereview.repository.GithubAccountRepository;
import com.example.codereview.repository.PullRequestRepository;
import com.example.codereview.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CodeReviewService {

    private final CodeReviewRepository codeReviewRepository;
    private final PullRequestRepository pullRequestRepository;
    private final UserRepository userRepository;
    private final GithubAccountRepository githubAccountRepository;
    private final GitHubClient gitHubClient;
    private final GroqClient groqClient;
    private final GroqPromptBuilder promptBuilder;
    private final ReviewMapper reviewMapper;
    private final ActivityService activityService;
    private final ObjectMapper objectMapper;

    public CodeReviewService(CodeReviewRepository codeReviewRepository,
                             PullRequestRepository pullRequestRepository,
                             UserRepository userRepository,
                             GithubAccountRepository githubAccountRepository,
                             GitHubClient gitHubClient,
                             GroqClient groqClient,
                             GroqPromptBuilder promptBuilder,
                             ReviewMapper reviewMapper,
                             ActivityService activityService,
                             ObjectMapper objectMapper) {
        this.codeReviewRepository = codeReviewRepository;
        this.pullRequestRepository = pullRequestRepository;
        this.userRepository = userRepository;
        this.githubAccountRepository = githubAccountRepository;
        this.gitHubClient = gitHubClient;
        this.groqClient = groqClient;
        this.promptBuilder = promptBuilder;
        this.reviewMapper = reviewMapper;
        this.activityService = activityService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ReviewResponse analyzePullRequest(Long prId, Long userId, AnalyzePullRequestRequest request) {
        PullRequest pr = pullRequestRepository.findById(prId)
                .orElseThrow(() -> new ResourceNotFoundException("Pull Request not found with id: " + prId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        RepositoryEntity repo = pr.getRepository();
        GithubAccount account = githubAccountRepository.findByUserId(userId).orElse(null);
        String token = account != null ? account.getAccessToken() : null;

        // Fetch PR diff from GitHub or fallback
        String diff = gitHubClient.getPullRequestDiffOrSummary(
                repo.getOwner(),
                repo.getName(),
                pr.getPrNumber(),
                token
        );

        String customFocus = request != null ? request.getCustomFocus() : null;
        String systemPrompt = promptBuilder.buildSystemPrompt();
        String userPrompt = promptBuilder.buildUserPrompt(pr, diff, customFocus);

        // Call Groq AI
        AiReviewPayload aiResult = groqClient.callGroq(systemPrompt, userPrompt);

        CodeReview review = new CodeReview();
        review.setPullRequest(pr);
        review.setReviewer(user);
        review.setScore(aiResult.getScore() != null ? aiResult.getScore() : 7);
        review.setSummary(aiResult.getSummary() != null ? aiResult.getSummary() : "Automated code review completed.");
        review.setStatus(ReviewStatus.COMPLETED);

        try {
            review.setStrengths(objectMapper.writeValueAsString(aiResult.getStrengths()));
            review.setImprovements(objectMapper.writeValueAsString(aiResult.getImprovements()));
        } catch (Exception e) {
            review.setStrengths("[]");
            review.setImprovements("[]");
        }

        if (aiResult.getFindings() != null) {
            for (AiReviewPayload.AiFindingItem item : aiResult.getFindings()) {
                ReviewFinding finding = new ReviewFinding();
                finding.setType(item.getType());
                finding.setSeverity(item.getSeverity());
                finding.setTitle(item.getTitle());
                finding.setDescription(item.getDescription());
                finding.setSuggestion(item.getSuggestion());
                finding.setFilePath(item.getFilePath());
                finding.setLineNumber(item.getLineNumber());
                review.addFinding(finding);
            }
        }

        CodeReview saved = codeReviewRepository.save(review);

        activityService.record(
                user,
                "REVIEW_GENERATED",
                String.format("AI Code Review completed for PR #%d (%s) with score %d/10", pr.getPrNumber(), pr.getTitle(), saved.getScore())
        );

        return reviewMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(Long reviewId, Long userId) {
        CodeReview review = codeReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        return reviewMapper.toResponse(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByPullRequestId(Long prId, Long userId) {
        List<CodeReview> reviews = codeReviewRepository.findByPullRequestIdOrderByCreatedAtDesc(prId);
        return reviews.stream().map(reviewMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getRecentReviews(Long userId) {
        List<CodeReview> reviews = codeReviewRepository.findByReviewerIdOrderByCreatedAtDesc(userId);
        return reviews.stream().map(reviewMapper::toResponse).toList();
    }
}
