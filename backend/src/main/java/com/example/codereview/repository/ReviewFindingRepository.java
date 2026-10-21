package com.example.codereview.repository;

import com.example.codereview.entity.ReviewFinding;
import com.example.codereview.enums.FindingSeverity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewFindingRepository extends JpaRepository<ReviewFinding, Long> {
    List<ReviewFinding> findByCodeReviewId(Long codeReviewId);

    @Query("SELECT COUNT(rf) FROM ReviewFinding rf WHERE rf.codeReview.reviewer.id = :userId")
    long countTotalIssuesByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(rf) FROM ReviewFinding rf WHERE rf.codeReview.reviewer.id = :userId AND rf.severity = :severity")
    long countByUserIdAndSeverity(@Param("userId") Long userId, @Param("severity") FindingSeverity severity);
}
