package com.example.codereview.mapper;

import com.example.codereview.dto.repository.RepositoryResponse;
import com.example.codereview.entity.RepositoryEntity;
import org.springframework.stereotype.Component;

@Component
public class RepositoryMapper {

    public RepositoryResponse toResponse(RepositoryEntity repo, long openPrCount, long totalPrCount) {
        if (repo == null) {
            return null;
        }

        RepositoryResponse response = new RepositoryResponse();
        response.setId(repo.getId());
        response.setName(repo.getName());
        response.setFullName(repo.getFullName());
        response.setOwner(repo.getOwner());
        response.setDescription(repo.getDescription());
        response.setDefaultBranch(repo.getDefaultBranch());
        response.setCloneUrl(repo.getCloneUrl());
        response.setPrivate(repo.isPrivate());
        response.setOpenPrCount(openPrCount);
        response.setTotalPrCount(totalPrCount);
        response.setCreatedAt(repo.getCreatedAt());
        response.setUpdatedAt(repo.getUpdatedAt());

        return response;
    }
}
