package com.example.codereview.repository;

import com.example.codereview.entity.PullRequest;
import com.example.codereview.enums.PullRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PullRequestRepository extends JpaRepository<PullRequest, Long> {
    List<PullRequest> findByRepositoryId(Long repositoryId);
    Optional<PullRequest> findByRepositoryIdAndPrNumber(Long repositoryId, Integer prNumber);
    long countByRepository_UserId(Long userId);
    long countByRepository_UserIdAndStatus(Long userId, PullRequestStatus status);
}
