package com.example.codereview.dto.repository;

import jakarta.validation.constraints.NotBlank;

public class AddRepositoryRequest {

    @NotBlank(message = "Repository name is required")
    private String name;

    @NotBlank(message = "Full repository name is required (owner/repo)")
    private String fullName;

    @NotBlank(message = "Owner is required")
    private String owner;

    private String description;

    private String defaultBranch;

    private String cloneUrl;

    private boolean isPrivate;

    public AddRepositoryRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDefaultBranch() {
        return defaultBranch;
    }

    public void setDefaultBranch(String defaultBranch) {
        this.defaultBranch = defaultBranch;
    }

    public String getCloneUrl() {
        return cloneUrl;
    }

    public void setCloneUrl(String cloneUrl) {
        this.cloneUrl = cloneUrl;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }
}
