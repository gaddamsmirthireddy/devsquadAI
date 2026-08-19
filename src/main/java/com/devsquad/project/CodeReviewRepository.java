package com.devsquad.project;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CodeReviewRepository extends JpaRepository<CodeReview, UUID> {
    List<CodeReview> findByProjectId(UUID projectId);
}