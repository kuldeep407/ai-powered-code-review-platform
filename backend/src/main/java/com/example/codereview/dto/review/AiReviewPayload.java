package com.example.codereview.dto.review;

import com.example.codereview.enums.FindingSeverity;
import com.example.codereview.enums.FindingType;
import java.util.ArrayList;
import java.util.List;

public class AiReviewPayload {
    private String summary;
    private Integer score;
    private List<String> strengths = new ArrayList<>();
    private List<String> improvements = new ArrayList<>();
    private List<AiFindingItem> findings = new ArrayList<>();

    public static class AiFindingItem {
        private FindingType type;
        private FindingSeverity severity;
        private String title;
        private String description;
        private String suggestion;
        private String filePath;
        private Integer lineNumber;

        public AiFindingItem() {
        }

        public FindingType getType() {
            return type;
        }

        public void setType(FindingType type) {
            this.type = type;
        }

        public FindingSeverity getSeverity() {
            return severity;
        }

        public void setSeverity(FindingSeverity severity) {
            this.severity = severity;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getSuggestion() {
            return suggestion;
        }

        public void setSuggestion(String suggestion) {
            this.suggestion = suggestion;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public Integer getLineNumber() {
            return lineNumber;
        }

        public void setLineNumber(Integer lineNumber) {
            this.lineNumber = lineNumber;
        }
    }

    public AiReviewPayload() {
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }

    public List<String> getImprovements() {
        return improvements;
    }

    public void setImprovements(List<String> improvements) {
        this.improvements = improvements;
    }

    public List<AiFindingItem> getFindings() {
        return findings;
    }

    public void setFindings(List<AiFindingItem> findings) {
        this.findings = findings;
    }
}
