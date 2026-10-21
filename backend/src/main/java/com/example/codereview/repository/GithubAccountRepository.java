package com.example.codereview.repository;

import com.example.codereview.entity.GithubAccount;
import com.example.codereview.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GithubAccountRepository extends JpaRepository<GithubAccount, Long> {
    Optional<GithubAccount> findByUser(User user);
    Optional<GithubAccount> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
