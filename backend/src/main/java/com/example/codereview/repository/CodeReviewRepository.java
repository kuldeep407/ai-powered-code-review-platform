package com.example.codereview.repository;

import com.example.codereview.entity.CodeReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeReviewRepository extends JpaRepository<CodeReview, Long> {
    List<CodeReview> findByPullRequestIdOrderByCreatedAtDesc(Long pullRequestId);
    Optional<CodeReview> findFirstByPullRequestIdOrderByCreatedAtDesc(Long pullRequestId);
    List<CodeReview> findByReviewerIdOrderByCreatedAtDesc(Long reviewerId);

    @Query("SELECT AVG(c.score) FROM CodeReview c WHERE c.reviewer.id = :userId")
    Double getAverageScoreByUserId(@Param("userId") Long userId);

    long countByReviewerId(Long reviewerId);
}
