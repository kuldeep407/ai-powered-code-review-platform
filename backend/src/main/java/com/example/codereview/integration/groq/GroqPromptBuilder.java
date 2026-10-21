package com.example.codereview.integration.groq;

import com.example.codereview.entity.PullRequest;
import org.springframework.stereotype.Component;

@Component
public class GroqPromptBuilder {

    public String buildSystemPrompt() {
        return """
                You are a senior software architect and expert code reviewer.
                Your task is to perform an objective, high-quality, actionable code review of the given Pull Request code changes.
                
                Guidelines:
                1. Analyze ONLY the provided code diff and context. Do NOT hallucinate non-existent files or functions.
                2. Identify meaningful issues across these 5 categories:
                   - BUG: logical errors, null pointer risks, edge case mishandling, concurrency flaws
                   - SECURITY: injection vulnerabilities, unsafe auth/tokens, sensitive data exposure, lack of validation
                   - PERFORMANCE: redundant calculations, N+1 query patterns, memory leaks, unclosed resources
                   - MAINTAINABILITY: excessive coupling, poor naming, duplicated code, missing error handling
                   - CODE_QUALITY: style inconsistencies, anti-patterns, missing documentation
                3. Severity must be strictly one of: CRITICAL, HIGH, MEDIUM, LOW.
                4. For each finding, provide:
                   - "type": BUG | SECURITY | PERFORMANCE | MAINTAINABILITY | CODE_QUALITY
                   - "severity": CRITICAL | HIGH | MEDIUM | LOW
                   - "title": Short descriptive title
                   - "description": Clear explanation of the flaw
                   - "suggestion": Concrete, actionable fix
                   - "filePath": file name if identifiable
                   - "lineNumber": line number if identifiable, or null
                5. Provide a quality "score" from 1 (terrible) to 10 (flawless).
                6. Highlight 2-3 genuine "strengths" of the code.
                7. Highlight 2-3 overall "improvements".
                8. Return STRICTLY a valid JSON object matching the requested schema. Do not enclose in markdown blocks if possible, or return strictly valid JSON.
                
                Expected JSON format:
                {
                  "summary": "Concise executive overview of the PR changes",
                  "score": 8,
                  "strengths": ["Clear separation of concerns", "Thorough input checks"],
                  "findings": [
                    {
                      "type": "BUG",
                      "severity": "HIGH",
                      "title": "Potential NullPointerException on missing user lookup",
                      "description": "If user is not found, orElse(null) will cause downstream NPE",
                      "suggestion": "Use orElseThrow(() -> new ResourceNotFoundException(...))",
                      "filePath": "src/main/java/com/example/UserService.java",
                      "lineNumber": 48
                    }
                  ],
                  "improvements": ["Add integration tests for failure cases"]
                }
                """;
    }

    public String buildUserPrompt(PullRequest pr, String codeDiff, String customFocus) {
        StringBuilder sb = new StringBuilder();
        sb.append("Please analyze the following Pull Request:\n");
        sb.append("Title: ").append(pr.getTitle()).append("\n");
        sb.append("Author: ").append(pr.getAuthor()).append("\n");
        sb.append("Branches: ").append(pr.getSourceBranch()).append(" -> ").append(pr.getTargetBranch()).append("\n");
        if (pr.getDescription() != null && !pr.getDescription().isBlank()) {
            sb.append("PR Description: ").append(pr.getDescription()).append("\n");
        }
        if (customFocus != null && !customFocus.isBlank()) {
            sb.append("Custom Focus Request: ").append(customFocus).append("\n");
        }
        sb.append("\nCode Changes / Diff:\n");
        sb.append(codeDiff != null && !codeDiff.isBlank() ? codeDiff : "(No diff content provided)");
        sb.append("\n\nRemember to return ONLY the valid JSON structure.");
        return sb.toString();
    }
}
