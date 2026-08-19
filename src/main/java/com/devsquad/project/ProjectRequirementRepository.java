package com.devsquad.project;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProjectRequirementRepository extends JpaRepository<ProjectRequirement, UUID> {
    Optional<ProjectRequirement> findByProjectId(UUID projectId);
}