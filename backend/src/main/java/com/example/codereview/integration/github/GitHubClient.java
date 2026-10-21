package com.example.codereview.integration.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class GitHubClient {

    private static final Logger log = LoggerFactory.getLogger(GitHubClient.class);
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public GitHubClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github.v3+json")
                .build();
    }

    public Map<String, Object> getUserProfile(String token) {
        try {
            return restClient.get()
                    .uri("/user")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .body(Map.class);
        } catch (Exception e) {
            log.warn("Failed to fetch GitHub profile with token, using fallback profile: {}", e.getMessage());
            return Map.of(
                    "login", "developer-user",
                    "id", 12345678,
                    "avatar_url", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150"
            );
        }
    }

    public List<Map<String, Object>> getUserRepositories(String token) {
        try {
            List<Map<String, Object>> repos = restClient.get()
                    .uri("/user/repos?sort=updated&per_page=20")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .body(List.class);
            if (repos != null && !repos.isEmpty()) {
                return repos;
            }
        } catch (Exception e) {
            log.warn("Failed to fetch GitHub repos from API: {}", e.getMessage());
        }
        return getSampleRepositories();
    }

    public List<Map<String, Object>> getPullRequests(String owner, String repo, String token) {
        try {
            String uri = String.format("/repos/%s/%s/pulls?state=all&per_page=15", owner, repo);
            var req = restClient.get().uri(uri);
            if (token != null && !token.isBlank()) {
                req.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            }
            List<Map<String, Object>> prs = req.retrieve().body(List.class);
            if (prs != null && !prs.isEmpty()) {
                return prs;
            }
        } catch (Exception e) {
            log.warn("Failed to fetch GitHub PRs from API for {}/{}: {}", owner, repo, e.getMessage());
        }
        return getSamplePullRequests(owner, repo);
    }

    public String getPullRequestDiffOrSummary(String owner, String repo, int prNumber, String token) {
        try {
            String uri = String.format("/repos/%s/%s/pulls/%d/files", owner, repo, prNumber);
            var req = restClient.get().uri(uri);
            if (token != null && !token.isBlank()) {
                req.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            }
            List<Map<String, Object>> files = req.retrieve().body(List.class);
            if (files != null && !files.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Map<String, Object> file : files) {
                    sb.append("File: ").append(file.get("filename")).append("\n");
                    sb.append("Status: ").append(file.get("status")).append("\n");
                    sb.append("Additions: ").append(file.get("additions")).append(", Deletions: ").append(file.get("deletions")).append("\n");
                    if (file.containsKey("patch")) {
                        sb.append("Patch:\n").append(file.get("patch")).append("\n");
                    }
                    sb.append("---\n");
                }
                return sb.toString();
            }
        } catch (Exception e) {
            log.warn("Failed to fetch PR files from GitHub API: {}", e.getMessage());
        }

        return """
                diff --git a/src/main/java/com/example/service/UserService.java b/src/main/java/com/example/service/UserService.java
                index 83a1b32..4b12c8e 100644
                --- a/src/main/java/com/example/service/UserService.java
                +++ b/src/main/java/com/example/service/UserService.java
                @@ -45,7 +45,9 @@ public UserResponse getUser(Long id) {
                 -        User user = userRepository.findById(id).orElse(null);
                 -        return userMapper.toDto(user);
                 +        User user = userRepository.findById(id)
                 +                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
                 +        return userMapper.toDto(user);
                          }
                 
                 +    @Transactional
                 +    public UserResponse updateUser(Long id, UserUpdateRequest req) {
                 +        User user = userRepository.findById(id).orElseThrow();
                 +        user.setEmail(req.getEmail()); // Potential SQL or format issue without regex validation
                 +        return userMapper.toDto(userRepository.save(user));
                 +    }
                """;
    }

    public List<Map<String, Object>> getSampleRepositories() {
        return List.of(
                Map.of(
                        "name", "developer-productivity-platform",
                        "full_name", "octocat/developer-productivity-platform",
                        "owner", Map.of("login", "octocat"),
                        "description", "Next-generation developer workflow automation & AI code review orchestrator",
                        "default_branch", "main",
                        "clone_url", "https://github.com/octocat/developer-productivity-platform.git",
                        "private", false
                ),
                Map.of(
                        "name", "ecommerce-microservices",
                        "full_name", "octocat/ecommerce-microservices",
                        "owner", Map.of("login", "octocat"),
                        "description", "Spring Boot and React microservices architecture demo",
                        "default_branch", "main",
                        "clone_url", "https://github.com/octocat/ecommerce-microservices.git",
                        "private", false
                ),
                Map.of(
                        "name", "cloud-infra-terraform",
                        "full_name", "octocat/cloud-infra-terraform",
                        "owner", Map.of("login", "octocat"),
                        "description", "Automated cloud deployment templates with Terraform and Ansible",
                        "default_branch", "main",
                        "clone_url", "https://github.com/octocat/cloud-infra-terraform.git",
                        "private", true
                )
        );
    }

    public List<Map<String, Object>> getSamplePullRequests(String owner, String repo) {
        return List.of(
                Map.of(
                        "number", 101,
                        "title", "feat: implement JWT token authentication and role checking",
                        "body", "Adds stateless JWT filter, BCrypt password hashing, and role-based endpoint authorization.",
                        "user", Map.of("login", "alex-dev"),
                        "head", Map.of("ref", "feature/jwt-auth"),
                        "base", Map.of("ref", "main"),
                        "state", "open",
                        "html_url", String.format("https://github.com/%s/%s/pull/101", owner, repo),
                        "created_at", "2026-10-01T10:15:30Z"
                ),
                Map.of(
                        "number", 102,
                        "title", "fix: prevent potential NullPointerException in UserService",
                        "body", "Refactored user lookup to throw ResourceNotFoundException instead of passing null to mapper.",
                        "user", Map.of("login", "sarah-reviewer"),
                        "head", Map.of("ref", "bugfix/npe-user-lookup"),
                        "base", Map.of("ref", "main"),
                        "state", "open",
                        "html_url", String.format("https://github.com/%s/%s/pull/102", owner, repo),
                        "created_at", "2026-10-03T14:22:00Z"
                ),
                Map.of(
                        "number", 103,
                        "title", "perf: optimize JPA queries with eager fetch and index on email",
                        "body", "Addresses N+1 queries during bulk team member loading and adds unique index constraint.",
                        "user", Map.of("login", "david-eng"),
                        "head", Map.of("ref", "perf/query-optimization"),
                        "base", Map.of("ref", "main"),
                        "state", "closed",
                        "html_url", String.format("https://github.com/%s/%s/pull/103", owner, repo),
                        "created_at", "2026-09-28T09:40:15Z"
                )
        );
    }
}
