package com.example.codereview.repository;

import com.example.codereview.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findTop10ByUserIdOrderByTimestampDesc(Long userId);
    List<Activity> findByUserIdOrderByTimestampDesc(Long userId);
}
