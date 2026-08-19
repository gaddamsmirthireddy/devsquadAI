package com.devsquad.project;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectEventRepository extends JpaRepository<ProjectEvent, UUID> {
    List<ProjectEvent> findByProjectId(UUID projectId);
}