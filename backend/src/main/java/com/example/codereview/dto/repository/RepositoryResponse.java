package com.example.codereview.dto.repository;

import java.time.LocalDateTime;

public class RepositoryResponse {
    private Long id;
    private String name;
    private String fullName;
    private String owner;
    private String description;
    private String defaultBranch;
    private String cloneUrl;
    private boolean isPrivate;
    private long openPrCount;
    private long totalPrCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RepositoryResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public long getOpenPrCount() {
        return openPrCount;
    }

    public void setOpenPrCount(long openPrCount) {
        this.openPrCount = openPrCount;
    }

    public long getTotalPrCount() {
        return totalPrCount;
    }

    public void setTotalPrCount(long totalPrCount) {
        this.totalPrCount = totalPrCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
