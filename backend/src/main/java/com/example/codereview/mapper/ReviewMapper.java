package com.example.codereview.mapper;

import com.example.codereview.dto.review.ReviewFindingResponse;
import com.example.codereview.dto.review.ReviewResponse;
import com.example.codereview.entity.CodeReview;
import com.example.codereview.entity.ReviewFinding;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ReviewMapper {

    private final ObjectMapper objectMapper;

    public ReviewMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ReviewResponse toResponse(CodeReview review) {
        if (review == null) {
            return null;
        }

        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        if (review.getPullRequest() != null) {
            response.setPullRequestId(review.getPullRequest().getId());
            response.setPrNumber(review.getPullRequest().getPrNumber());
            response.setPrTitle(review.getPullRequest().getTitle());
            if (review.getPullRequest().getRepository() != null) {
                response.setRepositoryName(review.getPullRequest().getRepository().getName());
            }
        }
        if (review.getReviewer() != null) {
            response.setReviewerId(review.getReviewer().getId());
            response.setReviewerName(review.getReviewer().getName());
        }
        response.setScore(review.getScore());
        response.setSummary(review.getSummary());
        response.setStatus(review.getStatus());
        response.setCreatedAt(review.getCreatedAt());

        // Parse strengths
        response.setStrengths(parseStringList(review.getStrengths()));
        // Parse improvements
        response.setImprovements(parseStringList(review.getImprovements()));

        // Map findings
        List<ReviewFindingResponse> findingResponses = new ArrayList<>();
        if (review.getFindings() != null) {
            for (ReviewFinding finding : review.getFindings()) {
                findingResponses.add(new ReviewFindingResponse(
                        finding.getId(),
                        finding.getType(),
                        finding.getSeverity(),
                        finding.getTitle(),
                        finding.getDescription(),
                        finding.getSuggestion(),
                        finding.getFilePath(),
                        finding.getLineNumber()
                ));
            }
        }
        response.setFindings(findingResponses);

        return response;
    }

    private List<String> parseStringList(String jsonOrText) {
        if (jsonOrText == null || jsonOrText.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(jsonOrText, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return Arrays.stream(jsonOrText.split("\n"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }
    }
}
