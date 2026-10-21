package com.example.codereview.repository;

import com.example.codereview.entity.RepositoryEntity;
import com.example.codereview.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryEntityRepository extends JpaRepository<RepositoryEntity, Long> {
    List<RepositoryEntity> findByUser(User user);
    List<RepositoryEntity> findByUserId(Long userId);
    Optional<RepositoryEntity> findByUserIdAndFullName(Long userId, String fullName);
}
