package com.example.codereview.dto.review;

public class AnalyzePullRequestRequest {
    private String customFocus; // e.g. "Security focus", "Performance focus", or general

    public AnalyzePullRequestRequest() {
    }

    public AnalyzePullRequestRequest(String customFocus) {
        this.customFocus = customFocus;
    }

    public String getCustomFocus() {
        return customFocus;
    }

    public void setCustomFocus(String customFocus) {
        this.customFocus = customFocus;
    }
}
