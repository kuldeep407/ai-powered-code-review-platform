package com.example.codereview.config;

import com.example.codereview.entity.*;
import com.example.codereview.enums.*;
import com.example.codereview.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final GithubAccountRepository githubAccountRepository;
    private final RepositoryEntityRepository repositoryEntityRepository;
    private final PullRequestRepository pullRequestRepository;
    private final CodeReviewRepository codeReviewRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ActivityRepository activityRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           GithubAccountRepository githubAccountRepository,
                           RepositoryEntityRepository repositoryEntityRepository,
                           PullRequestRepository pullRequestRepository,
                           CodeReviewRepository codeReviewRepository,
                           TeamRepository teamRepository,
                           TeamMemberRepository teamMemberRepository,
                           ActivityRepository activityRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.githubAccountRepository = githubAccountRepository;
        this.repositoryEntityRepository = repositoryEntityRepository;
        this.pullRequestRepository = pullRequestRepository;
        this.codeReviewRepository = codeReviewRepository;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.activityRepository = activityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already contains data, skipping initialization.");
            return;
        }

        log.info("Bootstrapping initial demo data for AI Code Review Platform...");

        // 1. Admin User
        User admin = new User();
        admin.setName("Admin Engineer");
        admin.setEmail("admin@codereview.ai");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(RoleType.ADMIN);
        admin = userRepository.save(admin);

        // 2. Developer User
        User dev = new User();
        dev.setName("Alex Developer");
        dev.setEmail("developer@codereview.ai");
        dev.setPassword(passwordEncoder.encode("dev123"));
        dev.setRole(RoleType.DEVELOPER);
        dev = userRepository.save(dev);

        // 3. GitHub Account for Dev
        GithubAccount githubAccount = new GithubAccount();
        githubAccount.setUser(dev);
        githubAccount.setGithubUsername("alex-dev");
        githubAccount.setGithubId("10293847");
        githubAccount.setAvatarUrl("https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150");
        githubAccount.setAccessToken("ghp_demo_token_mock");
        githubAccountRepository.save(githubAccount);

        // 4. Sample Repositories
        RepositoryEntity repo1 = new RepositoryEntity();
        repo1.setUser(dev);
        repo1.setName("cloud-backend-service");
        repo1.setFullName("alex-dev/cloud-backend-service");
        repo1.setOwner("alex-dev");
        repo1.setDescription("Enterprise Spring Boot REST API for high-throughput cloud automation.");
        repo1.setDefaultBranch("main");
        repo1.setCloneUrl("https://github.com/alex-dev/cloud-backend-service.git");
        repo1.setPrivate(false);
        repo1 = repositoryEntityRepository.save(repo1);

        RepositoryEntity repo2 = new RepositoryEntity();
        repo2.setUser(dev);
        repo2.setName("react-saas-dashboard");
        repo2.setFullName("alex-dev/react-saas-dashboard");
        repo2.setOwner("alex-dev");
        repo2.setDescription("High performance frontend dashboard and analytics engine.");
        repo2.setDefaultBranch("main");
        repo2.setCloneUrl("https://github.com/alex-dev/react-saas-dashboard.git");
        repo2.setPrivate(false);
        repo2 = repositoryEntityRepository.save(repo2);

        // 5. Sample Pull Requests
        PullRequest pr1 = new PullRequest();
        pr1.setRepository(repo1);
        pr1.setPrNumber(101);
        pr1.setTitle("feat: implement JWT token authentication and role checking");
        pr1.setDescription("Adds stateless JWT filter, BCrypt password hashing, and role-based endpoint authorization.");
        pr1.setAuthor("alex-dev");
        pr1.setSourceBranch("feature/jwt-auth");
        pr1.setTargetBranch("main");
        pr1.setStatus(PullRequestStatus.OPEN);
        pr1.setPrUrl("https://github.com/alex-dev/cloud-backend-service/pull/101");
        pr1.setGithubCreatedAt(LocalDateTime.now().minusDays(3));
        pr1 = pullRequestRepository.save(pr1);

        PullRequest pr2 = new PullRequest();
        pr2.setRepository(repo1);
        pr2.setPrNumber(102);
        pr2.setTitle("fix: prevent potential NullPointerException in UserService");
        pr2.setDescription("Refactored user lookup to throw ResourceNotFoundException instead of passing null to mapper.");
        pr2.setAuthor("sarah-reviewer");
        pr2.setSourceBranch("bugfix/npe-user-lookup");
        pr2.setTargetBranch("main");
        pr2.setStatus(PullRequestStatus.OPEN);
        pr2.setPrUrl("https://github.com/alex-dev/cloud-backend-service/pull/102");
        pr2.setGithubCreatedAt(LocalDateTime.now().minusDays(1));
        pr2 = pullRequestRepository.save(pr2);

        PullRequest pr3 = new PullRequest();
        pr3.setRepository(repo2);
        pr3.setPrNumber(42);
        pr3.setTitle("refactor: migrate state management to context and reduce bundle size");
        pr3.setDescription("Cleaned up unneeded vendor packages and converted global state to lightweight React context.");
        pr3.setAuthor("david-frontend");
        pr3.setSourceBranch("refactor/state-cleanup");
        pr3.setTargetBranch("main");
        pr3.setStatus(PullRequestStatus.OPEN);
        pr3.setPrUrl("https://github.com/alex-dev/react-saas-dashboard/pull/42");
        pr3.setGithubCreatedAt(LocalDateTime.now().minusDays(2));
        pr3 = pullRequestRepository.save(pr3);

        // 6. Sample Initial Code Review for PR 1
        CodeReview review = new CodeReview();
        review.setPullRequest(pr1);
        review.setReviewer(dev);
        review.setScore(8);
        review.setSummary("AI-generated analysis: The pull request establishes a robust JWT security filter chain with clean Spring Security integration. Code is well-structured with minor edge cases in token expiry handling.");
        review.setStrengths("[\"Clear separation of concerns between JwtUtils and filter\", \"Strict BCrypt password hashing utilized\", \"Standardized claims formatting\"]");
        review.setImprovements("[\"Enforce token revocation blacklist or short expiry\", \"Add integration tests for expired token cases\"]");
        review.setStatus(ReviewStatus.COMPLETED);

        ReviewFinding f1 = new ReviewFinding();
        f1.setType(FindingType.SECURITY);
        f1.setSeverity(FindingSeverity.HIGH);
        f1.setTitle("Missing token expiration error response differentiation");
        f1.setDescription("Expired tokens produce generic 401 response without giving clients a clear REFRESH_TOKEN signal.");
        f1.setSuggestion("Catch ExpiredJwtException specifically in the filter or entry point to return an EXPIRED_TOKEN error code.");
        f1.setFilePath("src/main/java/com/example/security/JwtAuthenticationFilter.java");
        f1.setLineNumber(38);
        review.addFinding(f1);

        ReviewFinding f2 = new ReviewFinding();
        f2.setType(FindingType.BUG);
        f2.setSeverity(FindingSeverity.MEDIUM);
        f2.setTitle("Unchecked Optional.orElse in User lookup");
        f2.setDescription("Using orElse(null) in the authentication provider risks cascading NullPointerExceptions down the chain.");
        f2.setSuggestion("Use .orElseThrow(() -> new UsernameNotFoundException(...)) to guarantee strict validation.");
        f2.setFilePath("src/main/java/com/example/security/CustomUserDetailsService.java");
        f2.setLineNumber(24);
        review.addFinding(f2);

        ReviewFinding f3 = new ReviewFinding();
        f3.setType(FindingType.CODE_QUALITY);
        f3.setSeverity(FindingSeverity.LOW);
        f3.setTitle("Hardcoded token prefix string");
        f3.setDescription("The string 'Bearer ' is hardcoded across multiple filter methods.");
        f3.setSuggestion("Extract 'Bearer ' to a static final constant: SecurityConstants.TOKEN_PREFIX.");
        f3.setFilePath("src/main/java/com/example/security/JwtAuthenticationFilter.java");
        f3.setLineNumber(45);
        review.addFinding(f3);

        codeReviewRepository.save(review);

        // 7. Sample Team
        Team team = new Team("Core Platform Engineers", "Team responsible for developer tools and AI infrastructure.", dev);
        team = teamRepository.save(team);

        TeamMember m1 = new TeamMember(team, dev, "LEAD");
        TeamMember m2 = new TeamMember(team, admin, "ADMIN");
        teamMemberRepository.saveAll(List.of(m1, m2));

        // 8. Activities
        activityRepository.save(new Activity(dev, "USER_REGISTERED", "Account created successfully"));
        activityRepository.save(new Activity(dev, "GITHUB_CONNECTED", "Connected GitHub account: @alex-dev"));
        activityRepository.save(new Activity(dev, "REPOS_SYNCED", "Synchronized 2 repositories from GitHub"));
        activityRepository.save(new Activity(dev, "REVIEW_GENERATED", "AI Code Review completed for PR #101 with score 8/10"));

        log.info("Demo data bootstrap complete: Developer user 'developer@codereview.ai' with password 'dev123' ready.");
    }
}
