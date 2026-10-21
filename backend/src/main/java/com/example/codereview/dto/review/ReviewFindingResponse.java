package com.example.codereview.dto.review;

import com.example.codereview.enums.FindingSeverity;
import com.example.codereview.enums.FindingType;

public class ReviewFindingResponse {
    private Long id;
    private FindingType type;
    private FindingSeverity severity;
    private String title;
    private String description;
    private String suggestion;
    private String filePath;
    private Integer lineNumber;

    public ReviewFindingResponse() {
    }

    public ReviewFindingResponse(Long id, FindingType type, FindingSeverity severity, String title, String description, String suggestion, String filePath, Integer lineNumber) {
        this.id = id;
        this.type = type;
        this.severity = severity;
        this.title = title;
        this.description = description;
        this.suggestion = suggestion;
        this.filePath = filePath;
        this.lineNumber = lineNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
